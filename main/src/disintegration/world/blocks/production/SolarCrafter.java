package disintegration.world.blocks.production;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.util.Time;
import arc.util.Tmp;
import disintegration.graphics.Pal2;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.HeatCrafter;

import static mindustry.Vars.control;
import static mindustry.Vars.tilesize;

public class SolarCrafter extends GenericCrafter {
    public float lightRequirement = 8f;
    /** After heat meets this requirement, excess heat will be scaled by this number. */
    public float overLightScale = 0.7f;
    /** Maximum possible efficiency after overheat. */
    public float maxEfficiency = 2f;
    public SolarCrafter(String name){
        super(name);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (SolarCrafterBuild entity) ->
                new Bar(() ->
                        Core.bundle.format("bar.lightpercent", (int)(entity.lightStrength + 0.01f), (int)(entity.efficiencyScale() * 100 + 0.01f)),
                        () -> Pal2.lightningWhite,
                        () -> entity.lightStrength / lightRequirement));
    }

    public class SolarCrafterBuild extends GenericCrafterBuild {
        public IntSeq links = new IntSeq();
        public int lightStrength;

        @Override
        public void updateTile(){
            super.updateTile();
            lightStrength = links.size;
        }

        @Override
        public float efficiencyScale(){
            float over = Math.max(lightStrength - lightRequirement, 0f);
            return Math.min(Mathf.clamp(lightStrength / lightRequirement) + over / lightRequirement * overLightScale, maxEfficiency);
        }
    }
}
