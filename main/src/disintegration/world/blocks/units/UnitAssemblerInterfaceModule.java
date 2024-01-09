package disintegration.world.blocks.units;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.player;
import static mindustry.Vars.tilesize;
public class UnitAssemblerInterfaceModule extends PayloadBlock {
    public TextureRegion sideRegion1;
    public TextureRegion sideRegion2;

    public UnitAssemblerInterfaceModule(String name) {
        super(name);
        rotate = true;
        rotateDraw = false;
        acceptsPayload = true;
    }

    @Override
    public void load(){
        super.load();
        sideRegion1 = Core.atlas.find(name + "-side1");
        sideRegion2 = Core.atlas.find(name + "-side2");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        var link = getLink(player.team(), x, y, rotation);
        if(link != null){
            link.block.drawPlace(link.tile.x, link.tile.y, link.rotation, true);
        }
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return getLink(team, tile.x, tile.y, rotation) != null;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(plan.rotation >= 2 ? sideRegion2 : sideRegion1, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.rect(topRegion, plan.drawx(), plan.drawy());
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }

    public @Nullable UnitAssembler.UnitAssemblerBuild getLink(Team team, int x, int y, int rotation){
        var results = Vars.indexer.getFlagged(team, BlockFlag.unitAssembler).<UnitAssembler.UnitAssemblerBuild>as();

        return results.find(b -> b.moduleFits(this, x * tilesize + offset, y * tilesize + offset, rotation));
    }

    public class UnitAssemblerInterfaceModuleBuild extends PayloadBlockBuild<Payload>{
        public UnitAssembler.UnitAssemblerBuild link;

        @Override
        public void draw(){
            Draw.rect(region, x, y);

            //draw input conveyors
            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                }
            }

            Draw.rect(rotation >= 2 ? sideRegion2 : sideRegion1, x, y, rotdeg());

            Draw.z(Layer.blockOver);
            payRotation = rotdeg();
            drawPayload();
            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRegion, x, y);
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return link != null && this.payload == null && link.acceptPayload(this, payload);
        }

        @Override
        public void drawSelect(){
            if(link != null){
                Drawf.selected(link, Pal.accent);
            }
        }

        @Override
        public void updateTile(){
            link = getLink(team, tile.x, tile.y, rotation);
            if(moveInPayload() && link != null && link.moduleFits(block, x, y, rotation) && !link.wasOccupied && link.acceptPayload(this, payload) && efficiency > 0){
                link.yeetPayload(payload);
                payload = null;
            }
        }
    }
}
