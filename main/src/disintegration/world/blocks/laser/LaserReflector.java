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
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.draw.DrawBlock;

import java.util.Arrays;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;


public class LaserReflector extends Block {
    public DrawBlock drawer;

    public boolean split = false;

    public int range;

    public float visualMaxLaser = 10f;
    public float laserScale = 0.7f;

    public LaserReflector(String name) {
        super(name);
        update = true;
        rotate = true;
        solid = true;
    }

    @Override
    public void load(){
        super.load();
        drawer.load(this);
    }

    @Override
    public void init(){
        updateClipRadius((size + range) * 8);
        super.init();
    }

    public float calculateLaser(int x, int y, int a, int rotation) {
        float l = 0;
        for (int i = 1; i <= range; i++) {
            Tile tile = world.tile(Geometry.d4x(rotation + a - 1) * i + x, Geometry.d4y(rotation + a - 1) * i + y);
            if (tile != null && tile.block().solid) {
                l = i - 0.5f;
                break;
            }
            l = range;
        }
        return l;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }
    
    @Override
    public void drawPlace(int x, int y,int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        float[] l = new float[]{0, range, 0};
        if(split){
            for (int i = 0; i < 3; i++) {
                l[i] = calculateLaser(x, y, i, rotation);
            }
        }
        else{
            l[1] = calculateLaser(x, y, 1, rotation);
        }
        Draw.z(Layer.blockOver);
        for (int i = 0; i < 3; i++) {
            if (l[i] != 0) {
                Drawf.dashLine(valid ? Pal.accent : Pal.remove, (x + Geometry.d4x(rotation + i - 1) / 2f) * tilesize, (y + Geometry.d4y(rotation + i - 1) / 2f) * tilesize, (Geometry.d4x(rotation + i - 1) * l[i] + x - Geometry.d4x(rotation + i - 1) / 4f) * tilesize, (Geometry.d4y(rotation + i - 1) * l[i] + y - Geometry.d4y(rotation + i - 1) / 4f) * tilesize);
            }
        }
        Draw.reset();
    }
    
    @Override
    public TextureRegion[] icons(){
        return drawer.icons(this);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("laser", (LaserReflectorBuild entity) -> new Bar(() -> Core.bundle.format("bar.laseramount", MathDef.round(entity.luminosity, 10)), () -> Pal.redLight, () -> Mathf.clamp(entity.luminosity() / visualMaxLaser)));
    }

    public class LaserReflectorBuild extends Building implements LaserBlock, LaserConsumer {
        float[] sideLaser = new float[getEdges().length];
        float[] callFrom = new float[getEdges().length];

        IntSet came = new IntSet();
        IntSet otherCame = new IntSet();
        float luminosity;

        float[] l = new float[]{0, range, 0};

        @Override
        public float luminosity() {
            return luminosity / (split ? 3 : 1);
        }

        public void callLaser(int a) {
            for (int i = 1; i <= range; i++) {
                Building other = world.build(Geometry.d4x(rotation + a - 1) * i + tileX(), Geometry.d4y(rotation + a - 1) * i + tileY());
                if (other != null && other.block.solid) {
                    if (other instanceof LaserConsumer build) {
                        build.call(split ? luminosity / 3 : luminosity, Arrays.asList(Edges.getEdges(other.block.size)).indexOf(new Point2(Geometry.d4x(rotation + a - 1) * (i - 1) + tileX() - other.tileX(), Geometry.d4y(rotation + a - 1) * (i - 1) + tileY() - other.tileY())), came);
                    }
                    break;
                }
            }
        }

        @Override
        public void call(float value, int from, IntSet cameFrom) {
            callFrom[from] = value;

            otherCame.addAll(cameFrom);
        }

        @Override
        public float luminosityFrac() {
            return Mathf.clamp(luminosity / visualMaxLaser) * laserScale;
        }

        @Override
        public float[] l(){
            return l;
        }

        @Override
        public void updateTile(){
            came.clear();
            came.addAll(otherCame);
            came.add(id);
            sideLaser = callFrom.clone();
            callFrom = new float[getEdges().length];
            luminosity = 0;
            label:
            {
                if(otherCame.contains(id)) break label;
                if (!split) {
                    for (int i = 0; i < sideLaser.length; i++) {
                        float side = sideLaser[i];
                        if (i != rotation) {
                            luminosity += side;
                        }
                    }
                } else {
                    luminosity = sideLaser[Mathf.mod(rotation + 2, 4)];
                }
            }
            if (split) {
                for (int a = 0; a < 3; a++) {
                    l[a] = calculateLaser(tileX(), tileY(), a, rotation);
                    callLaser(a);
                }
            }
            else {
                l[1] = calculateLaser(tileX(), tileY(), 1, rotation);
                callLaser(1);
            }
            otherCame.clear();
        }

        @Override
        public void draw(){
            drawer.draw(this);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(luminosity);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            luminosity = read.f();
        }
    }
}
