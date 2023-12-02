package disintegration.world.blocks.laser;

public interface LaserBlock extends LaserConsumer {
    float luminosityFrac();
    float[] l();
    float luminosity();
}
