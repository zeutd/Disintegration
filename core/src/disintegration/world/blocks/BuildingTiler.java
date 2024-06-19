package disintegration.world.blocks;

import arc.math.Mathf;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;

public interface BuildingTiler {

    class BuildingTilerHolder {
        static final int[] blendresult = new int[5];
        static final BuildPlan[] directionals = new BuildPlan[4];
    }

    default int[] buildBlending(Building block) {
        int[] blendresult = BuildingTilerHolder.blendresult;
        blendresult[0] = 0;
        blendresult[1] = blendresult[2] = 1;

        int num =
                (blendBuilds(block, 2) && blendBuilds(block, 1) && blendBuilds(block, 3)) ? 0 :
                        (blendBuilds(block, 1) && blendBuilds(block, 3)) ? 1 :
                                (blendBuilds(block, 1) && blendBuilds(block, 2)) ? 2 :
                                        (blendBuilds(block, 3) && blendBuilds(block, 2)) ? 3 :
                                                blendBuilds(block, 1) ? 4 :
                                                        blendBuilds(block, 3) ? 5 :
                                                                -1;
        transformCase(num, blendresult);

        // Calculate bitmask for direction.

        blendresult[3] = 0;

        for (int i = 0; i < 4; i++) {
            if (blendBuilds(block, i)) {
                blendresult[3] |= (1 << i);
            }
        }

        // Calculate direction for non-square sprites.

        blendresult[4] = 0;

        for (int i = 0; i < 4; i++) {
            int realDir = Mathf.mod(block.rotation - i, 4);
            if (blendBuilds(block, i) && block.nearby(realDir) != null && !block.nearby(realDir).block.squareSprite) {
                blendresult[4] |= (1 << i);
            }
        }

        return blendresult;
    }

    default boolean blendBuilds(Building block, int direction) {
        Building other = block.nearby(Mathf.mod(block.rotation - direction, 4));
        return other != null && other.team == block.team() && blendBuilds(block, other);
    }

    default void transformCase(int num, int[] bits) {
        switch (num) {
            case 0 -> bits[0] = 3;
            case 1 -> bits[0] = 4;
            case 2 -> bits[0] = 2;
            case 3 -> {
                bits[0] = 2;
                bits[2] = -1;
            }
            case 4 -> {
                bits[0] = 1;
                bits[2] = -1;
            }
            case 5 -> bits[0] = 1;
        }
    }

    boolean blendBuilds(Building block, Building other);
}
