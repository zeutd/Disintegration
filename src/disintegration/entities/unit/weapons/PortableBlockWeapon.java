package disintegration.entities.unit.weapons;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import mindustry.world.Block;

public class PortableBlockWeapon extends Weapon {
    public Block unitContent;

    public PortableBlockWeapon(String name){
        super(name);
    }

    public PortableBlockWeapon()

    {
        predictTarget = false;
        autoTarget = false;
        controllable = true;
        rotate = true;
        useAmmo = false;
    }

    @Override
    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation){
        if(Vars.world.tile(unit.tileX(), unit.tileY()).block() == Blocks.air) {
            unit.remove();
            Vars.world.tile(unit.tileX(), unit.tileY()).setBlock(unitContent, unit.team);
            Fx.mineImpactWave.at(unit.tileX() * 8, unit.tileY() * 8, 0f);
            Sounds.drillImpact.at(unit.x, unit.y);
            unit.kill();
        }
    }
}
