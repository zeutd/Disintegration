package degradation.world.draw;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Eachable;
import degradation.util.TileDef;
import degradation.world.blocks.temperature.TemperatureBlock;
import degradation.world.blocks.temperature.TemperatureConduit;
import degradation.world.blocks.temperature.TemperatureProducer;
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
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        if(topRegion.found()) {
            Draw.rect(topRegion, plan.x, plan.y, plan.rotation * 90);
        }
    }

    @Override
    public void draw(Building build){
        float otherTemperature = -1;

        Seq<Building> proximityBuilds = build.proximity();

        for(Building block : proximityBuilds) {
            if (TileDef.toBlock(block, build) && (block instanceof TemperatureConduit.TemperatureConduitBuild other)) {
                otherTemperature = other.temperature();
                break;
            }
        }
        if(otherTemperature < 0) {
            float temperatureOutput = 1f;
            if(build instanceof TemperatureProducer.TemperatureProducerBuild block) {
                temperatureOutput = block.temperatureOutput();
            }
            otherTemperature = ((TemperatureBlock) build).temperature() * percent * 60 * 1.5f / temperatureOutput;
        }
        if(topRegion.found()) {
            Draw.rect(topRegion, build.x, build.y, build.rotation * 90);
        }
        Draw.color(heatColor);

        Draw.blend(Blending.additive);

        Draw.alpha(Math.min(otherTemperature, 0.9f * percent) / percent);

        Draw.rect(heatRegion, build.x, build.y, build.rotation * 90);

        Draw.color(sideHeatColor);

        Draw.alpha(Math.min(otherTemperature, 0.9f * percent) / percent / 5);

        Draw.rect(sideHeatRegion, build.x, build.y, build.rotation * 90);

        Draw.blend();
        Draw.reset();
    }

    @Override
    public void load(Block block){
        heatRegion = atlas.find(block.name + "-heat");
        sideHeatRegion = atlas.find(block.name + "-heat-side");
        topRegion = atlas.find(block.name + "-top");
    }
}
