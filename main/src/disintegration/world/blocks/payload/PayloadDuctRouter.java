package disintegration.world.blocks.payload;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import disintegration.util.DrawDef;
import mindustry.graphics.Layer;
import mindustry.world.blocks.payloads.PayloadConveyor;
import mindustry.world.blocks.payloads.PayloadRouter;

public class PayloadDuctRouter extends PayloadRouter {
    public TextureRegion arrowRegion;
    public PayloadDuctRouter(String name) {
        super(name);
    }
    @Override
    public void load(){
        super.load();
        arrowRegion = Core.atlas.find(name + "-arrow");
    }

    public class PayloadDuctRouterBuild extends PayloadRouterBuild {
        @Override
        public void draw(){
            if (this.block.variants != 0 && this.block.variantRegions != null) {
                Draw.rect(block.variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, block.variantRegions.length - 1))], x, y, drawrot());
            } else {
                Draw.rect(block.region, x, y, drawrot());
            }

            drawTeamTop();
            Draw.z(Layer.blockAdditive);
            if(item != null){
                item.draw();
            }
            Draw.z(Layer.blockOver);
            Draw.rect(topRegion, x, y);
            Draw.rect(arrowRegion, x, y, smoothRot);
        }
    }
}
