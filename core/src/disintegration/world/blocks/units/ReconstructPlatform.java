package disintegration.world.blocks.units;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.net.DTCall;
import mindustry.ai.types.AssemblerAI;
import mindustry.content.Fx;
import mindustry.gen.BuildingTetherc;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.blocks.units.Reconstructor;

import static mindustry.Vars.net;
import static mindustry.Vars.state;

public class ReconstructPlatform extends Reconstructor {
    public UnitType droneType;
    public int dronesCreated = 4;
    public float droneConstructTime = 60f * 4f;
    public float[] dronePos;

    public static void reconstructDroneSpawned(Tile tile, int id) {
        if (tile == null || !(tile.build instanceof ReconstructorPlatformBuild build)) return;
        build.droneSpawned(id);
    }

    public ReconstructPlatform(String name) {
        super(name);
    }

    public class ReconstructorPlatformBuild extends ReconstructorBuild {
        protected IntSeq readUnits = new IntSeq();
        //holds drone IDs that have been sent, but not synced yet - add to list as soon as possible
        protected IntSeq whenSyncedUnits = new IntSeq();
        public Seq<Unit> units = new Seq<>();

        public float droneWarmup;
        public float droneProgress, totalDroneProgress;

        @Override
        public void updateTile() {
            super.updateTile();
            if (!readUnits.isEmpty()) {
                units.clear();
                readUnits.each(i -> {
                    var unit = Groups.unit.getByID(i);
                    if (unit != null) {
                        units.add(unit);
                    }
                });
                readUnits.clear();
            }

            //read newly synced drones on client end
            if (units.size < dronesCreated && whenSyncedUnits.size > 0) {
                whenSyncedUnits.each(id -> {
                    var unit = Groups.unit.getByID(id);
                    if (unit != null) {
                        units.addUnique(unit);
                    }
                });
            }

            units.removeAll(u -> !u.isAdded() || u.dead || !(u.controller() instanceof AssemblerAI));

            //unsupported
            if (!allowUpdate()) {
                progress = 0f;
                units.each(Unit::kill);
                units.clear();
            }

            float powerStatus = power == null ? 1f : power.status;
            droneWarmup = Mathf.lerpDelta(droneWarmup, units.size < dronesCreated ? powerStatus : 0f, 0.1f);
            totalDroneProgress += droneWarmup * delta();

            if (units.size < dronesCreated && (droneProgress += delta() * state.rules.unitBuildSpeed(team) * powerStatus / droneConstructTime) >= 1f) {
                if (!net.client()) {
                    var unit = droneType.create(team);
                    if (unit instanceof BuildingTetherc bt) {
                        bt.building(this);
                    }
                    unit.set(x, y);
                    unit.rotation = 90f;
                    unit.add();
                    units.add(unit);
                    DTCall.reconstructDroneSpawned(tile, unit.id);
                }
            }

            if (units.size >= dronesCreated) {
                droneProgress = 0f;
            }

            for (int i = 0; i < units.size; i++) {
                var unit = units.get(i);
                var ai = (AssemblerAI) unit.controller();
                ai.targetPos.set(dronePos[i * 2] + x, dronePos[i * 2 + 1] + y);
                ai.targetAngle = i * 90f + 45f + 180f;
            }
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);

            //draw input
            boolean fallback = true;
            for (int i = 0; i < 4; i++) {
                if (blends(i) && i != rotation) {
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                    fallback = false;
                }
            }
            if (fallback) Draw.rect(inRegion, x, y, rotation * 90);

            Draw.rect(outRegion, x, y, rotdeg());

            if (droneWarmup > 0.001f) {
                Draw.draw(Layer.blockOver + 0.2f, () -> {
                    Drawf.construct(this, droneType.fullIcon, Pal.accent, 0f, droneProgress, droneWarmup, totalDroneProgress, 14f);
                });
            }

            if (constructing() && hasArrived()) {
                Draw.draw(Layer.blockOver, () -> {
                    Draw.rect(payload.unit.type.fullIcon, x, y, payload.rotation() - 90);
                    Draw.reset();
                });
                Draw.z(Layer.buildBeam);
                Draw.color(Pal.accent);
                for (var unit : units) {
                    if (!((AssemblerAI) unit.controller()).inPosition()) continue;

                    float
                            px = unit.x + Angles.trnsx(unit.rotation, unit.type.buildBeamOffset),
                            py = unit.y + Angles.trnsy(unit.rotation, unit.type.buildBeamOffset);

                    Drawf.buildBeam(px, py, x, y, upgrade(payload.unit.type).hitSize / 2f);
                }
                Fill.rect(x, y, upgrade(payload.unit.type).hitSize, upgrade(payload.unit.type).hitSize);
                Draw.reset();
                Draw.draw(Layer.blockBuilding, () -> {
                    Draw.color(Pal.accent);

                    Shaders.blockbuild.region = upgrade(payload.unit.type).fullIcon;
                    Shaders.blockbuild.time = Time.time;
                    //margin due to units not taking up whole region
                    Shaders.blockbuild.progress = fraction();

                    Draw.rect(upgrade(payload.unit.type).fullIcon, x, y, payload.rotation() - 90f);
                    Draw.flush();
                    Draw.color();
                });
            } else {
                Draw.z(Layer.blockOver);

                drawPayload();
            }

            Draw.z(Layer.blockBuilding + 0.1f);
            Draw.rect(topRegion, x, y);
        }

        public void droneSpawned(int id) {
            Fx.spawn.at(x, y);
            droneProgress = 0f;
            if (net.client()) {
                whenSyncedUnits.add(id);
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.b(units.size);
            for (var unit : units) {
                write.i(unit.id);
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            int count = read.b();
            readUnits.clear();
            for (int i = 0; i < count; i++) {
                readUnits.add(read.i());
            }
            whenSyncedUnits.clear();
        }
    }
}
