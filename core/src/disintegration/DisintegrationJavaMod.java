package disintegration;

import arc.Events;
import arc.graphics.Color;
import disintegration.content.*;
import disintegration.core.*;
import disintegration.entities.DTGroups;
import disintegration.gen.entities.EntityRegistry;
import disintegration.graphics.DTShaders;
import disintegration.ui.DTUI;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.world.meta.Env;

import java.io.IOException;

import static arc.Core.app;

public class DisintegrationJavaMod extends Mod{
    public DisintegrationJavaMod(){
        DTVars.init();
        DTGroups.init();
        app.addListener(DTVars.saves = new DTSaves());
        app.addListener(DTVars.spaceStationIO = new SpaceStationIO());
        app.addListener(DTVars.exportHandler = new ExportHandler());
        app.addListener(DTVars.exportIO = new ExportIO());
        DTVars.exportHandler.init();
        Events.run(EventType.Trigger.update, DTGroups::update);
        Events.on(EventType.TurnEvent.class, __ -> DTVars.exportHandler.updateItem((int) Vars.turnDuration / 60));
        /*Events.run(EventType.Trigger.draw, DTVars.renderer3D.models::clear);
        Events.run(EventType.Trigger.postDraw, () -> {
            DTVars.renderer3D.cam.position.set(Core.camera.position, Core.camera.height);
            DTVars.renderer3D.render();
        });*/
    }

    @Override
    public void init(){
        app.addListener(DTVars.renderer = new DTRenderer());
        app.addListener(DTVars.DTUI = new DTUI());
        DTVars.DTUI.init();
        Events.run(EventType.Trigger.universeDraw, DTVars.renderer.spaceStation::drawExportLines);
        DTPlanets.init();
        Planets.sun.iconColor = Color.valueOf("ffebab");
        Vars.content.units().each(u -> {
            if(u.flying)u.envEnabled |= Env.space;
        });
        Vars.content.blocks().each(b -> {
            b.envDisabled &= ~Env.space;
            b.envEnabled |= Env.space;
        });
        Vars.content.planets().each(p -> p.parent == p.solarSystem, p -> {
            p.orbitRadius *= 0.5;
            p.orbitRadius += p.solarSystem.radius * 2f;
        });
        Vars.content.planets().each(p -> p == p.solarSystem, p -> {
            p.radius *= 2f;
            p.reloadMesh();
            p.clipRadius *= 2f;
        });
        DTPlanets.luna.orbitRadius *= 0.5f;
        try {
            DTVars.spaceStationIO.read();
            DTVars.exportIO.read();
        } catch (Throwable ignored) {

        }
    }
    @Override
    public void loadContent() {
        EntityRegistry.register();
        DTShaders.init();
        DTItems.load();
        DTLiquids.load();
        DTStatusEffects.load();
        DTBullets.load();
        DTUnitTypes.load();
        DTBlocks.load();
        DTLoadouts.load();
        DTPlanets.load();
        DTSectorPresets.load();
        OmurloTechTree.load();
        VanillaTechTree.load();
    }
}
