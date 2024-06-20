package disintegration.world.blocks.effect;

import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.util.Tmp;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class BuildingBuilder extends Block {
    public int buildOffset;

    public Block placeBlock;

    public boolean drawArea;

    public BuildingBuilder(String name) {
        super(name);
        update = true;
        solid = true;
    }

    public Rect getRect(Rect rect, float x, float y, int rotation) {
        rect.setCentered(x, y, ((buildOffset - 1) * 2 + 1) * tilesize);
        float len = tilesize * (((buildOffset - 1) * 2 + 1) + size) / 2f;

        rect.x += Geometry.d4x(rotation) * len;
        rect.y += Geometry.d4y(rotation) * len;

        return rect;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;

        Rect rect = getRect(Tmp.r1, x, y, rotation);

        Drawf.dashRect(valid ? Pal.accent : Pal.remove, rect);
    }

    public class BuildingBuilderBuild extends Building {
        @Override
        public void updateTile() {
            super.updateTile();

            Tile other = world.tile(Geometry.d4x(rotation) * buildOffset + tileX(), Geometry.d4y(rotation) * buildOffset + tileY());
            other.setBlock(placeBlock);

            tile.setBlock(Blocks.air);
        }
    }
}
