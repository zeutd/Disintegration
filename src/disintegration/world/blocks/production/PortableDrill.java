package disintegration.world.blocks.production;

import arc.util.Nullable;
import mindustry.content.UnitTypes;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.blocks.ControlBlock;
import mindustry.world.blocks.production.Drill;

public class PortableDrill extends Drill {
    public UnitType portableUnitType;

    public PortableDrill(String name) {
        super(name);
    }

    public class PortableDrillBuild extends DrillBuild implements ControlBlock {
        @Nullable
        public BlockUnitc unit;
        public Unit portableUnit;
        public Unit unit() {
            if (unit == null) {
                unit = (BlockUnitc) UnitTypes.block.create(this.team);
                unit.tile(this);
            }

            return (Unit)unit;
        }

        @Override
        public boolean canControl(){
            return true;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(unit != null && isControlled()){
                portableUnit = portableUnitType.create(team);
                portableUnit.set(x, y);
                portableUnit.add();
                portableUnit.rotation(90);
                unit.getPlayer().unit(portableUnit);
                unit.remove();
                unit.killed();
                tile().remove();
            }
        }
    }
}
