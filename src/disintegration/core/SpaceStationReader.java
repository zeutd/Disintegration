package disintegration.core;

import arc.ApplicationListener;
import arc.Core;
import disintegration.DTVars;
import disintegration.type.SpaceStation;
import mindustry.Vars;
import mindustry.type.Planet;

import java.io.IOException;
import java.io.Writer;

public class SpaceStationReader implements ApplicationListener {
    public void read(){
        for (String s : DTVars.SpaceStationFi.readString().split("/")) {
            Planet parent = Vars.content.planet(s);
            if(parent != null){
                SpaceStation spaceStation = new SpaceStation(parent.name + "-spacestation", parent);
                spaceStation.localizedName = parent.localizedName + " " + Core.bundle.get("spacestation");
                DTVars.spaceStations.add(spaceStation);
                DTVars.spaceStationPlanets.add(parent);
            }
        }
    }
    @Override
    public void exit(){
        Writer writer = DTVars.SpaceStationFi.writer(false);
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
}
