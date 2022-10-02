package degradation.world.blocks.temperature;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.world.Block;

import static arc.Core.atlas;
import static mindustry.Vars.world;

public class HeatConduit extends Block {
    public TextureRegion topRegion;
    public TextureRegion edgeRegion;
    public TextureRegion bottomRegion;
    public TextureRegion heatRegion;

    public float temperatureCapacity;

    public float conductionSpeed;

    public Color heatColor = Pal.turretHeat;

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
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(bottomRegion, plan.x * 8, plan.y * 8);
        Draw.rect(topRegion, plan.x * 8, plan.y * 8);

        for (int i = 0; i < 4; i++){
            var pt = Geometry.d4((4-i)%4).cpy().add(plan.x,plan.y);
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

    public class HeatConduitBuild extends Building implements TemperatureBlock{
        public float temperature = 0;

        @Override
        public float temperature() {
            return temperature;
        }

        @Override
        public float temperatureFrac() {
            return 0;
        }

        @Override
        public float temperatureCapacity() {
            return temperatureCapacity;
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
                    if (other.temperature() >= other.temperatureCapacity()){
                        temperature += other.conductionSpeed();
                    }
                }
            }
        }

        @Override
        public void draw(){

            Draw.rect(bottomRegion, x, y);

            Draw.color(heatColor);
            Draw.alpha(temperature);
            Draw.rect(heatRegion, x, y);

            Draw.reset();

            Draw.rect(topRegion, x, y);

            for (int i = 0; i < 4; i++){

                int dx = (int) x + Geometry.d4x(i)*8;
                int dy = (int) y + Geometry.d4y(i)*8;
                Building build = world.build(dx / 8, dy / 8);
                if (!(build instanceof TemperatureBlock)){
                    Draw.rect(edgeRegion, x, y, i * 90);
                }
            }
        }
    }
}
