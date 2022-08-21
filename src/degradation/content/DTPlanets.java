package degradation.content;

import arc.graphics.Color;
import degradation.maps.planet.OmurloPlanetGenerator;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.type.Planet;
import mindustry.world.meta.Env;

public class DTPlanets {
    public static Planet omurlo;
    public static void load(){
        omurlo = new Planet("omurlo", Planets.sun, 1f, 2){{
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
                hiddenItems.addAll(DTItems.omurloItems).removeAll(Items.serpuloItems);
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
