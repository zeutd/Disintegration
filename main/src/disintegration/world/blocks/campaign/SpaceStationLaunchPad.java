package disintegration.world.blocks.campaign;

import arc.Core;
import arc.Graphics;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.util.Log;
import disintegration.content.DTFx;
import disintegration.content.DTItems;
import disintegration.content.DTPlanets;
import disintegration.type.SpaceStation;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.ctype.ContentType;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.Planet;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.PlanetDialog;
import mindustry.world.Block;
import mindustry.world.meta.BlockFlag;

import java.util.Objects;

import static disintegration.DTVars.*;
import static mindustry.Vars.*;

public class SpaceStationLaunchPad extends Block {
    public float launchTime = 5f;
    public Sound launchSound = Sounds.none;

    public TextureRegion lightRegion;
    public TextureRegion podRegion;
    public Color lightColor = Color.valueOf("eab678");

    public static Planet selectedPlanet;

    public SpaceStationLaunchPad(String name) {
        super(name);
        hasItems = true;
        solid = true;
        update = true;
        configurable = true;
        flags = EnumSet.of(BlockFlag.launchPad);
        itemCapacity = spaceStationRequirement;
    }

    public static boolean selectable(Planet planet) {
        if (PlanetDialog.debugSelect) return true;
        return DTPlanets.canSpaceStation(planet);
    }

    @Override
    public void load() {
        super.load();
        region = Core.atlas.find(name);
        lightRegion = Core.atlas.find(name + "-light");
        podRegion = Core.atlas.find("disintegration-space-station-launchpod");
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("items", entity -> new Bar(() -> Core.bundle.format("bar.items", entity.items.total()), () -> Pal.items, () -> (float) entity.items.total() / itemCapacity));

        addBar("progress",
                (SpaceStationLaunchPadBuild build) -> new Bar(() -> Core.bundle.get("bar.launchcooldown"),
                        () -> Pal.ammo,
                        () -> Mathf.clamp((float) build.items.get(DTItems.spaceStationPanel) / spaceStationRequirement)));
    }

    @Override
    public boolean outputsItems() {
        return false;
    }

    public class SpaceStationLaunchPadBuild extends Building {
        public float launchCounter;

        public boolean canLaunch;

        @Override
        public Graphics.Cursor getCursor() {
            return !state.isCampaign() || net.client() ? Graphics.Cursor.SystemCursor.arrow : super.getCursor();
        }

        @Override
        public boolean shouldConsume() {
            return items.get(DTItems.spaceStationPanel) >= spaceStationRequirement;
        }

        @Override
        public double sense(LAccess sensor) {
            if (sensor == LAccess.progress)
                return Mathf.clamp((float) items.get(DTItems.spaceStationPanel) / spaceStationRequirement);
            return super.sense(sensor);
        }

        @Override
        public void draw() {
            super.draw();

            if (!state.isCampaign()) return;
            float progress = (float) items.get(DTItems.spaceStationPanel) / itemCapacity;
            /*if(lightRegion.found()){
                Draw.color(lightColor);
                int steps = 3;
                float step = 1f;

                for(int i = 0; i < 4; i++){
                    for(int j = 0; j < steps; j++){
                        float alpha = Mathf.curve(progress, (float)j / steps, (j+1f) / steps);
                        float offset = -(j - 1f) * step;

                        Draw.color(Pal.metalGrayDark, lightColor, alpha);
                        Draw.rect(lightRegion, x + Geometry.d8edge(i).x * offset, y + Geometry.d8edge(i).y * offset, i * 90);
                    }
                }

                Draw.reset();
            }*/
            Draw.alpha(progress);
            Draw.rect(podRegion, x, y);

            Draw.reset();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return item == DTItems.spaceStationPanel && items.get(DTItems.spaceStationPanel) < itemCapacity;
        }

        @Override
        public void updateTile() {
            Log.info(spaceStationPlanets);
            if (!state.isCampaign() || selectedPlanet == null) return;
            canLaunch = !spaceStationPlanets.contains(selectedPlanet);

            //increment launchCounter then launch when full and base conditions are met
            if (items.get(DTItems.spaceStationPanel) >= itemCapacity && canLaunch) {
                if ((launchCounter += edelta()) >= launchTime) {
                    launchCounter = 0;
                    launchSound.at(x, y);
                    Fx.launchPod.at(this);
                    DTFx.spaceStationLaunchPayload.at(this);
                    items.clear();
                    Effect.shake(3f, 3f, this);
                    content.setCurrentMod(mods.getMod(modName));
                    SpaceStation.create(selectedPlanet);
                    content.setCurrentMod(null);
                }
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            if (!state.isCampaign() || net.client()) {
                deselect();
                return;
            }
            for (int i = 0; i < content.planets().size; i++) {
                Planet planet = content.planets().get(i);
                if (selectable(planet)) {
                    table.button(planet.localizedName,
                                    Icon.icons.get(planet.icon + "Small",
                                            Icon.icons.get(planet.icon, Icon.commandRallySmall)),
                                    Styles.flatTogglet, () -> {
                                        selectedPlanet = planet;
                                        deselect();
                                    })
                            .width(190).height(40).growX()
                            .update(bb -> bb.setChecked(selectedPlanet == planet))
                            .with(w -> w.marginLeft(10f))
                            .disabled(t -> spaceStationPlanets.contains(planet))
                            .get().getChildren().get(1)
                            .setColor(planet.iconColor);
                    table.row();
                }
            }
        }
    }
}
