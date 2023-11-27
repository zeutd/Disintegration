package disintegration.world.blocks.debug;

import disintegration.entities.BlackHole;
import mindustry.gen.Building;
import mindustry.world.Block;

public class BlackHoleBlock extends Block {
    public BlackHoleBlock(String name) {
        super(name);
        update = true;
        solid = true;
        configurable = true;
    }

    public class BlackHoleBuild extends Building {
        public BlackHole blackHole;

        @Override
        public void add(){
            super.add();
            blackHole = new BlackHole();
            blackHole.team = team;
            blackHole.set(this);
            blackHole.force(20);
            blackHole.attractForce(3);
            blackHole.radius(100);
            blackHole.add();
        }

        @Override
        public void remove(){
            super.remove();
            blackHole.remove();
        }
    }
}