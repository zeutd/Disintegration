package disintegration.world.blocks.effect;

import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Nullable;
import disintegration.util.WorldDef;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.environment.Floor;

@SuppressWarnings("all")
public class FloorBuilder extends Block {
    public int range = 1;

    public int floorOffset = 0;

    public Floor floor;

    @Nullable
    public ItemStack returnItem;
    public Seq<Floor> whiteList = new Seq<>();
    public Seq<Floor> blackList = new Seq<>();

    public FloorBuilder(String name) {
        super(name);
        update = true;
        solid = false;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashRect(valid ? Pal.accent : Pal.remove, x * 8 - range * 4 + Geometry.d4x(rotation) * floorOffset * 4, y * 8 - range * 4 + Geometry.d4y(rotation) * floorOffset * 4, range * 8, range * 8);
    }

    public class FloorBuilderBuild extends Building {
        @Override
        public void updateTile() {
            super.updateTile();
            if (!allowUpdate()) return;
            WorldDef.getAreaTile(new Vec2(
                            tileX() - (float) range / 2 + Geometry.d4x(rotation) * (float) floorOffset / 2,
                            tileY() - (float) range / 2 + Geometry.d4y(rotation) * (float) floorOffset / 2),
                    range, range).each(t -> {
                if (t != null &&
                        whiteList.size > 0 ? whiteList.contains(t.floor()) :
                        blackList.size > 0 ? !blackList.contains(t.floor()) : true
                ) {
                    t.setFloor(floor);
                    Fx.coreBuildBlock.at(t.worldx(), t.worldy(), 0, floor);
                    if (returnItem != null) core().items.add(returnItem.item, returnItem.amount);
                }
            });
            tile.setBlock(Blocks.air);
        }
    }
}
