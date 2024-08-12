package disintegration.content;

import arc.graphics.Color;
import arc.graphics.Mesh;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import disintegration.graphics.g3d.SphereMesh;
import disintegration.type.SpaceStation;
import disintegration.type.maps.planet.CaelpsePlanetGenerator;
import disintegration.type.maps.planet.CosiuazPlanetGenerator;
import disintegration.type.maps.planet.LunaPlanetGenerator;
import disintegration.type.maps.planet.OmurloPlanetGenerator;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.*;
import mindustry.type.Planet;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Env;

public class DTPlanets {
    static Planet sun = Planets.sun;

    public static Planet luna, omurlo, cosiuaz, caelpse, terminsi, twinCenter;

    public static final ObjectMap<Planet, Boolean> canSpaceStation = new ObjectMap<>();

    public static void init(){
        canSpaceStation.putAll(terminsi, true);
        for (Planet p : Vars.content.planets()){
            if (canSpaceStation.containsKey(p)) continue;
            boolean b = p.accessible && p.isLandable() && !(p instanceof SpaceStation);
            canSpaceStation.put(p, b);
        }
    }
    public static void load(){
        Planets.serpulo.hiddenItems.add(DTItems.nitride);
        luna = new Planet("luna", Planets.serpulo, 0.7f, 2){{
            generator = new LunaPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 5);
            alwaysUnlocked = true;
            landCloudColor = Color.valueOf("88aabb");
            atmosphereColor = Color.valueOf("122240");
            defaultEnv = Env.terrestrial;
            startSector = 10;
            hasAtmosphere = false;
            tidalLock = true;
            lightSrcTo = 0.5f;
            lightDstFrom = 0.2f;
            clearSectorOnLose = true;
            defaultCore = Blocks.coreBastion;
            iconColor = Color.valueOf("cceeff");
            hiddenItems.addAll(Items.erekirItems);

            updateLighting = false;
        }};
        twinCenter = new Planet("twin-center", sun, 0f){{
            hasAtmosphere = false;
            orbitSpacing = 0.5f;
        }};
        cosiuaz = new Planet("cosiuaz", twinCenter, 0.8f, 2){{
            minZoom = 0.65f;
            generator = new CosiuazPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 6);
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 1, 0.15f, 0.14f, 5, Color.valueOf("eb6748").a(0.75f), 2, 0.42f, 1f, 0.43f),
                    new HexSkyMesh(this, 3, 0.6f, 0.15f, 5, Color.valueOf("ee8253").a(0.75f), 2, 0.42f, 1.2f, 0.45f)
            );
            alwaysUnlocked = true;
            landCloudColor = Color.valueOf("bd7552");
            atmosphereColor = Color.valueOf("d06218");
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
            iconColor = Color.valueOf("dd5b24");
            allowWaves = true;
            allowWaveSimulation = true;
            allowSectorInvasion = false;
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

        caelpse = new Planet("caelpse", twinCenter, 0.5f, 2){{
            minZoom = 1.5f;
            generator = new CaelpsePlanetGenerator();
            meshLoader = () -> new HexMesh(this, 5);
            alwaysUnlocked = true;
            landCloudColor = Color.valueOf("fd5542");
            atmosphereColor = Color.valueOf("ff5218");
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
            iconColor = Color.valueOf("dd5b24");
            allowWaves = true;
            allowWaveSimulation = true;
            allowSectorInvasion = false;
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
        }
        @Override
        public float getOrbitAngle(){
            return Mathf.mod(cosiuaz.getOrbitAngle() + 180f, 360f);
        }};

        Vars.content.planets().each(p -> p.parent == p.solarSystem, p -> {
            p.orbitRadius += sun.orbitSpacing;
            twinCenter.orbitRadius = sun.orbitSpacing + sun.radius;
        });
        cosiuaz.orbitRadius += 0.5f;
        caelpse.orbitRadius += 0.5f;
        omurlo = new Planet("omurlo", sun, 1f, 3){{
                generator = new OmurloPlanetGenerator();
                /*Prov<OBJModel> modelLoader = () -> {
                    OBJModel model = DTUtil.loadObj("aaaaaa.obj").first();
                    model.transformation.scale(new Vec3(10, 10, 10));
                    return model;
                };*/
                meshLoader = () -> new HexMesh(this, 6);
                cloudMeshLoader = () -> new MultiMesh(
                        new HexSkyMesh(this, 4, 0.15f, 0.14f, 5, Color.valueOf("e6e6fa").a(0.75f), 2, 0.42f, 1f, 0.43f),
                        new HexSkyMesh(this, 3, 0.6f, 0.15f, 5, Color.valueOf("f8f8ff").a(0.75f), 2, 0.42f, 1.2f, 0.45f),
                        new HexSkyMesh(this, 5, 0.1f, 0.16f, 4, Color.valueOf("e3e9ff").a(0.75f), 2, 0.42f, 1.5f, 0.44f)
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
                itemWhitelist.addAll(DTItems.omurloItems);
                updateLighting = false;

                ruleSetter = r -> {
                    r.waveTeam = Team.blue;
                    r.placeRangeCheck = false;
                    r.attributes.clear();
                    r.showSpawns = false;
                };

                unlockedOnLand.add(DTBlocks.corePedestal);
            }};

        terminsi = new Planet("terminsi", sun, 2.7f){{
            bloom = true;
            accessible = true;
            alwaysUnlocked = true;

            hasAtmosphere = true;
            atmosphereRadIn = 0.05f;
            atmosphereRadOut = 3.1f;
            atmosphereColor = Color.valueOf("3941ff");
            landCloudColor = Color.valueOf("aedeff");
            iconColor = Color.valueOf("2871ff");

            meshLoader = () -> new SphereMesh(
                    this, 6,
                    5, 0.4, 1, 1.3, 1,
                    0.8f,
                    Color.valueOf("3f53ff"),
                    Color.valueOf("4661ff"),
                    Color.valueOf("4d6fff"),
                    Color.valueOf("517aff"),
                    Color.valueOf("5088ff"),
                    Color.valueOf("5599ff")
            );

            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 7, 0.35f, 0.02f, 6, Color.valueOf("93ccff").a(0.4f), 3, 0.09f, 1f, 0.3f),
                    new HexSkyMesh(this, 8, 0.7f, 0.03f, 6, Color.valueOf("62acff").a(0.4f), 3, 0.07f, 1.3f, 0.45f)
            );
        }};
    }
}
