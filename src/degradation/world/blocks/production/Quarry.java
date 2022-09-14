package degradation.world.blocks.production;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.Tmp;
import degradation.MathDef;
import degradation.content.DTFx;
import degradation.content.DTItems;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static arc.Core.atlas;
import static arc.math.Mathf.rand;
import static mindustry.Vars.*;

public class Quarry extends Block {
    public TextureRegion sideRegion1;
    public TextureRegion sideRegion2;
    public TextureRegion locator;
    public TextureRegion armRegion;
    public TextureRegion drill;
    public float mineTime = 600;
    public float liquidBoostIntensity = 2f;
    public int areaSize = 11;
    public Color areaColor = Pal.accent;
    public Color boostColor = Color.sky.cpy().mul(0.87f);
    public Effect drillEffect = new MultiEffect(DTFx.quarryDrillEffect, Fx.mine);
    public Item[] blackListItem = {Items.sand, Items.thorium, DTItems.iridium};
    public Quarry(String name){
        super(name);
        update = true;
        solid = true;
        rotate = true;
        group = BlockGroup.drills;
        hasItems = true;
        hasLiquids = true;
        acceptsItems = true;
        liquidCapacity = 5f;
        hasPower = true;
        //drills work in space I guess
        envEnabled |= Env.space;
        quickRotate = false;
    }
    @Override
    public void load(){
        super.load();
        sideRegion1 = atlas.find(name + "-side1");
        sideRegion2 = atlas.find(name + "-side2");
        locator = atlas.find(name + "-locator");
        armRegion = atlas.find(name + "-arm");
        drill = atlas.find(name + "-drill");
    }
    public Rect getRect(Rect rect, float x, float y, int rotation){
        rect.setCentered(x, y, areaSize * tilesize);
        float len = tilesize * (areaSize + size)/2f;

        rect.x += Geometry.d4x(rotation) * len;
        rect.y += Geometry.d4y(rotation) * len;

        return rect;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;

        Rect rect = getRect(Tmp.r1, x, y, rotation);

        Drawf.dashRect(valid ? Pal.accent : Pal.remove, rect);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(plan.rotation >= 2 ? sideRegion2 : sideRegion1, plan.drawx(), plan.drawy(), plan.rotation * 90);
    }
    public Item getDrop(Tile tile){
        return tile.overlay().itemDrop;
    }
    public Tile[][] getMiningTile(Vec2 pos, int width, int height){
        Tile[][] tilesGet = new Tile[width][height];
        int dx = (int) pos.x;
        int dy = (int) pos.y;
        for (int ix = 0; ix <= width - 1; ix ++){
            for (int iy = 0; iy <= height - 1; iy ++){
                Tile other = world.tile(ix + dx + 1, iy + dy + 1)/*.setBlock(Blocks.shockMine)*/;
                //Log.info(other);
                if(other != null) tilesGet[ix][iy] = other;
            }
        }
        return tilesGet;
    }
    public Item[][] getDropArray(Tile[][] tiles, int width, int height){
        Item[][] items = new Item[width + 1][height + 1];
        for (int ix = 0; ix <= width - 1; ix ++){
            for (int iy = 0; iy <= height - 1; iy ++){
                Tile tile = tiles[ix][iy];
                if (tile != null) items[ix][iy] = getDrop(tile);
            }
        }
        return items;
    }
    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.drillSpeed, 60f / mineTime * areaSize * areaSize / 4, StatUnit.itemsSecond);
        if(liquidBoostIntensity != 1){
            stats.add(Stat.boostEffect, liquidBoostIntensity, StatUnit.timesSpeed);
        }
    }
    public class QuarryBuild extends Building {
        float progress;
        float mx = 0, mxS = 0, mxP = 0, mxN = 0, mxM = 0, mxR = 0, mxL = 0,my = 0, myS = 0, myP = 0, myN = 0, myM = 0, myR = 0, myL = 0;
        float warmup;
        Color fullColor;
        float fulls = areaSize * tilesize/2f;
        float boostWarmup;
        float areaAlpha;
        float armAlphaAni;
        float armAlpha;
        float drillAlpha;
        float angle;
        boolean paused;
        Vec2 DrillPos;
        Tile[][] tiles;
        Item[][] itemsArray;

        public Vec2 getMiningArea(){
            float len = tilesize * (areaSize + size)/2f;
            float mineX = x + Geometry.d4x(rotation) * len, mineY = y + Geometry.d4y(rotation) * len;
            return Tmp.v4.set(mineX, mineY);
        }
        @Override
        public void updateTile(){
            tiles = new Tile[areaSize][areaSize];
            itemsArray = new Item[areaSize][areaSize];

            paused = state.isPaused();

            boostWarmup = Mathf.lerpDelta(boostWarmup, optionalEfficiency, 0.1f);

            warmup = Mathf.lerpDelta(warmup, efficiency, 0.1f);

            Vec2 MiningPos = new Vec2(World.conv(mx - fulls), World.conv(my - fulls));

            tiles = getMiningTile(MiningPos, areaSize, areaSize);

            itemsArray = getDropArray(tiles, areaSize, areaSize);

            Vec2 mineCentre = getMiningArea();

            mx = mineCentre.x;
            my = mineCentre.y;
            float speed = 0;
            if(items.total() < itemCapacity && efficiency > 0) {
                speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency;
            }
            if (efficiency > 0 && armAlphaAni == 0) {
                progress += delta() * speed;
            }
            if(itemsArray != null && progress >= mineTime && items.total() < itemCapacity && armAlphaAni == 0){
                progress %= mineTime;
                DrillPos = new Vec2(mxM + mx, myM + my);
                Item tileItem;
                for (int ix = 0; ix <= areaSize - 1; ix ++){
                    for (int iy = 0; iy <= areaSize - 1; iy ++){
                        float dx = ix * 8 + mx - fulls + 4;
                        float dy = iy * 8 + my - fulls + 4;
                        tileItem = itemsArray[ix][iy];
                        if (tileItem != null){
                            if (items.total() < itemCapacity){
                                Fx.itemTransfer.at(dx + Mathf.range(1f), dy + Mathf.range(1f), 0, tileItem.color, DrillPos);
                                offload(tileItem);
                            }
                        }
                    }
                }
            }
            areaAlpha = efficiency > 0 ? 1 : Math.abs(myP - 0.1) <= 0.1 ? 0 : 1;
            if (efficiency > 0 || items.total() >= itemCapacity) {
                mxS = MathDef.lerp(mxS, mx - fulls - x, 4, 2, paused);
                mxP = MathDef.lerp(mxP, mx + fulls - x, 4, 2, paused);
                myS = MathDef.lerp(myS, my - fulls - y, 4, 2, paused);
                myP = MathDef.lerp(myP, my + fulls - y, 4, 2, paused);
                if (Math.abs(myP - (my + fulls - y)) <= 0.1){
                    mxN = MathDef.lerp(mxN, -fulls, 4, 3, paused);
                    myN = MathDef.lerp(myN, -fulls, 4, 3, paused);
                    if (mxN + fulls >= -0.01) {
                        mxL = MathDef.lerp(mxL, -fulls, 4, 3, paused);
                        myL = MathDef.lerp(myL, -fulls, 4, 3, paused);
                        if(mxL + fulls >= -0.01) {
                            drillAlpha = MathDef.lerp(drillAlpha, 1, 4, 2, paused);
                            if (1 - drillAlpha <= 0.01){
                                armAlphaAni = 0;
                                armAlpha = 1;
                                if (items.total() < itemCapacity) {
                                    if (Math.abs(mxM - mxR) <= 0.01) {
                                        mxR = rand.random(fulls - fulls / 3, -fulls + fulls / 3);
                                        myR = rand.random(fulls - fulls / 3, -fulls + fulls / 3);
                                    }
                                    mxM = MathDef.linear(mxM, mxR, 0.1f, paused);
                                    myM = MathDef.linear(myM, myR, 0.1f, paused);
                                }
                                else{
                                    mxR = 0;
                                    myR = 0;
                                    mxM = MathDef.lerp(mxM, mxR, 4, 4, paused);
                                    myM = MathDef.lerp(myM, myR, 4, 4, paused);
                                }
                            }
                        }
                        else{
                            armAlphaAni = 1;
                            armAlpha = 0;
                        }
                    }
                }
            }
            else{
                mxR = 0;
                myR = 0;
                mxM = MathDef.lerp(mxM, mxR, 4, 4, paused);
                myM = MathDef.lerp(myM, myR, 4, 4, paused);
                if(Math.abs(mxM) <= 0.001) {
                    drillAlpha = MathDef.lerp(drillAlpha, 0, 4, 2, paused);
                    if (drillAlpha <= 0.01){
                        armAlphaAni = 1;
                        armAlpha = 0;
                        myL = MathDef.lerp(mxL, -2 * fulls, 4, 4, paused);
                        mxL = MathDef.lerp(myL, -2 * fulls, 4, 4, paused);
                        if (mxL <= -2 * fulls + 0.006) {
                            if (mxN <= -2 * fulls + 0.006) {
                                mxS = MathDef.lerp(mxS, 0, 4, 2, paused);
                                mxP = MathDef.lerp(mxP, 0, 4, 2, paused);
                                myS = MathDef.lerp(myS, 0, 4, 2, paused);
                                myP = MathDef.lerp(myP, 0, 4, 2, paused);
                            }
                            mxN = MathDef.lerp(mxN, -2 * fulls, 4, 4, paused);
                            myN = MathDef.lerp(myN, -2 * fulls, 4, 4, paused);
                        }
                    }
                }
            }
            dumpOutputs();
        }

        public void dumpOutputs(){
            for(Item[] tileItems : itemsArray){
                for(Item tileItem : tileItems){
                    dump(tileItem);
                }
            }
        }
        @Override
        public void draw(){
            fullColor = Tmp.c1.set(areaColor).lerp(boostColor, boostWarmup);
            Draw.rect(region, x, y);

            Draw.rect(rotation >= 2 ? sideRegion2 : sideRegion1, x, y, rotdeg());

            Draw.z(Layer.buildBeam);
            //draw full area
            Lines.stroke(2f, fullColor);
            Draw.alpha(warmup);
            Drawf.dashRectBasic(mx - fulls, my - fulls, fulls*2f, fulls*2f);

            Draw.reset();
            Draw.color();
            Draw.alpha(areaAlpha);
            Draw.z(Layer.buildBeam + 1.3f);
            //draw locator
            Vec2 locator1 = new Vec2(mxS + x, myS + y);
            Vec2 locator2 = new Vec2(mxP + x, myS + y);
            Vec2 locator3 = new Vec2(mxS + x, myP + y);
            Vec2 locator4 = new Vec2(mxP + x, myP + y);

            Draw.rect(locator, locator1.x, locator1.y);
            Draw.rect(locator, locator2.x, locator2.y);
            Draw.rect(locator, locator3.x, locator3.y);
            Draw.rect(locator, locator4.x, locator4.y);
            //Draw arm
            Draw.z(Layer.buildBeam + 1.2f);
            Lines.stroke(3.5f);
            //up
            Lines.line(armRegion,
                    mx - fulls,
                    my + fulls,
                    mxN + mx + fulls,
                    my + fulls, false
            );
            Lines.line(armRegion,
                    mx + fulls,
                    my + fulls,
                    -mxN + mx - fulls,
                    my + fulls, false
            );
            //down
            Lines.line(armRegion,
                    mx - fulls,
                    my - fulls,
                    mxN + mx + fulls,
                    my - fulls, false
            );
            Lines.line(armRegion,
                    mx + fulls,
                    my - fulls,
                    -mxN + mx - fulls,
                    my - fulls, false
            );
            //right
            Lines.line(armRegion,
                    mx + fulls,
                    my - fulls,
                    mx + fulls,
                    myN + my + fulls, false
            );
            Lines.line(armRegion,
                    mx + fulls,
                    my + fulls,
                    mx + fulls,
                    -myN + my - fulls, false
            );
            //left
            Lines.line(armRegion,
                    mx - fulls,
                    my - fulls,
                    mx - fulls,
                    myN + my + fulls, false
            );
            Lines.line(armRegion,
                    mx - fulls,
                    my + fulls,
                    mx - fulls,
                    -myN + my - fulls, false
            );
            //draw across arm animation
            Draw.z(Layer.buildBeam + 1.1f);
            Draw.alpha(armAlphaAni);
            Lines.line(armRegion,
                    mx,
                    my - fulls,
                    mx,
                    myL + my + fulls, false
            );
            Lines.line(armRegion,
                    mx,
                    my + fulls,
                    mx,
                    -myL + my - fulls, false
            );
            Lines.line(armRegion,
                    mx - fulls,
                    my,
                    mxL + mx + fulls,
                    my, false
            );
            Lines.line(armRegion,
                    mx + fulls,
                    my,
                    -mxL + mx - fulls,
                    my, false
            );
            //draw across arm
            Draw.alpha(armAlpha);
            Lines.line(armRegion,
                    mxM + mx,
                    my - fulls,
                    mxM + mx,
                    my + fulls, false
            );
            Lines.line(armRegion,
                    mx - fulls,
                    myM + my,
                    mx + fulls,
                    myM + my , false
            );
            //draw drill
            Draw.z(Layer.buildBeam + 1.2f);

            Draw.alpha(drillAlpha);

            Draw.rect(drill, mxM + mx, myM + my);
            //draw ore stroke
            Draw.z(Layer.blockOver);

            Lines.stroke(0.6f);
            if (1 - drillAlpha <= 0.01 && !paused && Mathf.range(1) >= 0 && efficiency > 0 && items.total() < itemCapacity) {
                angle = rand.random(0, 360f);
                drillEffect.at(mxM + mx, myM + my, angle, fullColor);
            }

            DrillPos = new Vec2(mxM + mx, myM + my);
            Item tileItem;
            for (int ix = 0; ix <= areaSize - 1; ix ++){
                for (int iy = 0; iy <= areaSize - 1; iy ++){
                    float dx = ix * 8 + mx - fulls + 4;
                    float dy = iy * 8 + my - fulls + 4;
                    tileItem = itemsArray[ix][iy];
                    if (tileItem != null){
                        for (int i = 0; i <= 1; i ++) {

                            Draw.color(tileItem.color);

                            Drawf.light(dx, dy, 4, tileItem.color, 1);
                            float rot = i * 360f / 2 - Time.time * 1.1f;
                            Draw.alpha(warmup);
                            Lines.arc(dx, dy, 4f, 0.3f, rot);
                        }
                    }
                }
            }
        }
        @Override
        public boolean shouldConsume(){
            return items.total() < itemCapacity && enabled;
        }
    }
}
