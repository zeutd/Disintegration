package degradation.world.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import degradation.MathDef;
import degradation.content.DTFx;
import degradation.content.DTItems;
import mindustry.content.Blocks;
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

    protected final float fulls = areaSize * tilesize/2f;

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

        Vec2 mineCentre = getMiningArea(x, y, rotation);

        int mx = (int) (x + Geometry.d4x(rotation) * (areaSize/2f-(areaSize % 2)/2f-sizeOffset*2)-areaSize/2f+(areaSize % 2));
        int my = (int) (y + Geometry.d4y(rotation) * (areaSize/2f-(areaSize % 2)/2f-sizeOffset*2)-areaSize/2f+(areaSize % 2));

        Tile[][] tiles = getMiningTile(new Vec2(mx - 1, my - 1), areaSize, areaSize);

        Item[][] items = getDropArray(tiles, areaSize - 1, areaSize - 1);

        Seq<Item> itemList = listItem(items);

        drawDrillText(itemList, items, x, y, valid);
        Log.info(itemList);
        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;

        Rect rect = getRect(Tmp.r1, x, y, rotation);

        Drawf.dashRect(valid ? Pal.accent : Pal.remove, rect);
    }

    public void drawText(Item item, int count, int x, int y, boolean valid, int layer){
        if(item != null){
            float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / mineTime * count, 2), x, y + layer, valid);
            float dx = x * tilesize + offset - width/2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5 + layer * 8, s = iconSmall / 4f;
            Draw.mixcol(Color.darkGray, 1f);
            Draw.rect(item.fullIcon, dx, dy - 1, s, s);
            Draw.reset();
            Draw.rect(item.fullIcon, dx, dy, s, s);
        }
    }

    public int countOre(Item target, Item[][] array){
        int count = 0;
        for (Item[] items : array){
            for (Item item : items){
                if (item == target) {
                    count += 1;
                }
            }
        }
        return count;
    }
    public void drawDrillText(Seq<Item> array, Item[][] all, int x, int y, boolean valid){
        int layer = 0;
        int count;
        for (Item item : array){
            count = countOre(item, all);
            drawText(item, count, x, y, valid, layer);
            if (item != null) {
                layer += 1;
            }
        }
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
        Item[][] items = new Item[width][height];
        for (int ix = 0; ix <= width - 1; ix ++){
            for (int iy = 0; iy <= height - 1; iy ++){
                Tile tile = tiles[ix][iy];
                if (tile != null && tile.block() == Blocks.air) items[ix][iy] = getDrop(tile);
                else items[ix][iy] = null;
            }
        }
        return items;
    }

    public Vec2 getMiningArea(float x, float y, int rotation){
        float len = tilesize * (areaSize + size)/2f;
        float mineX = x + Geometry.d4x(rotation) * len, mineY = y + Geometry.d4y(rotation) * len;
        return Tmp.v4.set(mineX, mineY);
    }
    public Seq<Item> listItem(Item[][] array){
        Seq<Item> items = new Seq<>();
        for(Item[] tileItems : array){
            for(Item tileItem : tileItems){
                if (!items.contains(tileItem)) items.add(tileItem);
            }
        }
        return items;
    }
    public boolean getEmptyDrop(Item[][] array){
        boolean empty = false;
        for (Item[] items : array){
            for (Item item : items){
                if (item != null) {
                    empty = true;
                    break;
                }
            }
        }
        return empty;
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
        float mx = 0, mxS = 0, mxP = 0, mxM = 0, mxR = 0, my = 0, myS = 0, myP = 0, myM = 0, myR = 0, mN = -2 * fulls, mL = -2 * fulls;
        float warmup;
        Color fullColor;
        float boostWarmup;
        float areaAlpha;
        float armAlphaAni;
        float armAlpha;
        float drillAlpha;
        float angle;
        boolean paused;

        boolean empty;
        Vec2 DrillPos;
        Tile[][] tiles;
        Item[][] itemsArray;

        Seq<Item> itemList;

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

            empty = getEmptyDrop(itemsArray);

            itemList = listItem(itemsArray);

            Vec2 mineCentre = getMiningArea(x, y, rotation);

            mx = mineCentre.x;
            my = mineCentre.y;
            float speed = 0;
            if(items.total() < itemCapacity && efficiency > 0) {
                speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency;
            }
            if (efficiency > 0 && armAlphaAni == 0) {
                progress += delta() * speed;
            }
            if(empty && progress >= mineTime && items.total() < itemCapacity && armAlphaAni == 0 && !paused){
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
            areaAlpha = efficiency > 0 && empty ? 1 : Math.abs(myP - 0.1) <= 0.1 ? 0 : 1;
            if (empty) {
                if (efficiency > 0 || items.total() >= itemCapacity) {
                    mxS = MathDef.lerp(mxS, mx - fulls - x, 4, 2, paused);
                    mxP = MathDef.lerp(mxP, mx + fulls - x, 4, 2, paused);
                    myS = MathDef.lerp(myS, my - fulls - y, 4, 2, paused);
                    myP = MathDef.lerp(myP, my + fulls - y, 4, 2, paused);
                    if (Math.abs(myP - (my + fulls - y)) <= 0.1) {
                        mN = MathDef.lerp(mN, -fulls, 4, 5, paused);
                        if (mN + fulls >= -0.01) {
                            mL = MathDef.lerp(mL, -fulls, 4, 5, paused);
                            if (mL + fulls >= -0.01) {
                                drillAlpha = MathDef.lerp(drillAlpha, 1, 4, 2, paused);
                                if (1 - drillAlpha <= 0.01) {
                                    armAlphaAni = 0;
                                    armAlpha = 1;
                                    if (items.total() < itemCapacity) {
                                        if (Math.abs(mxM - mxR) <= 0.01) {
                                            mxR = rand.random(fulls - fulls / 3, -fulls + fulls / 3);
                                            myR = rand.random(fulls - fulls / 3, -fulls + fulls / 3);
                                        }
                                        mxM = MathDef.linear(mxM, mxR, 0.07f, paused);
                                        myM = MathDef.linear(myM, myR, 0.07f, paused);
                                    } else {
                                        mxR = 0;
                                        myR = 0;
                                        mxM = MathDef.lerp(mxM, mxR, 4, 6, paused);
                                        myM = MathDef.lerp(myM, myR, 4, 6, paused);
                                    }
                                }
                            } else {
                                armAlphaAni = 1;
                                armAlpha = 0;
                            }
                        }
                    }
                } else {
                    mxR = 0;
                    myR = 0;
                    mxM = MathDef.lerp(mxM, mxR, 4, 5, paused);
                    myM = MathDef.lerp(myM, myR, 4, 5, paused);
                    if (Math.abs(mxM) <= 0.001) {
                        drillAlpha = MathDef.lerp(drillAlpha, 0, 4, 2, paused);
                        if (drillAlpha <= 0.01) {
                            armAlphaAni = 1;
                            armAlpha = 0;
                            mL = MathDef.lerp(mL, -2 * fulls, 4, 6, paused);
                            if (mL <= -2 * fulls + 0.006) {
                                if (mN <= -2 * fulls + 0.006) {
                                    mxS = MathDef.lerp(mxS, 0, 4, 2, paused);
                                    mxP = MathDef.lerp(mxP, 0, 4, 2, paused);
                                    myS = MathDef.lerp(myS, 0, 4, 2, paused);
                                    myP = MathDef.lerp(myP, 0, 4, 2, paused);
                                }
                                mN = MathDef.lerp(mN, -2 * fulls, 4, 5, paused);
                            }
                        }
                    }
                }
            }
            dumpOutputs(itemList);
        }

        public void dumpOutputs(Seq<Item> array){
            array.forEach(this::dump);
        }
        @Override
        public void draw(){
            super.draw();

            fullColor = Tmp.c1.set(areaColor).lerp(boostColor, boostWarmup);
            Draw.rect(region, x, y);

            Draw.rect(rotation >= 2 ? sideRegion2 : sideRegion1, x, y, rotdeg());

            Draw.z(Layer.buildBeam);
            //draw full area
            Lines.stroke(2f, fullColor);
            Draw.alpha(warmup);
            if (empty) Drawf.dashRectBasic(mx - fulls, my - fulls, fulls*2f, fulls*2f);
            Draw.reset();
            Draw.color();
            Draw.alpha(areaAlpha);
            Draw.z(Layer.buildBeam + 1.3f);
            //draw locator
            if (empty) {
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
                        mN + mx + fulls,
                        my + fulls, false
                );
                Lines.line(armRegion,
                        mx + fulls,
                        my + fulls,
                        -mN + mx - fulls,
                        my + fulls, false
                );
                //down
                Lines.line(armRegion,
                        mx - fulls,
                        my - fulls,
                        mN + mx + fulls,
                        my - fulls, false
                );
                Lines.line(armRegion,
                        mx + fulls,
                        my - fulls,
                        -mN + mx - fulls,
                        my - fulls, false
                );
                //right
                Lines.line(armRegion,
                        mx + fulls,
                        my - fulls,
                        mx + fulls,
                        mN + my + fulls, false
                );
                Lines.line(armRegion,
                        mx + fulls,
                        my + fulls,
                        mx + fulls,
                        -mN + my - fulls, false
                );
                //left
                Lines.line(armRegion,
                        mx - fulls,
                        my - fulls,
                        mx - fulls,
                        mN + my + fulls, false
                );
                Lines.line(armRegion,
                        mx - fulls,
                        my + fulls,
                        mx - fulls,
                        -mN + my - fulls, false
                );
                //draw across arm animation
                Draw.z(Layer.buildBeam + 1.1f);
                Draw.alpha(armAlphaAni);
                Lines.line(armRegion,
                        mx,
                        my - fulls,
                        mx,
                        mL + my + fulls, false
                );
                Lines.line(armRegion,
                        mx,
                        my + fulls,
                        mx,
                        -mL + my - fulls, false
                );
                Lines.line(armRegion,
                        mx - fulls,
                        my,
                        mL + mx + fulls,
                        my, false
                );
                Lines.line(armRegion,
                        mx + fulls,
                        my,
                        -mL + mx - fulls,
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
                        myM + my, false
                );
                //draw drill
                Draw.z(Layer.buildBeam + 1.2f);

                Draw.alpha(drillAlpha);

                Draw.rect(drill, mxM + mx, myM + my);
                //draw ore stroke
                Draw.z(Layer.blockOver);

                Lines.stroke(0.6f);
                if (1 - drillAlpha <= 0.01 && !paused && Mathf.range(1) >= 0 && efficiency > 0 && items.total() < itemCapacity && !empty) {
                    angle = rand.random(0, 360f);
                    drillEffect.at(mxM + mx, myM + my, angle, fullColor);
                }

                DrillPos = new Vec2(mxM + mx, myM + my);
                Item tileItem;
                for (int ix = 0; ix <= areaSize - 1; ix++) {
                    for (int iy = 0; iy <= areaSize - 1; iy++) {
                        float dx = ix * 8 + mx - fulls + 4;
                        float dy = iy * 8 + my - fulls + 4;
                        tileItem = itemsArray[ix][iy];
                        if (tileItem != null) {
                            for (int i = 0; i <= 1; i++) {

                                Draw.color(tileItem.color);

                                Drawf.light(dx, dy, 5, tileItem.color, 50);
                                float rot = i * 360f / 2 - Time.time * 1.1f;
                                Draw.alpha(warmup);
                                Lines.arc(dx, dy, 4f, 0.3f, rot);
                            }
                        }
                    }
                }
            }
        }
        @Override
        public boolean shouldConsume(){
            return items.total() < itemCapacity && enabled;
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.f(warmup);

            write.f(mxP);
            write.f(mxS);
            write.f(mxM);

            write.f(myP);
            write.f(myS);
            write.f(myM);

            write.f(mL);
            write.f(mN);

            write.f(drillAlpha);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1){
                progress = read.f();
                warmup = read.f();

                mxP = read.f();
                mxS = read.f();
                mxM = read.f();

                myP = read.f();
                myS = read.f();
                myM = read.f();

                mL = read.f();
                mN = read.f();

                drillAlpha = read.f();
            }
        }
    }
}
