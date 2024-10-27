package disintegration.world.blocks.power;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.core.Renderer;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.meta.Env;

import javax.swing.*;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class PowerDriver extends PowerNode {
    public float rotateSpeed = 1f;
    public TextureRegion baseRegion;

    public PowerDriver(String name) {
        super(name);
        update = true;
        solid = true;
        configurable = true;
        hasPower = true;
        outlineIcon = true;
        sync = true;
        maxNodes = 1;
        laserRange = 150f;
    }

    @Override
    public void load() {
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    public class PowerDriverBuild extends PowerNodeBuild{
        public float rotation = 0;
        public float angle;

        @Override
        public void updateTile() {
            if (power.links.size <= 0) return;
            Building other = world.build(power.links.get(0));
            angle = angleTo(other) - 90;
            rotation = Angles.moveToward(rotation, angle, rotateSpeed * efficiency);
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Draw.rect(region, x, y, rotation);
            if(Mathf.zero(Renderer.laserOpacity) || isPayload()) return;
            Draw.z(Layer.power);
            setupColor(power.graph.getSatisfaction());
            if (power.links.size <= 0) return;
            Building other = world.build(power.links.get(0));
            if(Angles.within(angle, rotation, 0.1f)) drawLaser(x, y, other.x, other.y, size, other.block.size);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(rotation);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            rotation = read.f();
        }
    }
}
