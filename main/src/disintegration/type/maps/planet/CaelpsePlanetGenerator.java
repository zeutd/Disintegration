package disintegration.type.maps.planet;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
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

public class CaelpsePlanetGenerator extends PlanetGenerator{

    public float heightScl = 1f, octaves = 8, persistence = 0.7f;

    Block[] terrain = {Blocks.carbonStone, DTBlocks.obsidianFloor, DTBlocks.obsidianFloor, Blocks.carbonStone};

    public float rawHeight(Vec3 position){
        return Simplex.noise3d(seed, octaves, persistence, 1f/heightScl, 10f + position.x, 10f + position.y, 10f + position.z) - 0.6f;
    }

    @Override
    public float getHeight(Vec3 position) {
        return  5 * Math.max(0, rawHeight(position));
    }

    @Override
    public Color getColor(Vec3 position) {
        return getBlock(position).mapColor;
    }

    public Block getBlock(Vec3 position){
        float height = getHeight(position);
        Block result = terrain[Mathf.clamp((int)(height * terrain.length), 0, terrain.length - 1)];
        if (Mathf.zero(height)) result = Blocks.slag;
        return result;
    }
}
