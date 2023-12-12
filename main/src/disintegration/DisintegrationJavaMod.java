package disintegration;

import arc.Events;
import disintegration.content.*;
import disintegration.entities.DTGroups;
import disintegration.gen.entities.EntityRegistry;
import disintegration.graphics.DTShaders;
import mindustry.game.EventType;
import mindustry.mod.Mod;

import static arc.Core.app;

public class DisintegrationJavaMod extends Mod{
    public DisintegrationJavaMod(){
        app.post(DTGroups::init);
        Events.run(EventType.Trigger.update, DTGroups::update);
    }

    @Override
    public void init(){
        app.post(DTVars::init);
        app.post(DTShaders::init);
        app.addListener(DTVars.DTUI);
        app.addListener(DTVars.spaceStationReader);
    }
    @Override
    public void loadContent() {
        EntityRegistry.register();
        DTItems.load();
        DTLiquids.load();
        DTStatusEffects.load();
        DTBullets.load();
        DTUnitTypes.load();
        DTBlocks.load();
        DTLoadouts.load();
        DTPlanets.load();
        app.post(DTVars.spaceStationReader::read);
        DTSectorPresets.load();
    }
}
