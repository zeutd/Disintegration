package degradation.content;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

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
            speed = 2.7f;
            accel = 0.08f;
            drag = 0.04f;
            flying = true;
            health = 200;
            engineOffset = 10;
            engineSize = 3.7f;
            hitSize = 11;
            weapons.add(new Weapon(){{
                    y = -10f;
                    x = 0f;
                    reload = 140f;
                    ejectEffect = Fx.hitLancer;
                    shoot.shots = 4;
                    bullet = new BasicBulletType(2.5f, 9) {{
                        frontColor = Color.valueOf(" 8aa3f4");
                        sprite = "circle-bullet";
                        width = 8f;
                        height = 8f;
                        lifetime = 45f;
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
