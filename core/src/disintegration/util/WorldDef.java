package disintegration.util;

import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.world.Tile;

import static mindustry.Vars.world;

public class WorldDef {
    public static boolean toBlock(Building block, Building other) {
        return !other.block().rotate || other.relativeTo(block) == other.rotation;
    }

    public static Seq<Tile> getAreaTile(Vec2 pos, int width, int height) {
        Seq<Tile> tilesGet = new Seq<>();
        int dx = (int) pos.x;
        int dy = (int) pos.y;
        for (int ix = 0; ix < width; ix++) {
            for (int iy = 0; iy < height; iy++) {
                Tile other = world.tile(ix + dx + 1, iy + dy + 1);
                tilesGet.add(other);
            }
        }
        return tilesGet;
    }
}
