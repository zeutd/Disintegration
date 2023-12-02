package disintegration.world.blocks.laser;

import arc.struct.IntSet;

public interface LaserConsumer {

    void call(float value, int from, IntSet cameFrom);
}
