package disintegration.world.blocks.laser;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.IntSet;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.world.meta.DTStatUnit;
import mindustry.entities.units.BuildPlan;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.HeatCrafter;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.state;

public class LaserDrill extends Drill {
    public float laserRequirement = 10f;
    public float overlaserScale = 1f;
    public float maxEfficiency = 4f;
    public DrawBlock drawer = new DrawDefault();
    public LaserDrill(String name) {
        super(name);
        drawMineItem = false;
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    @Override
    public void load(){
        super.load();
        drawer.load(this);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (LaserDrillBuild entity) ->
                new Bar(() ->
                        Core.bundle.format("bar.heatpercent", (int)(entity.luminosity + 0.01f), (int)(entity.efficiencyScale() * 100 + 0.01f)),
                        () -> Pal.lightOrange,
                        () -> entity.luminosity / laserRequirement));
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.input, laserRequirement, DTStatUnit.laserUnits);
        stats.add(Stat.maxEfficiency, (int)(maxEfficiency * 100f), StatUnit.percent);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    public class LaserDrillBuild extends DrillBuild implements LaserConsumer {
        float[] sideLaser = new float[4];
        float[] callFrom = new float[4];

        public float luminosity;

        public boolean flushed;

        @Override
        public void call(float value, int from, IntSet cameFrom) {
            callFrom[from] = value;
        }

        @Override
        public void updateTile() {
            sideLaser = callFrom.clone();
            callFrom = new float[4];
            luminosity = 0;
            for (float side : sideLaser) {
                luminosity += side;
            }
            if (flushed) {
                super.updateTile();
            }
            flushed = true;
        }

        @Override
        public float efficiencyScale(){
            float over = Math.max(luminosity - laserRequirement, 0f);
            return Math.min(Mathf.clamp(luminosity / laserRequirement) + over / laserRequirement * overlaserScale, maxEfficiency);
        }

        @Override
        public void draw(){
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

        @Override
        public float warmup(){
            return Mathf.clamp(warmup);
        }
    }
}
