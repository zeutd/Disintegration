package disintegration.ai.types;

import disintegration.world.blocks.defence.RepairDroneStation;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Building;
import mindustry.gen.BuildingTetherc;
import mindustry.gen.Call;
import mindustry.world.blocks.ConstructBlock;

import static mindustry.Vars.indexer;

public class RepairDroneAI extends AIController {
    Building damagedTarget;

    @Override
    public void updateMovement(){
        if(unit instanceof BuildingTetherc bt){
            RepairDroneStation.RepairDroneStationBuild parent = (RepairDroneStation.RepairDroneStationBuild) bt.building();
            if (parent == null) return;
            if (parent.efficiency <= 0) Call.unitDespawn(unit);
            if (parent.repairing) {
                if (target instanceof Building) {
                    boolean shoot = false;

                    if (target.within(unit, unit.type.range)) {
                        unit.aim(target);
                        shoot = true;
                    }

                    unit.controlWeapons(shoot);
                } else if (target == null) {
                    unit.controlWeapons(false);
                }

                if (target != null) {
                    if (unit.type.circleTarget) {
                        circleAttack(120f);
                    } else if (!target.within(unit, unit.type.range * 0.65f)) {
                        moveTo(target, unit.type.range * 0.65f);
                    }

                    if (!unit.type.circleTarget) {
                        unit.lookAt(target);
                    }
                }
            }
            else{
                unit.lookAt(parent);
                moveTo(parent,0);
                if(unit.within(parent,parent.block.size)){
                    Units.unitDespawn(unit);
                }
            }
        }
    }

    @Override
    public void updateTargeting(){
        if(unit instanceof BuildingTetherc bt) {
            RepairDroneStation.RepairDroneStationBuild parent = (RepairDroneStation.RepairDroneStationBuild) bt.building();
            if (parent == null) return;
            if (timer.get(timerTarget, 15)) {
                damagedTarget = indexer.getDamaged(unit.team).copy().removeAll(b -> b.dst(parent.x, parent.y) > ((RepairDroneStation) parent.block).repairRange).min(b -> b.dst2(unit.x, unit.y));
                if (damagedTarget instanceof ConstructBlock.ConstructBuild) damagedTarget = null;
            }

            if (damagedTarget != null) {
                this.target = damagedTarget;
            }
        }
    }
}
