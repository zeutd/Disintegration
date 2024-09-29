package disintegration.world.blocks.payload;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.payloads.PayloadLoader;
import mindustry.world.blocks.payloads.PayloadMassDriver;

import static mindustry.Vars.world;

public class PayloadPropulsor extends PayloadMassDriver {
    public PayloadPropulsor(String name) {
        super(name);
    }
    public class PayloadPropulsorBuild extends PayloadDriverBuild {
        @Override
        public void draw(){
            float
                    tx = x + Angles.trnsx(turretRotation + 180f, reloadCounter * knockback),
                    ty = y + Angles.trnsy(turretRotation + 180f, reloadCounter * knockback), r = turretRotation - 90;

            Draw.rect(baseRegion, x, y);

            //draw input
            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                }
            }

            Draw.rect(outRegion, x, y, rotdeg());

            if(payload != null){
                updatePayload();

                if(effectDelayTimer <= 0){
                    Draw.z(loaded ? Layer.turret + 0.2f : Layer.blockOver);
                    payload.draw();
                }
            }

            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRegion, x, y);

            Draw.z(Layer.turret);
            //TODO
            Drawf.shadow(region, tx - (size / 2f), ty - (size / 2f), r);

            Tmp.v1.trns(turretRotation, 0, -(curSize/2f - grabWidth));
            Tmp.v2.trns(rotation, -Math.max(curSize/2f - grabHeight - length, 0f), 0f);
            float rx = tx + Tmp.v1.x + Tmp.v2.x, ry = ty + Tmp.v1.y + Tmp.v2.y;
            float lx = tx - Tmp.v1.x + Tmp.v2.x, ly = ty - Tmp.v1.y + Tmp.v2.y;

            Draw.rect(capOutlineRegion, tx, ty, r);
            Draw.rect(leftOutlineRegion, lx, ly, r);
            Draw.rect(rightOutlineRegion, rx, ry, r);

            Draw.rect(capRegion, tx, ty, r);
            Draw.rect(leftRegion, lx, ly, r);
            Draw.rect(rightRegion, rx, ry, r);

            Draw.z(Layer.effect);

            if(charge > 0 && linkValid()){
                Building link = world.build(this.link);

                float fin = Interp.pow2Out.apply(charge / chargeTime), fout = 1f-fin, len = length*1.8f, w = curSize/2f + 7f*fout;
                Vec2 right = Tmp.v1.trns(turretRotation, len, w);
                Vec2 left = Tmp.v2.trns(turretRotation, len, -w);

                Lines.stroke(fin * 1.2f, Pal.accent);
                Lines.line(x + left.x, y + left.y, link.x - right.x, link.y - right.y);
                Lines.line(x + right.x, y + right.y, link.x - left.x, link.y - left.y);

                for(int i = 0; i < 4; i++){
                    Tmp.v3.set(x, y).lerp(link.x, link.y, 0.5f + (i - 2) * 0.1f);
                    Draw.scl(fin * 1.1f);
                    Draw.rect(arrow, Tmp.v3.x, Tmp.v3.y, turretRotation);
                    Draw.scl();
                }

                Draw.reset();
            }
        }
    }
}
