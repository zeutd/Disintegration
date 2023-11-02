package disintegration;

import arc.Events;
import disintegration.content.*;
import disintegration.graphics.DTShaders;
import mindustry.game.EventType;
import mindustry.mod.Mod;

import static arc.Core.app;

public class DisintegrationJavaMod extends Mod{
    public DisintegrationJavaMod(){
        Events.on(EventType.ClientLoadEvent.class, e -> {
            app.post(DTVars::init);
            app.post(DTShaders::init);
            app.addListener(DTVars.DTUI);
            app.post(DTVars.spaceStationReader::read);
            app.addListener(DTVars.spaceStationReader);
            /*Core.app.post(() -> Vars.content.setCurrentMod(new Mods.LoadedMod(null, null, null, null,
                    new Mods.ModMeta(){{
                        displayName = "disintegration";
                        name = "disintegration";
                        author = "zeutd";
                        main = "disintegration.DisintegrationJavaMod";
                        description = "A Mindustry Java mod.";
                        version = "0.0.1";
                        minGameVersion = "140";
                        java = true;
                    }})));
            Core.app.post(this::loadContent);*/
        });
    }
    @Override
    public void loadContent() {
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
