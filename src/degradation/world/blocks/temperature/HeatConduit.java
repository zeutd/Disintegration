package degradation.world.blocks.temperature;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import degradation.graphics.Pal2;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;

import static arc.Core.atlas;
import static mindustry.Vars.world;

public class HeatConduit extends Block {
    public TextureRegion topRegion;
    public TextureRegion edgeRegion;
    public TextureRegion bottomRegion;
    public TextureRegion heatRegion;
    public TextureRegion edgeHeatRegion;
    public TextureRegion topHeatRegion;

    public float conductionSpeed;

    public Color heatColor = Pal2.burn;
    public Color sideHeatColor = Pal2.heat;

    public HeatConduit(String name) {
        super(name);

        update = true;
        underBullets = true;
    }

    @Override
    public void load() {
        super.load();
        topRegion = atlas.find(name + "-top");
        bottomRegion = atlas.find(name + "-bottom");
        edgeRegion = atlas.find(name + "-edge");
        heatRegion = atlas.find(name + "-heat");
        edgeHeatRegion = atlas.find(name + "-edge-heat");
        topHeatRegion = atlas.find(name + "-top-heat");
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(bottomRegion, plan.x * 8, plan.y * 8);
        Draw.rect(topRegion, plan.x * 8, plan.y * 8);

        for (int i = 0; i < 4; i++){
            int dx = plan.x + Geometry.d4x(i);
            int dy = plan.y + Geometry.d4y(i);
            Building build = world.build(dx, dy);
            final boolean[] listDraw = {false};
            list.each(req -> {
                if(req.x == dx && req.y == dy){
                    listDraw[0] = true;
                }
            });
            if (!(build instanceof TemperatureBlock || listDraw[0])){
                Draw.rect(edgeRegion, plan.x * 8, plan.y * 8, i * 90);
            }
        }
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (HeatConduitBuild entity) -> new Bar(() -> Core.bundle.format("bar.heatamount", (float)(Math.round(entity.temperature * 10)) / 10), () -> Pal.lightOrange, () -> entity.temperature / 5f));
    }

    public class HeatConduitBuild extends Building implements TemperatureBlock{
        public float temperature = 0;

        @Override
        public float temperature() {
            return temperature;
        }

        @Override
        public float conductionSpeed(){
            return conductionSpeed;

        }

        @Override
        public void updateTile(){
            Seq<Building> proximityBuilds = this.proximity();

            for(Building build : proximityBuilds){
                if(build instanceof TemperatureBlock other){
                    temperature += other.conductionSpeed() * (other.temperature() - temperature);
                }
            }
        }

        @Override
        public void draw(){

            Draw.rect(bottomRegion, x, y);

            Draw.color(heatColor);
            Draw.blend(Blending.additive);
            Draw.alpha(Math.min(temperature, 3.6f) / 4);
            Draw.rect(heatRegion, x, y);

            Draw.blend();
            Draw.reset();

            Draw.rect(topRegion, x, y);
            Draw.color(sideHeatColor);
            Draw.blend(Blending.additive);
            Draw.alpha(Math.min(temperature, 1f) / 6);
            Draw.rect(topHeatRegion, x, y);

            Draw.blend();
            Draw.reset();

            for (int i = 0; i < 4; i++){

                int dx = (int) x + Geometry.d4x(i)*8;
                int dy = (int) y + Geometry.d4y(i)*8;
                Building build = world.build(dx / 8, dy / 8);
                if (!(build instanceof TemperatureBlock)){
                    Draw.rect(edgeRegion, x, y, i * 90);
                    Draw.color(sideHeatColor);
                    Draw.blend(Blending.additive);
                    Draw.alpha(Math.min(temperature, 1f) / 6);
                    Draw.rect(edgeHeatRegion, x, y, i * 90);

                    Draw.blend();
                    Draw.reset();
                }
            }
            Drawf.light(x, y, heatRegion, heatColor, temperature * 100);
        }
        @Override
        public void write(Writes write){
            super.write(write);
            write.f(temperature);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            temperature = read.f();
        }
    }
}
