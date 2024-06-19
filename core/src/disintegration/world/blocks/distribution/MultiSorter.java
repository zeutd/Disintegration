package disintegration.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.distribution.Sorter;

import static mindustry.Vars.content;


/**
 * wo shi sha bi
 */
public class MultiSorter extends Sorter {
    TextureRegion invertRegion;

    public MultiSorter(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        invertRegion = Core.atlas.find(name + "-inverted");
    }

    public class MultiSorterBuild extends SorterBuild {
        boolean invert = false;

        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.upOpen, Styles.cleari, () -> invert = !invert).size(40f * selectionColumns, 40);
            table.row();
            ItemSelection.buildTable(MultiSorter.this, table, content.items(), () -> sortItem, this::configure, selectionRows, selectionColumns);
        }

        @Override
        public void draw() {
            super.draw();
            if (invert) Draw.rect(invertRegion, x, y);
        }

        public Building getTileTarget(Item item, Building source, boolean flip) {
            int dir = source.relativeTo(tile.x, tile.y);
            if (dir == -1) return null;
            Building to;

            if (((item == sortItem) != invert) == enabled) {
                //prevent 3-chains
                if (isSame(source) && isSame(nearby(dir))) {
                    return null;
                }
                to = nearby(dir);
            } else {
                Building a = nearby(Mathf.mod(dir - 1, 4));
                Building b = nearby(Mathf.mod(dir + 1, 4));
                boolean ac = a != null && !(a.block.instantTransfer && source.block.instantTransfer) &&
                        a.acceptItem(this, item);
                boolean bc = b != null && !(b.block.instantTransfer && source.block.instantTransfer) &&
                        b.acceptItem(this, item);

                if (ac && !bc) {
                    to = a;
                } else if (bc && !ac) {
                    to = b;
                } else if (!bc) {
                    return null;
                } else {
                    to = (rotation & (1 << dir)) == 0 ? a : b;
                    if (flip) rotation ^= (1 << dir);
                }
            }

            return to;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            invert = read.bool();
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.bool(invert);
        }
    }
}
