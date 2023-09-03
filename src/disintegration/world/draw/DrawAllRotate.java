package disintegration.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawAllRotate extends DrawBlock {
    public int iconIndex = 0;
    public TextureRegion[] regions;


    public DrawAllRotate(){}

    public DrawAllRotate(int iconIndex){
        this.iconIndex = iconIndex;
    }
    @Override
    public TextureRegion[] icons(Block block){return new TextureRegion[]{regions[iconIndex]};}

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
        regions = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            regions[i] = Core.atlas.find(block.name + (i + 1));
        }
    }
}
