package disintegration.world.draw;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.util.Eachable;
import disintegration.DTVars;
import disintegration.util.DrawDef;
import disintegration.world.blocks.laser.LaserBlock;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

import static arc.Core.atlas;

public class DrawLaser extends DrawBlock {
    public TextureRegion laser, laserEnd, arrowRegion;

    public boolean drawArrow;

    public DrawLaser(boolean drawArrow){
        this.drawArrow = drawArrow;
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{drawArrow ? arrowRegion : DrawDef.noneRegion};
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        if(drawArrow) Draw.rect(arrowRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
    }

    @Override
    public void draw(Building build){
        if(drawArrow) Draw.rect(arrowRegion, build.x, build.y, build.rotation * 90);
        Draw.z(Layer.blockOver);
        for (int i = 0; i < 3; i++) {
            if (((LaserBlock)build).l()[i] != 0) {
                Drawf.laser(laser, laserEnd, build.x, build.y, Geometry.d4x(build.rotation + i - 1) * ((LaserBlock) build).l()[i] * Vars.tilesize + build.x, Geometry.d4y(build.rotation + i - 1) * ((LaserBlock) build).l()[i] * Vars.tilesize + build.y, Math.min(((LaserBlock) build).luminosity(), 0.7f * DTVars.laserScale) / DTVars.laserScale);
            }
        }
        Draw.reset();
    }

    @Override
    public void load(Block block){
        laser = atlas.find("disintegration-laser-beam");
        laserEnd = atlas.find("disintegration-laser-beam-end");
        arrowRegion = atlas.find(block.name + "-arrow");
    }
}
