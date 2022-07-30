package degradation.content;

import arc.graphics.g2d.Fill;
import degradation.graphics.Pal2;
import mindustry.entities.Effect;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;

public class DTFx {
    public static final Effect
    electricResonated = new Effect(40f, e -> {
        color(Pal2.hyperBlue);
        randLenVectors(e.id, 2, 1f + e.fin() * 2f, (x, y) -> {
            Fill.square(e.x + x, e.y + y, e.fslope() * 1.1f, 45f);
        });
    });
}
