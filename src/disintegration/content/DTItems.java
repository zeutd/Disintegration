package disintegration.content;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.type.Item;

import static mindustry.content.Items.*;

public class DTItems {
    public static Item iridium, iron, heavite, ionicAlloy, silver, concussionAlloy;
    public static final Seq<Item> omurloItems = new Seq<>();
    public static void load() {
        iridium = new Item("iridium", Color.valueOf("00ffff")){{
             hardness = 4;
             cost = 2;
        }};
        iron = new Item("iron", Color.valueOf("8691c3")){{
            hardness = 1;
            cost = 1;
        }};
        heavite = new Item("heavite", Color.valueOf("8FABD9")){{
            hardness = 7;
            cost = 3;
        }};
        ionicAlloy = new Item("ionic-alloy", Color.valueOf("84F491")){{
            hardness = 2;
            cost = 4;
        }};
        silver = new Item("silver", Color.valueOf("F2F2F2")){{
            hardness = 2;
            cost = 2;
        }};
        concussionAlloy = new Item("concussion-alloy", Color.valueOf("EFEAEA")){{
            hardness = 2;
            cost = 2;
        }};
        omurloItems.addAll(
                iridium, iron, heavite, ionicAlloy, silver, concussionAlloy, sand, silicon, graphite, coal
        );
    }
}
