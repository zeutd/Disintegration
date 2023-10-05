package disintegration.content;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.type.Item;

import static mindustry.content.Items.*;

public class DTItems {
    public static Item iridium, iron, steel, silver, concussionAlloy, spaceStationPanel;
    public static final Seq<Item>
            omurloItems = new Seq<>(),
            omurloOnlyItems = new Seq<>(),
            spaceStationItems = new Seq<>(),
            moddedItems = new Seq<>();
    public static void load() {
        iridium = new Item("iridium", Color.valueOf("BDCFE6")){{
             hardness = 4;
             cost = 2;
        }};
        iron = new Item("iron", Color.valueOf("8691c3")){{
            hardness = 1;
            cost = 1;
        }};

        steel = new Item("steel", Color.valueOf("A3A4B9")){{
            hardness = 1;
            cost = 1.2f;
        }};
        /*heavite = new Item("heavite", Color.valueOf("8FABD9")){{
            hardness = 7;
            cost = 3;
        }};
        ionicAlloy = new Item("ionic-alloy", Color.valueOf("84F491")){{
            hardness = 2;
            cost = 4;
        }};*/
        silver = new Item("silver", Color.valueOf("F2F2F2")){{
            hardness = 2;
            cost = 2;
        }};
        spaceStationPanel = new Item("space-station-panel", Color.valueOf("F5F6FF")){{
            hardness = 2;
            cost = 2;
        }};

        concussionAlloy = new Item("concussion-alloy", Color.valueOf("EFEAEA")){{
            hardness = 2;
            cost = 2;

        }};

        omurloItems.addAll(
                iridium, iron, silver, concussionAlloy, sand, silicon, graphite, coal, metaglass, lead, blastCompound
        );
        omurloOnlyItems.addAll(
                iridium, iron, silver, concussionAlloy
        );
        spaceStationItems.addAll(
                spaceStationPanel
        );
        moddedItems.addAll(omurloOnlyItems).addAll(spaceStationItems);
    }
}
