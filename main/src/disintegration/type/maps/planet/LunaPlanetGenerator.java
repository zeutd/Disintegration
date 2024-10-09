package disintegration.type.maps.planet;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.Tmp;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.game.Schematics;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.TileGen;
import mindustry.world.blocks.environment.Floor;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static mindustry.Vars.world;

public class LunaPlanetGenerator extends PlanetGenerator {
    {
        baseSeed = 10;
    }

    public float rimWidth = 0.5f, rimSteepness = 2f, floorHeight = -0.3f;

    float scl = 2f;

    float airScl = 14f, airThresh = 0.13f;

    public float cavityShape(float x) {
        return x * x - 1;
    }

    public float rimShape(float x) {
        x = Math.abs(x) - 1 - rimWidth;
        return rimSteepness * x * x;
    }

    public float craterShape(float x) {
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
            float d = position.dst(Tmp.v31.set(x, y, z).setLength(0.7f));
            if (d > w + rimWidth) continue;
            ch += craterShape(d);
        }
        return ch + rawHeight(position);
    }

    float rawHeight(Vec3 position) {
        position = Tmp.v33.set(position).scl(scl);
        return Mathf.pow(Simplex.noise3d(seed, 7, 0.5f, 1f / 3f, position.x, position.y, position.z), 2.3f);
    }

    @Override
    public Color getColor(Vec3 position) {
        return getBlock(position).mapColor;
    }

    public Block getBlock(Vec3 position) {
        float tar = Simplex.noise3d(seed, 4, 0.6f, 5f / 10f, position.x, position.y + 999f, position.z) * 0.3f + Tmp.v31.dst(0, 0, 1f) * 0.2f;
        if (tar > 0.47f) return Blocks.stone;
        return Blocks.dacite;
    }

    @Override
    public void genTile(Vec3 position, TileGen tile) {
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;

        if (Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, airScl) > airThresh) {
            tile.block = Blocks.air;
        }
    }

    @Override
    protected void generate() {
        float length = width / 2.6f;
        Vec2 trns = Tmp.v1.trns(rand.random(360f), length);
        int
                spawnX = (int) (trns.x + width / 2f), spawnY = (int) (trns.y + height / 2f);
        pass((x, y) -> {
            Floor tmp = floor.asFloor();
            if (noise(x, y, 20f, 1f) > 0.7 && block == Blocks.dacite) {
                tmp = Blocks.stone.asFloor();
            } else if (noise(x, y, 40f, 1f) > 0.7 && block == Blocks.stone) {
                tmp = Blocks.dacite.asFloor();
            }

            if (noise(x, y, 60f, 1f) > 0.8) {
                tmp = Blocks.ice.asFloor();
            }
            floor = tmp;
            block = floor.asFloor().wall;
            if (noise(x, y, airScl, 1f) > airThresh) {
                block = Blocks.air;
            }

            float max = 0;
            for (Point2 p : Geometry.d8) {
                max = Math.max(max, world.getDarkness(x + p.x, y + p.y));
            }
            if (max > 0) {
                block = floor.asFloor().wall;
            }
        });
        distort(5, 200);
        cells(6);
        float poles = Math.abs(sector.tile.v.y);
        Seq<Block> ores = Seq.with(Blocks.oreCopper, Blocks.oreLead, Blocks.oreCoal, Blocks.oreTitanium, Blocks.oreThorium);
        FloatSeq frequencies = new FloatSeq();
        for (int i = 0; i < ores.size; i++) {
            frequencies.add(rand.random(-0.1f, 0.01f) - i * 0.01f + poles * 0.04f);
        }
        pass((x, y) -> {
            if (!floor.asFloor().hasSurface()) return;

            int offsetX = x - 4, offsetY = y + 23;
            for (int i = ores.size - 1; i >= 0; i--) {
                Block entry = ores.get(i);
                float freq = frequencies.get(i);
                if (Math.abs(0.5f - noise(offsetX, offsetY + i * 999, 2, 0.7, (40 + i * 2))) > 0.4f + i * 0.01 &&
                        Math.abs(0.5f - noise(offsetX, offsetY - i * 999, 1, 1, (30 + i * 4))) > 0.5f + freq) {
                    ore = entry;
                    break;
                }
            }
        });
        int clearRadius = 10;
        for(int x = -clearRadius; x < clearRadius; x++){
            for(int y = -clearRadius; y < clearRadius; y++){
                if(x * x + y * y < clearRadius * clearRadius){
                    Tile tile = tiles.get(x + spawnX, y + spawnY);
                    if(tile != null) tile.setBlock(Blocks.air);
                }
            }
        }
        inverseFloodFill(tiles.get(spawnX, spawnY));
        median(9);
        Schematics.placeLaunchLoadout(spawnX, spawnY);
    }
}
