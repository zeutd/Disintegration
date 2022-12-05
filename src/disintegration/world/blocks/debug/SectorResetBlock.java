package disintegration.world.blocks.debug;

import arc.scene.ui.layout.Table;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.indexer;

public class SectorResetBlock extends Block {
    public SectorResetBlock(String name) {
        super(name);
        update = true;
        solid = true;
        configurable = true;
    }

    public class SectorResetBuild extends Building {
        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.upOpen, Styles.cleari, () -> {
                indexer.getFlagged(team, BlockFlag.core).forEach(b -> {
                    b.damage(100000);
                });
                deselect();
            }).size(40f);
        }
    }
}
