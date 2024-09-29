package disintegration.world.blocks.payload;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import disintegration.util.DrawDef;
import mindustry.graphics.Layer;
import mindustry.world.blocks.payloads.PayloadConveyor;

public class PayloadDuct extends PayloadConveyor {
    public TextureRegion tileRegion;
    public PayloadDuct(String name) {
        super(name);
    }
    @Override
    public void load(){
        super.load();
        tileRegion = Core.atlas.find(name + "-tiles");
    }

    public class PayloadDuctBuild extends PayloadConveyorBuild {
        @Override
        public void draw(){
            Draw.rect(block.region, x, y);

            int a = 0;
            for(int i = 0; i < 4; i++){
                if(!blends(i)){
                    a += 1 << i;
                }
            }
            Draw.z(Layer.blockAdditive);
            TextureRegion[] tiles = DrawDef.splitRegionTile(tileRegion, 8, 2, 64);
            if(item != null){
                item.draw();
            }
            Draw.z(Layer.blockOver);
            Draw.rect(tiles[15 - a], x, y);
            Draw.rect(topRegion, x, y, drawrot());
        }
    }
}
