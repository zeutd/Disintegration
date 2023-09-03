package disintegration.world.blocks.heat;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import disintegration.graphics.Pal2;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConductor;
import mindustry.world.blocks.heat.HeatConsumer;

public class HeatConduitRouter extends HeatConductor{
    public TextureRegion topRegion;
    public TextureRegion botRegion;
    public TextureRegion heatRegion;
    public TextureRegion capRegion;
    public Color botColor = Color.valueOf("2f2d39");
    public Color heatColor1 = new Color(1f, 0.3f, 0.3f);
    public Color heatColor2 = Pal2.heat;
    public float heatPulse = 0.3f, heatPulseScl = 10f;
    public HeatConduitRouter(String name) {
        super(name);
        rotate = true;
        update = true;
        splitHeat = true;
    }
    @Override
    public void load(){
        super.load();
        topRegion = Core.atlas.find(name + "-top");
        botRegion = Core.atlas.find(name + "-bottom");
        heatRegion = Core.atlas.find(name + "-heat");
        capRegion = Core.atlas.find(name + "-cap");
    }
    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{Core.atlas.find(name)};
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(Core.atlas.find(name), plan.drawx(), plan.drawy(), plan.rotation * 90);
    }

    public class HeatConduitRouterBuild extends HeatConductorBuild {
        public boolean[] capped = new boolean[4];
        boolean blend(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock){
            /*return (
            otherblock instanceof HeatConduit && (
                    (Tile.relativeTo(otherx, othery, tile.x, tile.y) != otherrot && Tile.relativeTo(tile.x, tile.y, otherx, othery) != (rotation + 2) % 4) ||
                    (Tile.relativeTo(otherx, othery, tile.x, tile.y) == otherrot && Tile.relativeTo(tile.x, tile.y, otherx, othery) == (rotation + 2) % 4)
            )) || (
            otherblock instanceof HeatConduitRouter && (
                    (Tile.relativeTo(tile.x, tile.y, otherx, othery) != (rotation + 2) % 4 && Tile.relativeTo(otherx, othery, tile.x, tile.y) == (otherrot + 2) % 4) ||
                    (Tile.relativeTo(tile.x, tile.y, otherx, othery) == (rotation + 2) % 4 && Tile.relativeTo(otherx, othery, tile.x, tile.y) != (otherrot + 2) % 4)
            ));*/
            return (otherblock.buildType.get() instanceof HeatBlock && Tile.relativeTo(otherx, othery, tile.x, tile.y) == otherrot) ||
                    (otherblock.buildType.get() instanceof HeatConsumer && Tile.relativeTo(tile.x, tile.y, otherx, othery) != Mathf.mod(rotation + 2, 4));
        }
        @Override
        public void draw(){
            Draw.color(botColor);
            Draw.rect(botRegion, x, y, rotdeg());
            Draw.color();
            Draw.rect(topRegion, x, y, rotdeg());
            Draw.blend(Blending.additive);
            Draw.tint(heatColor1, heatColor2, Mathf.clamp(heatFrac() / 4));
            Draw.alpha(heatFrac() * (1f - heatPulse + Mathf.absin(heatPulseScl, heatPulse)));
            Draw.rect(heatRegion, x, y, rotdeg());
            Draw.blend();
            Draw.reset();
            for (int i = 0; i < 4; i++){
                if (capped[i]) Draw.rect(capRegion, x, y, i * 90);
            }
        }
        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            for (int i = 0; i < 4; i++){
                Building b = nearby(i);
                capped[i] = b == null || !blend(tile, rotation, b.tileX(), b.tileY(), b.rotation, b.block);
            }
        }

        @Override
        public float heat(){
            int h = -1;
            for (int i = 0; i < 4; i++) {
                if (!capped[i])h += 1;
            }
            return heat / h * 3;
        }
    }
}
