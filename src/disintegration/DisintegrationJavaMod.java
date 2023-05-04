package disintegration;

import arc.Core;
import arc.Events;
import arc.util.Log;
import disintegration.content.*;
import mindustry.game.EventType;
import mindustry.mod.Mod;

public class DisintegrationJavaMod extends Mod{
    public DisintegrationJavaMod(){
        Events.on(EventType.ClientLoadEvent.class, e -> {
            Core.app.post(DTVars.DTUI::init);
            Core.app.addListener(DTVars.DTUI);
            Core.app.post(DTVars.spaceStationReader::read);
            Core.app.addListener(DTVars.spaceStationReader);
        });
        Log.info("Loaded disintegrationMod constructor.");
    }
    @Override
    public void loadContent() {
        Log.info("Loading disintegration content.");
        DTItems.load();
        DTLiquids.load();
        DTStatusEffects.load();
        DTBullets.load();
        DTUnitTypes.load();
        DTBlocks.load();
        DTLoadouts.load();
        DTPlanets.load();
        DTSectorPresets.load();
    }
}
