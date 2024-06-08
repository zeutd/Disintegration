package disintegration.world.blocks.payload;

import mindustry.gen.Building;

import static arc.math.Mathf.mod;
import static java.lang.Math.abs;

public class PayloadDecelerator extends VelocityPayloadConveyor{
    public float deceleration = 0.05f;
    public PayloadDecelerator(String name) {
        super(name);
    }

    public class PayloadDeceleratorBuild extends VelocityPayloadConveyorBuild {
        @Override
        public void updateTile(){
            super.updateTile();
            velocity *= (1 - deceleration * edelta());
        }
    }
}
