package disintegration.ui.dialogs;

import arc.Core;
import arc.scene.ui.layout.Table;
import disintegration.DTVars;
import disintegration.type.SpaceStation;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.PlanetDialog;

import static arc.Core.bundle;
import static mindustry.Vars.content;
import static mindustry.Vars.ui;

public class SpaceStationDialog extends BaseDialog {

    public Planet selectedPlanet;

    public SpaceStationDialog() {
        super(bundle.get("spacestation", "Space Stations Menu"));
        addCloseButton();

        shown(this::setup);
    }

    boolean selectable(Planet planet){
        return (((planet.alwaysUnlocked && planet.isLandable()) || planet.sectors.contains(Sector::hasBase)) && !(planet instanceof SpaceStation)) || PlanetDialog.debugSelect;
    }

    void setup(){
        Table tableAdd = new Table(st -> {
            st.button(Icon.add, () -> {
                DTVars.DTUI.spaceStationBuild.show();
            });
        });

        Table tableSpaceStation = new Table(st -> {
            st.image(Core.atlas.find("disintegration-spacestation")).size(300, 400).bottom();
            for (int i = 0; i < 1; i++) {
                st.row();
            }
            st.button(Icon.trash, () -> ui.showConfirm("@confirm", "@disintegration.clearspacestation.confirm", () -> {
                SpaceStation spacestationi = DTVars.spaceStations.find(s -> s.parent == selectedPlanet);
                DTVars.spaceStations.remove(spacestationi);
                DTVars.spaceStationPlanets.remove(selectedPlanet);
                spacestationi.accessible = false;
                spacestationi.visible = false;
                spacestationi.sectors.clear();
                Vars.ui.planet.show();
            })).size(100, 50);
        });

        cont.clear();
        cont.add(
            new Table(Tex.pane, t -> {
                t.margin(4f);
                for(int i = 0; i < content.planets().size; i++){
                    Planet planet = content.planets().get(i);
                    if(selectable(planet)){
                        t.button(planet.localizedName, Icon.icons.get(planet.icon + "Small", Icon.icons.get(planet.icon, Icon.commandRallySmall)), Styles.flatTogglet, () -> {
                            selectedPlanet = planet;
                        }).width(190).height(40).growX().update(bb -> bb.setChecked(selectedPlanet == planet)).with(w -> w.marginLeft(10f)).get().getChildren().get(1).setColor(planet.iconColor);
                        t.row();
                    }
                }
            }),
            new Table(Tex.button, t -> {

                t.margin(12f);
                t.row();
                t.add(
                    new Table(pt -> {
                        pt.defaults().size(300, 400);
                        pt.stack(
                                tableAdd,
                                tableSpaceStation
                        );
                        pt.update(() -> {
                            if (DTVars.spaceStationPlanets.contains(selectedPlanet)){
                                tableSpaceStation.visible = true;
                                tableAdd.visible = false;
                            }
                            else {
                                tableSpaceStation.visible = false;
                                tableAdd.visible = true;
                            }
                        });
                    })
                );
                t.setSize(300, 400);
            }).top()
        );
    }
}
