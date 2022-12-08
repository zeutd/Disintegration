package disintegration.content;

import arc.graphics.Color;
import disintegration.maps.planet.OmurloPlanetGenerator;
import disintegration.maps.planet.SpaceStationGenerator;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.world.meta.Env;

public class DTPlanets {
    public static Planet omurlo;
    public static void load(){
        Planet sun = Planets.sun;
        sun.alwaysUnlocked = true;
        sun.hasAtmosphere = false;
        sun.updateLighting = false;
        sun.accessible = true;
        sun.sectors.add(new Sector(sun, PlanetGrid.Ptile.empty));
        sun.defaultEnv = Env.space;
        sun.generator = new SpaceStationGenerator();
        sun.hiddenItems.removeAll(DTItems.spaceStationItems);

        Planets.erekir.hiddenItems.addAll(DTItems.moddedItems);
        Planets.serpulo.hiddenItems.addAll(DTItems.moddedItems);
        omurlo = new Planet("omurlo", sun, 1f, 2){{
                generator = new OmurloPlanetGenerator();
                meshLoader = () -> new HexMesh(this, 5);
                cloudMeshLoader = () -> new MultiMesh(
                        new HexSkyMesh(this, 2, 0.15f, 0.14f, 5, Color.valueOf("e6e6fa").a(0.75f), 2, 0.42f, 1f, 0.43f),
                        new HexSkyMesh(this, 3, 0.6f, 0.15f, 5, Color.valueOf("f8f8ff").a(0.75f), 2, 0.42f, 1.2f, 0.45f)
                );
                alwaysUnlocked = true;
                landCloudColor = Color.valueOf("ed6542");
                atmosphereColor = Color.valueOf("f0f0ff");
                startSector = 17;
                atmosphereRadIn = 0.02f;
                atmosphereRadOut = 0.3f;
                tidalLock = true;
                orbitSpacing = 4f;
                totalRadius += 2.6f;
                lightSrcTo = 0.5f;
                lightDstFrom = 0.2f;
                clearSectorOnLose = true;
                hiddenItems.addAll(Items.erekirOnlyItems).addAll(Items.serpuloItems).removeAll(DTItems.omurloItems);
                updateLighting = false;

                ruleSetter = r -> {
                    r.waveTeam = Team.blue;
                    r.placeRangeCheck = false;
                    r.attributes.clear();
                    r.showSpawns = false;
                };

                unlockedOnLand.add(DTBlocks.corePedestal);
            }};
    }
}
