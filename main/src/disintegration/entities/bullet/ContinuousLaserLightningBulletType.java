package disintegration.entities.bullet;

import arc.graphics.Color;
import arc.math.Mathf;
import disintegration.graphics.Pal2;
import mindustry.entities.Damage;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;

public class ContinuousLaserLightningBulletType extends ContinuousLaserBulletType {
    public float interval = 0.1f, interval2 = 0.2f, speed = 1f, mag = 20f;
    public Color color = Pal2.lightningWhite;

    public ContinuousLaserLightningBulletType(float damage){
        super(damage);
    }

    @Override
    public void draw(Bullet b) {
        super.draw(b);
        float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
        float realLength = Damage.findLength(b, length * fout, laserAbsorb, pierceCap);
    }
}
