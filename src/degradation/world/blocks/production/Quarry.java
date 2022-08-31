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
import arc.util.Tmp;
import degradation.mathDef;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static arc.Core.atlas;
import static arc.math.Mathf.rand;
import static mindustry.Vars.tilesize;

public class Quarry extends Block {
    public TextureRegion sideRegion1;
    public TextureRegion sideRegion2;
    public TextureRegion locator;
    public TextureRegion armRegion;
    public int areaSize = 11;
    public Color areaColor = Pal.accent;
    public Color boostColor = Color.sky.cpy().mul(0.87f);
    public Quarry(String name){
        super(name);
        update = true;
        solid = true;
        rotate = true;
        group = BlockGroup.drills;
        hasItems = true;
        hasLiquids = true;
        liquidCapacity = 5f;
        hasPower = true;
        //drills work in space I guess
        envEnabled |= Env.space;
    }
    @Override
    public void load(){
        super.load();
        sideRegion1 = atlas.find(name + "-side1");
        sideRegion2 = atlas.find(name + "-side2");
        locator = atlas.find(name + "-locator");
        armRegion = atlas.find(name + "-arm");
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

    public class QuarryBuild extends Building {
        float mxS = 0, mxP = 0, mxN = 0, mxM = 0, mxR = 0, mxL = 0, myS = 0, myP = 0, myN = 0, myM = 0, myR = 0, myL = 0;
        float warmup;
        Color fullColor;
        float fulls = areaSize * tilesize/2f;
        float boostWarmup;
        float areaAlpha;
        float armAlpha;

        public Vec2 getMiningArea(){
            float len = tilesize * (areaSize + size)/2f;
            float mineX = x + Geometry.d4x(rotation) * len, mineY = y + Geometry.d4y(rotation) * len;
            return Tmp.v4.set(mineX, mineY);
        }

        @Override
        public void draw(){
            fullColor = Tmp.c1.set(areaColor).lerp(boostColor, boostWarmup);
            Draw.rect(region, x, y);
            Draw.rect(rotation >= 2 ? sideRegion2 : sideRegion1, x, y, rotdeg());
            boostWarmup = Mathf.lerpDelta(boostWarmup, optionalEfficiency, 0.1f);
            warmup = Mathf.lerpDelta(warmup, efficiency, 0.1f);
            Vec2 mineCentre = getMiningArea();
            float mx = mineCentre.x, my = mineCentre.y;
            Draw.z(Layer.buildBeam);
            //draw full area
            Lines.stroke(2f, fullColor);
            Draw.alpha(warmup);
            Drawf.dashRectBasic(mx - fulls, my - fulls, fulls*2f, fulls*2f);

            Draw.reset();
            Draw.color();
            areaAlpha = efficiency > 0 ? 1 : myP <= 0.1 ? 0 : 1;
            Draw.alpha(areaAlpha);
            Draw.z(Layer.buildBeam + 6);
            //draw locator
            if (efficiency > 0) {
                mxS = mathDef.lerp(mxS, mx - fulls - x, 4, 2);
                mxP = mathDef.lerp(mxP, mx + fulls - x, 4, 2);
                myS = mathDef.lerp(myS, my - fulls - y, 4, 2);
                myP = mathDef.lerp(myP, my + fulls - y, 4, 2);
                if (myP >= my + fulls - y - 0.1){
                    mxN = mathDef.lerp(mxN, -fulls, 4, 3);
                    myN = mathDef.lerp(myN, -fulls, 4, 3);
                    if(mxL > -fulls + 0.01 || mxL == 0) {
                        mxR = mx;
                        myR = my;
                        mxM = mx;
                        myM = my;
                    }
                    if (mxN <= -fulls + 0.01) {
                        mxL = mathDef.lerp(mxL, -fulls, 7, 3);
                        myL = mathDef.lerp(myL, -fulls, 7, 3);
                        if(mxL < -fulls + 0.01) {
                            armAlpha = 0;
                            if (Math.abs(mxM - mxR) <= 0.01) {
                                mxR = rand.random(mx - fulls + fulls / 3, mx + fulls - fulls / 3);
                                myR = rand.random(my - fulls + fulls / 3, my + fulls - fulls / 3);
                            }
                            mxM = mathDef.lerp(mxM, mxR, 3, 2);
                            myM = mathDef.lerp(myM, myR, 3, 2);
                        }
                        else{
                            armAlpha = 1;
                        }
                    }
                }
            }
            else{
                if (mxN <= -2 * fulls + 0.01) {
                    mxS = mathDef.lerp(mxS, 0, 4, 2);
                    mxP = mathDef.lerp(mxP, 0, 4, 2);
                    myS = mathDef.lerp(myS, 0, 4, 2);
                    myP = mathDef.lerp(myP, 0, 4, 2);
                }
                mxN = mathDef.lerp(mxN, -2 * fulls, 4, 3);
                myN = mathDef.lerp(myN, -2 * fulls, 4, 3);
            }
            Vec2 locator1 = new Vec2(mxS + x, myS + y);
            Vec2 locator2 = new Vec2(mxP + x, myS + y);
            Vec2 locator3 = new Vec2(mxS + x, myP + y);
            Vec2 locator4 = new Vec2(mxP + x, myP + y);
            Draw.rect(locator, locator1.x, locator1.y);
            Draw.rect(locator, locator2.x, locator2.y);
            Draw.rect(locator, locator3.x, locator3.y);
            Draw.rect(locator, locator4.x, locator4.y);
            //Draw arm
            Draw.z(Layer.buildBeam + 5);
            Lines.stroke(3.5f);
            //up
            Lines.line(armRegion,
                    mx - fulls,
                    my + fulls,
                    mxN + x + fulls,
                    my + fulls, false
            );
            Lines.line(armRegion,
                    mx + fulls,
                    my + fulls,
                    -mxN + x - fulls,
                    my + fulls, false
            );
            //down
            Lines.line(armRegion,
                    mx - fulls,
                    my - fulls,
                    mxN + x + fulls,
                    my - fulls, false
            );
            Lines.line(armRegion,
                    mx + fulls,
                    my - fulls,
                    -mxN + x - fulls,
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
            if (areaAlpha <= 0 ){
                mxL = 0;
                myL = 0;
            }
            //draw across arm animation
            Draw.alpha(armAlpha);
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
                    mxL + x + fulls,
                    my, false
            );
            Lines.line(armRegion,
                    mx + fulls,
                    my,
                    -mxL + x - fulls,
                    my, false
            );
            //draw across arm
            Draw.alpha(areaAlpha);
            Draw.z(Layer.buildBeam + 4);
            Lines.line(armRegion,
                    mxR,
                    my - fulls,
                    mxR,
                    my + fulls, false
            );
            Lines.line(armRegion,
                    mx - fulls,
                    myR,
                    mx + fulls,
                    myR , false
            );

        }

    }
}
