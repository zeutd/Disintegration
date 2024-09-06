package disintegration.world.blocks.defence;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.gen.Groups;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.defense.turrets.PointDefenseTurret;

import static mindustry.Vars.state;

public class LaserTower extends PointDefenseTurret {
    public LaserTower(String name) {
        super(name);
        color = Color.brick;
        retargetTime = 4f;
        outlineIcon = false;
        outlineRadius = 0;
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region};
    }

    public class LaserTowerBuild extends PointDefenseBuild {
        @Override
        public void updateTile(){

            //retarget
            if(timer(timerTarget, retargetTime)){
                target = Groups.bullet.intersect(x - range, y - range, range*2, range*2).min(b -> b.team != team && b.type().hittable, b -> b.dst2(this));
            }

            //pooled bullets
            if(target != null && !target.isAdded()){
                target = null;
            }

            if(coolant != null){
                updateCooling();
            }

            //look at target
            if(target != null && target.within(this, range) && target.team != team && target.type() != null && target.type().hittable){
                reloadCounter += edelta();

                //shoot when possible
                if(reloadCounter >= reload){
                    float realDamage = bulletDamage * state.rules.blockDamage(team);
                    if(target.damage() > realDamage){
                        target.damage(target.damage() - realDamage);
                    }else{
                        target.remove();
                    }


                    beamEffect.at(x, y, angleTo(target), color, new Vec2().set(target));
                    shootEffect.at(x, y, angleTo(target), color);
                    hitEffect.at(target.x, target.y, color);
                    shootSound.at(x, y, Mathf.random(0.9f, 1.1f));
                    reloadCounter = 0;
                }
            }
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
        }
    }
}
