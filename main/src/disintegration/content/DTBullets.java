package disintegration.content;

import arc.graphics.Color;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LaserBulletType;

public class DTBullets {
    public static BulletType
            shard, nitrideLaser;

    public static void load() {
        shard = new BasicBulletType(4f, 50f, "shell") {{
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

        nitrideLaser = new LaserBulletType(100) {{
            pierceCap = 8;
            length = 80;
            colors = new Color[]{Color.valueOf("9a9dbf").a(0.4f), Color.valueOf("9a9dbf"), Color.valueOf("ededed")};
        }};
    }
}
