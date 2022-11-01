package disintegration.world.blocks.temperature;

public interface TemperatureBlock {
    float temperature();

    void addTemperature(float target);
    void setTemperature(float target);
}
