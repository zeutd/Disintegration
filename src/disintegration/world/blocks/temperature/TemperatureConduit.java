package disintegration.world.blocks.temperature;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.graphics.Pal2;
import disintegration.util.DrawDef;
import disintegration.util.MathDef;
import disintegration.util.WorldDef;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;

import static arc.Core.atlas;
import static mindustry.Vars.world;

public class TemperatureConduit extends Block {
    public TextureRegion bottomRegion;
    public TextureRegion heatRegion;

    public TextureRegion[] regions;
    public TextureRegion[] heatRegions;
    public float conductionSpeed;
    public float temperaturePercent;

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
        bottomRegion = atlas.find(name + "-bottom");
        heatRegion = atlas.find(name + "-heat");
        heatRegions = DrawDef.splitRegionTile(atlas.find(name + "-heat-tile"), 8, 2);
        regions = DrawDef.splitRegionTile(atlas.find(name + "-tile"), 8, 2);
    }



    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(bottomRegion, plan.drawx(), plan.drawy());

        int index = 0;
        for (int i = 0; i < 4; i++){
            Point2 other = Geometry.d4(i).cpy().add(plan.x, plan.y);
            if(world.build(other.x,other.y) instanceof TemperatureConduitBuild){
                index += 1 << i;
            }else{
                final boolean[] isDraw ={false};
                list.each(req->{
                    if(!isDraw[0] && req.x == other.x && req.y == other.y){
                        isDraw[0] = true;
                    }
                });
                if(isDraw[0]){
                    index += 1 << i;
                }
            }
        }
        Draw.rect(regions[index], plan.drawx(), plan.drawy());
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (TemperatureConduitBuild entity) -> new Bar(() -> Core.bundle.format("bar.heatamount", MathDef.round(entity.temperature, 10)), () -> Pal.lightOrange, () -> entity.temperature / temperaturePercent));
    }

    public class TemperatureConduitBuild extends Building implements TemperatureBlock{

        public float temperature = 0;

        public int index;

        @Override
        public float temperature() {
            return temperature;
        }

        @Override
        public void addTemperature(float target) {
            temperature += target;
        }
        @Override
        public void setTemperature(float target){
            temperature = target;
        }

        @Override
        public void onProximityUpdate(){
            index = 0;
            for (int i = 0; i < 4; i++) {
                Building other = nearby(i);
                if (other instanceof TemperatureBlock && WorldDef.conductSideTemperature(this, other)) {
                    index += 1 << i;
                }
            }
        }

        @Override
        public void updateTile(){
            Seq<Building> proximityBuilds = this.proximity();

            for(Building build : proximityBuilds){
                if(build instanceof TemperatureConduitBuild other){
                    addTemperature(conductionSpeed * (other.temperature() - temperature));
                    other.addTemperature(conductionSpeed * (temperature - other.temperature()));
                }
                else if (build instanceof TemperatureCrafter.TemperatureCrafterBuild other && WorldDef.toBlock(this, other)) {
                    if(other.temperature() <= temperature) {
                        addTemperature(-conductionSpeed);
                        other.addTemperature(conductionSpeed);
                    }
                }
            }
        }

        @Override
        public void draw(){
            Draw.rect(bottomRegion, x, y);

            Draw.blend(Blending.additive);

            Draw.color(heatColor);
            Draw.alpha(Math.min(temperature, 0.9f * temperaturePercent) / temperaturePercent);
            Draw.rect(heatRegion, x, y);

            Draw.color(sideHeatColor);
            Draw.alpha(Math.min(temperature, 0.9f * temperaturePercent) / temperaturePercent / 5);
            Draw.z(Layer.block + 0.002f);
            Draw.rect(heatRegions[index], x, y);

            Draw.blend();
            Draw.reset();

            Draw.z(Layer.block + 0.001f);
            Draw.rect(regions[index], x, y);
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
