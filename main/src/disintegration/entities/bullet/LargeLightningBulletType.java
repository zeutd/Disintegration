package disintegration.entities.bullet;

import arc.graphics.Color;
import arc.math.Mathf;
import disintegration.entities.LargeLightning;
import disintegration.graphics.Pal2;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;

public class LargeLightningBulletType extends BulletType {
    public Color lightningColor = Pal2.lightningWhite;
    public int lightningLength = 30, lightningLengthRand = 0;
    public BulletType end;
    public LargeLightningBulletType(){
        damage = 1f;
        speed = 0f;
        lifetime = 1;
        despawnEffect = Fx.none;
        hitEffect = Fx.hitLancer;
        keepVelocity = false;
        hittable = false;
        //for stats
        status = StatusEffects.shocked;
    }

    @Override
    protected float calculateRange(){
        return (lightningLength + lightningLengthRand/2f) * 6f;
    }

    @Override
    public float estimateDPS(){
        return super.estimateDPS() * Math.max(lightningLength / 10f, 1);
    }

    @Override
    public void draw(Bullet b){
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        LargeLightning.create(b, end, lightningColor, damage, b.x, b.y, b.rotation(), lightningLength + Mathf.random(lightningLengthRand));
    }
}
