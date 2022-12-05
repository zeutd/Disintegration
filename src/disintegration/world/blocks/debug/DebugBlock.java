package disintegration.world.blocks.debug;

import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.world.Block;

public class DebugBlock extends Block {
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
                Vars.state.rules.infiniteResources = !Vars.state.rules.infiniteResources;
                deselect();
            }).size(40f);
        }
    }
}
