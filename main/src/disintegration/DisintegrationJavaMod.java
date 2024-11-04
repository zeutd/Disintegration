package disintegration;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.struct.Seq;
import arclibrary.graphics.g3d.model.obj.OBJModel;
import arclibrary.graphics.g3d.render.GenericRenderer3D;
import disintegration.content.*;
import disintegration.core.*;
import disintegration.entities.DTGroups;
import disintegration.gen.entities.EntityRegistry;
import disintegration.graphics.DTCacheLayer;
import disintegration.graphics.DTShaders;
import disintegration.ui.DTUI;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.content.TechTree;
import mindustry.game.EventType;
import mindustry.graphics.MultiPacker;
import mindustry.mod.Mod;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.ui.dialogs.PlanetDialog;
import mindustry.world.meta.Env;
import multicraft.IOEntry;
import multicraft.MultiCrafter;
import multicraft.Recipe;

import java.util.Objects;

import static arc.Core.app;
import static arc.Core.graphics;
import static mindustry.Vars.renderer;
import static mindustry.Vars.state;

//Vars.content.each(c=>{if(c.minfo.mod!=null&&c.minfo.mod.name=="disintegration"&&c.localizedName==c.name)Log.info(c)})
//Vars.content.each(c=>{if(c.minfo.mod!=null&&c.minfo.mod.name=="disintegration"&&c instanceof UnlockableContent&&!c.unlocked())Log.info(c)})
public class DisintegrationJavaMod extends Mod{
    public static OBJModel test;
    public DisintegrationJavaMod(){
        DTVars.init();
        DTGroups.init();
        app.addListener(DTVars.spaceStationIO = new SpaceStationIO());
        app.addListener(DTVars.exportHandler = new ExportHandler());
        app.addListener(DTVars.exportIO = new ExportIO());
        DTVars.exportHandler.init();
        Events.run(EventType.Trigger.update, DTGroups::update);
        //Events.run(EventType.Trigger.postDraw, DTVars.loadRenderer::draw);
        Events.on(EventType.TurnEvent.class, __ -> DTVars.exportHandler.updateItem((int) Vars.turnDuration / 60));
        Events.on(EventType.WorldLoadEvent.class, __ -> DTBlocks.updateVisibility());
        /*Events.run(EventType.ContentInitEvent.class, () -> {
            content.setCurrentMod(mods.getMod(modName));
            DTBlocks.liquidCellPacker = new multicraft.MultiCrafter("liquid-cell-packer"){{
                size = 2;
                hasItems = true;
                hasLiquids = true;
                liquidCapacity = 200;
                resolvedRecipes = new Seq<>();
                requirements(Category.crafting, with());
            }};
            Vars.content.liquids().each(l -> {
                if(!l.hidden){
                    Item i = new Item(l.name + "-cell", l.color);
                    i.alwaysUnlocked = true;
                    String whiteSpace = Objects.equals(Core.bundle.get("whitespacebetween"), "true") ? " " : "";
                    i.localizedName = l.localizedName + whiteSpace + Core.bundle.get("cell");
                    ((MultiCrafter)DTBlocks.liquidCellPacker).resolvedRecipes.add(new Recipe(
                            new IOEntry(
                                    Seq.with(ItemStack.with(Items.graphite, 1)),
                                    Seq.with(LiquidStack.with(l, 100))
                            ),
                            new IOEntry(
                                    Seq.with(ItemStack.with(i, 1)),
                                    Seq.with()
                            ),
                            30)
                    );
                    DTItems.cells.put(l, i);
                }
            });
            DTBlocks.liquidCellPacker.init();
            content.setCurrentMod(null);
        });*/
    }

