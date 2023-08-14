package disintegration.util;

import arc.math.Mathf;
import arc.util.Time;

public class MathDef {
    public static float round(float value, int step){
        return (float) Math.round(value * step) / step;
    }

    /**Better Mathf.lerp*/

    //public static float lerp(float value, float target, float power, float speed){
        //return (((target / speed - value / speed) / power + value / speed) * speed + (target - value) / speed);
    //}

    public static float linear(float value, float target, float power) {
        return Math.abs(value - target) <= power ? target : value + power * (value < target ? 1 : -1);
    }
    public static float linearDelta(float value, float target, float power) {
        return linear(value, target, Mathf.clamp(power * Time.delta));
    }

    public static int randomSeedRange(long seed){
        int range = Mathf.randomSeed(seed, -1, 1);
        int i = 0;
        while(range == 0){
            range = Mathf.randomSeed(seed + i, -1, 1);
            i ++;
        }
        return range;
    }
}
