package disintegration.world.blocks.temperature;

import arc.struct.Seq;
import disintegration.util.WorldDef;
import mindustry.gen.Building;
import mindustry.world.Block;

public class TemperatureSource extends Block{
    public TemperatureSource(String name) {
        super(name);

        update = true;
    }

    public class TemperatureSourceBuild extends Building implements TemperatureBlock{
        @Override
        public float temperature() {
            return 1000;
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
                    ((TemperatureBlock)build).setTemperature(1000);
                }
            }
        }
    }
}
