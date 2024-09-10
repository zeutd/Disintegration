package disintegration.util;

import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Tmp;

import static arc.Core.camera;
import static arc.math.Mathf.*;
import static mindustry.Vars.renderer;

public class M3d {
    public static float height(float h){
        return h * renderer.getDisplayScale();
    }

    public static float x(float x, float h){
        return x + (x - camera.position.x) * height(h);
    }

    public static float y(float y, float h){
        return y + (y - camera.position.y) * height(h);
    }

    public static float tubeStartAngle(float x1, float y1, float x2, float y2, float rad1, float rad2){
        if(x1 == x2 && y1 == y2) return 0f;

        float d = Mathf.dst(x2 - x1,y2 - y1);
        float f = sqrt(d * d - sqr(rad2 - rad1));
        float a = rad1 > rad2 ? atan2(rad1 - rad2, f) : (rad1 < rad2 ? pi - atan2(rad2 - rad1, f) : halfPi);
        Tmp.v1.set(x2 - x1, y2 - y1).scl(1f / d); //normal
        Tmp.v2.set(Tmp.v1).rotateRad(pi - a).scl(-rad2).add(x2, y2); //tangent

        return Angles.angle(x2, y2, Tmp.v2.x, Tmp.v2.y);
    }
}
