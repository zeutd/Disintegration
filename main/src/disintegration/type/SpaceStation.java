package disintegration.type;

import arc.Core;
import arc.files.Fi;
import arc.util.Log;
import disintegration.DTVars;
import disintegration.content.DTBlocks;
import disintegration.type.maps.planet.SpaceStationGenerator;
import mindustry.Vars;
import mindustry.game.Saves;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.io.SaveIO;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.world.meta.Env;

import java.util.Objects;

import static disintegration.DTVars.spaceStationPlanets;
import static disintegration.DTVars.spaceStations;
import static mindustry.Vars.control;

public class SpaceStation extends Planet {
    public SpaceStation(String name, Planet parent) {
        super(name, parent, 0.05f);
        generator = new SpaceStationGenerator();
        mesh = new HexMesh(this, 0);
        sectors.add(new Sector(this, PlanetGrid.Ptile.empty));
        hasAtmosphere = false;
        updateLighting = false;
        drawOrbit = true;
        accessible = true;
        clipRadius = 2;
        orbitRadius = parent.radius + 1f;
        alwaysUnlocked = true;
        defaultEnv = Env.space;
        allowWaves = false;
        icon = "commandRally";
        defaultCore = DTBlocks.spaceStationCore;
    }

    public static SpaceStation create(Planet parent){
        Planet s = Vars.content.planet("disintegration-" + parent.name + "-space-station");
        if (s != null) {
            s.sectors.add(new Sector(s, PlanetGrid.Ptile.empty));
            s.accessible = true;
            s.visible = true;
            spaceStations.add((SpaceStation) s);
            spaceStationPlanets.add(s.parent);
            return (SpaceStation) s;
        }
        String whiteSpace = Objects.equals(Core.bundle.get("whitespacebetween"), "true") ? " " : "";
        SpaceStation spaceStation = new SpaceStation(parent.name + "-space-station", parent);
        spaceStation.localizedName = parent.localizedName + whiteSpace + Core.bundle.get("spacestation");
        DTVars.spaceStations.add(spaceStation);
        DTVars.spaceStationPlanets.add(parent);
        Sector sector = spaceStation.getSector(PlanetGrid.Ptile.empty);
        try {
            Fi f = control.saves.getSectorFile(sector);
            if (f.exists()) {
                Saves.SaveSlot slot = control.saves.new SaveSlot(f);
                slot.meta = SaveIO.getMeta(f);
                sector.save = slot;
                slot.setAutosave(true);
            }
        } catch (Exception e) {
            Log.err(e);
        }
        sector.loadInfo();
        sector.info.wasCaptured = true;
        sector.info.spawnPosition = 0;
        return spaceStation;
    }

    public static void remove(Planet p) {
        spaceStations.remove((SpaceStation) p);
        spaceStationPlanets.remove(p.parent);
        p.accessible = false;
        p.visible = false;
        p.sectors.clear();
    }
}
