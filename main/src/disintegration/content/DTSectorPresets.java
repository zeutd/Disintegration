package disintegration.content;

import mindustry.type.SectorPreset;

import static disintegration.content.DTPlanets.omurlo;

public class DTSectorPresets {
    public static SectorPreset landingArea;
    public static void load() {
        landingArea = new SectorPreset("landingArea", omurlo, 17){{
            alwaysUnlocked = true;
            addStartingItems = true;
            captureWave = 11;
            difficulty = 1;
            overrideLaunchDefaults = true;
            noLighting = true;
            startWaveTimeMultiplier = 3f;
        }};
    }
}