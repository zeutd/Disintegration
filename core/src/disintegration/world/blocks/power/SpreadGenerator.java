package disintegration.world.blocks.power;

import arc.func.Boolf;
import arc.util.Nullable;
import mindustry.entities.Puddles;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.power.ConsumeGenerator;

public class SpreadGenerator extends ConsumeGenerator {
    public float spreadMinWarmup;
    public float spreadAmount;

    public Liquid[] spreadLiquids;

    @Nullable
    public Boolf<Building> spreadTarget;

    public SpreadGenerator(String name) {
        super(name);
    }

    public class SpreadGeneratorBuild extends ConsumeGeneratorBuild {
        @Override
        public void updateTile() {
            super.updateTile();
            if (warmup() >= spreadMinWarmup) {
                for (Liquid spreadLiquid : spreadLiquids) {
                    proximity.each(b -> {
                        if (spreadTarget == null || spreadTarget.get(b)) {
                            Puddles.deposit(b.tile, spreadLiquid, spreadAmount);
                        }
                    });
                }
            }
        }
    }
}
