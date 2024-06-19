package disintegration.world.blocks.defence;

import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.world.blocks.defense.MendProjector;

import static mindustry.Vars.indexer;
import static mindustry.Vars.world;

public class ContinuousMendProjector extends MendProjector {
    public ContinuousMendProjector(String name) {
        super(name);
    }

    public class ContinuousMendBuild extends MendBuild {
        public int lastChange = -2;
        public Seq<Building> targets = new Seq<>();

        public void updateTargets() {
            targets.clear();
            indexer.eachBlock(this, range + phaseHeat * phaseRangeBoost, b -> b.damaged() && !b.isHealSuppressed(), targets::add);
        }

        @Override
        public void updateTile() {
            if (lastChange != world.tileChanges) {
                lastChange = world.tileChanges;
                updateTargets();
            }
            boolean canHeal = !checkSuppression() && !targets.isEmpty();

            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
            heat = Mathf.lerpDelta(heat, efficiency > 0 && canHeal ? 1f : 0f, 0.08f);

            phaseHeat = Mathf.lerpDelta(phaseHeat, optionalEfficiency, 0.1f);

            if (optionalEfficiency > 0 && timer(timerUse, useTime) && canHeal) {
                consume();
            }

            targets.each(other -> {
                other.heal(other.maxHealth() * (healPercent + phaseHeat * phaseBoost) / 100f * edelta());
                other.recentlyHealed();
            });
        }
    }
}
