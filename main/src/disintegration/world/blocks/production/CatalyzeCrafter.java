package disintegration.world.blocks.production;

import arc.Core;
import arc.util.Nullable;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

public class CatalyzeCrafter extends GenericCrafter {
    @Nullable public ItemStack catalystItem;
    @Nullable public ItemStack[] catalystItems;
    @Nullable public LiquidStack catalystLiquid;
    @Nullable public LiquidStack[] catalystLiquids;

    public float catalysisEfficiency;
    public float baseEfficiency;
    public CatalyzeCrafter(String name) {
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.remove(Stat.booster);
        if (catalystItem != null) {
            stats.add(Stat.booster, StatValues.items(craftTime, catalystItems));
        }
        if (catalystLiquids != null) {
            stats.add(Stat.booster, StatValues.liquids(1f, catalystLiquids));
        }
    }

    @Override
    public void init(){
        super.init();
        if(catalystItems == null && catalystItem != null){
            catalystItems = new ItemStack[]{catalystItem};
        }
        if(catalystLiquids == null && catalystLiquid != null){
            catalystLiquids = new LiquidStack[]{catalystLiquid};
        }
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("efficiency", (CatalyzeCrafterBuild entity) ->
                new Bar(
                        () -> Core.bundle.format("bar.efficiency", (int)(entity.efficiencyScale() * 100)),
                        () -> Pal.lightOrange,
                        () -> entity.efficiencyScale() - 1f));
    }

    public class CatalyzeCrafterBuild extends GenericCrafterBuild {
        @Override
        public boolean acceptItem(Building source, Item item){
            for (ItemStack stack : catalystItems){
                if (stack.item == item && items.get(item) < itemCapacity) return true;
            }
            return super.acceptItem(source, item);
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(catalystLiquids != null) {
                for (var stack : catalystLiquids) {
                    liquids.remove(stack.liquid, stack.amount * edelta());
                }
            }
        }

        @Override
        public void craft(){
            super.craft();
            if (catalystItems != null) {
                items.remove(catalystItems);
            }
        }

        @Override
        public float efficiencyScale(){
            float e = 0;
            int totalCatalysts = 0;
            if (catalystItems != null) {
                totalCatalysts += catalystItems.length;
                for (ItemStack item : catalystItems) {
                    e += (float) items.get(item.item) / itemCapacity;
                }
            }
            if (catalystLiquids != null) {
                totalCatalysts += catalystLiquids.length;
                for (LiquidStack liquid : catalystLiquids) {
                    e += (float) liquids.get(liquid.liquid) / liquidCapacity;
                }
            }
            e /= totalCatalysts;
            return baseEfficiency + e * catalysisEfficiency;
        }
    }
}
