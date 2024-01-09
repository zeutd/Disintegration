package disintegration.content;

import arc.graphics.Color;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;

public class DTBullets {
    public static BulletType
            shard;
    public static void load(){
        shard = new BasicBulletType(4f,10f, "shell"){{
            frontColor = Color.valueOf("D0D3E7");
            backColor = Color.valueOf("B5B9D5");
            pierce = true;
            pierceBuilding = false;
            lightRadius = 0f;
            drag = 0.13f;
            width = 5.5f;
            height = 5.5f;
            speed = 7f;
            lifetime = 30f;
        }};
    }
}
