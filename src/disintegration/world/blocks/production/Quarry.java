package disintegration.world.blocks.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.content.DTFx;
import disintegration.util.DTUtil;
import disintegration.util.WorldDef;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
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
import static arc.Core.graphics;
import static arc.math.Mathf.clamp;
import static arc.math.Mathf.rand;
import static mindustry.Vars.iconSmall;
import static mindustry.Vars.tilesize;

public class Quarry extends Block {
    public TextureRegion sideRegion1;
    public TextureRegion sideRegion2;
    public TextureRegion locator;
    public TextureRegion armRegion;
    public TextureRegion drill;
    public float mineTime;
    public float liquidBoostIntensity;
    public int areaSize;
    public Color areaColor = Pal.accent;
    public Color boostColor = Color.sky.cpy().mul(0.87f);
    public Effect drillEffect = new MultiEffect(DTFx.quarryDrillEffect, Fx.mine);

    public Interp deployInterp;
    public Interp deployInterpInverse;

    public float drillMoveSpeed;
    public float deploySpeed;
    public float drillMargin = 20f;
    public float elevation = 8f;

    protected float fulls = areaSize * tilesize/2f;

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
        updateInUnits = false;
        quickRotate = false;
        ambientSoundVolume = 0.05f;
        ambientSound = Sounds.minebeam;
    }

    @Override
    public void init(){
        updateClipRadius((size + areaSize + 1) * 8);
        super.init();
        fulls = areaSize * tilesize/2f;
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

        int mx = (int) (x + Geometry.d4x(rotation) * (areaSize / 2f - (areaSize % 2) / 2f - sizeOffset * 2) - areaSize / 2f + (areaSize % 2));
        int my = (int) (y + Geometry.d4y(rotation) * (areaSize / 2f - (areaSize % 2) / 2f - sizeOffset * 2) - areaSize / 2f + (areaSize % 2));

        Seq<Tile> tiles = WorldDef.getAreaTile(new Vec2(mx - 1, my - 1), areaSize, areaSize);

        Seq<Item> items = getDropArray(tiles);

        Seq<Item> itemList = DTUtil.listItem(items);

        drawDrillText(itemList, items, x, y, valid);
        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;

        Rect rect = getRect(Tmp.r1, x, y, rotation);

        Drawf.dashRect(valid ? Pal.accent : Pal.remove, rect);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        int mx = (int) (tile.x + Geometry.d4x(rotation) * (areaSize/2f-(areaSize % 2)/2f-sizeOffset*2)-areaSize/2f+(areaSize % 2));
        int my = (int) (tile.y + Geometry.d4y(rotation) * (areaSize/2f-(areaSize % 2)/2f-sizeOffset*2)-areaSize/2f+(areaSize % 2));

        Seq<Tile> tiles = WorldDef.getAreaTile(new Vec2(mx - 1, my - 1), areaSize, areaSize);

        Seq<Item> items = getDropArray(tiles);
        Seq<Item> itemList = DTUtil.listItem(items);
        return !itemList.isEmpty();
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

    public int countOre(Item target, Seq<Item> array){
        int count = 0;
        for (Item item : array){
            if (item == target) {
                count += 1;
            }
        }
        return count;
    }
    public void drawDrillText(Seq<Item> array, Seq<Item> all, int x, int y, boolean valid){
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
    public Seq<Item> getDropArray(Seq<Tile> tiles){
        Seq<Item> items = new Seq<>();
        tiles.forEach(tile -> {
            if (tile != null && tile.block() == Blocks.air) items.add(getDrop(tile));
            else items.add((Item)null);
        });
        return items;
    }

    @Override
    public boolean rotatedOutput(int x, int y){
        return false;
    }

    public Vec2 getMiningArea(float x, float y, int rotation){
        float len = tilesize * (areaSize + size)/2f;
        float mineX = x + Geometry.d4x(rotation) * len, mineY = y + Geometry.d4y(rotation) * len;
        return Tmp.v4.set(mineX, mineY);
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
        private FrameBuffer shadow = null;
        public float progress;
        public float deployProgress;
        //float mx = 0, mxS = 0, mxP = 0, mxM = 0, mxR = 0, my = 0, myS = 0, myP = 0, myM = 0, myR = 0, mN = -2 * fulls, mL = -2 * fulls;
        public float warmup;
        Color fullColor;
        public float boostWarmup;
        public boolean drawDrill;
        public boolean deploying;
        public boolean empty;
        Seq<Tile> tiles = new Seq<>();
        Seq<Item> itemsArray = new Seq<>();

        Seq<Item> itemList = new Seq<>();

        public float mx = 0, my = 0, drillX = 0, drillY = 0, targetX = 0, targetY = 0;

        public float deployProgress1, deployProgress2, deployProgress3, deployProgress4;

        Interp inp;

        @Override
        public void updateTile(){
            inp = deploying ? deployInterp : deployInterpInverse;

            boostWarmup = Mathf.lerpDelta(boostWarmup, optionalEfficiency, 0.1f);

            warmup = Mathf.lerpDelta(warmup, efficiency, 0.1f);

            Vec2 mineCentre = getMiningArea(x, y, rotation);
            mx = mineCentre.x;
            my = mineCentre.y;

            Vec2 MiningPos = new Vec2(World.conv(mx - fulls), World.conv(my - fulls));

            tiles = WorldDef.getAreaTile(MiningPos, areaSize, areaSize);

            itemsArray = getDropArray(tiles);

            empty = itemsArray.isEmpty();

            itemList = DTUtil.listItem(itemsArray);

            fullColor = Tmp.c1.set(areaColor).lerp(boostColor, boostWarmup);
            float speed = 0;
            if(items.total() < itemCapacity && efficiency > 0) {
                speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency;
            }
            if(!empty && progress >= mineTime && items.total() < itemCapacity && deployProgress >= 4){
                progress = 0;
                Item tileItem;
                for (int ix = 0; ix < areaSize; ix ++){
                    for (int iy = 0; iy < areaSize; iy ++){
                        float dx = ix * 8 + mx - fulls + 4;
                        float dy = iy * 8 + my - fulls + 4;
                        tileItem = itemsArray.get(ix * areaSize + iy);
                        if (tileItem != null){
                            if (items.total() < itemCapacity){
                                Fx.itemTransfer.at(dx + Mathf.range(1f), dy + Mathf.range(1f), 0, tileItem.color, new Vec2(drillX + mx, drillY + my));
                                offload(tileItem);
                            }
                        }
                    }
                }
            }
            if (deployProgress >= 4 && Mathf.range(1) >= 0 && efficiency > 0 && items.total() < itemCapacity) {
                drillEffect.at(drillX + mx, drillY + my, rand.random(0, 360f), fullColor);
            }
            drawDrill = deployProgress > 0;
            if (!empty) {
                if (efficiency > 0){
                    if(deployProgress <= 4) {
                        deployProgress += delta() * deploySpeed;
                        deploying = true;
                    }
                    else progress += delta() * speed;
                }
                else if(deployProgress > 0 && Mathf.zero(drillX, 0.01f) && Mathf.zero(drillY, 0.01f) && items.total() < itemCapacity){
                    deployProgress -= delta() * deploySpeed;
                    deploying = false;
                }
                if (items.total() >= itemCapacity || efficiency <= 0 ) {
                    drillX = Mathf.lerpDelta(drillX, 0, 0.07f);
                    drillY = Mathf.lerpDelta(drillY, 0, 0.07f);
                } else {
                    if (Mathf.equal(drillX, targetX) && Mathf.equal(drillY, targetY)){
                        targetX = Mathf.randomSeed(id + (long)Time.time, (float) -areaSize / 2 * tilesize + drillMargin, (float) areaSize / 2 * tilesize - drillMargin);
                        targetY = Mathf.randomSeed(id / 2 + (long)Time.time, (float) -areaSize / 2 * tilesize + drillMargin, (float) areaSize / 2 * tilesize - drillMargin);
                    }
                    if(deployProgress >= 4 && efficiency > 0) {
                        drillX = Mathf.approachDelta(drillX, targetX, drillMoveSpeed);
                        drillY = Mathf.approachDelta(drillY, targetY, drillMoveSpeed);
                    }
                }
            }
            else deployProgress = 0;
            dumpOutputs();
        }

        public void dumpOutputs(){
            if(!empty && timer(timerDump, dumpTime / timeScale)){
                for(Item output : itemList){
                    dump(output);
                }
            }
        }

        public void drawDrill(float x, float y, float mx, float my, float layer){
            Draw.z(layer - 1f);

                for (Point2 p : Geometry.d8edge) {
                    Draw.rect(locator,
                            x + deployProgress1 * (mx - x + fulls * p.x),
                            y + deployProgress1 * (my - y + fulls * p.y)
                    );
                }
            //Draw arm
            Draw.z(layer - 1.1f);
            Lines.stroke(3.5f);
            if (deployProgress > 2) {
                for (int i = 0; i < Geometry.d8edge.length; i++){
                    Point2 p1 = Geometry.d8edge(i);
                    Point2 p2 = Geometry.d8edge(i + 1);
                    Lines.line(armRegion,
                            mx + fulls * p1.x,
                            my + fulls * p1.y,
                            mx + fulls * p2.x,
                            my + fulls * p2.y,
                            false
                    );
                }
            }
            else if(deployProgress > 1) {
                for (Point2 p : Geometry.d8edge) {
                    for (int d : Mathf.zeroOne) {
                        Lines.line(armRegion,
                                mx + fulls * p.x,
                                my + fulls * p.y,
                                deployProgress2 * -(-d + 1) * fulls * p.x + mx + fulls * p.x,
                                deployProgress2 *    -d     * fulls * p.y + my + fulls * p.y,
                                false
                        );
                    }
                }
            }
            //draw across arm
            Draw.z(layer - 1.2f);
            if (deployProgress > 3) {
                /*Lines.line(armRegion,
                        drillX * 0 + mx + fulls * 1,
                        drillY * 1 + my + fulls * 0,
                        drillX * 0 + mx + fulls * 1,
                        drillY * 1 + my + fulls * 0,
                        false
                );*/
                Lines.line(armRegion,
                        mx + drillX,
                        my + fulls,
                        mx + drillX,
                        my - fulls,
                        false
                );
                Lines.line(armRegion,
                        mx + fulls,
                        my + drillY,
                        mx - fulls,
                        my + drillY,
                        false
                );
            }
            else if (deployProgress > 2){
                for (int d : Mathf.zeroOne) {
                    for (int i : Mathf.signs) {
                        Lines.line(armRegion,
                                drillX * d + mx + fulls * i * (-d + 1),
                                drillY * (-d + 1) + my + fulls * i * d,
                                drillX * d + mx + i * (-d + 1) * (-deployProgress3 * fulls) + fulls * i * (-d + 1),
                                drillY * (-d + 1) + my + i * d * (-deployProgress3 * fulls) + fulls * i * d,
                                false
                        );
                    }
                }
            }
            //draw drill
            Draw.z(layer - 1.1f);

            if (deployProgress4 > 0)Draw.alpha(deployProgress4 * Draw.getColor().a);

            Draw.rect(drill, drillX + mx, drillY + my);
        }
        @Override
        public void draw(){
            if (deployProgress >= 0 && deployProgress <= 1)deployProgress1 = inp.apply(clamp(deployProgress - 0));
            else if (deployProgress >= 1 && deployProgress <= 2)deployProgress2 = inp.apply(clamp(deployProgress - 1));
            else if (deployProgress >= 2 && deployProgress <= 3)deployProgress3 = inp.apply(clamp(deployProgress - 2));
            else if (deployProgress >= 3 && deployProgress <= 4)deployProgress4 = inp.apply(clamp(deployProgress - 3));
            Draw.rect(region, x, y);
            Draw.rect(rotation >= 2 ? sideRegion2 : sideRegion1, x, y, rotdeg());
            drawDrill(x, y, mx, my, Layer.flyingUnitLow - 0.01f);
            Draw.draw(Layer.floor + 2, () -> {
                if (shadow == null) shadow = new FrameBuffer(graphics.getWidth(), graphics.getHeight());
                Draw.flush();
                shadow.resize(graphics.getWidth(), graphics.getHeight());
                shadow.begin();
                drawDrill(x - elevation, y - elevation, mx - elevation, my - elevation, Layer.floor + 2);
                Draw.flush();
                shadow.end();
                Draw.color(Pal.shadow);
                Draw.rect(Draw.wrap(shadow.getTexture()), Core.camera.position.x, Core.camera.position.y, Core.camera.width, -Core.camera.height);
            });
            if (drawDrill) {
                Draw.reset();
                //draw ore stroke
                Draw.z(Layer.blockOver);

                Lines.stroke(0.6f);
                Item tileItem;
                for (int ix = 0; ix < areaSize; ix++) {
                    for (int iy = 0; iy < areaSize; iy++) {
                        float dx = ix * 8 + mx - fulls + 4;
                        float dy = iy * 8 + my - fulls + 4;
                        tileItem = itemsArray.get(ix * areaSize + iy);
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
        public void drawSelect(){
            Drawf.dashRect(Pal.accent, getRect(Tmp.r1, x, y, rotation));
        }

        @Override
        public boolean shouldConsume(){
            return items.total() < itemCapacity && enabled && !empty;
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
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1){
                progress = read.f();
                warmup = read.f();
            }
        }
    }
}
