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
import degradation.util.TileDef;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;

import static arc.Core.atlas;
import static mindustry.Vars.world;

public class TemperatureConduit extends Block {
    public TextureRegion topRegion;
    public TextureRegion edgeRegion;
    public TextureRegion bottomRegion;
    public TextureRegion heatRegion;
    public TextureRegion edgeHeatRegion;
    public TextureRegion topHeatRegion;

    public float conductionSpeed;

    public float temperatureCapacity;

    public Color heatColor = Pal2.burn;
    public Color sideHeatColor = Pal2.heat;

    public TemperatureConduit(String name) {
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
            final boolean[] isDraw = {false};
            list.each(req -> {
                if(req.x == dx && req.y == dy){
                    isDraw[0] = true;
                }
            });
            if (!(build instanceof TemperatureConduitBuild || isDraw[0])){
                Draw.rect(edgeRegion, plan.x * 8, plan.y * 8, i * 90);
            }
        }
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (TemperatureConduitBuild entity) -> new Bar(() -> Core.bundle.format("bar.heatamount", (float)(Math.round(entity.temperature * 10)) / 10), () -> Pal.lightOrange, () -> entity.temperature / 15f));
    }

    public class TemperatureConduitBuild extends Building implements TemperatureBlock{
        public float temperature = 0;

        @Override
        public float temperature() {
            return temperature;
        }

        public void setTemperature(float target) {
            temperature = target;
        }

        @Override
        public void updateTile(){
            Seq<Building> proximityBuilds = this.proximity();

            for(Building build : proximityBuilds){
                if(build instanceof TemperatureConduitBuild other){
                    temperature += conductionSpeed * (other.temperature() - temperature);
                }
                else if(build instanceof TemperatureProducer.TemperatureProducerBuild other && TileDef.toBlock(this, other)){
                    temperature += other.temperature();
                }
            }
        }

        @Override
        public void draw(){

            Draw.rect(bottomRegion, x, y);

            Draw.color(heatColor);
            Draw.blend(Blending.additive);
            Draw.alpha(Math.min(temperature, 13f) / 15);
            Draw.rect(heatRegion, x, y);

            Draw.blend();
            Draw.reset();

            Draw.rect(topRegion, x, y);
            Draw.color(sideHeatColor);
            Draw.blend(Blending.additive);
            Draw.alpha(Math.min(temperature, 13f) / 78);
            Draw.rect(topHeatRegion, x, y);

            Draw.blend();
            Draw.reset();

            for (int i = 0; i < 4; i++){

                Building build = nearby(i);
                if (!TileDef.conductSideTemperature(this, build)){
                    Draw.rect(edgeRegion, x, y, i * 90);
                    Draw.color(sideHeatColor);
                    Draw.blend(Blending.additive);
                    Draw.alpha(Math.min(temperature, 13f) / 78);
                    Draw.rect(edgeHeatRegion, x, y, i * 90);

                    Draw.blend();
                    Draw.reset();
                }
            }
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
