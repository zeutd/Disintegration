package disintegration.world.draw;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.gl.FrameBuffer;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import arc.util.Tmp;
import arclibrary.graphics.EDraw;
import disintegration.graphics.DTShaders;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.draw.DrawBlock;

import static arc.Core.graphics;
import static arc.math.Mathf.sqr;
import static mindustry.Vars.tilesize;

public class DrawArcs extends DrawBlock {
    public Rand rand2 = new Rand();
    public Color flameColor = Color.valueOf("3969ff"), midColor = Color.valueOf("bfefff");
    public float flameRad = 5f, circleSpace = 2f, flameRadiusScl = 3f, flameRadiusMag = 0.3f, circleStroke = 1.5f;
    public float alpha = 0.68f;
    public int arcs = 20, arcPoints = 10;
    public float arcLife = 15f, arcRad = 18f, arcStroke = 1f, arcRange = 1.5f;
    public boolean drawCenter = true;
    public Blending blending = Blending.additive;

    @Override
    public void draw(Building build){
        if(build.warmup() > 0f && flameColor.a > 0.001f){
            Lines.stroke(circleStroke * build.warmup());

            float si = Mathf.absin(flameRadiusScl, flameRadiusMag);
            float a = alpha * build.warmup();
            Draw.blend(blending);

            Draw.color(midColor, a);
            if(drawCenter) Fill.circle(build.x, build.y, flameRad + si);

            Draw.color(flameColor, a);
            if(drawCenter) Lines.circle(build.x, build.y, (flameRad + circleSpace + si) * build.warmup());

            Lines.stroke(arcStroke * build.warmup());

            float base = (Time.time / arcLife / 2);
            rand.setSeed(build.id + Mathf.floor((Time.time % arcLife) / arcLife));
            for(int i = 0; i < arcs; i++) {
                float fin = Interp.pow5In.apply(1 - (rand.random(1f) + base) % 1f);
                float angle = rand.random(360f);
                float nx, ny;
                float tx, ty;
                Lines.beginLine();
                for (int j = 0; j < arcPoints; j++) {
                    Tmp.v1.trns(angle, (float) j / arcPoints * arcRad).add(build);
                    tx = Tmp.v1.x;
                    ty = Tmp.v1.y;
                    rand2.setSeed((long) i * j);
                    if (j == 0) {
                        nx = build.x;
                        ny = build.y;
                    } else {
                        Tmp.v1.setToRandomDirection(rand2).scl(arcRange / 2f);
                        nx = tx + Tmp.v1.x;
                        ny = ty + Tmp.v1.y;
                    }
                    //Draw.alpha((1f - 5 * Mathf.pow(j - fin, 2f))*(1f - Mathf.pow(1f - 2f * fin, 2f)));
                    Lines.linePoint(nx, ny);
                }
                Draw.alpha(fin);
                Lines.endLine();
            }

            Draw.blend();
            Draw.reset();
        }
    }
}
