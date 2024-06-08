package disintegration.type.maps.planet;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.util.Log;
import arc.util.Tmp;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import disintegration.content.DTBlocks;
import mindustry.ai.Astar;
import mindustry.content.Blocks;
import mindustry.content.Loadouts;
import mindustry.game.Schematics;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.TileGen;
import mindustry.world.blocks.environment.TallBlock;

import static arc.math.Mathf.cos;
import static arc.math.Mathf.sin;
import static mindustry.Vars.state;
import static mindustry.Vars.world;

public class CosiuazPlanetGenerator extends PlanetGenerator{
    public float heightScl = 0.9f, octaves = 8, persistence = 0.7f, heightPow = 3f, heightMult = 1.6f;

    //TODO inline/remove
    public static float redThresh = 3.1f;
    public static float airThresh = 0.1f, airScl = 10;

    Block[] terrain = {Blocks.slag, Blocks.slag, Blocks.slag, Blocks.slag, Blocks.carbonStone, DTBlocks.obsidianFloor, DTBlocks.obsidianFloor, Blocks.carbonStone};

    {
        baseSeed = 2;
        defaultLoadout = Loadouts.basicBastion;
    }

    @Override
    public void generateSector(Sector sector){
        //no bases right now
    }

    @Override
    public float getHeight(Vec3 position){
        return Mathf.pow(rawHeight(position), heightPow) * heightMult;
    }

    @Override
    public Color getColor(Vec3 position){
        Block block = getBlock(position);

        //more obvious color
        if(block == Blocks.crystallineStone) block = Blocks.crystalFloor;
        //TODO this might be too green
        //if(block == Blocks.beryllicStone) block = Blocks.arkyicStone;

        return Tmp.c1.set(block.mapColor).a(1f - block.albedo);
    }

    @Override
    public float getSizeScl(){
        //TODO should sectors be 600, or 500 blocks?
        return 2000 * 1.07f * 6f / 5f;
    }

    float rawHeight(Vec3 position){
        if (crater(position))return 0.35f;
        return Simplex.noise3d(seed, octaves, persistence, 1f/heightScl, 10f + position.x, 10f + position.y, 10f + position.z);
    }

    float rawTemp(Vec3 position){
        return position.dst(0, 0, 1)*2.2f - Simplex.noise3d(seed, 8, 0.54f, 1.4f, 10f + position.x, 10f + position.y, 10f + position.z) * 2.9f;
    }

    boolean crater(Vec3 position){
        float radians = 360f / 18f * 15.5f * Mathf.degreesToRadians;
        Vec3 centre = new Vec3(cos(radians), 0f, sin(radians));
        //float rad = position.angleRad(centre);
        //return position.dst(centre) <= 0.5f;
        float dst = centre.dst(position);
        float noise = Math.abs(0.5f - Simplex.noise3d(seed + 3, 7, 0f, 2f, position.x, position.y, position.z));
        return (dst < 1 && 0 < noise && noise < 0 + 0.5 * Mathf.pow(1 - dst, 1.3f));
        //return false;
    }

    Block getBlock(Vec3 position){

        if(crater(position)) return Blocks.slag;
        float ice = rawTemp(position);
        Tmp.v32.set(position);

        float height = rawHeight(position);
        Tmp.v31.set(position);
        height *= 1.2f;
        height = Mathf.clamp(height);

        Block result = terrain[Mathf.clamp((int)(height * terrain.length), 0, terrain.length - 1)];

        /*if(ice < 0.3 + Math.abs(Ridged.noise3d(seed + crystalSeed, position.x + 4f, position.y + 8f, position.z + 1f, crystalOct, crystalScl)) * crystalMag){
            return Blocks.crystallineStone;
        }*/

        if(ice < 0.6){
            if(result == Blocks.rhyolite || result == Blocks.yellowStone || result == Blocks.regolith){
                //TODO bio(?) luminescent stuff? ice?
                return DTBlocks.obsidianFloor; //TODO perhaps something else.
            }
        }

        //TODO tweak this to make it more natural
        //TODO edge distortion?

        if(ice > redThresh){
            result = Blocks.ferricStone;
        }else if(ice > redThresh - 0.4f){
            //TODO this may increase the amount of regolith, but it's too obvious a transition.
            result = Blocks.basalt;
        }

        return result;
    }

