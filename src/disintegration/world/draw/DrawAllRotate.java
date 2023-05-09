package disintegration.world.draw;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

import static arc.Core.atlas;

public class DrawAllRotate extends DrawBlock {
    public TextureRegion region1, region2, region3, region4;
    public TextureRegion[] regions;


    public DrawAllRotate(){}

    @Override
    public void draw(Building build){
        Draw.rect(regions[build.rotation], build.x, build.y);
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(regions[plan.rotation], plan.drawx(), plan.drawy());
    }

    @Override
    public void load(Block block){
        region1 = atlas.find(block.name + "1");
        region2 = atlas.find(block.name + "2");
        region3 = atlas.find(block.name + "3");
        region4 = atlas.find(block.name + "4");
        regions = new TextureRegion[]{region1, region2, region3, region4};
    }
}
