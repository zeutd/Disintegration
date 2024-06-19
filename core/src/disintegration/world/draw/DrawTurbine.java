package disintegration.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawTurbine extends DrawBlock {
    public String suffix = "-turbine";
    public float mag = 5f, scl = 2f;
    public float blurThresh = 0.99f;
    public TextureRegion region, blurRegion;
    public boolean vertical = false;

    public DrawTurbine(String suffix) {
        this.suffix = suffix;
    }

    public DrawTurbine() {
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return new TextureRegion[]{region};
    }

    @Override
    public void draw(Building build) {
        float sin = Mathf.absin(Mathf.mod(build.totalProgress(), scl * Mathf.PI * 2), scl, mag);
        float
                width = region.width / 4f,
                height = region.height / 4f;
        if (vertical) {
            width -= sin;
        } else {
            height -= sin;
        }
        Draw.rect(build.warmup() > blurThresh ? blurRegion : region, build.x, build.y, width, height, build.rotdeg());
        Draw.reset();
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(region, plan.drawx(), plan.drawy(), plan.rotation * 90);
    }

    @Override
    public void load(Block block) {
        region = Core.atlas.find(block.name + suffix);
        blurRegion = Core.atlas.find(block.name + suffix + "-blur");
    }
}
