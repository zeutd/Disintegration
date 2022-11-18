package disintegration.world.draw;

import arc.graphics.Color;
import disintegration.util.DrawDef;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.world.draw.DrawBlock;

public class DrawFusion extends DrawBlock {
    public int spikes = 5;
    public float radius = 32f;
    public float width = 10f;
    public float minSpeed = 2f;
    public float maxSpeed = 6f;
    public float scl = 1.5f;

    public Color color1 = Pal.meltdownHit;
    public Color color2 = Color.white;

    public DrawFusion(){}

    public DrawFusion(int spikes, float radius, float width){
        this.spikes = spikes;
        this.radius = radius;
        this.width = width;
    }

    @Override
    public void draw(Building build){
        DrawDef.drawFusion(build.x, build.y, spikes, minSpeed * build.warmup(), maxSpeed * build.warmup(), scl, radius * build.warmup(), width * build.warmup(), build.id, color1, color2);
    }
}
