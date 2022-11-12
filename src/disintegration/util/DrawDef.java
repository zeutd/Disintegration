package disintegration.util;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import mindustry.graphics.Drawf;

public class DrawDef {
    public static TextureRegion noneRegion = Core.atlas.find("disintegration-none");

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
            Drawf.tri(x, y, (radius - offset) / 8f, lengths[i] * 50 - offset / 8f, rotations[i]);
        }
    }
}
