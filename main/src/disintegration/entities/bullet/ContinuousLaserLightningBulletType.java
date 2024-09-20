package disintegration.entities.bullet;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import disintegration.graphics.LaserLightning;
import disintegration.graphics.Pal2;
import mindustry.entities.Damage;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;

import static mindustry.Vars.state;

public class ContinuousLaserLightningBulletType extends ContinuousLaserBulletType {
    public float interval = 0.1f, interval2 = 0.3f, speed = 1f, mag = 20f;
    public static LaserLightning.LaserLightningData data;
    public Color color = Pal2.lightningWhite;

    public ContinuousLaserLightningBulletType(float damage){
        super(damage);
        data = new LaserLightning.LaserLightningData(new Vec2(), new Vec2(), color, id, interval, interval2, speed, mag);
    }

    @Override
    public void draw(Bullet b) {
        super.draw(b);
        float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
        float realLength = Damage.findLength(b, length * fout, laserAbsorb, pierceCap);
        data.start = new Vec2(b.x, b.y);
        data.end = new Vec2().trns(b.rotation(), realLength).add(data.start);
        data.draw();
    }
}
