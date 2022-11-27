package disintegration.world.blocks.temperature;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.DTVars;
import disintegration.graphics.Pal2;
import disintegration.util.MathDef;
import disintegration.util.WorldDef;
import disintegration.world.draw.DrawAllRotate;
import disintegration.world.draw.DrawTemperature;
import disintegration.world.meta.DTStatUnit;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.Stat;

public class TemperatureProducer extends Block {
    public Color heatColor = Pal2.burn;
    public Color sideHeatColor = Pal2.heat;

    public float temperatureOutput;
    public float temperaturePercent = DTVars.temperaturePercent;

    public float warmupRate = 0.15f;

    public DrawBlock drawer = new DrawMulti(new DrawAllRotate(), new DrawTemperature(heatColor, sideHeatColor, temperaturePercent));

    public TemperatureProducer(String name) {
        super(name);

        rotate = true;
        update = true;
        hasItems = true;
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.icons(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.output, temperatureOutput, DTStatUnit.temperatureUnitsPerSecond);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (TemperatureProducerBuild entity) -> new Bar(() -> Core.bundle.format("bar.heatamount", MathDef.round(entity.temperature, 10)), () -> Pal.lightOrange, () -> entity.temperature / temperatureOutput));
    }

    public class TemperatureProducerBuild extends Building implements TemperatureBlock{
        public float productionEfficiency = 1f;

        public float temperature = 0;

        @Override
        public float temperature() {
            return temperature / 60;
        }

        public float temperatureOutput(){
            return temperatureOutput;
        }

        @Override
        public void addTemperature(float target){}

        @Override
        public void setTemperature(float target){}

        @Override
        public void updateTile(){
            temperature = Mathf.approachDelta(temperature, temperatureOutput * productionEfficiency, warmupRate * delta());
            Point2[] edges = getEdges();
            for(Point2 edge : edges) {
                Building build = nearby(edge.x, edge.y);
                if (build != null && build.team == team && build instanceof TemperatureBlock other) {
                    if(WorldDef.toBlock(this, build) && WorldDef.toBlock(build, this)){
                        other.addTemperature(temperature() / build.block.size);
                    }
                }
            }
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
