package degradation;

import arc.util.Log;

public class mathDef {
    public static float lerp(float value, float target, float power, float speed){
        Log.info(value);
        return (((target / speed - value / speed) / power / 5 + value / speed) * speed + (target - value) / speed / 5);
    }
    public static float lerp(float value, float target, float power){
        return ((target - value) / power / 5 + value);
    }
}
