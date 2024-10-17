package disintegration.world.blocks.logic;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.world.blocks.logic.SwitchBlock;

public class BuildingSwitch extends SwitchBlock {

    public TextureRegion[] regions;
    public BuildingSwitch(String name) {
        super(name);
        rotate = true;
    }

    @Override
    public void load(){
        super.load();
        regions = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            regions[i] = Core.atlas.find(name + (i + 1));
        }
    }

    public class BuildingSwitchBuild extends SwitchBuild {
        @Override
        public void updateTile(){
            super.updateTile();
            if(nearby(rotation) == null) return;
            nearby(rotation).enabled = enabled;
        }

        @Override
        public void draw(){
            Draw.rect(regions[rotation], x, y);

            if(enabled){
                Draw.rect(onRegion, x, y);
            }
        }
    }
}
