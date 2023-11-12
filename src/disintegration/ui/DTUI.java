package disintegration.ui;

import arc.ApplicationListener;
import arc.scene.ui.Button;
import disintegration.type.SpaceStation;
import mindustry.gen.Icon;
import mindustry.type.Planet;

import static disintegration.DTVars.spaceStationPlanets;
import static disintegration.DTVars.spaceStations;
import static mindustry.Vars.ui;

public class DTUI implements ApplicationListener {
    /*public SpaceStationDialog spaceStation;
    public SpaceStationBuildDialog spaceStationBuild;

    public boolean added = false;

    @Override
    public void init(){
        spaceStation = new SpaceStationDialog();
        spaceStationBuild = new SpaceStationBuildDialog();
    }*/
    public boolean added = false;
    Button removeButton;
    Planet p;
    @Override
    public void update(){
        ui.planet.shown(() -> {
            added = false;
        });
        if (p != ui.planet.state.planet) {
            p = ui.planet.state.planet;
            if(removeButton != null)ui.planet.buttons.removeChild(removeButton);
            if (p instanceof SpaceStation && !added) {
                removeButton = ui.planet.buttons.button(Icon.trash, () -> ui.showConfirm("@confirm", "@disintegration.clearspacestation.confirm", () -> {
                    spaceStations.remove((SpaceStation) p);
                    spaceStationPlanets.remove(p.parent);
                    p.accessible = false;
                    p.visible = false;
                    p.sectors.clear();
                    ui.planet.show();
                    added = true;
                })).get();
            } else {
                added = false;
            }
        }
        p = ui.planet.state.planet;
    }
}
