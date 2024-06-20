package disintegration.world.draw;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import disintegration.world.blocks.laser.LaserBlock;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

import static arc.Core.atlas;

public class DrawLaser extends DrawBlock {
    public TextureRegion laser, laserEnd;


    public DrawLaser() {
    }

    @Override
    public void draw(Building build) {
        Draw.z(Layer.blockOver);
        for (int i = 0; i < 3; i++) {
            if (build instanceof LaserBlock b && (b).l()[i] != 0) {
                Drawf.laser(laser, laserEnd, build.x, build.y, Geometry.d4x(build.rotation + i - 1) * b.l()[i] * Vars.tilesize + build.x, Geometry.d4y(build.rotation + i - 1) * b.l()[i] * Vars.tilesize + build.y, b.luminosityFrac());
            }
        }
        Draw.reset();
    }

    @Override
    public void load(Block block) {
        laser = atlas.find("disintegration-laser-beam");
        laserEnd = atlas.find("disintegration-laser-beam-end");
    }
}
