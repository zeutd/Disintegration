package disintegration.content;

import arc.graphics.Color;
import mindustry.content.Liquids;
import mindustry.type.Liquid;

public class DTLiquids {
    public static Liquid steam, liquidCrystal, oxygen, algalWater,
        carbonDioxide, moltenLithium;

    public static void load() {
        Liquids.gallium.hidden = false;

        steam = new Liquid("steam", Color.valueOf("f1f4ff")) {{
            gas = true;
            temperature = 1f;
            coolant = false;
        }};
        oxygen = new Liquid("oxygen", Color.valueOf("9eaefd")) {{
            gas = true;
            coolant = false;
        }};
        carbonDioxide = new Liquid("carbon-dioxide", Color.valueOf("8a8ea3")){{
            gas = true;
            coolant = false;
        }};
        liquidCrystal = new Liquid("liquid-crystal", Color.valueOf("d0d3d5")) {{
            heatCapacity = 0.9f;
            temperature = 0.25f;
            boilPoint = 1.3f;
            gasColor = Color.valueOf("e8e8e8");
        }};
        moltenLithium = new Liquid("molten-lithium", Color.valueOf("e3e3e3")){{
            temperature = 1.8f;
            explosiveness = 1.7f;
        }};
        algalWater = new Liquid("algal-water", Color.valueOf("4b7952")) {{
            coolant = false;
        }};
    }
}
