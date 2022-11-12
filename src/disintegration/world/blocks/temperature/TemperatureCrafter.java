package disintegration.world.blocks.temperature;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.DTVars;
import disintegration.util.MathDef;
import disintegration.world.draw.DrawTemperature;
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
    public TextureRegion[] icons(){
        return drawer.icons(this);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (TemperatureCrafterBuild entity) -> new Bar(() -> Core.bundle.format("bar.heatamount", MathDef.round(entity.temperature, 10)), () -> Pal.lightOrange, () -> entity.temperature / temperturePercent));
    }

    public class TemperatureCrafterBuild extends GenericCrafterBuild implements TemperatureBlock{
        float temperature = 0f;
        @Override
        public float temperature() {
            return temperature;
        }

        @Override
        public void addTemperature(float target) {
            temperature += target;
        }

        @Override
        public void setTemperature(float target){
            temperature = target;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            temperature -= temperatureConsumes / 60 * efficiency;
        }

        @Override
        public float efficiencyScale(){
            return temperature >= 0 ? efficiency : 0;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(temperature);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            temperature = read.f();
        }
    }
}