    @Override
    public void genTile(Vec3 position, TileGen tile){
        tile.floor = getBlock(position);

        tile.block = tile.floor.asFloor().wall;

        if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, airScl) > airThresh){
            tile.block = Blocks.air;
        }

        //TODO only certain places should have carbon stone...
        if(Ridged.noise3d(seed + 2, position.x, position.y + 4f, position.z, 3, 6f) > 0.6){
            tile.floor = Blocks.carbonStone;
        }
    }

    @Override
    protected void generate(){
        float temp = rawTemp(sector.tile.v);

        if(temp > 0.7){

            pass((x, y) -> {
                if(floor != Blocks.redIce){
                    float noise = noise(x + 782, y, 7, 0.8f, 280f, 1f);
                    if(noise > 0.62f){
                        if(noise > 0.635f){
                            floor = Blocks.slag;
                        }else{
                            floor = DTBlocks.obsidianFloor;
                        }
                        ore = Blocks.air;
                    }

                    //TODO this needs to be tweaked
                    if(noise > 0.55f && floor == Blocks.beryllicStone){
                        floor = Blocks.yellowStone;
                    }
                }
            });
        }

        cells(4);

        //regolith walls for more dense terrain
        pass((x, y) -> {
            if(floor == Blocks.regolith && noise(x, y, 3, 0.4f, 13f, 1f) > 0.59f){
                block = Blocks.regolithWall;
            }
        });

        //TODO: yellow regolith biome tweaks
        //TODO ice biome

        float length = width/2.6f;
        Vec2 trns = Tmp.v1.trns(rand.random(360f), length);
        int
                spawnX = (int)(trns.x + width/2f), spawnY = (int)(trns.y + height/2f),
                endX = (int)(-trns.x + width/2f), endY = (int)(-trns.y + height/2f);
        float maxd = Mathf.dst(width/2f, height/2f);

        erase(spawnX, spawnY, 15);
        brush(pathfind(spawnX, spawnY, endX, endY, tile -> (tile.solid() ? 300f : 0f) + maxd - tile.dst(width/2f, height/2f)/10f, Astar.manhattan), 9);
        erase(endX, endY, 15);

        //TODO may overwrite floor blocks under walls and look bad
        blend(Blocks.slag, DTBlocks.obsidianFloor, 4);

        distort(10f, 12f);
        distort(5f, 7f);

        //does arkycite need smoothing?

        //smooth out slag to prevent random 1-tile patches
        median(3, 0.6, Blocks.slag);

        pass((x, y) -> {
            //rough rhyolite
            if(noise(x, y + 600 + x, 5, 0.86f, 60f, 1f) < 0.41f && floor == Blocks.rhyolite){
                floor = Blocks.crystalFloor;
            }

            if(floor == Blocks.slag && Mathf.within(x, y, spawnX, spawnY, 30f + noise(x, y, 2, 0.8f, 9f, 15f))){
                floor = Blocks.ferricStone;
            }

            float max = 0;
            for(Point2 p : Geometry.d8){
                //TODO I think this is the cause of lag
                max = Math.max(max, world.getDarkness(x + p.x, y + p.y));
            }
            if(max > 0){
                block = floor.asFloor().wall;
                if(block == Blocks.air) block = DTBlocks.obsidianWall;
            }
        });

        inverseFloodFill(tiles.getn(spawnX, spawnY));

        //TODO veins, blend after inverse flood fill?
        blend(Blocks.redStoneWall, Blocks.denseRedStone, 4);

        //make sure enemies have room
        erase(endX, endY, 6);

        //TODO enemies get stuck on 1x1 passages.

        tiles.getn(endX, endY).setOverlay(Blocks.spawn);

        //ores
        pass((x, y) -> {

            if(block != Blocks.air){
                if(nearAir(x, y)){
                    if(block == Blocks.carbonWall && noise(x + 78, y, 4, 0.7f, 33f, 1f) > 0.52f){
                        block = Blocks.graphiticWall;
                    }else if(block != Blocks.carbonWall && noise(x + 782, y, 4, 0.8f, 38f, 1f) > 0.665f){
                        ore = Blocks.wallOreBeryllium;
                    }

                }
            }else if(!nearWall(x, y)){

                if(noise(x + 150, y + x*2 + 100, 4, 0.8f, 55f, 1f) > 0.76f){
                    ore = Blocks.oreTungsten;
                }

                //TODO design ore generation so it doesn't overlap
                if(noise(x + 999, y + 600 - x, 4, 0.63f, 45f, 1f) < 0.27f && floor == Blocks.crystallineStone){
                    ore = Blocks.oreCrystalThorium;
                }

            }

            if(noise(x + 999, y + 600 - x, 5, 0.8f, 45f, 1f) < 0.44f && floor == Blocks.crystallineStone){
                floor = Blocks.crystalFloor;
            }

            if(block == Blocks.air && (floor == Blocks.crystallineStone || floor == Blocks.crystalFloor) && rand.chance(0.09) && nearWall(x, y)
                    && !near(x, y, 4, Blocks.crystalCluster) && !near(x, y, 4, Blocks.vibrantCrystalCluster)){
                block = floor == Blocks.crystalFloor ? Blocks.vibrantCrystalCluster : Blocks.crystalCluster;
                ore = Blocks.air;
            }

            if(block == Blocks.arkyicWall && rand.chance(0.23) && nearAir(x, y) && !near(x, y, 3, Blocks.crystalOrbs)){
                block = Blocks.crystalOrbs;
                ore = Blocks.air;
            }

            //TODO test, different placement
            //TODO this biome should have more blocks in general
            if(block == Blocks.regolithWall && rand.chance(0.3) && nearAir(x, y) && !near(x, y, 3, Blocks.crystalBlocks)){
                block = Blocks.crystalBlocks;
                ore = Blocks.air;
            }
        });

        //remove props near ores, they're too annoying
        pass((x, y) -> {
            if(ore.asFloor().wallOre || block.itemDrop != null || (block == Blocks.air && ore != Blocks.air)){
                removeWall(x, y, 3, b -> b instanceof TallBlock);
            }
        });

        trimDark();

        for(Tile tile : tiles){
            if(tile.overlay().needsSurface && !tile.floor().hasSurface()){
                tile.setOverlay(Blocks.air);
            }
        }

        decoration(0.017f);

        //it is very hot
        state.rules.env = sector.planet.defaultEnv;
        state.rules.placeRangeCheck = true;

        //TODO remove slag and arkycite around core.
        Schematics.placeLaunchLoadout(spawnX, spawnY);

        //all sectors are wave sectors
        state.rules.waves = false;
        state.rules.showSpawns = true;
    }
}
