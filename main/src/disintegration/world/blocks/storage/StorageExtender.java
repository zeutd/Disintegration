package disintegration.world.blocks.storage;

import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.storage.StorageBlock;

public class StorageExtender extends StorageBlock {
    public StorageExtender(String name) {
        super(name);
    }
    public class ExtenderBuild extends StorageBuild {
        @Override
        public boolean acceptItem(Building source, Item item){
            return linkedCore != null && linkedCore.acceptItem(source, item);
        }
    }
}
