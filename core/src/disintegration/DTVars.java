package disintegration;

import arc.files.Fi;
import arc.files.ZipFi;
import arc.struct.Seq;
import arc.util.serialization.Jval;
import disintegration.core.*;
import disintegration.type.SpaceStation;
import disintegration.ui.DTUI;
import disintegration.util.DTUtil;
import mindustry.Vars;
import mindustry.type.Planet;

import java.util.Objects;

public class DTVars {
    public static String modName = "disintegration";
    public static int spaceStationRequirement = 10000;
    public static int spaceStationBaseRequirement = 2;
    public static boolean debugMode = true;

    public static DTRenderer renderer;
    public static DTUI DTUI;
    public static SpaceStationIO spaceStationIO;
    public static ExportIO exportIO;
    public static DTSaves saves;
    public static ExportHandler exportHandler;

    public static Fi modFile;
    public static Fi DTRoot = DTUtil.getFiChild(Vars.dataDirectory, modName + "/");
    public static Fi spaceStationFi = DTUtil.createFi(DTRoot, "space_stations");
    public static Fi exportFi = DTUtil.createFi(DTRoot, "export");

    public static Seq<SpaceStation> spaceStations = new Seq<>();
    public static Seq<Planet> spaceStationPlanets = new Seq<>();

    public static ZipFi modFi(){
        Seq<Fi> modFiles = Vars.modDirectory.findAll(f -> {
            Fi metaFile = null;
            try {
                Fi zip = f.isDirectory() ? f : new ZipFi(f);
                for(String name : new String[]{"mod.json", "mod.hjson", "plugin.json", "plugin.hjson"}){
                    if((metaFile = zip.child(name)).exists()){
                        break;
                    }
                }
                if (!metaFile.exists()) return false;
                String name = Jval.read(metaFile.readString()).getString("name");
                return Objects.equals(name, modName);
            }catch(Throwable ignored){
                return false;
            }
        });
        return new ZipFi(modFiles.first());
    }

    public static void init(){
        modFile = modFi();
    }
}
