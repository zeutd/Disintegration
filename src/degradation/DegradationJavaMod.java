package degradation;

import arc.util.*;
import degradation.content.modBlocks;
import degradation.content.modBullets;
import degradation.content.modItems;
import degradation.content.modUnits;
import mindustry.mod.*;

public class DegradationJavaMod extends Mod{
    public static modItems ModItems;
    public static modUnits ModUnits;
    public static modBlocks ModBlocks;
    public static modBullets ModBullets;
    public DegradationJavaMod(){
        Log.info("Loaded DegradationMod constructor.");
    }
    @Override
    public void loadContent() {
        Log.info("Loading degradation content.");
        ModItems = new modItems();
        ModItems.load();
        ModUnits = new modUnits();
        ModItems.load();
        ModBlocks = new modBlocks();
        ModBlocks.load();
        ModBullets = new modBullets();
        ModBullets.load();
    }
}
