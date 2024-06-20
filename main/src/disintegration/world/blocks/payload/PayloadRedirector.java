package disintegration.world.blocks.payload;

import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class PayloadRedirector extends Block {
    public int length = 5;

    public Block point;

    public DrawBlock drawer = new DrawDefault();

    public PayloadRedirector(String name) {
        super(name);
        quickRotate = false;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        Tile tile1 = Vars.world.tile(tile.x + Geometry.d8edge(rotation).x * length, tile.y + Geometry.d8edge(rotation).y * length);
        Tile tile2 = Vars.world.tile(tile.x - Geometry.d8edge(rotation).x * length, tile.y - Geometry.d8edge(rotation).y * length);
        return super.canPlaceOn(tile, team, rotation)
                && (tile1 != null && canReplace(tile1.block()))
                && (tile2 != null && canReplace(tile2.block()));
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public void init() {
        clipSize = length * tilesize * 2;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons() {
        return drawer.finalIcons(this);
    }

    public class PayloadRedirectorBuild extends Building {

        public int lastChange;

        public Building[] buildings = new Building[3];

        public Seq<Payload> payloads = new Seq<>();

        public ObjectMap<Payload, Vec2> payVectors = new ObjectMap<>();

        public ObjectMap<Payload, Float> velocities = new ObjectMap<>();

        public ObjectMap<Payload, Building> dests = new ObjectMap<>();


        @Override
        public void placed() {
            super.placed();
            Tile tile1 = Vars.world.tile(tileX() + Geometry.d8edge(rotation).x * length, tileY() + Geometry.d8edge(rotation).y * length);
            Tile tile2 = Vars.world.tile(tileX() - Geometry.d8edge(rotation).x * length, tileY() - Geometry.d8edge(rotation).y * length);
            tile1.setBlock(point, team, rotation + 1);
            tile2.setBlock(point, team, rotation + 2);
            buildings[0] = tile1.build;
            buildings[1] = this;
            buildings[2] = tile2.build;
            ((PayloadRedirectorPoint.PayloadRedirectorPointBuild) buildings[0]).buildings = buildings;
            ((PayloadRedirectorPoint.PayloadRedirectorPointBuild) buildings[2]).buildings = buildings;
        }

        @Override
        public void remove() {
            super.remove();
            for (Building b : buildings) {
                if (b != null && b != this && b.isAdded()) b.tile.remove();
            }
        }

        @Override
        public void draw() {
            drawer.draw(this);
            payloads.each(Payload::draw);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if (lastChange != world.tileChanges) {
                lastChange = world.tileChanges;
                Tile tile1 = Vars.world.tile(tileX() + Geometry.d8edge(rotation).x * length, tileY() + Geometry.d8edge(rotation).y * length);
                Tile tile2 = Vars.world.tile(tileX() - Geometry.d8edge(rotation).x * length, tileY() - Geometry.d8edge(rotation).y * length);
                if (!(tile1.build instanceof PayloadRedirectorPoint.PayloadRedirectorPointBuild b1) || !(tile2.build instanceof PayloadRedirectorPoint.PayloadRedirectorPointBuild b2)) {
                    tile.remove();
                    return;
                }
                buildings[0] = b1;
                buildings[1] = this;
                buildings[2] = b2;
                b1.buildings = buildings;
                b2.buildings = buildings;
            }
            if (payloads.isEmpty()) return;
            payloads.each(p -> {
                Vec2 v = payVectors.get(p);
                p.set(x + v.x, y + v.y, 0);
                Tmp.v1.set(dests.get(p)).sub(p).setLength(p.size());
                if (!payloads.contains(p2 -> p != p2 && dests.get(p) == dests.get(p2) && p2.within(p.x() + Tmp.v1.x, p.y() + Tmp.v1.y, p.size() / 2)))
                    v.approach(Tmp.v1.set(dests.get(p).x - x, dests.get(p).y - y), velocities.get(p) * delta());
                if (v.within(Tmp.v1, 0.001f)) {
                    Tile next;
                    if (dests.get(p) == buildings[0]) {
                        next = dests.get(p).tile.nearby(Geometry.d4(this.rotation + 1).x, Geometry.d4(this.rotation + 1).y);
                    } else {
                        next = dests.get(p).tile.nearby(Geometry.d4(this.rotation + 2).x, Geometry.d4(this.rotation + 2).y);
                    }
                    if (next != null && next.build != null && next.build.team == this.team && next.build.acceptPayload(this, p)) {
                        next.build.handlePayload(dests.get(p), p);
                        if (next.build instanceof VelocityPayloadConveyor.VelocityPayloadConveyorBuild build) {
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
    }
}

