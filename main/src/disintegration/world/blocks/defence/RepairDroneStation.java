package disintegration.world.blocks.defence;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.ai.types.RepairDroneAI;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;

import static mindustry.Vars.*;

public class RepairDroneStation extends Block {
    public UnitType unitType;
    public int dronesCreated = 4;

    public float buildTime = 300f;

    public float intervalTime = 10f;
    public float repairRange = 200;

    public float polyStroke = 1.8f, polyRadius = 8f;
    public int polySides = 6;
    public float polyRotateSpeed = 1f;
    public Color polyColor = Pal.heal;

    public RepairDroneStation(String name) {
        super(name);

        solid = true;
        update = true;
        hasItems = true;
        itemCapacity = 200;
        ambientSound = Sounds.respawning;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return super.canPlaceOn(tile, team, rotation) && Units.canCreate(team, unitType);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * 8, y * 8, repairRange, Pal.accent);
        if(!Units.canCreate(Vars.player.team(), unitType)){
            drawPlaceText(Core.bundle.get("bar.cargounitcap"), x, y, valid);
        }
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("units", (RepairDroneStationBuild e) ->
                new Bar(
                        () ->
                                Core.bundle.format("bar.unitcap",
                                        Fonts.getUnicodeStr(unitType.name),
                                        e.unitsCreated,
                                        dronesCreated
                                ),
                        () -> Pal.power,
                        () -> (float)e.unitsCreated / dronesCreated
                ));
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.unitType, table -> {
            table.row();
            table.table(Styles.grayPanel, b -> {
                b.image(unitType.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                b.table(info -> {
                    info.add(unitType.localizedName).left();
                    if(Core.settings.getBool("console")){
                        info.row();
                        info.add(unitType.name).left().color(Color.lightGray);
                    }
                });
            }).growX().pad(5).marginLeft(12f).row();
        });
    }

    public class RepairDroneStationBuild extends Building {
        public boolean repairing;

        public float droneProgress;
        public float readyness;
        public float buildProgress;
        public float totalProgress;
        public float warmup;

        public Seq<Unit> units = new Seq<>();

        public IntSeq readUnits = new IntSeq(dronesCreated);

        public int unitsCreated;

        public boolean buildingUnits(){
            return unitsCreated < dronesCreated;
        }

        @Override
        public void updateTile() {
            if(!readUnits.isEmpty()){
                readUnits.each(id -> {
                    var unit = Groups.unit.getByID(id);
                    if (unit != null) {
                        units.add(unit);
                        if (unit instanceof BuildingTetherc bt) {
                            bt.building(this);
                        }
                    }
                });
            }
            if (buildingUnits()) {
                buildProgress += edelta() / buildTime;
                if (buildProgress >= 1) {
                    buildProgress = 0;
                    unitsCreated++;
                    Fx.spawn.at(x, y);
                }
            }
            warmup = Mathf.approachDelta(warmup, efficiency * (buildingUnits() ? 1 : 0), 1f / 60f);
            totalProgress += edelta();
            for (int i = 0; i < units.size; i++){
                Unit u = units.get(i);
                if (!u.isAdded() || u.dead || !(u.controller() instanceof RepairDroneAI)){
                    units.remove(i);
                    unitsCreated--;
                }
            }
            //units.removeAll(u -> !u.isAdded() || u.dead || !(u.controller() instanceof RepairDroneAI));
            if(!allowUpdate() || dead()){
                units.each(Units::unitDespawn);
                units.clear();
            }

            repairing = !indexer.getDamaged(team).copy().removeAll(b -> b.dst(x, y) > repairRange || b.id == id || b.dead()).isEmpty();

            if(repairing && units.size < dronesCreated && units.size < unitsCreated && (droneProgress += delta() * state.rules.unitBuildSpeed(team) * efficiency / intervalTime) >= 1f){
                if(!net.client()){
                    var unit = unitType.create(team);
                    if(unit instanceof BuildingTetherc bt){
                        bt.building(this);
                    }
                    Fx.spawn.at(x,y);
                    unit.set(x, y);
                    unit.rotation = 90f;
                    unit.add();
                    units.add(unit);
                }
                droneProgress = 0;
            }

            if(units.size >= dronesCreated){
                droneProgress = 0f;
            }

            for(int i = 0; i < units.size; i++){
                var unit = units.get(i);
                if(unit instanceof BuildingTetherc bt){
                    bt.building(this);
                }
            }

            readyness = Mathf.approachDelta(readyness, !units.isEmpty() ? 1f : 0f, 1f / 60f);
        }

        @Override
        public void draw(){
            super.draw();
            Draw.z(Layer.bullet - 0.01f);
            Draw.color(polyColor);
            Lines.stroke(polyStroke * readyness);
            Lines.poly(x, y, polySides, polyRadius, Time.time * polyRotateSpeed);
            Draw.reset();
            if(!Mathf.zero(warmup)) {
                Draw.draw(Layer.blockOver, () -> {
                    Drawf.construct(this, unitType.fullIcon, 0f, buildProgress, warmup, totalProgress);
                });
            }
            Draw.z(Layer.block);
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, repairRange, Pal.accent);
        }

        @Override
        public boolean shouldConsume(){
            return repairing || units.size > 0 || buildingUnits();
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.b(units.size);
            write.b(unitsCreated);
            for(var unit : units){
                write.i(unit.id);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            int count = read.b();
            readUnits.clear();
            unitsCreated = read.b();
            for (int i = 0; i < count; i++) {
                readUnits.add(read.i());
            }
        }
    }
}
