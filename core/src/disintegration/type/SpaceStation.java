package disintegration.type;

import disintegration.content.DTBlocks;
import disintegration.type.maps.planet.SpaceStationGenerator;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.world.meta.Env;

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
        orbitRadius = parent.radius + 0.8f;
        alwaysUnlocked = true;
        defaultEnv = Env.space;
        icon = "commandRally";
        defaultCore = DTBlocks.spaceStationCore;
    }
}
