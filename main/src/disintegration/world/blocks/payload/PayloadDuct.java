package disintegration.world.blocks.payload;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Tmp;
import disintegration.util.DrawDef;
import mindustry.graphics.Layer;
import mindustry.world.blocks.payloads.PayloadConveyor;

import static arc.Core.graphics;
import static arc.math.geom.Geometry.d4;
import static mindustry.Vars.tilesize;

public class PayloadDuct extends PayloadConveyor {
    public TextureRegion tileRegion;
    public PayloadDuct(String name) {
        super(name);
    }
    private FrameBuffer buffer;
    @Override
    public void load(){
        super.load();
        tileRegion = Core.atlas.find(name + "-tiles");
    }

    public class PayloadDuctBuild extends PayloadConveyorBuild {
        @Override
        public void draw(){
            Draw.z(Layer.block);
            Draw.rect(block.region, x, y);

            int a = 0;
            for(int i = 0; i < 4; i++){
                if(!blends(i)){
                    a += 1 << i;
                }
            }
            TextureRegion[] tiles = DrawDef.splitRegionTile(tileRegion, 8, 2, 64);
            Draw.draw(Layer.blockAdditive, () -> {
                if (buffer == null) buffer = new FrameBuffer(graphics.getWidth(), graphics.getHeight());
                Draw.z(Layer.blockAdditive);
                buffer.begin(Color.clear);
                if(item != null){
                    item.draw();
                }
                buffer.end();
                Tmp.tr1.set(buffer.getTexture());
                Tmp.v1.set(Core.camera.project(x - offset + 0.5f * tilesize - size * tilesize / 2f, y - offset + 0.5f * tilesize - size * tilesize / 2f));
                Tmp.v2.set(Core.camera.project(size * tilesize + Core.camera.position.x - Core.camera.width / 2, size * tilesize + Core.camera.position.y - Core.camera.height / 2));
                Tmp.tr1.set((int)Tmp.v1.x, (int)Tmp.v1.y, (int)Tmp.v2.x, (int) Tmp.v2.y);
                Draw.rect(Tmp.tr1, x, y, size * tilesize, -size * tilesize);
                for(int i = 0; i < 4; i++){
                    if(blends(i)){
                        Tmp.tr1.set((int) (Tmp.v1.x + d4(i).x * Tmp.v2.x), (int) (Tmp.v1.y + d4(i).y * Tmp.v2.y), (int) Tmp.v2.x, (int) Tmp.v2.y);
                        Draw.rect(Tmp.tr1, x + d4(i).x * size * tilesize, y + d4(i).y * size * tilesize, size * tilesize, -size * tilesize);
                    }
                }
            });

            Draw.z(Layer.blockOver);
            Draw.rect(tiles[15 - a], x, y);
            Draw.rect(topRegion, x, y, drawrot());
        }
    }
}
