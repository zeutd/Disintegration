package disintegration.content;

import arc.graphics.Color;
import disintegration.type.maps.planet.CosiuazPlanetGenerator;
import disintegration.type.maps.planet.OmurloPlanetGenerator;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.type.Planet;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Env;

public class DTPlanets {
    public static Planet omurlo, cosiuaz;
    public static void load(){
        Planet sun = Planets.sun;
        omurlo = new Planet("omurlo", sun, 1f, 3){{
                generator = new OmurloPlanetGenerator();
                meshLoader = () -> new HexMesh(this, 6);
                cloudMeshLoader = () -> new MultiMesh(
                        new HexSkyMesh(this, 2, 0.15f, 0.14f, 5, Color.valueOf("e6e6fa").a(0.75f), 2, 0.42f, 1f, 0.43f),
                        new HexSkyMesh(this, 3, 0.6f, 0.15f, 5, Color.valueOf("f8f8ff").a(0.75f), 2, 0.42f, 1.2f, 0.45f),
                        new HexSkyMesh(this, 5, 0.1f, 0.16f, 4, Color.valueOf("e6f3f8").a(0.75f), 2, 0.42f, 1.5f, 0.44f)
                );
                alwaysUnlocked = true;
                landCloudColor = Color.valueOf("bbd9ff");
                atmosphereColor = Color.valueOf("051245");
                startSector = 17;
                atmosphereRadIn = 0.02f;
                atmosphereRadOut = 0.3f;
                tidalLock = true;
                defaultEnv = Env.terrestrial | Env.groundWater | Env.groundOil | Env.oxygen;
                orbitSpacing = 4f;
                totalRadius += 2.6f;
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

        //WIP
        cosiuaz = new Planet("cosiuaz", sun, 1f, 2){{
            generator = new CosiuazPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 5);
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 2, 0.15f, 0.14f, 5, Color.valueOf("eba768").a(0.75f), 2, 0.42f, 1f, 0.43f),
                    new HexSkyMesh(this, 3, 0.6f, 0.15f, 5, Color.valueOf("eea293").a(0.75f), 2, 0.42f, 1.2f, 0.45f)
            );
            alwaysUnlocked = true;
            landCloudColor = Color.valueOf("ed6542");
            atmosphereColor = Color.valueOf("f07218");
            defaultEnv = Env.scorching | Env.terrestrial;
            startSector = 10;
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;
            tidalLock = true;
            orbitSpacing = 2f;
            totalRadius += 10f;
            lightSrcTo = 0.5f;
            lightDstFrom = 0.2f;
            clearSectorOnLose = true;
            defaultCore = Blocks.coreBastion;
            iconColor = Color.valueOf("ff9266");
            allowWaves = true;
            allowWaveSimulation = true;
            allowSectorInvasion = true;
            allowLaunchSchematics = true;
            hiddenItems.addAll(Items.serpuloItems).removeAll(Items.erekirItems);

            updateLighting = false;

            defaultAttributes.set(Attribute.heat, 0.8f);

            ruleSetter = r -> {
                r.waveTeam = DTTeam.rebel;
                r.placeRangeCheck = false;
                r.showSpawns = true;
                r.fog = true;
                r.staticFog = true;
                r.lighting = false;
                r.coreDestroyClear = true;
                r.onlyDepositCore = true;
            };

            unlockedOnLand.add(Blocks.coreBastion);
        }};
    }
}
