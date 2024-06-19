package disintegration.world.blocks.payload;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.draw.DrawDefault;

public class PayloadCrossPoint extends VelocityPayloadConveyor {

    public PayloadCrossPoint(String name) {
        super(name);
        quickRotate = false;
        hasShadow = false;
        drawer = new DrawDefault();
    }

    public class PayloadCrossPointBuild extends VelocityPayloadConveyorBuild {


        public Building[] buildings;

        @Override
        public void remove() {
            super.remove();
            if (buildings == null) return;
            for (Building b : buildings) {
                if (b != null && b != this && b.isAdded()) b.tile.remove();
            }
        }

        @Override
        public void draw() {
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if (payload == null || buildings == null) return;
            Building other = buildings[Mathf.mod(rotation + 2, 4)];
            PayloadCross.PayloadCrossBuild parent = (PayloadCross.PayloadCrossBuild) buildings[4];
            if (parent == null || other == null) return;
            if (!parent.payloads.contains(p -> p.within(x, y, payload.size() / 2))) {
                parent.payloads.add(payload);
                parent.velocities.put(payload, Math.abs(velocity));
                parent.dests.put(payload, other);
                parent.payVectors.put(payload, new Vec2(x - parent.x, y - parent.y));
                velocity = 0;
                payload = null;
            }
        }
    }
}
