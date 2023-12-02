package disintegration.util;

import arc.math.Interp;

import static arc.math.Mathf.clamp;

public class DTInterp {
    public static class PowOutDelay extends Interp.PowOut {
        float delay;
        public PowOutDelay(int power, float delay) {
            super(power);
            this.delay = delay;
        }

        @Override
        public float apply(float a){
            return super.apply(clamp(a / (1 - delay)));
        }
    }

    public static class PowInDelay extends Interp.PowIn {
        float delay;
        public PowInDelay(int power, float delay) {
            super(power);
            this.delay = delay;
        }

        @Override
        public float apply(float a){
            return super.apply(clamp(a / (1 - delay)));
        }
    }
}
