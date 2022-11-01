package disintegration;

import arc.util.*;
import disintegration.content.*;
import mindustry.mod.*;

public class disintegrationJavaMod extends Mod{
    public disintegrationJavaMod(){
        Log.info("Loaded disintegrationMod constructor.");
    }
    @Override
    public void loadContent() {
        Log.info("Loading disintegration content.");
        DTItems.load();
        DTLiquids.load();
        DTUnitTypes.load();
        DTBlocks.load();
        DTBullets.load();
        DTPlanets.load();
        DTStatusEffects.load();
    }
}
