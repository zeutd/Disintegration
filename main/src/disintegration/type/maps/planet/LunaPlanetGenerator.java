package disintegration.type.maps.planet;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Tmp;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.graphics.Pal;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.world.Block;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class LunaPlanetGenerator extends PlanetGenerator {
    {
        baseSeed = 5;
    }
    public float rimWidth = 0.25f, rimSteepness = 1f, floorHeight = -0.2f;

    float scl = 2f;

    public float cavityShape(float x){
        return x * x - 1;
    }

    public float rimShape(float x){
        x = Math.abs(x) - 1 - rimWidth;
        return  rimSteepness * x * x;
    }

    public float craterShape(float x){
        return max(min(cavityShape(x), rimShape(x)), floorHeight);
    }
    @Override
    public float getHeight(Vec3 position) {
        float ch = 0;
        for (int i = 0; i < 10; i++) {
            rand.setSeed(seed + i * 100);
            float x = rand.random(-0.7f, 0.7f);
            rand.setSeed(seed + i * 100 + 10);
            float y = rand.random(-0.7f, 0.7f);
            rand.setSeed(seed + i * 100 + 20);
            float z = rand.random(-0.7f, 0.7f);
            rand.setSeed(seed + i * 100 + 30);
            float w = rand.random(0.1f, 0.3f);
            float d = position.dst(Tmp.v31.set(x,y,z).setLength(0.7f));
            if (d > w + rimWidth) continue;
            ch += craterShape(d);
        }
        return ch + rawHeight(position);
    }

    float rawHeight(Vec3 position){
        position = Tmp.v33.set(position).scl(scl);
        return Mathf.pow(Simplex.noise3d(seed, 7, 0.5f, 1f/3f, position.x, position.y, position.z), 2.3f);
    }

    @Override
    public Color getColor(Vec3 position) {
        return getBlock(position).mapColor;
    }

    public Block getBlock(Vec3 position){
        float tar = Simplex.noise3d(seed, 4, 0.6f, 1f/10f, position.x, position.y + 999f, position.z) * 0.3f + Tmp.v31.dst(0, 0, 1f) * 0.2f;
        if (tar > 0.25f) return Blocks.stone;
        return Blocks.shale;
    }
}
