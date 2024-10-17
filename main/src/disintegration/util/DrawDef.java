package disintegration.util;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Mat3D;
import arc.util.Time;
import arclibrary.graphics.Draw3d;
import disintegration.DTVars;
import mindustry.graphics.Drawf;

import static arc.math.Mathf.cosDeg;
import static arc.math.Mathf.sinDeg;

public class DrawDef {
    private static final Mat3D tmpMat = new Mat3D();
    private static final Color tmpCol = new Color();
    public static void rect3d(TextureRegion region, float x, float y, float rotation, float height){
        float h = M3d.height(height);
        tmpMat.idt();
        tmpMat.translate(0, 0, -h * 40);
        Draw3d.rect(tmpMat, region, M3d.x(x, height) - region.width / 8f, M3d.y(y, height) - region.height / 8f, region.width / 4f, region.height / 4f, rotation);
    }
    public static void line3d(float x1, float y1, float z1, float x2, float y2, float z2){
        Lines.line(M3d.x(x1, z1), M3d.y(y1, z1), M3d.x(x2, z2), M3d.y(y2, z2));
    }

    /**
     * Original code from Prog-mats
     * @author: MEEPofFaith
     */
    public static void tube3d(float x, float y, float rad, float height, Color baseColorLight, Color baseColorDark, Color topColorLight, Color topColorDark){
        int vert = Lines.circleVertices(rad);
        float space = 360f / vert;
        float angle = M3d.tubeStartAngle(x, y, M3d.x(x, height), M3d.y(y, height), rad, rad * M3d.height(height));

        for(int i = 0; i < vert; i++){
            float a = angle + space * i, cos = cosDeg(a), sin = sinDeg(a), cos2 = cosDeg(a + space), sin2 = sinDeg(a + space);

            float x1 = x + rad * cos,
                    y1 = y + rad * sin,
                    x2 = x + rad * cos2,
                    y2 = y + rad * sin2;

            float x3 = M3d.x(x1, height),
                    y3 = M3d.y(y1, height),
                    x4 = M3d.x(x2, height),
                    y4 = M3d.y(y2, height);

            float cLerp1 = 1f - Angles.angleDist(a, 45f) / 180f,
                    cLerp2 = 1f - Angles.angleDist(a + space, 45f) / 180f;
            float bc1f = tmpCol.set(baseColorLight).lerp(baseColorDark, cLerp1).toFloatBits(),
                    tc1f = tmpCol.set(topColorLight).lerp(topColorDark, cLerp1).toFloatBits(),
                    bc2f = tmpCol.set(baseColorLight).lerp(baseColorDark, cLerp2).toFloatBits(),
                    tc2f = tmpCol.set(topColorLight).lerp(topColorDark, cLerp2).toFloatBits();

            Fill.quad(x1, y1, bc1f, x2, y2, bc2f, x4, y4, tc2f, x3, y3, tc1f);
        }
    }

    public static void tube3d(float x, float y, float rad, float height, Color baseColor, Color topColor){
        tube3d(x, y, rad, height, baseColor, baseColor, topColor, topColor);
    }
    public static TextureRegion noneRegion = Core.atlas.find("disintegration-none");

    public static int[][] dir4 = {
            {0, 1},
            {-1, 0},/*{X}*/{1, 0},
            {0, -1}
    };

    /**
     * Original code from Project Unity
     * @author: @Xeloboyo
     */
    public static TextureRegion[] splitRegionTile(TextureRegion region, int w, int h, int tilesize) {
        int size = w * h;
        TextureRegion[] regions = new TextureRegion[size];

        float tileW = (region.u2 - region.u) / w;
        float tileH = (region.v2 - region.v) / h;

        for (int i = 0; i < size; i++) {
            float tileX = ((float) (i % w)) / w;
            float tileY = ((float) (i / w)) / h;
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

    public static TextureRegion[] splitRegionTile(TextureRegion region, int w, int h) {
        return splitRegionTile(region, w, h, 32);
    }

    public static void drawFusion(float x, float y, int spikes, float minSpeed, float maxSpeed, float scl, float radius, float width, long seed, Color color1, Color color2) {
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

    public static void drawFusion(float x, float y, int spikes, float radius, float width, float[] lengths, float[] rotations, Color color1, Color color2) {
        Draw.color(color1);
        Fill.circle(x, y, radius / 10);
        drawSpikes(x, y, spikes, radius, 0, lengths, rotations);
        Draw.color(color2);
        drawSpikes(x, y, spikes, radius, width, lengths, rotations);
        Fill.circle(x, y, (radius - width) / 10);
        Draw.reset();
    }

    public static void drawSpikes(float x, float y, int spikes, float radius, float offset, float[] lengths, float[] rotations) {
        for (int i = 0; i < spikes; i++) {
            Drawf.tri(x, y, (radius - offset) / 6f, lengths[i] * 1.5f * radius - offset / 8f, rotations[i]);
        }
    }

    public static Texture loadTex(String name){
        return new Texture(DTVars.modFile.child("sprites").child(name + ".png"));
    }
}
