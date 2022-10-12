package degradation.world.blocks.temperature;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import degradation.DTVars;
import degradation.graphics.Pal2;
import degradation.world.draw.DrawAllRotate;
import degradation.world.draw.DrawTemperature;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static arc.Core.atlas;

public class TemperatureProducer extends Block {
    public TextureRegion region1;
    public TextureRegion region2;
    public TextureRegion region3;
    public TextureRegion region4;
    public TextureRegion heatRegion;
    public TextureRegion sideHeatRegion;

    public Color heatColor = Pal2.burn;
    public Color sideHeatColor = Pal2.heat;

    public float temperatureOutput;
    public float temperturePercent = DTVars.temperaturePercent;

    public float warmupRate = 0.15f;

    public DrawBlock drawer = new DrawMulti(new DrawAllRotate(), new DrawTemperature(heatColor, sideHeatColor, temperturePercent));

    public TemperatureProducer(String name) {
        super(name);

        rotate = true;
        update = true;
        hasItems = true;
    }

    @Override
    public void load() {
        super.load();
        region1 = atlas.find(name + "1");
        region2 = atlas.find(name + "2");
        region3 = atlas.find(name + "3");
        region4 = atlas.find(name + "4");
        heatRegion = atlas.find(name + "-heat");
        sideHeatRegion = atlas.find(name + "-heat-side");
        drawer.load(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.output, temperatureOutput, StatUnit.heatUnits);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (TemperatureProducerBuild entity) -> new Bar(() -> Core.bundle.format("bar.heatamount", (float)(Math.round(entity.temperature * 10)) / 10), () -> Pal.lightOrange, () -> entity.temperature / temperatureOutput));
    }

    public class TemperatureProducerBuild extends Building implements TemperatureBlock{
        public float productionEfficiency = 1f;

        public float temperature = 0;

        @Override
        public float temperature() {
            return temperature / 60 / 2;
        }

        public float temperatureOutput(){
            return temperatureOutput;
        }

        @Override
        public void setTemperature(float target){}

        @Override
        public void updateTile(){
            temperature = Mathf.approachDelta(temperature, temperatureOutput * productionEfficiency, warmupRate * delta());
        }

        @Override
        public void draw(){
            drawer.draw(this);
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
