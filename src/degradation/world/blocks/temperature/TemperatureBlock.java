package degradation.world.blocks.temperature;

public interface TemperatureBlock {
    float temperature();

    float temperatureFrac();

    float temperatureCapacity();

    float conductionSpeed();
}
