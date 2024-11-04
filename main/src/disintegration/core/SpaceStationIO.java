package disintegration.core;

import arc.ApplicationListener;
import arc.Core;
import arc.files.Fi;
import arc.util.Log;
import disintegration.DTVars;
import disintegration.type.SpaceStation;
import mindustry.Vars;
import mindustry.game.Saves;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.io.SaveIO;
import mindustry.type.Planet;
import mindustry.type.Sector;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

import static disintegration.DTVars.modName;
import static mindustry.Vars.*;

public class SpaceStationIO implements ApplicationListener {
    public void read() throws IOException {
        content.setCurrentMod(mods.getMod(modName));
        for (String s : DTVars.spaceStationFi.readString().split("/")) {
            SpaceStation ss = SpaceStation.create(content.planet(s));
            ss.init();
        }
        content.setCurrentMod(null);
    }

    @Override
    public void exit() {
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
        } catch (IOException ignored) {

        }
    }
}
