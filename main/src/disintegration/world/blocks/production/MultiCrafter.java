package disintegration.world.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.gen.Tex;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.ItemDisplay;
import mindustry.ui.LiquidDisplay;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeLiquidsDynamic;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;

import static mindustry.Vars.content;

//nope

/*public class MultiCrafter extends Block {
    public Seq<MultiCrafterPlan> plans = new Seq<>();
    public DrawBlock drawer = new DrawDefault();

    public MultiCrafter(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        ambientSound = Sounds.machine;
        sync = true;
        ambientSoundVolume = 0.03f;
        flags = EnumSet.of(BlockFlag.factory);
        drawArrow = false;
        configurable = true;
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.output, table -> {
            for(var plan : plans){
                table.row();
                table.table(Styles.grayPanel, t -> {
                    t.table(s -> {
                        if(plan.inputItems != null) {
                            for (ItemStack stack : plan.inputItems) {
                                s.add(new ItemDisplay(stack.item, stack.amount, plan.craftTime, true));
                            }
                        }
                        if(plan.inputLiquids != null) {
                            s.row();
                            for (LiquidStack stack : plan.inputLiquids) {
                                s.add(new LiquidDisplay(stack.liquid, stack.amount / plan.craftTime, true));
                            }
                        }
                    }).left().pad(10f).left().scaling(Scaling.fit);
                    t.table(s -> {
                        s.add(Strings.autoFixed(plan.craftTime / 60f, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
                        s.image(Icon.right);
                    }).pad(10f).growX();
                    t.table(s -> {
                        if(plan.outputItems != null) {
                            for (ItemStack stack : plan.outputItems) {
                                s.add(new ItemDisplay(stack.item, stack.amount, plan.craftTime, true));
                            }
                        }
                        if(plan.outputLiquids != null) {
                            s.row();
                            for (LiquidStack stack : plan.outputLiquids) {
                                s.add(new LiquidDisplay(stack.liquid, stack.amount / plan.craftTime, true));
                            }
                        }
                    }).right().pad(10f).scaling(Scaling.fit);
                }).growX().pad(5);
            }
        });
    }

    @Override
    public void init(){
        boolean isConsumeItem = false;
        boolean isConsumeFluid = false;
        for(MultiCrafterPlan plan : plans){
            if(plan.inputLiquids != null) isConsumeFluid = true;
            if(plan.inputItems != null) isConsumeItem = true;
        }
        if (isConsumeItem) consume(new ConsumeItemDynamic(
                (MultiCrafterBuild b) -> {
                    if (b.planIndex < 0 || b.planIndex >= plans.size) return ItemStack.empty;
                    MultiCrafterPlan plan = plans.get(b.planIndex);
                    return plan.inputItems;
                }
        ));
        if (isConsumeFluid) consume(new ConsumeLiquidsDynamic(
                (MultiCrafterBuild b) -> {
                    if (b.planIndex < 0 || b.planIndex >= plans.size) return LiquidStack.empty;
                    MultiCrafterPlan plan = plans.get(b.planIndex);
                    return plan.inputLiquids;
                }
        ));
    }

    @Override
    public boolean rotatedOutput(int x, int y){
        return false;
    }

    @Override
    public void load(){
        super.load();
        for(MultiCrafterPlan plan : plans) {
            if(plan.drawer != null)plan.drawer.load(this);
        }
        drawer.load(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    @Override
    public boolean outputsItems(){
        for(MultiCrafterPlan plan : plans){
            if(plan.outputItems != null) return true;
        }
        return false;
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out){
        drawer.getRegionsToOutline(this, out);
    }

    public class MultiCrafterBuild extends Building {
        public float progress;
        public float totalProgress;
        public float warmup;
        public @Nullable int planIndex = -1;

        @Override
        public void draw(){
            *//*if (planIndex >= 0 && planIndex < plans.size) {
                MultiCrafterPlan plan = plans.get(planIndex);
                if(plan.drawer != null) {
                    plan.drawer.draw(this);
                    return;
                }
            }*//*
            drawer.draw(this);
        }

        @Override
        public void drawLight(){
            super.drawLight();
            if (planIndex >= 0 && planIndex < plans.size) {
                MultiCrafterPlan plan = plans.get(planIndex);
                if(plan.drawer != null) {
                    plan.drawer.drawLight(this);
                    return;
                }
            }
            drawer.drawLight(this);
        }

        @Override
        public boolean shouldConsume(){
            if (planIndex < 0 || planIndex >= plans.size) return false;
            MultiCrafterPlan plan = plans.get(planIndex);
            if(plan.outputItems != null){
                for(var output : plan.outputItems){
                    if(items.get(output.item) + output.amount > itemCapacity){
                        return false;
                    }
                }
            }
            if(plan.outputLiquids != null){
                boolean allFull = true;
                for(var output : plan.outputLiquids){
                    if(liquids.get(output.liquid) >= liquidCapacity - 0.001f){
                        return false;
                    }else{
                        //if there's still space left, it's not full for all liquids
                        allFull = false;
                    }
                }

                //if there is no space left for any liquid, it can't reproduce
                if(allFull){
                    return false;
                }
            }

            return enabled;
        }

        @Override
        public void updateTile(){
            if (planIndex < 0 || planIndex >= plans.size) return;
            MultiCrafterPlan plan = plans.get(planIndex);
            if(efficiency > 0){
                progress += getProgressIncrease(plan.craftTime);
                warmup = Mathf.approachDelta(warmup, warmupTarget(), plan.warmupSpeed);

                //continuously output based on efficiency
                if(plan.outputLiquids != null){
                    float inc = getProgressIncrease(1f);
                    for(var output : plan.outputLiquids){
                        handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                    }
                }

                if(wasVisible && Mathf.chanceDelta(plan.updateEffectChance)){
                    plan.updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
                }
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, plan.warmupSpeed);
            }

            totalProgress += warmup * Time.delta;

            if(progress >= 1f){
                craft();
            }

            dumpOutputs();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if (planIndex < 0 || planIndex >= plans.size) return false;
            MultiCrafterPlan plan = plans.get(planIndex);
            return hasItems &&
                    Structs.contains(plan.inputItems, s -> s.item == item) &&
                    items.get(item) < liquidCapacity;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (planIndex < 0 || planIndex >= plans.size) return false;
            MultiCrafterPlan plan = plans.get(planIndex);
            return hasLiquids &&
                    Structs.contains(plan.inputLiquids, s -> s.liquid == liquid) &&
                    liquids.get(liquid) < liquidCapacity;
        }

        public void craft(){
            consume();
            if (planIndex < 0 || planIndex >= plans.size) return;
            MultiCrafterPlan plan = plans.get(planIndex);
            for(var stack : plan.inputItems){
                items.remove(stack.item, stack.amount);
            }

            if(plan.outputItems != null){
                for(var output : plan.outputItems){
                    for(int i = 0; i < output.amount; i++){
                        offload(output.item);
                    }
                }
            }

            if(wasVisible){
                plan.craftEffect.at(x, y);
            }
            progress %= 1f;
        }

        public float warmupTarget(){
            return 1f;
        }

        public void dumpOutputs(){
            if (planIndex < 0 || planIndex >= plans.size) return;
            MultiCrafterPlan plan = plans.get(planIndex);
            if(plan.outputItems != null && timer(timerDump, dumpTime / timeScale)){
                for(ItemStack output : plan.outputItems){
                    dump(output.item);
                }
            }

            if(plan.outputLiquids != null){
                for(int i = 0; i < plan.outputLiquids.length; i++){
                    int dir = plan.liquidOutputDirections.length > i ? plan.liquidOutputDirections[i] : -1;

                    dumpLiquid(plan.outputLiquids[i].liquid, 2f, dir);
                }
            }
        }

        @Override
        public float progress(){
            return Mathf.clamp(progress);
        }

        @Override
        public int getMaximumAccepted(Item item){
            return itemCapacity;
        }

        @Override
        public boolean shouldAmbientSound(){
            return efficiency > 0;
        }

        @Override
        public void buildConfiguration(Table table){
            for(int i = 0; i < plans.size; i++){
                MultiCrafterPlan plan  = plans.get(i);
                int finalI = i;
                table.table(Styles.grayPanel, t -> {
                    ImageButton imageButton = t.button(Tex.whiteui, Styles.clearNoneTogglei, 40, () -> {
                        planIndex = finalI;
                    }).get();
                    UnlockableContent c = null;
                    if(plan.outputItems != null)c = plan.outputItems[0].item;
                    else if(plan.outputLiquids != null)c = plan.outputLiquids[0].liquid;
                    if (c != null) {
                        imageButton.getStyle().imageUp = new TextureRegionDrawable(c.uiIcon);
                    }
                    imageButton.update(() -> imageButton.setChecked(planIndex == finalI));
                });
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.f(warmup);
            write.i(planIndex);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
            warmup = read.f();
            planIndex = read.i();
        }
    }

    public static class MultiCrafterPlan {
        public @Nullable ItemStack[] inputItems;
        public @Nullable LiquidStack[] inputLiquids;
        public @Nullable ItemStack[] outputItems;
        public @Nullable LiquidStack[] outputLiquids;
        public @Nullable DrawBlock drawer;
        public float craftTime = 80;
        public Effect craftEffect = Fx.none;
        public Effect updateEffect = Fx.none;
        public float updateEffectChance = 0.04f;
        public float warmupSpeed = 0.019f;
        public int[] liquidOutputDirections = {-1};
    }
}*/
