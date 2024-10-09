package disintegration.graphics;

import mindustry.graphics.CacheLayer;
import mindustry.graphics.Shaders;

public class DTCacheLayer {
    public static CacheLayer
        moltenLithium
            ;
    public static void init(){
        CacheLayer.add(
                moltenLithium = new CacheLayer.ShaderLayer(DTShaders.lithium){}
        );
    }
}
