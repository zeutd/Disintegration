package disintegration.world.blocks.payload;

import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.draw.DrawDefault;

public class PayloadRedirectorPoint extends VelocityPayloadConveyor {

    public PayloadRedirectorPoint(String name) {
        super(name);
        quickRotate = false;
        hasShadow = false;
        drawer = new DrawDefault();
    }

    public class PayloadRedirectorPointBuild extends VelocityPayloadConveyorBuild {


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
            Building other = buildings[0];
            if (other == this) other = buildings[2];
            PayloadRedirector.PayloadRedirectorBuild parent = (PayloadRedirector.PayloadRedirectorBuild) buildings[1];
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
