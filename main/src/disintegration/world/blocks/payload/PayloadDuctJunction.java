package disintegration.world.blocks.payload;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Tmp;
import disintegration.util.DrawDef;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadConveyor;

import static mindustry.Vars.tilesize;

public class PayloadDuctJunction extends PayloadConveyor {
    public PayloadDuctJunction(String name) {
        super(name);
    }

    public class PayloadDuctJunctionBuild extends PayloadConveyorBuild {
        public int cameFrom = 0;

        @Override
        public void onProximityUpdate(){
            noSleep();

            Building accept = nearby(Geometry.d4(cameFrom + 2).x * (size/2+1), Geometry.d4(cameFrom + 2).y * (size/2+1));
            //next block must be aligned and of the same size
            if(accept != null && (
                    //same size
                    (accept.block.size == size && tileX() + Geometry.d4(cameFrom + 2).x * size == accept.tileX() && tileY() + Geometry.d4(cameFrom + 2).y * size == accept.tileY()) ||

                            //differing sizes
                            (accept.block.size > size &&
                                    ((cameFrom + 2) % 2 == 0 ? //check orientation
                                            Math.abs(accept.y - y) <= (accept.block.size * tilesize - size * tilesize)/2f : //check Y alignment
                                            Math.abs(accept.x - x) <= (accept.block.size * tilesize - size * tilesize)/2f   //check X alignment
                                    )))){
                next = accept;
            }else{
                next = null;
            }

            int ntrns = 1 + size/2;
            Tile next = tile.nearby(Geometry.d4(rotation).x * ntrns, Geometry.d4(rotation).y * ntrns);
            blocked = (next != null && next.solid() && !(next.block().outputsPayload || next.block().acceptsPayload)) || (this.next != null && this.next.payloadCheck(rotation));
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            super.handlePayload(source, payload);
            cameFrom = relativeTo(source);
            onProximityUpdate();
        }

        @Override
        public void updatePayload(){
            if(item != null){
                if(animation > fract()){
                    animation = Mathf.lerp(animation, 0.8f, 0.15f);
                }

                animation = Math.max(animation, fract());

                float fract = animation;
                float rot = Mathf.slerp(itemRotation, rotdeg(), fract);

                if(fract < 0.5f){
                    Tmp.v1.trns(itemRotation + 180, (0.5f - fract) * tilesize * size);
                }else{
                    Tmp.v1.trns(Mathf.mod(cameFrom + 2, 4) * 90, (fract - 0.5f) * tilesize * size);
                }

                float vx = Tmp.v1.x, vy = Tmp.v1.y;

                item.set(x + vx, y + vy, rot);
            }
        }
        @Override
        public void draw(){
            if (this.block.variants != 0 && this.block.variantRegions != null) {
                Draw.rect(block.variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, block.variantRegions.length - 1))], x, y, drawrot());
            } else {
                Draw.rect(block.region, x, y, drawrot());
            }

            drawTeamTop();
            Draw.z(Layer.blockAdditive);
            if(item != null){
                item.draw();
            }
            Draw.z(Layer.blockOver);
            Draw.rect(topRegion, x, y, drawrot());
        }
    }
}
