package disintegration.world.blocks.production;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.production.BeamDrill;

import static mindustry.Vars.*;

public class MonoBeamDrill extends BeamDrill {
    public MonoBeamDrill(String name) {
        super(name);
        laserWidth = 1.1f;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Item item = null, invalidItem = null;
        boolean multiple = false;
        int count = 0;

        int i = size / 2;
        nearbySide(x, y, rotation, i, Tmp.p1);

        int j = 0;
        Item found = null;
        for(; j < range; j++){
            int rx = Tmp.p1.x + Geometry.d4x(rotation)*j, ry = Tmp.p1.y + Geometry.d4y(rotation)*j;
            Tile other = world.tile(rx, ry);
            if(other != null && other.solid()){
                Item drop = other.wallDrop();
                if(drop != null){
                    if(drop.hardness <= tier){
                        found = drop;
                        count++;
                    }else{
                        invalidItem = drop;
                    }
                }
                break;
            }
        }

        if(found != null){
            //check if multiple items will be drilled
            item = found;
        }

        int len = Math.min(j, range - 1);
        Drawf.dashLine(found == null ? Pal.remove : Pal.placing,
                Tmp.p1.x * tilesize,
                Tmp.p1.y *tilesize,
                (Tmp.p1.x + Geometry.d4x(rotation)*len) * tilesize,
                (Tmp.p1.y + Geometry.d4y(rotation)*len) * tilesize
        );

        if(item != null){
            float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / getDrillTime(item) * count, 2), x, y, valid);
            if(!multiple){
                float dx = x * tilesize + offset - width/2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
                Draw.mixcol(Color.darkGray, 1f);
                Draw.rect(item.fullIcon, dx, dy - 1, s, s);
                Draw.reset();
                Draw.rect(item.fullIcon, dx, dy, s, s);
            }
        }else if(invalidItem != null){
            drawPlaceText(Core.bundle.get("bar.drilltierreq"), x, y, false);
        }

    }

    public class MonoBeamDrillBuild extends BeamDrillBuild {
        @Override
        public void draw(){
            Draw.rect(block.region, x, y);
            Draw.rect(topRegion, x, y, rotdeg());

            if(isPayload()) return;

            var dir = Geometry.d4(rotation);
            int ddx = Geometry.d4x(rotation + 1), ddy = Geometry.d4y(rotation + 1);

            int i = size / 2;
            Tile face = facing[i];
            if(face != null){
                Point2 p = lasers[i];
                float lx = face.worldx() - (dir.x/2f)*tilesize, ly = face.worldy() - (dir.y/2f)*tilesize;

                float width = (laserWidth + Mathf.absin(Time.time + i*5 + (id % 9)*9, glowScl, pulseIntensity)) * warmup;

                Draw.z(Layer.power - 1);
                Draw.mixcol(glowColor, Mathf.absin(Time.time + i*5 + id*9, glowScl, glowIntensity));
                if(Math.abs(p.x - face.x) + Math.abs(p.y - face.y) == 0){
                    Draw.scl(width);

                    if(boostWarmup < 0.99f){
                        Draw.alpha(1f - boostWarmup);
                        Draw.rect(laserCenter, lx, ly);
                    }

                    if(boostWarmup > 0.01f){
                        Draw.alpha(boostWarmup);
                        Draw.rect(laserCenterBoost, lx, ly);
                    }

                    Draw.scl();
                }else{
                    float lsx = (p.x - dir.x/2f) * tilesize, lsy = (p.y - dir.y/2f) * tilesize;

                    if(boostWarmup < 0.99f){
                        Draw.alpha(1f - boostWarmup);
                        Drawf.laser(laser, laserEnd, lsx, lsy, lx, ly, width);
                    }

                    if(boostWarmup > 0.001f){
                        Draw.alpha(boostWarmup);
                        Drawf.laser(laserBoost, laserEndBoost, lsx, lsy, lx, ly, width);
                    }
                }
                Draw.color();
                Draw.mixcol();

                Draw.z(Layer.effect);
                Lines.stroke(warmup);
                rand.setState(i, id);
                Color col = face.wallDrop().color;
                Color spark = Tmp.c3.set(sparkColor).lerp(boostHeatColor, boostWarmup);
                for(int j = 0; j < sparks; j++){
                    float fin = (Time.time / sparkLife + rand.random(sparkRecurrence + 1f)) % sparkRecurrence;
                    float or = rand.range(2f);
                    Tmp.v1.set(sparkRange * fin, 0).rotate(rotdeg() + rand.range(sparkSpread));

                    Draw.color(spark, col, fin);
                    float px = Tmp.v1.x, py = Tmp.v1.y;
                    if(fin <= 1f) Lines.lineAngle(lx + px + or * ddx, ly + py + or * ddy, Angles.angle(px, py), Mathf.slope(fin) * sparkSize);
                }
                Draw.reset();
            }

            if(glowRegion.found()){
                Draw.z(Layer.blockAdditive);
                Draw.blend(Blending.additive);
                Draw.color(Tmp.c1.set(heatColor).lerp(boostHeatColor, boostWarmup), warmup * (heatColor.a * (1f - heatPulse + Mathf.absin(heatPulseScl, heatPulse))));
                Draw.rect(glowRegion, x, y, rotdeg());
                Draw.blend();
                Draw.color();
            }

            Draw.blend();
            Draw.reset();
        }
    }
}
