package disintegration.entities.comp;

import arc.util.Tmp;
import disintegration.content.DTBlocks;
import disintegration.type.unit.WorldUnitType;
import ent.anno.Annotations.EntityComponent;
import ent.anno.Annotations.Import;
import mindustry.content.Blocks;
import mindustry.core.World;
import mindustry.gen.*;
import mindustry.type.UnitType;
import mindustry.world.Tiles;

@EntityComponent
abstract class WorldComp implements Unitc, Healthc {
    @Import
    UnitType type;
    public World unitWorld;

    public int worldWidth, worldHeight;

    public void initWorld() {
        if (!(type instanceof WorldUnitType w)) return;
        unitWorld = new World();
        worldWidth = w.worldWidth;
        worldHeight = w.worldHeight;
        unitWorld.resize(worldWidth, worldHeight);
        unitWorld.tiles = new Tiles(worldWidth, worldHeight);
        unitWorld.tiles.fill();
        unitWorld.tiles.eachTile(t -> t.setFloor(DTBlocks.spaceStationFloor.asFloor()));
        unitWorld.tile(9, 9).setBlock(Blocks.duo);
        unitWorld.tile(0, 9).setBlock(Blocks.duo);
    }

    @Override
    public void update(){
        if (unitWorld == null) return;
        unitWorld.tiles.eachTile(t -> {
            Building b = t.build;
            if(b != null) {
                b.set(Tmp.v1.set(t).rotate(rotation()).add(this));
                b.updateTile();
            }
        });
    }
}
