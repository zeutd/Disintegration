package disintegration.world.blocks.environment;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import static arc.Core.atlas;

public class ConnectFloor extends Floor {
    public TextureRegion sideRegion;

    public ConnectFloor(String name) {
        super(name);
    }

    @Override
    public void load(){
        super.load();
        sideRegion = atlas.find(name + "-side");
    }

    @Override
    public void drawBase(Tile tile){
        Draw.rect(region, tile.worldx(), tile.worldy());
        for (int i = 0; i < 4; i++) {
            Tile other = tile.nearby(i);
            if (other != null && other.floor().id != id){
                Draw.rect(sideRegion, other.worldx(), other.worldy(), i * 90);
            }
        }
    }
}
