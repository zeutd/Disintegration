package disintegration.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.meta.Env;

public class PowerDriver extends Block {
    public float range;
    public float rotateSpeed = 5f;
    public TextureRegion baseRegion;

    public PowerDriver(String name) {
        super(name);
        update = true;
        solid = true;
        configurable = true;
        hasItems = true;
        hasPower = true;
        outlineIcon = true;
        sync = true;
        envEnabled |= Env.space;

        //point2 is relative
        config(Point2.class, (MassDriver.MassDriverBuild tile, Point2 point) -> tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
        config(Integer.class, (MassDriver.MassDriverBuild tile, Integer point) -> tile.link = point);
    }

    @Override
    public void load() {
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
    }
}
