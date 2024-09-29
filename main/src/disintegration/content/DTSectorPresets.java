package disintegration.content;

import mindustry.type.SectorPreset;

import static disintegration.content.DTPlanets.omurlo;

public class DTSectorPresets {
    public static SectorPreset landingArea, geothermalGlacier, algaeFeild;
    public static void load() {
        landingArea = new SectorPreset("landingArea", omurlo, 17){{
            alwaysUnlocked = true;
            captureWave = 15;
            difficulty = 0;
            overrideLaunchDefaults = true;
            noLighting = true;
            startWaveTimeMultiplier = 3f;
        }};
        geothermalGlacier = new SectorPreset("geothermalGlacier", omurlo, 209){{
            captureWave = 30;
            difficulty = 1;
            overrideLaunchDefaults = true;
            noLighting = true;
            startWaveTimeMultiplier = 2f;
        }};
        algaeFeild = new SectorPreset("algaeField", omurlo, 99){{
            captureWave = 30;
            difficulty = 1;
            overrideLaunchDefaults = true;
            noLighting = true;
            startWaveTimeMultiplier = 2f;
        }};
    }
}