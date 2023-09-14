package disintegration.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.OverflowGate;
import mindustry.world.meta.BlockGroup;

public class MultiOverflowGate extends OverflowGate {
    TextureRegion invertRegion;
    public MultiOverflowGate(String name) {
        super(name);
        hasItems = true;
        underBullets = true;
        update = false;
        destructible = true;
        group = BlockGroup.transportation;
        instantTransfer = true;
        unloadable = false;
        canOverdrive = false;
        itemCapacity = 0;
        configurable = true;
        config(Boolean.class, (MultiOverflowGateBuild entity, Boolean b) -> entity.invert = b);
    }

    @Override
    public void load(){
        super.load();
        invertRegion = Core.atlas.find(name + "-inverted");
    }

    public class MultiOverflowGateBuild extends OverflowGateBuild {
        boolean invert = false;
        @Override
        public boolean configTapped(){
            configure(!invert);
            return false;
        }

        @Override
        public void draw(){
            super.draw();
            if (invert) Draw.rect(invertRegion, x, y);
        }

        @Override
        public Boolean config(){
            return invert;
        }

        public @Nullable Building getTileTarget(Item item, Building src, boolean flip){
            int from = relativeToEdge(src.tile);
            if(from == -1) return null;
            Building to = nearby((from + 2) % 4);
            boolean
                    fromInst = src.block.instantTransfer,
                    canForward = to != null && to.team == team && !(fromInst && to.block.instantTransfer) && to.acceptItem(this, item),
                    inv = invert == enabled;

            if(!canForward || inv){
                Building a = nearby(Mathf.mod(from - 1, 4));
                Building b = nearby(Mathf.mod(from + 1, 4));
                boolean ac = a != null && !(fromInst && a.block.instantTransfer) && a.team == team && a.acceptItem(this, item);
                boolean bc = b != null && !(fromInst && b.block.instantTransfer) && b.team == team && b.acceptItem(this, item);

                if(!ac && !bc){
                    return inv && canForward ? to : null;
                }

                if(ac && !bc){
                    to = a;
                }else if(bc && !ac){
                    to = b;
                }else{
                    to = (rotation & (1 << from)) == 0 ? a : b;
                    if(flip) rotation ^= (1 << from);
                }
            }

            return to;
        }


        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            invert = read.bool();
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.bool(invert);
        }
    }
}
