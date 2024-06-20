package disintegration.ui;

import arc.ApplicationListener;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import disintegration.type.SpaceStation;
import disintegration.ui.dialogs.ExportOverviewDialog;
import mindustry.gen.Icon;
import mindustry.type.Planet;

import static disintegration.DTVars.spaceStationPlanets;
import static disintegration.DTVars.spaceStations;
import static mindustry.Vars.ui;

public class DTUI implements ApplicationListener {
    public ExportOverviewDialog exportOverview;
    /*public SpaceStationDialog spaceStation;
    public SpaceStationBuildDialog spaceStationBuild;

    public boolean added = false;

    @Override
    public void init(){
        spaceStation = new SpaceStationDialog();
        spaceStationBuild = new SpaceStationBuildDialog();
    }*/
    @Override
    public void init(){
        exportOverview = new ExportOverviewDialog();
    }
    public boolean added = false;
    Planet p;

    @Override
    public void update() {
        if (ui.planet != null) {
            ui.planet.shown(() -> {
                added = false;
            });
            p = ui.planet.state.planet;
            if (!added) {
                ui.planet.add(new Table(t -> {
                    t.button(Icon.fileText, () -> {
                        exportOverview.show();
                    }).expandX();
                    t.row();
                    t.button(Icon.trash, () -> ui.showConfirm("@confirm", "@disintegration.clearspacestation.confirm", () -> {
                        spaceStations.remove((SpaceStation) p);
                        spaceStationPlanets.remove(p.parent);
                        p.accessible = false;
                        p.visible = false;
                        p.sectors.clear();
                        ui.planet.show();
                    })).visible(() -> p instanceof SpaceStation).expandX();
                })).width(0);
            }
            added = true;
        }
    }
}
