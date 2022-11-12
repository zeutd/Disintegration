package disintegration.util;

public class MathDef {
    public static float round(float value, int step){
        return (float) Math.round(value * step) / step;
    }

    /**Better Mathf.lerp*/
    public static float lerp(float value, float target, float power, float speed){
        return (((target / speed - value / speed) / power / 5 + value / speed) * speed + (target - value) / speed / 5);
    }
    /** Better Mathf.lerp*/
    public static float lerp(float value, float target, float power, float speed, boolean paused){
        if (!paused) {
            return (((target / speed - value / speed) / power / 5 + value / speed) * speed + (target - value) / speed / 5);
        }
        else {
            return value;
        }
    }
    public static float linear(float value, float target, float power){
        return Math.abs(value - target) <= 0.1 ? target : value + power * (value < target ? 1 : -1);
    }
    public static float linear(float value, float target, float power, boolean paused){
        if (!paused) {
            return Math.abs(value - target) <= 0.1 ? target : value + power * (value < target ? 1 : -1);
        }
        else{
            return value;
        }
    }
}
