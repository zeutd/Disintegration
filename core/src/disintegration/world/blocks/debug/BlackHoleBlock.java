package disintegration.world.blocks.debug;

import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.entities.DTGroups;
import disintegration.gen.entities.BlackHole;
import mindustry.gen.Building;
import mindustry.world.Block;

import static mindustry.Vars.net;

public class BlackHoleBlock extends Block {
    public BlackHoleBlock(String name) {
        super(name);
        update = true;
        solid = true;
        configurable = true;
    }

    public class BlackHoleBuild extends Building {
        public BlackHole blackHole;

        public int readBlackHoleId = -1;

        @Override
        public void add() {
            super.add();
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if (readBlackHoleId != -1) {
                blackHole = (BlackHole) DTGroups.blackHole.getByID(readBlackHoleId);
                if (blackHole != null || !net.client()) {
                    readBlackHoleId = -1;
                }
            }
            if (blackHole == null) blackHole = BlackHole.create();
            blackHole.team(team);
            blackHole.set(this);
            blackHole.force(20);
            blackHole.teamForceMultiplier(1);
            blackHole.attractForce(10);
            blackHole.attractRadius(80);
            blackHole.scaledForce(150);
            blackHole.radius(50);
            blackHole.add();
        }

        @Override
        public void remove() {
            super.remove();
            if (blackHole != null) blackHole.remove();
        }

        @Override
        public void buildConfiguration(Table table) {
            Log.info(blackHole.id);
            Log.info(DTGroups.blackHole.getByID(blackHole.id));
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(blackHole != null ? blackHole.id : -1);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            readBlackHoleId = read.i();
        }
    }
}