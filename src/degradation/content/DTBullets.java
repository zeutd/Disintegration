package degradation.content;

import arc.graphics.Color;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;

public class DTBullets {
    public static BulletType shard;
    public static void load(){
        shard = new BasicBulletType(4f,50f){{
            frontColor = Color.white;
            backColor = Color.white;
            pierce = true;
            pierceBuilding = false;
            pierceCap = 15;
            drag = 0.1f;
            width = 3f; height = 4f;
        }};
    }
}
