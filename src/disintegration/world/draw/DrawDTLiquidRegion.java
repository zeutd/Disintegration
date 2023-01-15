package disintegration.world.draw;

import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.draw.DrawLiquidRegion;

public class DrawDTLiquidRegion extends DrawLiquidRegion {
    public boolean rotate;

    /**No...........*/
    public DrawDTLiquidRegion(Liquid drawLiquid){
        this.drawLiquid = drawLiquid;
    }

    /**No...........*/
    public DrawDTLiquidRegion(){
    }

    @Override
    public void draw(Building build){
        /*
        Liquid drawn = drawLiquid != null ? drawLiquid : build.liquids.current();
        TextureRegion region = renderer.fluidFrames[drawn.gas ? 1 : 0][drawn.getAnimationFrame()];
        Tmp.tr1.texture = new Texture(liquid.width, liquid.height);
        Pixmap tmpP = new Pixmap(liquid.width, liquid.height);
        PixmapRegion liquidP = Core.atlas.getPixmap(liquid);
        PixmapRegion regionP = Core.atlas.getPixmap(region);
        Pixmap toDraw = new Pixmap(liquid.width, liquid.height);
        TextureAtlas.AtlasRegion region1 = new TextureAtlas.AtlasRegion(new TextureRegion(new Texture(liquid.width, liquid.height)));
        //region1.texture = new Texture(liquid.width, liquid.height);
        for (int sx = 0; sx < build.block.size; sx++) {
            for (int sy = 0; sy < build.block.size; sy++) {

                for (int x = 0; x < regionP.width; x++) {
                    for (int y = 0; y < regionP.height; y++) {
                        if(liquidP.getA(x + sx * 32, y + sy * 32) != 0) {
                            tmpP.set(x, y, regionP.get(x, y));
                        }
                    }
                }
                //tmpP = regionP.pixmap.copy();
                //Tmp.tr1.texture.draw(regionP.pixmap, sx * 32, sy * 32);
                //toDraw.draw(regionP.pixmap, 0, 0, true);
                region1.pixmapRegion = regionP;
            }
        }
        Drawf.liquid(region1, build.x, build.y,
                //build.liquids.get(drawn) / build.block.liquidCapacity * alpha,
                0.5f,
                drawn.color,
                rotate ? build.rotation * 90 : 0);

         */
    }
}
