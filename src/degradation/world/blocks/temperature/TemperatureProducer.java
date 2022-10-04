package degradation.world.blocks.temperature;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import degradation.graphics.Pal2;
import degradation.util.TileDef;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
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

    public TemperatureProducer(String name) {
        super(name);

        rotate = true;
        update = true;
        underBullets = true;
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
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        switch (plan.rotation) {
            case 0 -> Draw.rect(region1, plan.x, plan.y);
            case 1 -> Draw.rect(region2, plan.x, plan.y);
            case 2 -> Draw.rect(region3, plan.x, plan.y);
            case 3 -> Draw.rect(region4, plan.x, plan.y);
        }
    }
    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.output, temperatureOutput, StatUnit.heatUnits);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (TemperatureProducerBuild entity) -> new Bar(() -> Core.bundle.format("bar.heatamount", (float)(Math.round(entity.temperature * 10)) / 10), () -> Pal.lightOrange, () -> entity.temperature / 5f));
    }

    public class TemperatureProducerBuild extends Building implements TemperatureBlock{
        public float temperature = 0;

        @Override
        public float temperature() {
            return temperature / 60;
        }

        @Override
        public void setTemperature(float target){}

        @Override
        public void updateTile(){
            temperature = temperatureOutput * efficiency();
        }

        @Override
        public void draw(){
            switch (rotation) {
                case 0 -> Draw.rect(region1, x, y);
                case 1 -> Draw.rect(region2, x, y);
                case 2 -> Draw.rect(region3, x, y);
                case 3 -> Draw.rect(region4, x, y);
            }
            Seq<Building> proximityBuilds = this.proximity();
            for(Building build : proximityBuilds) {
                if (TileDef.toBlock(build, this) && (build instanceof TemperatureConduit.TemperatureConduitBuild other)) {
                    float otherTemperature = other.temperature();

                    Draw.color(heatColor);

                    Draw.blend(Blending.additive);

                    Draw.alpha(Math.min(otherTemperature, 13f) / 15);

                    Draw.rect(heatRegion, x, y, rotation * 90);

                    Draw.color(sideHeatColor);

                    Draw.alpha(Math.min(otherTemperature, 13f) / 78);

                    Draw.rect(sideHeatRegion, x, y, rotation * 90);

                    Draw.blend();
                    Draw.reset();

                    break;
                }
            }
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
