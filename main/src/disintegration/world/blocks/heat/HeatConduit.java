package disintegration.world.blocks.heat;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Eachable;
import arc.util.Nullable;
import disintegration.graphics.Pal2;
import disintegration.util.WorldDef;
import disintegration.world.blocks.BuildingTiler;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.Autotiler;
import mindustry.world.blocks.distribution.ChainedBuilding;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConductor;
import mindustry.world.blocks.heat.HeatConsumer;

import java.util.Arrays;

import static mindustry.Vars.tilesize;

public class HeatConduit extends HeatConductor implements Autotiler{
    public TextureRegion[] topRegions;
    public TextureRegion[] botRegions;
    public TextureRegion[] heatRegions;
    public TextureRegion capRegion;
    public Color botColor = Color.valueOf("2f2d39");
    public Color heatColor1 = new Color(1f, 0.3f, 0.3f);
    public Color heatColor2 = Pal2.heat;
    public float heatPulse = 0.3f, heatPulseScl = 10f;
    public HeatConduit(String name) {
        super(name);
        rotate = true;
        update = true;
    }
    @Override
    public void load(){
        super.load();
        topRegions = new TextureRegion[5];
        botRegions = new TextureRegion[5];
        heatRegions = new TextureRegion[5];
        for(int i = 0; i < 5; i++){
            topRegions[i] = Core.atlas.find(name + "-top-" + i);
            botRegions[i] = Core.atlas.find("conduit-bottom-" + i);
            heatRegions[i] = Core.atlas.find(name + "-heat-" + i);
        }
        capRegion = Core.atlas.find(name + "-cap");
    }
    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{Core.atlas.find(name)};
    }

    public boolean blends(Tile tile, int rotation, @Nullable BuildPlan[] directional, int direction, boolean checkWorld){
        int realDir = Mathf.mod(rotation - direction, 4);
        if(directional != null && directional[realDir] != null){
            BuildPlan req = directional[realDir];
            if(blends(tile, rotation, req.x, req.y, req.rotation, req.block)){
                return true;
            }
        }
        return checkWorld && blends(tile, rotation, direction);
    }

    public int[] buildBlending(Tile tile, int rotation, BuildPlan[] directional, boolean world){
        int[] blendresult = new int[5];
        blendresult[0] = 0;
        blendresult[1] = blendresult[2] = 1;

        int num =
                (blends(tile, rotation, directional, 2, world) && blends(tile, rotation, directional, 1, world) && blends(tile, rotation, directional, 3, world)) ? 0 :
                        (blends(tile, rotation, directional, 1, world) && blends(tile, rotation, directional, 3, world)) ? 1 :
                                (blends(tile, rotation, directional, 1, world) && blends(tile, rotation, directional, 2, world)) ? 2 :
                                        (blends(tile, rotation, directional, 3, world) && blends(tile, rotation, directional, 2, world)) ? 3 :
                                                blends(tile, rotation, directional, 1, world) ? 4 :
                                                        blends(tile, rotation, directional, 3, world) ? 5 :
                                                                -1;
        transformCase(num, blendresult);

        // Calculate bitmask for direction.

        blendresult[3] = 0;

        for(int i = 0; i < 4; i++){
            if(blends(tile, rotation, directional, i, world)){
                blendresult[3] |= (1 << i);
            }
        }

        // Calculate direction for non-square sprites.

        blendresult[4] = 0;

        for(int i = 0; i < 4; i++){
            int realDir = Mathf.mod(rotation - i, 4);
            if(blends(tile, rotation, directional, i, world) && (tile != null && tile.nearbyBuild(realDir) != null && !tile.nearbyBuild(realDir).block.squareSprite)){
                blendresult[4] |= (1 << i);
            }
        }

        return blendresult;
    }

    public int[] getTiling(BuildPlan req, Eachable<BuildPlan> list){
        if(req.tile() == null) return null;
        BuildPlan[] directionals = new BuildPlan[4];

        Arrays.fill(directionals, null);
        //TODO this is O(n^2), very slow, should use quadtree or intmap or something instead
        list.each(other -> {
            if(other.breaking || other == req) return;

            int i = 0;
            for(Point2 point : Geometry.d4){
                int x = req.x + point.x, y = req.y + point.y;
                if(x >= other.x -(other.block.size - 1) / 2 && x <= other.x + (other.block.size / 2) && y >= other.y -(other.block.size - 1) / 2 && y <= other.y + (other.block.size / 2)){
                    directionals[i] = other;
                }
                i++;
            }
        });

        return buildBlending(req.tile(), req.rotation, directionals, req.worldContext);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        int[] bits = getTiling(plan, list);

        if(bits == null) return;

        Draw.scl(bits[1], bits[2]);
        Draw.color(botColor);
        Draw.alpha(0.5f);
        Draw.rect(botRegions[bits[0]], plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.color();
        Draw.rect(topRegions[bits[0]], plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.scl();
    }

    public void transformCase(int num, int[] bits){
        switch(num){
            case 0 -> bits[0] = 3;
            case 1 -> bits[0] = 4;
            case 2 -> bits[0] = 2;
            case 3 -> {
                bits[0] = 2;
                bits[2] = -1;
            }
            case 4 -> {
                bits[0] = 1;
                bits[2] = -1;
            }
            case 5 -> bits[0] = 1;
        }
    }

    public boolean blends(Tile tile, int rotation, int direction){
        Building other = tile.nearbyBuild(Mathf.mod(rotation - direction, 4));
        return other != null && other.team == tile.team() && blends(tile, rotation, other.tileX(), other.tileY(), other.rotation, other.block);
    }

    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
        return (otherblock instanceof HeatConduit && (
                Tile.relativeTo(tile.x, tile.y, otherx, othery) == rotation ||
                Tile.relativeTo(otherx, othery, tile.x, tile.y) == otherrot
            )
        ) ||
        otherblock instanceof HeatConduitRouter && (
                (Tile.relativeTo(tile.x, tile.y, otherx, othery) == rotation && Tile.relativeTo(otherx, othery, tile.x, tile.y) == (otherrot + 2) % 4) ||
                (Tile.relativeTo(tile.x, tile.y, otherx, othery) != rotation && Tile.relativeTo(otherx, othery, tile.x, tile.y) != (otherrot + 2) % 4)
        );
    }

    public class HeatConduitBuild extends HeatConductorBuild implements ChainedBuilding, BuildingTiler {

        @Override
        public boolean blendBuilds(Building block, Building other){
            return blends(block.tile, block.rotation, other.tileX(), other.tileY(), other.rotation, other.block) || (
                    ((other instanceof HeatBlock) && WorldDef.toBlock(block, other)) ||
                    ((other instanceof HeatConsumer) && WorldDef.toBlock(other, block))
            );
        }
        public int blendbits, xscl = 1, yscl = 1, blending;
        public boolean capped, backCapped = false;
        @Override
        public void draw(){
            int r = this.rotation;

            //draw extra conduits facing this one for tiling purposes
            Draw.z(Layer.blockUnder);
            for(int i = 0; i < 4; i++){
                if((blending & (1 << i)) != 0){
                    int dir = r - i;
                    drawAt(x + Geometry.d4x(dir) * tilesize*0.75f, y + Geometry.d4y(dir) * tilesize*0.75f, 0, i == 0 ? r : dir, i != 0 ? Autotiler.SliceMode.bottom : Autotiler.SliceMode.top);
                }
            }

            Draw.z(Layer.block);

            Draw.scl(xscl, yscl);
            drawAt(x, y, blendbits, r, Autotiler.SliceMode.none);
            Draw.reset();
        }

        public TextureRegion sliced(TextureRegion input, SliceMode mode){
            return mode == SliceMode.none ? input : mode == SliceMode.bottom ? botHalf(input) : topHalf(input);
        }
        protected void drawAt(float x, float y, int bits, int rotation, Autotiler.SliceMode slice){
            float angle = rotation * 90f;
            Draw.color(botColor);
            Draw.rect(sliced(botRegions[bits], slice), x, y, angle);
            Draw.color();
            Draw.rect(sliced(topRegions[bits], slice), x, y, angle);
            Draw.blend(Blending.additive);
            Draw.tint(heatColor1, heatColor2, Mathf.clamp(heatFrac() / 4));
            Draw.alpha(heatFrac() * (1f - heatPulse + Mathf.absin(heatPulseScl, heatPulse)));
            Draw.rect(sliced(heatRegions[bits], slice), x, y, angle);
            Draw.blend();
            Draw.reset();
            if(capped && capRegion.found()) Draw.rect(capRegion, x, y, rotdeg());
            if(backCapped && capRegion.found()) Draw.rect(capRegion, x, y, rotdeg() + 180);
        }
        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            int[] bits = buildBlending(this);
            blendbits = bits[0];
            xscl = bits[1];
            yscl = bits[2];
            blending = bits[4];

            Building next = front(), prev = back();
            capped = next == null || next.team != team || !blendBuilds(this, next);
            backCapped = blendbits == 0 && (prev == null || prev.team != team || !blendBuilds(this, prev));
        }

        @Nullable
        @Override
        public Building next(){
            Tile next = tile.nearby(rotation);
            if(next != null && next.build instanceof HeatConduitBuild){
                return next.build;
            }
            return null;
        }
    }
}
