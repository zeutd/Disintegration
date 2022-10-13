package degradation.content;

import arc.graphics.Color;
import mindustry.type.Liquid;

public class DTLiquids {
    public static Liquid steam;

    public static void load(){
        steam = new Liquid("steam", Color.valueOf("fafafa")){{
            gas = true;
        }};
    }
}
