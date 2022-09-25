package degradation.world.blocks.defence.turrets;

import arc.audio.Sound;
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.gen.Sounds;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.meta.Env;

public class ElectricTowerTurret extends BaseTurret {
    public float range = 50f;
    public float damage = 0f;
    public float lightningWidth = 0.6f;

    public Effect hitEffect;
    public Effect damageEffect = Fx.chainLightning;

    public Color lightningColor;

    public boolean targetAir = true, targetGround = false;

    public Sound shootSound = Sounds.release;

    public StatusEffect status = StatusEffects.shocked;
    public ElectricTowerTurret(String name) {
        super(name);
        rotateSpeed = 10f;
        coolantMultiplier = 1f;
        envEnabled |= Env.space;
    }
    public class ElectricTowerTurretBuild extends BaseTurretBuild{

    }
}
