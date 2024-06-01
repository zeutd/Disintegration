package disintegration.world.blocks.production;

import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.indexer;
import static mindustry.Vars.tilesize;

public class PortableDrillUnloadPoint extends Block {
    public float range;

    public float detectTime = 60f * 5f;

    public PortableDrillUnloadPoint(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.range, range / tilesize, StatUnit.blocks);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;

        Drawf.dashCircle(x, y, range, valid ? Pal.accent : Pal.remove);
    }

    public class PortableDrillUnloadPointBuild extends Building {
        public int timerDetect = timers++;

        public Seq<Building> seq = new Seq<>();
        @Override
        public void updateTile(){
            super.updateTile();
            if(timer(timerDetect, detectTime)){
                seq.clear();
                seq.addAll(indexer.getFlagged(team, BlockFlag.drill));
                seq.removeAll(b -> !(b instanceof PortableDrill.PortableDrillBuild && b.dst(this) < range));
            }
            if(timer(timerDump, dumpTime)){
                dump();
                if (seq.isEmpty()) return;
                seq.forEach(b -> {
                    if(!(b instanceof PortableDrill.PortableDrillBuild && b.dst(this) < range)) return;
                    b.items().each((item, amount) -> {
                        if(items.get(item) < itemCapacity) {
                            b.items.remove(item, amount);
                            items.add(item, amount);
                            for (int i = 0; i < amount; i++) {
                                Fx.itemTransfer.at(b.x, b.y, 0, item.color, this);
                            }
                        }
                    });
                });
            }
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, Pal.accent);
        }
    }
}
