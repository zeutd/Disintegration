package disintegration.world.blocks.power;

import mindustry.entities.Puddles;
import mindustry.type.Liquid;
import mindustry.world.blocks.power.ConsumeGenerator;

public class SpreadGenerator extends ConsumeGenerator {
    public float spreadMinWarmup;

    public Liquid[] spreadLiquids;
    public SpreadGenerator(String name) {
        super(name);
    }

    public class SpreadGeneratorBuild extends ConsumeGeneratorBuild {
        @Override
        public void updateTile(){
            super.updateTile();
            if(warmup() >= spreadMinWarmup){
                for (Liquid spreadLiquid : spreadLiquids) {
                    proximity.forEach(b -> {
                        if (b.liquids != null && b.liquids.get(spreadLiquid) == 0) {
                            Puddles.deposit(b.tile, spreadLiquid, 0.03f);
                        }
                    });
                }
            }
        }
    }
}
