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

public class PayloadCross extends Block {
    public int length = 2;

    public Block point;

    public DrawBlock drawer = new DrawDefault();

    public PayloadCross(String name) {
        super(name);
        rotate = false;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        Tile tile1 = Vars.world.tile(tile.x + Geometry.d4x(0) * length, tile.y + Geometry.d4y(0) * length);
        Tile tile2 = Vars.world.tile(tile.x + Geometry.d4x(1) * length, tile.y + Geometry.d4y(1) * length);
        Tile tile3 = Vars.world.tile(tile.x + Geometry.d4x(2) * length, tile.y + Geometry.d4y(2) * length);
        Tile tile4 = Vars.world.tile(tile.x + Geometry.d4x(3) * length, tile.y + Geometry.d4y(3) * length);
        return super.canPlaceOn(tile, team, rotation)
                && (tile1 != null && canReplace(tile1.block()))
                && (tile2 != null && canReplace(tile2.block()))
                && (tile3 != null && canReplace(tile3.block()))
                && (tile4 != null && canReplace(tile4.block()));
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

    public class PayloadCrossBuild extends Building {

        public int lastChange;

        public Building[] buildings = new Building[5];

        public Seq<Payload> payloads = new Seq<>();

        public ObjectMap<Payload, Vec2> payVectors = new ObjectMap<>();

        public ObjectMap<Payload, Float> velocities = new ObjectMap<>();

        public ObjectMap<Payload, Building> dests = new ObjectMap<>();


        @Override
        public void placed() {
            super.placed();
            Tile tile1 = Vars.world.tile(tile.x + Geometry.d4x(0) * length, tile.y + Geometry.d4y(0) * length);
            Tile tile2 = Vars.world.tile(tile.x + Geometry.d4x(1) * length, tile.y + Geometry.d4y(1) * length);
            Tile tile3 = Vars.world.tile(tile.x + Geometry.d4x(2) * length, tile.y + Geometry.d4y(2) * length);
            Tile tile4 = Vars.world.tile(tile.x + Geometry.d4x(3) * length, tile.y + Geometry.d4y(3) * length);
            tile1.setBlock(point, team, 0);
            tile2.setBlock(point, team, 1);
            tile3.setBlock(point, team, 2);
            tile4.setBlock(point, team, 3);
            buildings[0] = tile1.build;
            buildings[1] = tile2.build;
            buildings[2] = tile3.build;
            buildings[3] = tile4.build;
            buildings[4] = this;
            ((PayloadCrossPoint.PayloadCrossPointBuild) buildings[0]).buildings = buildings;
            ((PayloadCrossPoint.PayloadCrossPointBuild) buildings[1]).buildings = buildings;
            ((PayloadCrossPoint.PayloadCrossPointBuild) buildings[2]).buildings = buildings;
            ((PayloadCrossPoint.PayloadCrossPointBuild) buildings[3]).buildings = buildings;
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
                Tile tile1 = Vars.world.tile(tile.x + Geometry.d4x(0) * length, tile.y + Geometry.d4y(0) * length);
                Tile tile2 = Vars.world.tile(tile.x + Geometry.d4x(1) * length, tile.y + Geometry.d4y(1) * length);
                Tile tile3 = Vars.world.tile(tile.x + Geometry.d4x(2) * length, tile.y + Geometry.d4y(2) * length);
                Tile tile4 = Vars.world.tile(tile.x + Geometry.d4x(3) * length, tile.y + Geometry.d4y(3) * length);
                if (!(tile1.build instanceof PayloadCrossPoint.PayloadCrossPointBuild b1) || !(tile2.build instanceof PayloadCrossPoint.PayloadCrossPointBuild b2) || !(tile3.build instanceof PayloadCrossPoint.PayloadCrossPointBuild b3) || !(tile4.build instanceof PayloadCrossPoint.PayloadCrossPointBuild b4)) {
                    tile.remove();
                    return;
                }
                buildings[0] = b1;
                buildings[1] = b2;
                buildings[2] = b3;
                buildings[3] = b4;
                buildings[4] = this;
                b1.buildings = buildings;
                b2.buildings = buildings;
                b3.buildings = buildings;
                b4.buildings = buildings;
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
                    next = dests.get(p).tile.nearby(Geometry.d4(dests.get(p).rotation).x, Geometry.d4(dests.get(p).rotation).y);
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

