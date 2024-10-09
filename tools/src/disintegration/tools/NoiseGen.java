package disintegration.tools;

import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.util.noise.Simplex;

public class NoiseGen {
    public static void main(String[] args){
        Pixmap pix = new Pixmap(128, 128);
        pix.each((x, y) -> {
            float n = Simplex.noise2d(300, 9, 0.1, 0.12, x, y);
            pix.set(x, y, new Color(n, n, n, 1));
        });
        Fi.get("noise2.png").writePng(pix);
    }
}
