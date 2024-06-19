package disintegration.world.blocks.laser;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.IntSet;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.util.MathDef;
import disintegration.world.meta.DTStatUnit;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.draw.DrawBlock;
import mindustry.world.meta.Stat;

import java.util.Arrays;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class LaserDevice extends Block {
    public DrawBlock drawer;

    public float visualMaxLaser = 10f;
    public float laserScale = 0.7f;

    public float laserOutput;

    public int range;

    public LaserDevice(String name) {
        super(name);
        update = true;
        rotate = true;
        solid = true;
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public void init() {
        updateClipRadius((size + range) * 8);
        super.init();
    }

    public float calculateLaser(int x, int y, int a, int rotation) {
        float l = 0;
        for (int i = 1; i <= range; i++) {
            Building other = world.build(Geometry.d4x(rotation + a - 1) * i + x, Geometry.d4y(rotation + a - 1) * i + y);
            if (other != null && other.block.solid) {
                l = i - 0.5f;
                break;
            }
            l = range;
        }
        return l;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        float l;
        l = calculateLaser(x, y, 1, rotation);
        Draw.z(Layer.blockOver);
        Drawf.dashLine(valid ? Pal.accent : Pal.remove, (x + Geometry.d4x(rotation) / 2f) * tilesize, (y + Geometry.d4y(rotation) / 2f) * tilesize, (Geometry.d4x(rotation) * l + x - Geometry.d4x(rotation) / 4f) * tilesize, (Geometry.d4y(rotation) * l + y - Geometry.d4y(rotation) / 4f) * tilesize);
        Draw.reset();
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons() {
        return drawer.finalIcons(this);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.output, laserOutput, DTStatUnit.laserUnits);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("laser", (LaserDeviceBuild entity) -> new Bar(() -> Core.bundle.format("bar.laseramount", MathDef.round(entity.luminosity(), 10)), () -> Pal.redLight, () -> entity.luminosity / laserOutput));
    }

    public class LaserDeviceBuild extends Building implements LaserBlock {
        IntSet came = new IntSet();

        float luminosity;
        float l = range;

        @Override
        public float luminosity() {
            return luminosity;
        }

        public void callLaser(int a) {
            for (int i = 1; i <= range; i++) {
                Building other = world.build(Geometry.d4x(rotation + a - 1) * i + tileX(), Geometry.d4y(rotation + a - 1) * i + tileY());
                if (other != null && other.block.solid) {
                    if (other instanceof LaserConsumer build) {
                        build.call(luminosity, Arrays.asList(Edges.getEdges(other.block.size)).indexOf(new Point2(Geometry.d4x(rotation + a - 1) * (i - 1) + tileX() - other.tileX(), Geometry.d4y(rotation + a - 1) * (i - 1) + tileY() - other.tileY())), came);
                    }
                    break;
                }
            }
        }

        @Override
        public void call(float value, int from, IntSet cameFrom) {

        }

        @Override
        public float luminosityFrac() {
            return Mathf.clamp(luminosity / visualMaxLaser) * laserScale;
        }

        @Override
        public float[] l() {
            return new float[]{0, l, 0};
        }

        @Override
        public void updateTile() {
            luminosity = Mathf.lerpDelta(luminosity, laserOutput * efficiency, 0.1f);
            for (int i = 1; i <= range; i++) {
                l = calculateLaser(tileX(), tileY(), 1, rotation);
                callLaser(1);
            }
        }

        @Override
        public void draw() {
            drawer.draw(this);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(luminosity);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            luminosity = read.f();
        }
    }
}
