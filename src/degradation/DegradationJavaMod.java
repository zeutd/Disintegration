package degradation;

import arc.util.*;
import degradation.content.*;
import mindustry.mod.*;

public class DegradationJavaMod extends Mod{
    public DegradationJavaMod(){
        Log.info("Loaded DegradationMod constructor.");
    }
    @Override
    public void loadContent() {
        Log.info("Loading degradation content.");
        DTItems.load();
        DTUnits.load();
        DTBlocks.load();
        DTBullets.load();
        DTStatusEffects.load();
    }
}
