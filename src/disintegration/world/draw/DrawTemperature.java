package disintegration.world.draw;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

import static arc.Core.atlas;

public class DrawTemperature extends DrawBlock {
    public TextureRegion heatRegion, sideHeatRegion, topRegion;
    
    public Color heatColor, sideHeatColor;

    public float percent;

    public DrawTemperature(){}

    public DrawTemperature(Color heatColor, Color sideHeatColor, float percent){
        this.heatColor = heatColor;
        this.sideHeatColor = sideHeatColor;
        this.percent = percent;
    }
    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(topRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{topRegion};
    }

    @Override
    public void draw(Building build){
        /*float otherTemperature = 0;

        Seq<Building> proximityBuilds = build.proximity();

        for(Building block : proximityBuilds) {
            if (WorldDef.toBlock(block, build) && WorldDef.toBlock(build, block) && (block instanceof TemperatureConduit.TemperatureConduitBuild other)) {
                otherTemperature = other.temperature();
                break;
            }
            else if (!(build instanceof TemperatureHeater.TemperatureProducerBuild) && WorldDef.toBlock(block, build) && WorldDef.toBlock(build, block) && (block instanceof TemperatureHeater.TemperatureProducerBuild other)) {
                float temperatureOutput = other.temperatureOutput();
                otherTemperature = other.temperature() * percent * 60 / temperatureOutput;
                break;
            }
            else if(build instanceof TemperatureHeater.TemperatureProducerBuild) {
                float temperatureOutput = ((TemperatureHeater.TemperatureProducerBuild)build).temperatureOutput();
                otherTemperature = ((TemperatureBlock) build).temperature() * percent * 60 / temperatureOutput;
            }
            else if(build instanceof TemperatureCrafter.TemperatureCrafterBuild){
                otherTemperature = ((TemperatureBlock) build).temperature();
            }
        }
        Draw.rect(topRegion, build.x, build.y, build.rotation * 90);

        Draw.color(heatColor);

        Draw.blend(Blending.additive);

        Draw.alpha(Math.min(otherTemperature, 0.9f * percent) / percent);

        Draw.rect(heatRegion, build.x, build.y, build.rotation * 90);

        Draw.color(sideHeatColor);

        Draw.alpha(Math.min(otherTemperature, 0.9f * percent) / percent / 5);

        Draw.rect(sideHeatRegion, build.x, build.y, build.rotation * 90);

        Draw.blend();
        Draw.reset();*/
    }

    @Override
    public void load(Block block){
        heatRegion = atlas.find(block.name + "-heat");
        sideHeatRegion = atlas.find(block.name + "-heat-side");
        topRegion = atlas.find(block.name + "-top");
    }
}
