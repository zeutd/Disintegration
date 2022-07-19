package degradation.content;

import mindustry.type.Category;
import mindustry.world.Block;
import degradation.world.blocks.defence.ShardWall;

import static mindustry.type.ItemStack.with;

public class modBlocks {
    public static Block
    //walls
            iridiumWall, iridiumWallLarge;
    public static void load() {
        iridiumWall = new ShardWall("iridium-wall"){{
            shardChance = 0.1f;
            size = 1;
            health = 700;
            requirements(Category.defense, with(modItems.iridium, 8));
        }};
        iridiumWallLarge = new ShardWall("iridium-wall-large"){{
            shardChance = 0.1f;
            size = 2;
            health = 2800;
            requirements(Category.defense, with(modItems.iridium, 8));
        }};
    }
}