    @Override
    public void init(){
        DTBlocks.init();
        try {
            DTVars.spaceStationIO.read();
            DTVars.exportIO.read();
        } catch (Throwable ignored) {

        }
        Events.run(EventType.Trigger.preDraw, () -> {
            if(renderer.backgroundBuffer != null){
                int size = Math.max(graphics.getWidth(), graphics.getHeight());
                Vars.renderer.backgroundBuffer.begin(Color.clear);

                var params = state.rules.planetBackground;

                //override some values
                params.viewW = size;
                params.viewH = size;
                params.alwaysDrawAtmosphere = true;
                params.drawUi = false;

                Vars.renderer.planets.render(params);

                Vars.renderer.backgroundBuffer.end();
            }
        });
        app.addListener(DTVars.renderer = new DTRenderer());
        app.addListener(DTVars.DTUI = new DTUI());
        DTVars.renderer3D = new GenericRenderer3D();
        DTVars.renderer3D.init();
        DTVars.DTUI.init();
        Events.run(EventType.Trigger.universeDraw, DTVars.renderer.spaceStation::drawExportLines);
        //Events.run(EventType.Trigger.update, DTVars.loadRenderer::draw);
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
            p.radius *= 2.1f;
            p.reloadMesh();
            p.clipRadius *= 2f;
        });
        Vars.content.planets().each(p -> {
            p.orbitTime /= 10;
            p.rotateTime /= 10;
        });
        DTPlanets.luna.orbitRadius *= 0.5f;

        Vars.content.planets().each(p -> p.solarSystem == Planets.sun, p -> {
            p.hiddenItems.remove(DTItems.spaceStationPanel);
        });
        if(DTVars.debugMode) TechTree.all.each(c -> {
            c.requiresUnlock = false;
            c.requirements = ItemStack.with();
            c.objectives.clear();
        });
/*        DTItems.cells.each((l, i) -> {
            TextureRegion base = Core.atlas.find("disintegration-cell");
            TextureRegion liq = Core.atlas.find("disintegration-cell-liquid");
            Pixmap liqPixmap = liq.texture.getTextureData().getPixmap().crop(liq.getX(), liq.getY(), liq.width, liq.height);
            Pixmap basePixmap = base.texture.getTextureData().getPixmap().crop(base.getX(), base.getY(), base.width, base.height);
            for(int x = 0; x < liqPixmap.width; x++){
                for(int y = 0; y < liqPixmap.height; y++) {
                    if (!liqPixmap.empty(x, y)) {
                        liqPixmap.set(x, y, l.color);
                    }
                    if(!basePixmap.empty(x, y)){
                        liqPixmap.set(x, y, basePixmap.get(x, y));
                    }
                }
            }
            i.fullIcon = i.uiIcon = new TextureRegion(new Texture(liqPixmap));
        });*/
    }
    @Override
    public void loadContent() {
        DTShaders.init();
        DTCacheLayer.init();
        EntityRegistry.register();
        DTSounds.load();
        DTItems.load();
        DTStatusEffects.load();
        DTLiquids.load();
        DTBullets.load();
        DTUnitTypes.load();
        DTBlocks.load();
        DTLoadouts.load();
        DTPlanets.load();
        DTSectorPresets.load();
        OmurloTechTree.load();
        VanillaTechTree.load();
        SpaceStationTechTree.load();
        Vars.content.liquids().each(l -> {
            if(!l.hidden){
                Item i = new Item(l.name + "-cell", l.color){

                    @Override
                    public void createIcons(MultiPacker packer){
                        super.createIcons(packer);
                        Pixmap liqPixmap = new Pixmap(DTVars.modFile.child("sprites").child("items").child("cell-liquid.png"));
                        Pixmap basePixmap = new Pixmap(DTVars.modFile.child("sprites").child("items").child("cell.png"));
                        for(int x = 0; x < liqPixmap.width; x++){
                            for(int y = 0; y < liqPixmap.height; y++) {
                                if (!liqPixmap.empty(x, y)) {
                                    liqPixmap.set(x, y, l.color);
                                }
                                if(!basePixmap.empty(x, y)){
                                    liqPixmap.set(x, y, basePixmap.get(x, y));
                                }
                            }
                        }
                        packer.add(MultiPacker.PageType.main, name, liqPixmap);
                    }
                };
                i.alwaysUnlocked = true;
                String whiteSpace = Objects.equals(Core.bundle.get("whitespacebetween"), "true") ? " " : "";
                i.localizedName = l.localizedName + whiteSpace + Core.bundle.get("cell");
                ((MultiCrafter)DTBlocks.liquidCellPacker).resolvedRecipes.add(new Recipe(
                        new IOEntry(
                                Seq.with(ItemStack.with(Items.graphite, 1)),
                                Seq.with(LiquidStack.with(l, 10))
                        ),
                        new IOEntry(
                                Seq.with(ItemStack.with(i, 1)),
                                Seq.with()
                        ),
                        30)
                );
                ((MultiCrafter)DTBlocks.liquidCellUnpacker).resolvedRecipes.add(new Recipe(
                        new IOEntry(
                                Seq.with(ItemStack.with(i, 1)),
                                Seq.with()
                        ),
                        new IOEntry(
                                Seq.with(),
                                Seq.with(LiquidStack.with(l, 10))
                        ),
                        30)
                );
                DTItems.cells.put(l, i);
            }
        });
    }
}
