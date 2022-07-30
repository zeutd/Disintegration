package degradation.content;

import degradation.graphics.Pal2;
import mindustry.type.StatusEffect;

public class DTStatusEffects {
    public static StatusEffect electricResonated;
    public static void load() {
        electricResonated = new StatusEffect("electric-resonated") {{
            color = Pal2.hyperBlue;
            speedMultiplier = 0.8f;
            reloadMultiplier = 0.7f;
            effect = DTFx.electricResonated;
            effectChance = 0.1f;
        }};
    }
}
