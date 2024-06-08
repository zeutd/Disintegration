package disintegration.world.blocks.payload;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.*;

public class PayloadFork extends VelocityPayloadConveyor{
    public boolean invert = false;

    public Block point;

    public Point2[] points;

    public int sortOutputIndex;
    public PayloadFork(String name) {
        super(name);
        quickRotate = false;
        configurable = true;
        clearOnDoubleTap = true;
        config(Block.class, (PayloadForkBuild tile, Block item) -> tile.sorted = item);
        config(UnitType.class, (PayloadForkBuild tile, UnitType item) -> tile.sorted = item);
        configClear((PayloadForkBuild tile) -> tile.sorted = null);
    }

    @Override
    public void init(){
        float max = 0;
        for(Point2 p : points) max = Math.max(p.dst(0, 0), max);
        clipSize = max * tilesize;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        for (Point2 point2 : points) {
            Tmp.p1.set(point2).rotate(rotation);
            Tile tile1 = Vars.world.tile(tile.x + Tmp.p1.x, tile.y + Tmp.p1.y);
            if (tile1 == null || !canReplace(tile1.block())) return false;
        }
        return super.canPlaceOn(tile, team, rotation);
    }

    public boolean canSort(Block b){
        return b.isVisible() && !(b instanceof CoreBlock) && !state.rules.isBanned(b) && b.environmentBuildable();
    }

    public boolean canSort(UnitType t){
        return !t.isHidden() && !t.isBanned() && t.supportsEnv(state.rules.env);
    }

    public class PayloadForkBuild extends VelocityPayloadConveyorBuild{
        public @Nullable UnlockableContent sorted;
        public int dir;

        public Building[] buildings = new Building[points.length + 1];

        public Seq<Payload> payloads = new Seq<>();

        public ObjectMap<Payload, Vec2> payVectors = new ObjectMap<>();

        public ObjectMap<Payload, Float> velocities = new ObjectMap<>();

        public ObjectMap<Payload, Building> dests = new ObjectMap<>();

        public Building other(){
            return buildings[dir + 1];
        }


        @Override
        public void placed(){
            super.placed();
            buildings[0] = this;
            for(int i = 0; i < points.length; i++) {
                Tmp.p1.set(points[i]).rotate(rotation);
                Tile tile1 = Vars.world.tile(tileX() + Tmp.p1.x, tileY() + Tmp.p1.y);
                tile1.setBlock(point, team, rotation + 2);
                buildings[i + 1] = tile1.build;
                ((PayloadForkPoint.PayloadForkPointBuild) buildings[i + 1]).buildings = buildings;
            }
        }

        @Override
        public void remove(){
            super.remove();
            for (Building b : buildings) {
                if(b != null && b != this && b.isAdded()) b.tile.remove();
            }
        }

        @Override
        public void draw(){
            super.draw();
            payloads.each(Payload::draw);
        }

        public boolean checkMatch(Payload item){
            boolean matches = sorted != null &&
                    (item instanceof BuildPayload build && build.block() == sorted) ||
                    (item instanceof UnitPayload unit && unit.unit.type == sorted);

            if(invert) matches = !matches;
            return matches;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(timer(timerDump, dumpTime)) {
                for (int i = 0; i < points.length; i++) {
                    Tmp.p1.set(points[i]).rotate(rotation);
                    Tile tile1 = Vars.world.tile(tileX() + Tmp.p1.x, tileY() + Tmp.p1.y);
                    if (!(tile1.build instanceof PayloadForkPoint.PayloadForkPointBuild)) {
                        tile.remove();
                        return;
                    }
                    buildings[i + 1] = tile1.build;
                    ((PayloadForkPoint.PayloadForkPointBuild) buildings[i + 1]).buildings = buildings;
                }
            }
            if(payload != null) {
                if (!payloads.contains(p -> p.within(x, y, payload.size()))) {
                    if(checkMatch(payload)){
                        dir = sortOutputIndex;

                    }
                    else {
                        dir += 1;
                        dir = Mathf.mod(dir, points.length);
                        if(dir == sortOutputIndex && sorted != null) dir++;
                        dir = Mathf.mod(dir, points.length);
                    }
                    payloads.add(payload);
                    velocities.put(payload, Math.abs(velocity));
                    dests.put(payload, other());
                    payVectors.put(payload, new Vec2(0, 0));
                    velocity = 0;
                    payload = null;

                }
            }
            if (payloads.isEmpty()) return;
            payloads.each(p -> {
                Vec2 v = payVectors.get(p);
                p.set(x + v.x, y + v.y, payRotation);
                Tmp.v1.set(dests.get(p)).sub(p).setLength(p.size() / 2);
                if(!payloads.contains(p2 -> p != p2 && dests.get(p) == dests.get(p2) && p2.within(p.x() + Tmp.v1.x, p.y() + Tmp.v1.y, p.size()))) v.approach(Tmp.v1.set(dests.get(p).x - x, dests.get(p).y - y), velocities.get(p) * delta());
                if(v.within(Tmp.v1, 0.001f)){
                    Tile next;
                    if(dests.get(p) == buildings[0]){
                        next = dests.get(p).tile.nearby(Geometry.d4(this.rotation).x, Geometry.d4(this.rotation).y);
                    }
                    else{
                        next = dests.get(p).tile.nearby(Geometry.d4(this.rotation + 2).x, Geometry.d4(this.rotation + 2).y);
                    }
                    if (next != null && next.build != null && next.build.team == this.team &&next.build.acceptPayload(this, p)) {
                        next.build.handlePayload(dests.get(p), p);
                        if(next.build instanceof VelocityPayloadConveyorBuild build){
                            build.velocity = velocities.get(p);
                        }
                        payloads.remove(p);
                        velocities.remove(p);
                        dests.remove(p);
                        payVectors.remove(p);
                    }
                }
            });
        }

        @Override
        public void buildConfiguration(Table table){
            ItemSelection.buildTable(PayloadFork.this, table,
                    content.blocks().select(PayloadFork.this::canSort).<UnlockableContent>as()
                            .add(content.units().select(PayloadFork.this::canSort).as()),
                    () -> (UnlockableContent)config(), this::configure);
        }

        @Override
        public Object config(){
            return sorted;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.b(sorted == null ? -1 : sorted.getContentType().ordinal());
            write.s(sorted == null ? -1 : sorted.id);
            write.b(dir);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            byte ctype = read.b();
            short sort = read.s();
            sorted = ctype == -1 ? null : Vars.content.getByID(ContentType.all[ctype], sort);
            dir = read.b();
        }
    }
}
