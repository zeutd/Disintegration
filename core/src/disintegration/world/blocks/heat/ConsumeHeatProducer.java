package disintegration.world.blocks.heat;

import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.consumers.ConsumeItemFilter;
import mindustry.world.consumers.ConsumeLiquidFilter;

public class ConsumeHeatProducer extends HeatProducer {

    public @Nullable ConsumeItemFilter filterItem;
    public @Nullable ConsumeLiquidFilter filterLiquid;

    public ConsumeHeatProducer(String name) {
        super(name);
        update = true;
        solid = true;
    }

    @Override
    public void init() {
        super.init();
        filterItem = findConsumer(c -> c instanceof ConsumeItemFilter);
        filterLiquid = findConsumer(c -> c instanceof ConsumeLiquidFilter);
    }

    public class ComsumeHeatProducerBuild extends HeatProducerBuild {
        public float efficiencyMultiplier;

        @Override
        public void updateEfficiencyMultiplier() {
            if (filterItem != null) {
                float m = filterItem.efficiencyMultiplier(this);
                if (m > 0) efficiencyMultiplier = m;
            } else if (filterLiquid != null) {
                float m = filterLiquid.efficiencyMultiplier(this);
                if (m > 0) efficiencyMultiplier = m;
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();
            heat = Mathf.approachDelta(heat, heatOutput * efficiency * efficiencyMultiplier, warmupRate * delta());
        }

        @Override
        public float heatFrac() {
            return heat / efficiencyMultiplier;
        }
    }
}
