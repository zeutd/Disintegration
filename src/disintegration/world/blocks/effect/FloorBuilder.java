package disintegration.world.blocks.effect;

import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import disintegration.util.WorldDef;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.blocks.environment.Floor;

public class FloorBuilder extends Block {
    public int range = 1;

    public int floorOffset;

    public Floor floor;

    public FloorBuilder(String name) {
        super(name);
        update = true;
        solid = false;

    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashRect(valid ? Pal.accent : Pal.remove, x * 8 - range * 4 + Geometry.d4x(rotation) * floorOffset * 4, y * 8 - range * 4 + Geometry.d4y(rotation) * floorOffset * 4, range * 8,range * 8);
    }

    public class FloorBuilderBuild extends Building {
        @Override
        public void updateTile(){
            super.updateTile();
            WorldDef.getAreaTile(new Vec2(tileX() - (float)range / 2 + Geometry.d4x(rotation) * (float)floorOffset / 2, tileY() - (float)range / 2 + Geometry.d4y(rotation) * (float)floorOffset / 2), range, range).forEach(t -> {
                if(t != null) {
                    t.setFloor(floor);
                    Fx.coreBuildBlock.at(t.worldx(), t.worldy(), 0, floor);
                }
            });
            tile.setBlock(Blocks.air);
        }
    }
}
