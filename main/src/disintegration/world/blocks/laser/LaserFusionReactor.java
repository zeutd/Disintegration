package disintegration.world.blocks.laser;

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.struct.IntSet;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.util.MathDef;
import mindustry.content.Fx;
import mindustry.game.EventType;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.ui.Bar;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawPlasma;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class LaserFusionReactor extends PowerGenerator{
    public float maxLaser;
    public final int timerUse = timers++;
    public float warmupSpeed = 0.001f;
    public float itemDuration = 60f;

    public LaserFusionReactor(String name){
        super(name);
        hasPower = true;
        hasItems = true;
        outputsPower = consumesPower = true;
        flags = EnumSet.of(BlockFlag.reactor, BlockFlag.generator);
        lightRadius = 115f;
        emitLight = true;
        envEnabled = Env.any;

        drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawPlasma(), new DrawDefault());

        explosionShake = 6f;
        explosionShakeDuration = 16f;
        explosionDamage = 1900 * 4;
        explosionMinWarmup = 0.3f;
        explodeEffect = Fx.impactReactorExplosion;
        explodeSound = Sounds.explosionbig;
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("power", (PowerGenerator.GeneratorBuild entity) -> new Bar(() ->
                Core.bundle.format("bar.poweroutput",
                        Strings.fixed(Math.max(entity.getPowerProduction() - consPower.usage, 0) * 60 * entity.timeScale(), 1)),
                () -> Pal.powerBar,
                () -> entity.productionEfficiency));

        addBar("laser", (LaserFusionReactorBuild entity) ->
                new Bar(() ->
                        Core.bundle.format("bar.laserpercent", MathDef.round(entity.luminosity, 10), (int) (Mathf.clamp(entity.luminosity / maxLaser) * 100)),
                        () -> Pal.redLight,
                        () -> entity.luminosity / maxLaser));
    }

    @Override
    public void setStats(){
        super.setStats();

        if(hasItems){
            stats.add(Stat.productionTime, itemDuration / 60f, StatUnit.seconds);
        }
    }

    public class LaserFusionReactorBuild extends GeneratorBuild implements LaserConsumer{
        public float warmup, totalProgress;

        float[] sideLaser = new float[4];
        float[] callFrom = new float[4];

        public float luminosity;

        public boolean flushed;

        @Override
        public void updateTile(){
            sideLaser = callFrom.clone();
            callFrom = new float[4];
            luminosity = 0;
            for (float side : sideLaser) {
                luminosity += side;
            }
            float fullness = Mathf.clamp(luminosity / maxLaser);
            if(flushed) {
                if (efficiency >= 0.9999f && power.status >= 0.99f) {

                    warmup = Mathf.lerpDelta(warmup, fullness, warmupSpeed * timeScale);
                    if (Mathf.equal(warmup, fullness, 0.001f)) {
                        warmup = fullness;
                    }

                    if (timer(timerUse, itemDuration / timeScale)) {
                        consume();
                    }
                } else {
                    warmup = Mathf.lerpDelta(warmup, 0f, warmupSpeed);
                }

                totalProgress += warmup * Time.delta;

                productionEfficiency = Mathf.pow(warmup, 5f);
            }

            flushed = true;
        }

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float totalProgress(){
            return totalProgress;
        }

        @Override
        public float ambientVolume(){
            return warmup;
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.heat) return warmup;
            return super.sense(sensor);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(warmup);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            warmup = read.f();
        }

        @Override
        public void call(float value, int from, IntSet cameFrom) {
            callFrom[from] = value;
        }
    }
}
