package disintegration.world.blocks.temperature;

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
import disintegration.graphics.Pal2;
import disintegration.util.MathDef;
import disintegration.util.TileDef;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;

import java.util.Arrays;

import static arc.Core.atlas;
import static mindustry.Vars.world;

public class TemperatureConduit extends Block {
    public TextureRegion topRegion;
    public TextureRegion edgeRegion;
    public TextureRegion bottomRegion;
    public TextureRegion heatRegion;
    public TextureRegion edgeHeatRegion;
    public TextureRegion topHeatRegion;
    public TextureRegion edgeHeatTopRegion;
    public TextureRegion edgeHeatTopRegionInverted;

    public float conductionSpeed;
    public float temperaturePercent;

    public Boolean[][][] drawEdgeTopHeatRegions;

    public Seq<String> edges;

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
        edgeHeatTopRegion = atlas.find(name + "-edge-heat-top");
        edgeHeatTopRegionInverted = atlas.find(name + "-edge-heat-top-inverted");
        //  2
        //3 O 1
        //  4
        drawEdgeTopHeatRegions = new Boolean[][][]{
                /*0000*/{{false, false, false, false}, {false, false, false, false}},
                /*0001*/{{false, true , false, false}, {false, false, false, true }},
                /*0010*/{{false, false, true , false}, {true , false, false, false}},
                /*0011*/{{false, false, true , false}, {false, false, false, true }},
                /*0100*/{{false, false, false, true }, {false, true , false, false}},
                /*0101*/{{false, true , false, true }, {false, true , false, true }},
                /*0110*/{{false, false, false, true }, {true , false, false, false}},
                /*0111*/{{false, false, false, true }, {false, false, false, true }},
                /*1000*/{{true , false, false, false}, {false, false, true , false}},
                /*1001*/{{false, true , false, false}, {false, false, true , false}},
                /*1010*/{{true , false, true , false}, {true , false, true , false}},
                /*1011*/{{false, false, true , false}, {false, false, true , false}},
                /*1100*/{{true , false, false, false}, {false, true , false, false}},
                /*1101*/{{false, true , false, false}, {false, true , false, false}},
                /*1110*/{{true , false, false, false}, {true , false, false, false}},
                /*1111*/{{false, false, false, false}, {false, false, false, false}}
        };
        edges = new Seq<>();
        edges.addAll(
                "[false, false, false, false]",
                "[true, false, false, false]",
                "[false, true, false, false]",
                "[true, true, false, false]",
                "[false, false, true, false]",
                "[true, false, true, false]",
                "[false, true, true, false]",
                "[true, true, true, false]",
                "[false, false, false, true]",
                "[true, false, false, true]",
                "[false, true, false, true]", 
                "[true, true, false, true]", 
                "[false, false, true, true]", 
                "[true, false, true, true]", 
                "[false, true, true, true]",
                "[true, true, true, true]"
        );
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

        addBar("heat", (TemperatureConduitBuild entity) -> new Bar(() -> Core.bundle.format("bar.heatamount", MathDef.round(entity.temperature, 10)), () -> Pal.lightOrange, () -> entity.temperature / temperaturePercent));
    }

    public class TemperatureConduitBuild extends Building implements TemperatureBlock{

        public float temperature = 0;

        public int side;
        public Boolean[] sides = new Boolean[4];

        public Boolean[] drawEdgeTopHeatRegion = drawEdgeTopHeatRegions[side][0].clone();
        public Boolean[] drawEdgeTopHeatRegionInverted = drawEdgeTopHeatRegions[side][1].clone();

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
        public void updateTile(){
            Seq<Building> proximityBuilds = this.proximity();

            for(Building build : proximityBuilds){
                if(build instanceof TemperatureConduitBuild other){
                    addTemperature(conductionSpeed * (other.temperature() - temperature));
                    other.addTemperature(conductionSpeed * (temperature - other.temperature()));
                }
                else if (build instanceof TemperatureCrafter.TemperatureCrafterBuild other && TileDef.toBlock(this, other)) {
                    if(other.temperature() <= temperature) {
                        addTemperature(-conductionSpeed);
                        other.addTemperature(conductionSpeed);
                    }
                }
            }
        }

        @Override
        public void onProximityUpdate(){
            for (int i = 0; i < 4; i++){
                Building build = nearby(i);
                sides[i] = TileDef.conductSideTemperature(this, build);
            }
            side = edges.indexOf(Arrays.toString(sides));
            drawEdgeTopHeatRegion = drawEdgeTopHeatRegions[side][0].clone();
            drawEdgeTopHeatRegionInverted = drawEdgeTopHeatRegions[side][1].clone();
        }

        @Override
        public void draw(){
            Draw.rect(bottomRegion, x, y);
            Draw.z(Layer.block + 0.002f);
            Draw.rect(topRegion, x, y);

            Draw.z(Layer.block + 0.0022f);
            for (int i = 0; i < 4; i++){
                if (!sides[i]){
                    Draw.rect(edgeRegion, x, y, i * 90);
                }
            }

            Draw.blend(Blending.additive);
            Draw.color(heatColor);
            Draw.alpha(Math.min(temperature, 0.9f * temperaturePercent) / temperaturePercent);
            Draw.z(Layer.block + 0.001f);
            Draw.rect(heatRegion, x, y);

            Draw.color(sideHeatColor);
            Draw.alpha(Math.min(temperature, 0.9f * temperaturePercent) / temperaturePercent / 5);
            Draw.z(Layer.block + 0.0021f);
            Draw.rect(topHeatRegion, x, y);

            Draw.z(Layer.block + 0.003f);
            for (int i = 0; i < 4; i++){
                if (!sides[i]){
                    Draw.rect(edgeHeatRegion, x, y, i * 90);
                }
                if(drawEdgeTopHeatRegion[i]) {
                    Draw.rect(edgeHeatTopRegion, x, y, i * 90);
                }
                if(drawEdgeTopHeatRegionInverted[i]) {
                    Draw.rect(edgeHeatTopRegionInverted, x, y, i * 90);
                }
            }

            Draw.blend();
            Draw.reset();
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
