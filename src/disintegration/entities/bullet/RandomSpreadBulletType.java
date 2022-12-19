package disintegration.entities.bullet;

import arc.math.Mathf;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;

public class RandomSpreadBulletType extends BasicBulletType {
    public float intervalChance;
    public RandomSpreadBulletType(float speed, float damage){
        super(speed, damage);
        hittable = false;
        reflectable = false;
    }

    @Override
    public void updateBulletInterval(Bullet b){
        if(intervalBullet != null && Mathf.chance(intervalChance)){
            float ang = b.rotation();
            for(int i = 0; i < intervalBullets; i++){
                intervalBullet.create(b, b.x, b.y, ang + Mathf.range(intervalRandomSpread) + intervalAngle + ((i - (intervalBullets - 1f)/2f) * intervalSpread));
            }
        }
    }
}
