package disintegration.content;

import arc.graphics.Color;
import mindustry.content.Liquids;
import mindustry.type.Liquid;

public class DTLiquids {
    public static Liquid steam, liquidCrystal, oxygen, algalWater;

    public static void load() {
        Liquids.gallium.hidden = false;

        steam = new Liquid("steam", Color.valueOf("f1f4ff")) {{
            gas = true;
        }};
        oxygen = new Liquid("oxygen", Color.valueOf("9eaefd")) {{
            gas = true;
        }};

        liquidCrystal = new Liquid("liquid-crystal", Color.valueOf("d0d3d5")) {{
            heatCapacity = 0.9f;
            temperature = 0.25f;
            boilPoint = 0.55f;
            gasColor = Color.valueOf("e8e8e8");
        }};

        algalWater = new Liquid("algal-water", Color.valueOf("4b7952")) {{
            coolant = false;
        }};
    }
}
