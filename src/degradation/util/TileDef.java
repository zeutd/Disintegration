package degradation.util;

import arc.math.geom.Vec2;
import arc.struct.Seq;
import degradation.world.blocks.temperature.*;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Tile;

import static mindustry.Vars.world;

public class TileDef {
    public static boolean toBlock(Building block, Building other){
        return !other.block().rotate || (block.relativeTo(other) + 2) % 4 == other.rotation;
    }

    public static boolean conductSideTemperature(Building block, Building other){
        if(other instanceof TemperatureConduit.TemperatureConduitBuild || other instanceof TemperatureVoid.TemperatureVoidBuild || other instanceof TemperatureSource.TemperatureSourceBuild){
            return true;
        }
        else if(other instanceof TemperatureProducer.TemperatureProducerBuild || other instanceof TemperatureCrafter.TemperatureCrafterBuild){
            return TileDef.toBlock(block, other);
        }
        return false;
    }
    public static Seq<Item> listItem(Item[][] array){
        Seq<Item> items = new Seq<>();
        for(Item[] tileItems : array){
            for(Item tileItem : tileItems){
                if (!items.contains(tileItem)) items.add(tileItem);
            }
        }
        return items;
    }

    public static Tile[][] getAreaTile(Vec2 pos, int width, int height){
        Tile[][] tilesGet = new Tile[width][height];
        int dx = (int) pos.x;
        int dy = (int) pos.y;
        for (int ix = 0; ix <= width - 1; ix ++){
            for (int iy = 0; iy <= height - 1; iy ++){
                Tile other = world.tile(ix + dx + 1, iy + dy + 1);
                if(other != null) tilesGet[ix][iy] = other;
            }
        }
        return tilesGet;
    }
}
