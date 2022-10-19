package degradation.world.draw;

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

    public DrawAllRotate(){}

    @Override
    public void draw(Building build){
        switch (build.rotation) {
            case 0 -> Draw.rect(region1, build.x, build.y);
            case 1 -> Draw.rect(region2, build.x, build.y);
            case 2 -> Draw.rect(region3, build.x, build.y);
            case 3 -> Draw.rect(region4, build.x, build.y);
        }
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        switch (plan.rotation) {
            case 0 -> Draw.rect(region1, plan.drawx(), plan.drawy());
            case 1 -> Draw.rect(region2, plan.drawx(), plan.drawy());
            case 2 -> Draw.rect(region3, plan.drawx(), plan.drawy());
            case 3 -> Draw.rect(region4, plan.drawx(), plan.drawy());
        }
    }

    @Override
    public void load(Block block){
        region1 = atlas.find(block.name + "1");
        region2 = atlas.find(block.name + "2");
        region3 = atlas.find(block.name + "3");
        region4 = atlas.find(block.name + "4");
    }
}
