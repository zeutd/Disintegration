package disintegration.content;

import arc.graphics.Color;
import mindustry.type.Liquid;

public class DTLiquids {
    public static Liquid steam, liquidCrystal;

    public static void load(){
        steam = new Liquid("steam", Color.valueOf("fafafa")){{
            gas = true;
        }};

        liquidCrystal = new Liquid("liquid-crystal", Color.valueOf("d0d3d5")){{
            heatCapacity = 0.9f;
            temperature = 0.25f;
            boilPoint = 0.55f;
            gasColor = Color.valueOf("e8e8e8");
        }};
    }
}
