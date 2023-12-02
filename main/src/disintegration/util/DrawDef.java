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

    public static int[][] dir4 = {
                    {0, 1},
            {-1, 0},/*{X}*/{1, 0},
                    {0,-1}
    };

    /**
     * Original code from Project Unity
     * Author: @Xeloboyo
     * */
    public static TextureRegion[] splitRegionTile(TextureRegion region, int w, int h, int tilesize){
        int size = w * h;
        TextureRegion[] regions = new TextureRegion[size];

        float tileW = (region.u2 - region.u) / w;
        float tileH = (region.v2 - region.v) / h;

        for(int i = 0; i < size; i++){
            float tileX = ((float)(i % w)) / w;
            float tileY = ((float)(i / w)) / h;
            TextureRegion reg = new TextureRegion(region);

            //start coordinate
            reg.u = Mathf.map(tileX, 0f, 1f, reg.u, reg.u2) + tileW * 0.01f;
            reg.v = Mathf.map(tileY, 0f, 1f, reg.v, reg.v2) + tileH * 0.01f;
            //end coordinate
            reg.u2 = reg.u + tileW * 0.98f;
            reg.v2 = reg.v + tileH * 0.98f;

            reg.width = reg.height = tilesize;

            regions[i] = reg;
        }
        return regions;
    }

    public static TextureRegion[] splitRegionTile(TextureRegion region, int w, int h){
        return splitRegionTile(region,w,h,32);
    }

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
