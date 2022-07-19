package degradation.content;

import arc.graphics.Color;
import mindustry.type.Item;

public class modItems {
    public static Item iridium, iron, heavite, ionicAlloy, silver, concussionAlloy;
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
        ionicAlloy = new Item("ionicAlloy", Color.valueOf("84F491")){{
            hardness = 2;
            cost = 4;
        }};
        silver = new Item("silver", Color.valueOf("F2F2F2")){{
            hardness = 2;
            cost = 2;
        }};
        concussionAlloy = new Item("concussionAlloy", Color.valueOf("EFEAEA")){{
            hardness = 2;
            cost = 2;
        }};
    }
}
