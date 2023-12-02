package disintegration.world.blocks.laser;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.IntSet;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.util.MathDef;
import disintegration.world.meta.DTStatUnit;
import mindustry.content.Fx;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WrapEffect;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.ui.Bar;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.meta.Stat;

import static mindustry.Vars.state;

public class LaserReactor extends PowerGenerator {
    public float maxLaser = 100f;
    public float heating = 1f / 60f / 3f;
    public float flashThreshold = 0.01f, flashAlpha = 0.4f, flashSpeed = 7f;
    public float warmupSpeed;
    public float coolantPower = 0.5f;

    public TextureRegion lightsRegion;

    public Color flashColor1 = Color.red, flashColor2 = Color.valueOf("89e8b6");

    public LaserReactor(String name) {
        super(name);
        update = true;
        solid = true;
        rotate = false;

        explosionRadius = 16;
        explosionDamage = 1500;
        explodeEffect = new MultiEffect(Fx.bigShockwave, new WrapEffect(Fx.titanSmoke, Color.valueOf("e3ae6f")));
        explodeSound = Sounds.explosionbig;
    }

    @Override
    public void load(){
        super.load();
        drawer.load(this);
        lightsRegion = Core.atlas.find(name + "-lights");
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.input, maxLaser, DTStatUnit.laserUnits);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (LaserReactorBuild entity) -> new Bar("bar.heat", Pal.sap, () -> entity.heat));

        addBar("laser", (LaserReactorBuild entity) ->
                new Bar(() ->
                        Core.bundle.format("bar.laserpercent", MathDef.round(entity.luminosity, 10), (int)(Mathf.clamp(entity.luminosity / maxLaser) * 100)),
                        () -> Pal.redLight,
                        () -> entity.luminosity / maxLaser));
    }
    public class LaserReactorBuild extends GeneratorBuild implements LaserConsumer {
        float[] sideLaser = new float[getEdges().length];
        float[] callFrom = new float[getEdges().length];

        public float luminosity;
        public float heat;
        public float flash;
        public float warmup;

        public boolean flushed;

        @Override
        public void call(float value, int from, IntSet cameFrom) {
            callFrom[from] = value;
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.heat) return heat;
            return super.sense(sensor);
        }

        @Override
        public void updateTile(){
            sideLaser = callFrom.clone();
            callFrom = new float[getEdges().length];
            luminosity = 0;
            for (float side : sideLaser) {
                luminosity += side;
            }
            float fullness = Mathf.clamp(luminosity / maxLaser);
            if(flushed) {
                if(fullness > productionEfficiency && enabled && fullness != 0) {
                    productionEfficiency = Mathf.lerpDelta(productionEfficiency, fullness, warmupSpeed * timeScale);
                    heat += fullness * heating * Math.min(delta(), 4f);
                } else {
                    productionEfficiency = Mathf.lerpDelta(productionEfficiency, fullness, warmupSpeed * 20);
                }
                if (Mathf.equal(productionEfficiency, fullness, 0.001f)) {
                    productionEfficiency = fullness;
                }
                warmup = productionEfficiency;
                if (heat > 0) {
                    float maxUsed = Math.min(liquids.currentAmount(), heat / coolantPower);
                    heat -= maxUsed * coolantPower;
                    liquids.remove(liquids.current(), maxUsed);
                }
                if (heat >= 1) {
                    kill();
                }
            }
            flushed = true;
        }
        @Override
        public void draw(){
            super.draw();

            if(heat > flashThreshold){
                if(!state.isPaused()) flash += (1f + ((heat - flashThreshold) / (1f - flashThreshold)) * flashSpeed) * Time.delta;
                Draw.z(Layer.blockAdditive);
                Draw.blend(Blending.additive);
                Draw.color(flashColor1, flashColor2, Mathf.absin(flash, 8f, 1f));
                Draw.alpha(flashAlpha * Mathf.clamp((heat - flashThreshold) / (1f - flashThreshold) * 4f));
                Draw.rect(lightsRegion, x, y);
                Draw.blend();
            }
        }

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(warmup);
            write.f(productionEfficiency);
            write.f(heat);
            write.f(luminosity);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            warmup = read.f();
            productionEfficiency = read.f() + 0.01f;
            heat = read.f();
            luminosity = read.f();
        }
    }
}
