package disintegration.content;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.type.Item;

import static mindustry.content.Items.*;

public class DTItems {
    public static Item
            spaceStationPanel,
            nitride,
            iridium, iron, steel, silver, magnetismAlloy, conductionAlloy,
            nickel, lithium, siliconCrystal, obsidian, tantalum, tantalumTungstenAlloy;
    public static final Seq<Item>
            omurloItems = new Seq<>(),
            twinItems = new Seq<>();

    public static void load() {
        spaceStationPanel = new Item("space-station-panel", Color.valueOf("F5F6FF")) {{
            hardness = 2;
            cost = 1;
        }};
        nitride = new Item("nitride", Color.valueOf("9a9dbf")) {{
            hardness = 2;
            cost = 2;
        }};
        iridium = new Item("iridium", Color.valueOf("BDCFE6")) {{
            hardness = 4;
            cost = 2;
        }};
        iron = new Item("iron", Color.valueOf("8691b4")) {{
            hardness = 1;
            cost = 1;
        }};

        steel = new Item("steel", Color.valueOf("A3A4B9")) {{
            hardness = 2;
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
        silver = new Item("silver", Color.valueOf("E0E8F3")) {{
            hardness = 3;
            cost = 2;
        }};

        magnetismAlloy = new Item("magnetism-alloy", Color.valueOf("DED4D4")) {{
            hardness = 2;
            cost = 2;

        }};

        conductionAlloy = new Item("conduction-alloy", Color.valueOf("4D536E")) {{
            hardness = 2;
            cost = 3;

        }};

        nickel = new Item("nickel", Color.valueOf("c7b784")){{
            hardness = 1;
            cost = 0.5f;
        }};
        lithium = new Item("lithium", Color.valueOf("d5d5d5")){{
            cost = 1;
            hardness = 1;
            flammability = 1f;
            explosiveness = 0.5f;
        }};
        siliconCrystal = new Item("silicon-crystal", Color.valueOf("dfdfe8")){{
            hardness = 2;
            cost = 1f;
        }};

        tantalum = new Item("tantalum", Color.valueOf("B6B6D4")){{
            hardness = 3;
            cost = 2f;
        }};

        obsidian = new Item("obsidian", Color.valueOf("4E4E79")){{
            hardness = 2;
            cost = 1f;
        }};

        tantalumTungstenAlloy = new Item("tantalum-tungsten-alloy", Color.valueOf("9696C3")){{
            hardness = 4;
            cost = 3f;
        }};

        omurloItems.addAll(
                iridium, iron, silver, magnetismAlloy, conductionAlloy, sand, silicon, surgeAlloy, graphite, coal, metaglass, lead, blastCompound, steel
        );
    }
}
