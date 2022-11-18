package disintegration.util;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.graphics.Drawf;

public class DrawDef {
    public static TextureRegion noneRegion = Core.atlas.find("disintegration-none");

    public static void drawFusion(float x, float y, int spikes, float minSpeed, float maxSpeed, float scl, float radius, float width, long seed, Color color1, Color color2){
        float[] speeds = new float[spikes];
        float[] lengths = new float[spikes];
        float[] rotations = new float[spikes];
        for (int i = 0; i < spikes; i++) {
            speeds[i] = Mathf.randomSeed(seed + i, minSpeed, maxSpeed) * MathDef.randomSeedRange(seed + i);
            rotations[i] = Time.time * speeds[i];
            lengths[i] = Mathf.absin(speeds[i] * scl, scl / 15);
        }
        drawFusion(x, y, spikes, radius, width, lengths, rotations, color1, color2);
    }

    public static void drawFusion(float x, float y, int spikes, float radius, float width, float[] lengths, float[] rotations, Color color1, Color color2){
        Draw.color(color1);
        Fill.circle(x, y, radius / 10);
        drawSpikes(x, y, spikes, radius, 0, lengths, rotations);
        Draw.color(color2);
        drawSpikes(x, y, spikes, radius, width, lengths, rotations);
        Fill.circle(x, y, (radius - width) / 10);
        Draw.reset();
    }
    public static void drawSpikes(float x, float y, int spikes, float radius, float offset, float[] lengths, float[] rotations){
        for (int i = 0; i < spikes; i++) {
            Drawf.tri(x, y, (radius - offset) / 6f, lengths[i] * 1.5f * radius - offset / 8f, rotations[i]);
        }
    }
}
