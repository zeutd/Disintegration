package degradation.util;

import degradation.world.blocks.temperature.TemperatureConduit;
import degradation.world.blocks.temperature.TemperatureProducer;
import degradation.world.blocks.temperature.TemperatureSource;
import degradation.world.blocks.temperature.TemperatureVoid;
import mindustry.gen.Building;

public class TileDef {
    public static boolean toBlock(Building block, Building other){
        return (block.relativeTo(other) + 2) % 4 == other.rotation;
    }

    public static boolean conductSideTemperature(Building block, Building other){
        if(other instanceof TemperatureConduit.TemperatureConduitBuild || other instanceof TemperatureVoid.TemperatureVoidBuild || other instanceof TemperatureSource.TemperatureSourceBuild){
            return true;
        }
        else if(other instanceof TemperatureProducer.TemperatureProducerBuild){
            return TileDef.toBlock(block, other);
        }
        return false;
    }
}
