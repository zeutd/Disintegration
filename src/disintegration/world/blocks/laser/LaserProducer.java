package disintegration.world.blocks.laser;

import arc.struct.IntSet;

public interface LaserProducer extends LaserBlock{
    IntSet cameFrom();
}
