package disintegration.entities;

import mindustry.gen.Drawc;
import mindustry.gen.Teamc;

public interface BlackHolec extends Teamc, Drawc {
    float radius();
    float force();

    float attractForce();

    void radius(float r);
    void force(float f);

    void attractForce(float f);
}
