package disintegration.content;

import arc.Events;
import arc.graphics.Color;
import disintegration.graphics.Pal2;
import mindustry.content.Fx;
import mindustry.game.EventType;
import mindustry.type.StatusEffect;

import static mindustry.Vars.state;
import static mindustry.content.StatusEffects.*;

public class DTStatusEffects {
    public static StatusEffect electricResonated, frozen;

    public static void load() {
        electricResonated = new StatusEffect("electric-resonated") {{
            color = Pal2.hyperBlue;
            speedMultiplier = 0.8f;
            reloadMultiplier = 0.7f;
            effect = DTFx.electricResonated;
            effectChance = 0.1f;
        }};

        frozen = new StatusEffect("frozen") {{
            color = Color.valueOf("6ecdec");
            speedMultiplier = 0.5f;
            reloadMultiplier = 0.7f;
            effect = Fx.freezing;
            transitionDamage = 22f;
            init(() -> {
                opposite(melting, burning);

                affinity(blasted, (unit, result, time) -> {
                    unit.damagePierce(transitionDamage);
                    if (unit.team == state.rules.waveTeam) {
                        Events.fire(EventType.Trigger.blastFreeze);
                    }
                });
            });
        }};
    }
}
