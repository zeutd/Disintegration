package disintegration.world.blocks.laser;

import arc.struct.IntSet;

public interface LaserBlock {
    float luminosity();

    void call(float value, int from, IntSet cameFrom);

    float[] l();
}
