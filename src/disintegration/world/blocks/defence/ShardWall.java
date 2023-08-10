package disintegration.world.blocks.defence;

import arc.math.Mathf;
import disintegration.world.meta.DTStat;
import mindustry.content.Bullets;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.StatUnit;


public class ShardWall extends Wall {
    public float shardChance = -1f;

    public BulletType shard = Bullets.placeholder;

    public ShardWall(String name){
        super(name);
        sync = true;
    }

    @Override
    public void setStats() {
        super.setStats();
        if(shardChance > 0f){
            stats.add(DTStat.shardChance, shardChance * 100f, StatUnit.percent);
            stats.add(DTStat.shardDamage, shard.damage, StatUnit.none);
        }
    }

    public class ShardWallBuild extends WallBuild{
        @Override
        public boolean collision(Bullet bullet) {
            super.collision(bullet);

            hit = 1f;

            //create shard if necessary
            if (shardChance > 0f) {
                if (Mathf.chance(shardChance)) {
                    shard.create(this, this.team, x, y, bullet.rotation() + 180f, 1f, 1f);
                }
            }
            return true;
        }
    }
}
