package degradation.world.blocks.temperature;

import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.Block;

public class TemperatureVoid extends Block{
    public TextureRegion bottomRegion;
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
        public float conductionSpeed() {
            return 1;
        }
    }
}
