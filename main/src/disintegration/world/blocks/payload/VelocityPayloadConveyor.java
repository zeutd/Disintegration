package disintegration.world.blocks.payload;

import arc.func.FloatFloatf;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.util.Eachable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.content.DTFx;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.draw.DrawBlock;

import static arc.math.Mathf.mod;
import static java.lang.Math.abs;
import static mindustry.Vars.tilesize;

public class VelocityPayloadConveyor extends PayloadBlock {
    public float force;
    public float explodeVelocity = 10f;
    public FloatFloatf forceFormula = f -> 1f / (abs(f) + 1f);

    public DrawBlock drawer;

    public VelocityPayloadConveyor(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons() {
        return drawer.finalIcons(this);
    }

    public class VelocityPayloadConveyorBuild extends PayloadBlockBuild<Payload> {
        public float velocity;

        public Vec2 dest;

        @Override
        public boolean acceptUnitPayload(Unit unit) {
            return payload == null;
        }

        @Override
        public void draw() {
            drawer.draw(this);
            drawPayload();
        }

        public void explode() {
            if (payload instanceof BuildPayload bp) {
                DTFx.blockFly.at(bp.x(), bp.y(), rotdeg(), bp.block());
            } else if (payload instanceof UnitPayload up) {
                up.dump();
                up.unit.vel.add(Tmp.v1.trns(rotdeg(), velocity * 2));
            }
            payload = null;
        }

        //let b=Vars.world.tile(Vars.player.tileX(),Vars.player.tileY()).build;b.block.forceFormula.get(b.velocity)
        @Override
        public void updateTile() {
            super.updateTile();
            if (payload == null) return;
            updatePayload();
            int trns = this.block.size / 2 + 1;
            Tile next;
            if (velocity >= 0) {
                dest = Tmp.v1.trns(rotdeg(), size * tilesize / 2f);
                payVector.approach(dest, velocity * delta());
                next = this.tile.nearby(Geometry.d4(this.rotation).x * trns, Geometry.d4(this.rotation).y * trns);
            } else {
                dest = Tmp.v1.trns(rotdeg() + 180, size * tilesize / 2f);
                payVector.approach(dest, -velocity * delta());
                next = this.tile.nearby(Geometry.d4(this.rotation + 2).x * trns, Geometry.d4(this.rotation + 2).y * trns);
            }
            if (payVector.within(dest, 0.001f)) {
                if (next != null && next.build != null && next.build.team == this.team) {
                    if (next.build instanceof VelocityPayloadConveyorBuild build) {
                        if (build.rotation == rotation || build.rotation == mod(rotation + 2, 4)) {
                            if (build.acceptPayload(this, payload)) {
                                float relativeVelocity = 0f;
                                if (build.rotation == rotation) relativeVelocity = velocity;
                                else if (build.rotation == mod(rotation + 2, 4)) relativeVelocity = -velocity;
                                build.handlePayload(this, payload);
                                payload = null;
                                build.velocity = relativeVelocity;
                                velocity = 0;
                            } else if (build.rotation == mod(rotation + 2, 4)) build.velocity = -velocity;
                            else if (build.rotation == rotation) build.velocity = velocity;
                        } else if (abs(velocity) > explodeVelocity) explode();
                    } else {
                        if (abs(velocity) < explodeVelocity) {
                            Building build = next.build;
                            if (build.acceptPayload(this, payload)) {
                                build.handlePayload(this, payload);
                                velocity = 0;
                                payload = null;
                            }
                        } else explode();
                    }
                } else if (abs(velocity) > explodeVelocity) explode();
                else if (payload instanceof UnitPayload up) {
                    up.dump();
                    up.unit.vel.add(Tmp.v1.trns(rotdeg(), velocity * 2));
                    payload = null;
                }
            } else {
                velocity += forceFormula.get(velocity) * force * edelta();
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(velocity);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            velocity = read.f();
        }
    }
}
