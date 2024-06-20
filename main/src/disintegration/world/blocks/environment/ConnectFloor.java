package disintegration.world.blocks.environment;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import static arc.Core.atlas;

public class ConnectFloor extends Floor {
    public TextureRegion sideRegion;

    public float sideLayer = Layer.floor + 0.1f;

    @Nullable
    public Seq<Floor> connects;

    public ConnectFloor(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        sideRegion = atlas.find(name + "-side");
    }

    @Override
    public void drawBase(Tile tile) {
        Draw.z(Layer.plans + 1);
        Draw.rect(region, tile.worldx(), tile.worldy());
        for (int i = 0; i < 4; i++) {
            Tile other = tile.nearby(i);
            if (other != null && !connects.contains(other.floor())) {
                Draw.z(sideLayer);
                Draw.rect(sideRegion, other.worldx(), other.worldy(), i * 90);
            }
        }
    }
}
