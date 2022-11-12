package disintegration.world.draw;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import disintegration.util.DrawDef;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.world.draw.DrawBlock;

import static mindustry.Vars.state;

public class DrawFusion extends DrawBlock {
    public int spikes = 5;
    public float radius = 32f;
    public float width = 10f;

    public Color color1 = Pal.meltdownHit;
    public Color color2 = Color.white;

    float[] lengths, speeds, rotations;
    boolean a = true;

    public DrawFusion(){}

    public DrawFusion(int spikes, float radius, float width){
        this.spikes = spikes;
        this.radius = radius;
        this.width = width;
    }

    @Override
    public void draw(Building build){
        float[] speeding = new float[spikes];
        if(a) {
            lengths = new float[spikes];
            speeds = new float[spikes];
            rotations = new float[spikes];
            for (int i = 0; i < spikes; i++) {
                speeds[i] = Mathf.range(2f, 6f);
                rotations[i] = Mathf.range(360f);
            }
            a = false;
        }
        for (int i = 0; i < spikes; i++) {
            speeding[i] = speeds[i] * build.warmup();
            lengths[i] = Mathf.absin(10 / speeding[i], 0.1f) * build.warmup();
            if(!state.isPaused()) rotations[i] = Time.time * speeds[i];
        }
        DrawDef.drawFusion(build.x, build.y, spikes, radius * build.warmup(), width * build.warmup(), lengths, rotations, color1, color2);
    }
}
