package disintegration.core;

import arc.ApplicationListener;
import arc.Core;
import arc.util.serialization.Json;
import arc.util.serialization.JsonValue;
import disintegration.DTVars;
import disintegration.type.SpaceStation;
import mindustry.Vars;
import mindustry.core.Control;
import mindustry.ctype.ContentType;
import mindustry.game.Saves;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.io.JsonIO;
import mindustry.type.Planet;
import mindustry.type.Sector;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

import static mindustry.Vars.control;

public class SpaceStationReader implements ApplicationListener {
    public void read() throws IOException {
        for (String s : DTVars.spaceStationFi.readString().split("/")) {
            Planet parent = Vars.content.planet(s);
            if(parent != null){
                String whiteSpace = Objects.equals(Core.bundle.get("spacestationwhitespace"), "true") ? " " : "";
                SpaceStation spaceStation = new SpaceStation(parent.name + "-spacestation", parent);
                spaceStation.localizedName = parent.localizedName + whiteSpace + Core.bundle.get("spacestation");
                DTVars.spaceStations.add(spaceStation);
                DTVars.spaceStationPlanets.add(parent);
                Sector sector = spaceStation.getSector(PlanetGrid.Ptile.empty);
                sector.save = control.saves.importSave(control.saves.getSectorFile(sector));
                sector.loadInfo();
            }
        }

    }
    @Override
    public void exit(){
        Writer writer = DTVars.spaceStationFi.writer(false);
        try {
            for (Planet p : DTVars.spaceStationPlanets) {
                if (p != null) {
                    writer.write(p.name);
                    writer.append('/');
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void update(){}
}
