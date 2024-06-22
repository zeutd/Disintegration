package disintegration;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g3d.Camera3D;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Log;
import arclibrary.graphics.g3d.model.obj.OBJModel;
import arclibrary.graphics.g3d.render.GenericRenderer3D;
import disintegration.content.*;
import disintegration.core.*;
import disintegration.entities.DTGroups;
import disintegration.gen.entities.EntityRegistry;
import disintegration.graphics.DTShaders;
import disintegration.ui.DTUI;
import disintegration.util.DTUtil;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.game.EventType;
import mindustry.graphics.Layer;
import mindustry.mod.Mod;
import mindustry.world.meta.Env;

import java.io.IOException;

import static arc.Core.app;

public class DisintegrationJavaMod extends Mod{
    public static OBJModel test;
    public DisintegrationJavaMod(){
        DTVars.init();
        DTGroups.init();
        app.addListener(DTVars.saves = new DTSaves());
        app.addListener(DTVars.spaceStationIO = new SpaceStationIO());
        app.addListener(DTVars.exportHandler = new ExportHandler());
        app.addListener(DTVars.exportIO = new ExportIO());
        DTVars.exportHandler.init();
        Events.run(EventType.Trigger.update, DTGroups::update);
        //Events.run(EventType.Trigger.postDraw, DTVars.loadRenderer::draw);
        Events.on(EventType.TurnEvent.class, __ -> DTVars.exportHandler.updateItem((int) Vars.turnDuration / 60));
    }

    @Override
    public void init(){
        app.addListener(DTVars.renderer = new DTRenderer());
        app.addListener(DTVars.DTUI = new DTUI());
        DTVars.renderer3D = new GenericRenderer3D();
        DTVars.renderer3D.init();
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
            p.orbitRadius *= 0.5f;
            p.orbitRadius += p.solarSystem.radius * 2f;
        });
        Vars.content.planets().each(p -> p == p.solarSystem, p -> {
            p.radius *= 2f;
            p.reloadMesh();
            p.clipRadius *= 2f;
        });
        /*Vars.content.planets().each(p -> {
            p.orbitTime /= 20000;
        });*/
        DTPlanets.luna.orbitRadius *= 0.5f;
        try {
            DTVars.spaceStationIO.read();
            DTVars.exportIO.read();
        } catch (Throwable ignored) {

        }
        /*test = DTUtil.loadObj("aaaaaa.obj").first();
        Events.run(EventType.Trigger.preDraw, DTVars.renderer3D.models::clear);
        Events.run(EventType.Trigger.drawOver, () -> {
            Draw.draw(Layer.max, () -> {
                Camera3D cam = DTVars.renderer3D.cam;
                cam.far = 0.01f;
                cam.near = 0.005f;
                cam.fov = 42f;
                cam.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
                cam.position.set(Core.camera.position.x, Core.camera.height / 2f / Mathf.tan(cam.fov / 2f * Mathf.degRad, 1f, 1f), -Core.camera.position.y);
                cam.update();

                DTVars.renderer3D.models.add(test);
                DTVars.renderer3D.render();
            });
        });*/
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
        DTSectorPresets.load();
        OmurloTechTree.load();
        VanillaTechTree.load();
    }
}
