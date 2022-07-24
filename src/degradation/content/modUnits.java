package degradation.content;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.EntityMapping;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.PowerAmmoType;

public class modUnits {
    public static UnitType
            //air-Hyper
            lancet, raven,
            //ground-Hyper
            essence, truth, solve,
            //air-attack
            knife
            //ground-attack
            ;
    public static void load(){
        //air-Hyper
        //T1 lancet
        lancet = new UnitType("lancet"){{
            constructor = EntityMapping.map(3);
            speed = 2.7f;
            accel = 0.08f;
            drag = 0.04f;
            flying = true;
            health = 200;
            engineOffset = 10;
            engineSize = 3.7f;
            hitSize = 11;
            weapons.add(new Weapon(){{
                    y = 10f;
                    x = 0f;
                    reload = 140f;
                    ejectEffect = Fx.lancerLaserShoot;
                    shoot.shots = 4;
                    inaccuracy = 4;
                    velocityRnd = 0.33;
                    bullet = new BasicBulletType(2f, 9) {{
                        frontColor = Color.valueOf("8aa3f4");
                        backColor = Color.valueOf("8aa4f4");
                        ammoType = new PowerAmmoType(500);
                        sprite = "circle-bullet";
                        width = 8f;
                        height = 10f;
                        weaveMag = 4;
                        weaveScale = 4;
                        lifetime = 110f;
                        shootEffect = Fx.lancerLaserShoot;
                        smokeEffect = Fx.none;
                        homingPower = 0.17f;
                        homingDelay = 19f;
                        homingRange = 30f;
                        trailColor = Color.valueOf(" 8aa3f4");
                        trailWidth = 4;
                        trailLength = 20;
                    }};
                }});
        }};
    }
}
