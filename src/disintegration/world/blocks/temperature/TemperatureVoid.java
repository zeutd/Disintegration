package disintegration.world.blocks.temperature;

import arc.struct.Seq;
import disintegration.util.WorldDef;
import mindustry.gen.Building;
import mindustry.world.Block;

public class TemperatureVoid extends Block {
    public TemperatureVoid(String name) {
        super(name);

        update = true;
    }

    public class TemperatureVoidBuild extends Building implements TemperatureBlock{
        @Override
        public float temperature() {
            return 0;
        }

        @Override
        public void addTemperature(float target){}

        @Override
        public void setTemperature(float target){}

        @Override
        public void updateTile(){
            Seq<Building> proximityBuilds = this.proximity();

            for(Building build : proximityBuilds){
                if(WorldDef.conductSideTemperature(this, build)){
                    ((TemperatureBlock)build).setTemperature(0);
                }
            }
        }
    }
}
