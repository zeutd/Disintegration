package degradation.world.blocks.defence;

import arc.math.*;
import degradation.content.modBullets;
import mindustry.entities.bullet.*;
import mindustry.gen.Bullet;
import mindustry.world.blocks.defense.*;


public class ShardWall extends Wall {
    public float shardChance = -1f;

    public ShardWall(String name){
        super(name);
    }

    public class ShardWallBuild extends WallBuild{
        @Override
        public boolean collision(Bullet bullet) {
            super.collision(bullet);

            hit = 1f;

            //create shard if necessary
            if (shardChance > 0f) {
                if (Mathf.chance(shardChance)) {
                    modBullets.shard.create(this, this.team, x, y, bullet.rotation() + 180f, 2f, 2f);
                }
            }
            return true;
        }
    }
}
