package disintegration.entities;

import mindustry.gen.Drawc;
import mindustry.gen.Teamc;

public interface BlackHolec extends Teamc, Drawc {
    float radius();
    float force();

    float attractForce();
    float attractRadius();

    void radius(float r);
    void force(float f);
    void attractForce(float f);
    void attractRadius(float r);
}
