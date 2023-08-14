package disintegration.entities.unit.weapons;

import arc.audio.Sound;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.Effect;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.input.InputHandler;
import mindustry.type.Weapon;
import mindustry.world.Block;

public class PortableBlockWeapon extends Weapon {
    public Block unitContent;

    public Effect placeEffect;

    public Sound placeSound;

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
            placeEffect.at(unit.tileX() * 8, unit.tileY() * 8, 0f);
            placeSound.at(unit.x, unit.y);
            InputHandler.unitClear(unit.getPlayer());
        }
    }
}
