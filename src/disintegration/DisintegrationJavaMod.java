package disintegration;

import arc.util.*;
import disintegration.content.*;
import mindustry.mod.*;

public class DisintegrationJavaMod extends Mod{
    public DisintegrationJavaMod(){
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
