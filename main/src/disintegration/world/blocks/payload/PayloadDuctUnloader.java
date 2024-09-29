package disintegration.world.blocks.payload;

import arc.graphics.g2d.Draw;
import mindustry.graphics.Layer;
import mindustry.world.blocks.payloads.PayloadUnloader;

public class PayloadDuctUnloader extends PayloadUnloader {
    public PayloadDuctUnloader(String name) {
        super(name);
    }
    public class PayloadDuctUnloaderBuild extends PayloadUnloaderBuild {
        @Override
        public void draw(){
            Draw.rect(region, x, y);

            boolean fallback = true;
            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                    fallback = false;
                }
            }
            if(fallback) Draw.rect(inRegion, x, y, rotation * 90);

            Draw.rect(outRegion, x, y, rotdeg());
            Draw.z(Layer.blockAdditive);
            drawPayload();

            Draw.z(Layer.blockOver);
            Draw.rect(topRegion, x, y);
        }
    }
}
