package disintegration.world.blocks.payload;

import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Vec2;
import arc.util.Eachable;
import arc.util.Structs;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.draw.DrawDefault;

import java.sql.Struct;

public class PayloadForkPoint extends VelocityPayloadConveyor{

    public PayloadForkPoint(String name) {
        super(name);
        hasShadow = false;
        quickRotate = false;
        drawer = new DrawDefault();
    }
    public class PayloadForkPointBuild extends VelocityPayloadConveyor.VelocityPayloadConveyorBuild {


        public Building[] buildings;

        @Override
        public void remove(){
            super.remove();
            if(buildings == null) return;
            for (Building b : buildings) {
                if(b != null && b != this && b.isAdded()) b.tile.remove();
            }
        }

        @Override
        public void draw(){
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(payload == null || buildings == null) return;
            Building other = buildings[0];
            PayloadFork.PayloadForkBuild parent = (PayloadFork.PayloadForkBuild)buildings[0];
            if(parent == null || other == null) return;
            if(!parent.payloads.contains(p -> p.within(x, y, payload.size()))){
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
