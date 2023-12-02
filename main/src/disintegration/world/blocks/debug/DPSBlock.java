package disintegration.world.blocks.debug;

import arc.graphics.g2d.Font;
import arc.util.Align;
import arc.util.Time;
import disintegration.util.MathDef;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.ui.Fonts;
import mindustry.world.Block;

public class DPSBlock extends Block {
    public DPSBlock(String name) {
        super(name);
        update = true;
        health = 999999;
    }
    public class DPSBuild extends Building {
        float timer, DPS;
        @Override
        public void updateTile(){
            timer += Time.delta;
            if(timer >= 60){
                DPS = block.health - health;
                timer = 0;
                health(block.health);
            }
        }

        @Override
        public void draw(){
            super.draw();
            Drawf.text();
            Font font = Fonts.outline;
            font.getCache().getLayouts().get(0).height = (float)block.size / 4;
            font.getCache().getLayouts().get(0).width = (float)block.size / 4;
            font.draw("DPS: " + MathDef.round(DPS, 100), x, y, Align.center);
        }
    }
}
