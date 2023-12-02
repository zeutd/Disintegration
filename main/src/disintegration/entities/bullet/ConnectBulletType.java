package disintegration.entities.bullet;

import arc.graphics.Color;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;

public class ConnectBulletType extends BasicBulletType {
    public float radius = 100f;
    public Color chainColor;
    public Effect chainEffect = Fx.chainEmp, chainHitEffect = Fx.none;

    @Override
    public void hit(Bullet b, float x, float y){
        super.hit(b, x, y);

        if(!b.absorbed){
            Vars.indexer.allBuildings(x, y, radius, other -> {
                if(other.team != b.team){
                    var absorber = Damage.findAbsorber(b.team, x, y, other.x, other.y);
                    if(absorber != null){
                        return;
                    }
                    other.damage(damage);
                    chainEffect.at(x, y, 0, hitColor, other);
                }
            });
            Units.nearbyEnemies(b.team, x, y, radius, other -> {
                if(other.team != b.team && other.hittable()){
                    var absorber = Damage.findAbsorber(b.team, x, y, other.x, other.y);
                    if(absorber != null){
                        return;
                    }
                    chainEffect.at(x, y, 0, chainColor, other);
                    chainHitEffect.at(other.x, other.y);
                    other.damage(damage);
                    other.apply(status, statusDuration);
                }
            });
        }
    }
}
