package disintegration.world.blocks.debug;

import arc.func.Cons;
import arc.scene.ui.layout.Table;
import arc.util.Nullable;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.world.Block;

public class DebugBlock extends Block {
    public @Nullable Cons<Building> runs;
    public DebugBlock(String name) {
        super(name);
        update = true;
        solid = true;
        configurable = true;
    }

    public class DebugBuild extends Building {
        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.upOpen, Styles.cleari, () -> {
                if (runs != null) runs.get(this);
                deselect();
            }).size(40f);
        }
    }
}
