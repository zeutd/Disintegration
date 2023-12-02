package disintegration.entities.bullet;

import arc.math.Angles;
import arc.math.Mathf;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;

public class WarpBulletType extends BasicBulletType {
    public float warpDistance = 100f;
    public float warpAngle = 0f;
    public float warpRotMin = 0f;
    public float warpRotMax = 0f;
    public float warpOver = 1f;
    public boolean resetLifetime = false;

    public WarpBulletType(float speed, float damage){
        super(speed, damage);
    }
    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
        b.data(build.hitSize());
        super.hitTile(b, build, x, y, initialHealth, direct);
    }
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        b.data(entity.hitSize());
        super.hitEntity(b, entity, health);
    }

    public void hit(Bullet b, float x, float y){
        super.hit(b, x, y);
        float bulletWarpDistance = this.warpDistance + (b.data() != null ? (Float)b.data() : 0) * warpOver;
        b.x(x + Angles.trnsx(b.rotation(), bulletWarpDistance));
        b.y(y + Angles.trnsy(b.rotation(), bulletWarpDistance));
        b.rotation(b.rotation() + warpAngle + Mathf.random(warpRotMin, warpRotMax));
        if(resetLifetime) b.lifetime(lifetime);
        trailEffect.at(b.x, b.y, trailRotation ? b.rotation() : trailParam, trailColor);
    }
}
