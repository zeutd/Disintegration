package disintegration.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Rand;
import arc.math.geom.Vec2;
import disintegration.graphics.Pal2;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import static arc.Core.atlas;
import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;

public class DTFx {
    public static final Rand rand = new Rand();
    public static final Vec2 v = new Vec2();

    public static final Effect
            electricResonated = new Effect(40f, e -> {
        color(Pal2.hyperBlue);
        randLenVectors(e.id, 2, 1f + e.fin() * 2f, (x, y) -> {
            Fill.square(e.x + x, e.y + y, e.fslope() * 1.1f, 45f);
        });
    }),
            hitLaserYellow = new Effect(8, e -> {
                color(Color.white, Pal.bulletYellow, e.fin());
                stroke(0.5f + e.fout());
                Lines.circle(e.x, e.y, e.fin() * 5f);

                Drawf.light(e.x, e.y, 23f, Pal.bulletYellow, e.fout() * 0.7f);
            }),
            quarryDrillEffect = new Effect(60, e -> {
                color(e.color, Color.gray, e.fin() * 3 >= 1 ? 1 : e.fin() * 3);
                Draw.rect(atlas.find("large-orb"), e.x + Angles.trnsx(e.rotation, e.finpow() * 20), e.y + Angles.trnsy(e.rotation, e.finpow() * 20), e.fout() * 8f + 2, e.fout() * 8f + 2);
            }),
            hitFracture = new Effect(30, e -> {
                color(Pal.berylShot);
                stroke(e.fout() * 3f);
                Lines.circle(e.x, e.y, 20);
            }),
            ethyleneGenerate = new Effect(100, e -> {
                color(Pal2.ethylene);
                alpha(e.fslope() * 0.8f);

                rand.setSeed(e.id);
                for (int i = 0; i < 3; i++) {
                    v.trns(rand.random(360f), rand.random(e.finpow() * 14f)).add(e.x, e.y);
                    Fill.circle(v.x, v.y, rand.random(1.4f, 3.4f));
                }
            }).layer(Layer.bullet - 1f),
            ethyleneVentSteam = new Effect(140f, e -> {
                color(e.color, Pal2.ethylene, e.fin());

                alpha(e.fslope() * 0.78f);

                float length = 3f + e.finpow() * 10f;
                rand.setSeed(e.id);
                for (int i = 0; i < rand.random(3, 5); i++) {
                    v.trns(rand.random(360f), rand.random(length));
                    Fill.circle(e.x + v.x, e.y + v.y, rand.random(1.2f, 3.5f) + e.fslope() * 1.1f);
                }
            }).layer(Layer.darkness - 1),

    shootEncourage = new Effect(80f, 300f, e -> {
        color(Pal.darkestMetal);
        rand.setSeed(e.id);
        for (int i = 0; i < 20; i++) {
            v.trns(e.rotation + rand.range(21f), rand.random(e.fin(Interp.pow10Out) * 60f)).add(rand.range(0.1f), rand.range(1f));
            e.scaled(e.lifetime * rand.random(0.2f, 1f), b -> Fill.circle(e.x + v.x, e.y + v.y, b.foutpowdown() * 4f + 0.3f));
        }
    }),

    shootSmokeMissileBeryl = new Effect(80f, 300f, e -> {
        color(Pal.darkerGray);
        rand.setSeed(e.id);
        for (int i = 0; i < 15; i++) {
            v.trns(e.rotation + 180f + rand.range(31f), rand.random(e.fin(Interp.pow10Out) * 20f)).add(rand.range(0.1f), rand.range(1f)).add(new Vec2(-15, 0).rotate(e.rotation));
            e.scaled(e.lifetime * rand.random(0.2f, 1f), b -> Fill.circle(e.x + v.x, e.y + v.y, b.fout() * 2f + 0.2f));
        }
    });
}