package degradation.world.blocks.defence.turrets;

import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.world.blocks.defense.turrets.PowerTurret;

public class HealTurret extends PowerTurret {
    public HealTurret(String name){super(name);}

    public class HealTurretBuild extends PowerTurretBuild {
        @Override
        public boolean collide(Bullet other){ return other.owner != this;}

        @Override
        public void findTarget() {
            Building HealTarget = Units.findDamagedTile(team, x, y);
            if (HealTarget != null && HealTarget != this && dst(HealTarget.x,HealTarget.y) <= range) {
                target = HealTarget;
            } else {
                super.findTarget();
            }
        }
        @Override
        public boolean validateTarget() {
            return !Units.invalidateTarget(target, Team.derelict, x, y) || isControlled() || logicControlled();
        }
    }
}
