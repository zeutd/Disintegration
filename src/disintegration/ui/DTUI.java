package disintegration.ui;

import arc.ApplicationListener;
import disintegration.ui.dialogs.SpaceStationBuildDialog;
import disintegration.ui.dialogs.SpaceStationDialog;

import static mindustry.Vars.ui;

public class DTUI implements ApplicationListener {
    public SpaceStationDialog spaceStation;
    public SpaceStationBuildDialog spaceStationBuild;

    public boolean added = false;

    @Override
    public void init(){
        spaceStation = new SpaceStationDialog();
        spaceStationBuild = new SpaceStationBuildDialog();
    }

    @Override
    public void update(){
        ui.planet.shown(() -> added = false);
        if(!added) {
            ui.planet.buttons.button("@planet", () -> {
                spaceStation.show();
            });
        }
        added = true;
    }
}
