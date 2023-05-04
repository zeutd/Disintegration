package disintegration;

import arc.files.Fi;
import arc.struct.Seq;
import disintegration.core.SpaceStationReader;
import disintegration.type.SpaceStation;
import disintegration.ui.DTUI;
import disintegration.util.DTUtil;
import mindustry.Vars;
import mindustry.type.Planet;

public class DTVars {
    public static float temperaturePercent = 15f;
    public static int spaceStationRequire = 10000;
    public static boolean debugMode = true;

    public static DTUI DTUI = new DTUI();
    public static SpaceStationReader spaceStationReader = new SpaceStationReader();

    public static Fi DTRoot = DTUtil.getFiChild(Vars.dataDirectory, "disintegration/");
    public static Fi SpaceStationFi = DTUtil.createFi(DTRoot, "spaceStations.txt");

    public static Seq<SpaceStation> spaceStations = new Seq<>();
    public static Seq<Planet> spaceStationPlanets = new Seq<>();
}
