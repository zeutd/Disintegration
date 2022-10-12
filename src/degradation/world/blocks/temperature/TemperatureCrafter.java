package degradation.world.blocks.temperature;

import arc.Core;
import arc.struct.Seq;
import degradation.DTVars;
import degradation.util.TileDef;
import degradation.world.draw.DrawTemperature;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawLiquidOutputs;
import mindustry.world.draw.DrawLiquidRegion;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class TemperatureCrafter extends GenericCrafter {
    public float temperatureConsumes;
    public float temperturePercent = DTVars.temperaturePercent;

    public TemperatureCrafter(String name) {
        super(name);
        update = true;
        rotate = true;
        drawArrow = true;
        drawer = new DrawMulti(new DrawDefault(), new DrawLiquidRegion(), new DrawLiquidOutputs(), new DrawTemperature());
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.input, String.valueOf(temperatureConsumes), StatUnit.heatUnits ,StatUnit.perSecond);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (TemperatureCrafterBuild entity) -> new Bar(() -> Core.bundle.format("bar.heatamount", (float)(Math.round(entity.temperature * 10)) / 10), () -> Pal.lightOrange, () -> entity.temperature / temperturePercent));
    }

    public class TemperatureCrafterBuild extends GenericCrafterBuild implements TemperatureBlock{
        float temperature;
        @Override
        public float temperature() {
            return temperature;
        }

        @Override
        public void setTemperature(float target) {
            temperature = target;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            Seq<Building> proximityBuilds = this.proximity();
            for(Building build : proximityBuilds){
                if(build instanceof TemperatureProducer.TemperatureProducerBuild other && TileDef.toBlock(this, other) && TileDef.toBlock(other, this)){
                    temperature += other.temperature() * 2;
                }
                if(build instanceof TemperatureConduit.TemperatureConduitBuild other && TileDef.toBlock(other, this)){
                    temperature += other.temperature();
                }
            }
            temperature -= temperatureConsumes / 60 * efficiency;
        }

        @Override
        public float efficiencyScale(){
            return temperature >= 0 ? efficiency : 0;
        }
    }
}
