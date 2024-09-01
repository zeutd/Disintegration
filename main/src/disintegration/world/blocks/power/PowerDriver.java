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
import mindustry.world.meta.Env;

import javax.swing.*;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class PowerDriver extends Block {
    public float range = 600f;
    public float rotateSpeed = 1f;
    public float shootOffset = 6.5f;
    public TextureRegion baseRegion;
    public TextureRegion rayRegion;

    public PowerDriver(String name) {
        super(name);
        update = true;
        solid = true;
        configurable = true;
        hasPower = true;
        outlineIcon = true;
        sync = true;

        //point2 is relative
        config(Point2.class, (PowerDriverBuild tile, Point2 point) -> {
            tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY());

        });
        config(Integer.class, (PowerDriverBuild tile, Integer point) -> {
            tile.link = point;
        });
    }

    @Override
    public void load() {
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
        rayRegion = Core.atlas.find(name + "-ray");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    public class PowerDriverBuild extends Building{
        public int link = -1;
        public float rotation = 90;
        public float angle;
        public boolean ready;

        protected boolean linkValid(){
            if(link == -1) return false;
            return world.build(this.link) instanceof PowerDriverBuild other && other.block == block && other.team == team && within(other, range);
        }

        @Override
        public void updateTile() {
            Building link = world.build(this.link);
            PowerDriverBuild other = (PowerDriverBuild)link;
            ready = false;
            if(!linkValid()) return;
            angle = angleTo(link);
            rotation = Angles.moveToward(rotation, angle, rotateSpeed * efficiency);
            ready = Angles.within(rotation, angle, 1) && Angles.within(other.rotation, other.angle, 1);
            if(ready){
                power.links.addUnique(other.pos());

                if(other.team == team){
                    other.power.links.addUnique(pos());
                }

                power.graph.addGraph(other.power.graph);
            }else{
                other.power.graph.remove(this);
                power.links.removeValue(other.pos());
                other.power.links.removeValue(pos());
                power.graph.remove(other);
            }
        }

        @Override
        public void draw(){
            Building link = world.build(this.link);
            Draw.rect(baseRegion, x, y);
            Draw.z(Layer.turret);

            Drawf.shadow(region, x, y, rotation - 90);
            Draw.rect(region,x , y, rotation - 90);

            if(!linkValid() || !ready || Mathf.zero(Renderer.bridgeOpacity)) return;
            Draw.alpha(Renderer.bridgeOpacity);
            Draw.blend(Blending.additive);
            Draw.z(Layer.effect);
            Tmp.v1.trns(rotation, shootOffset);
            Lines.stroke(rayRegion.height / 4f);
            Lines.line(rayRegion, x + Tmp.v1.x, y + Tmp.v1.y, link.x - Tmp.v1.x, link.y - Tmp.v1.y, false);
            Draw.blend();
            Draw.reset();
        }

        @Override
        public void drawConfigure(){
            Draw.color(Pal.accent);

            Drawf.dashCircle(x, y, range, Pal.accent);
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(this == other){
                if(link == -1) deselect();
                configure(-1);
                return false;
            }

            if(link == other.pos()){
                configure(-1);
                other.configure(-1);
                return false;
            }else if(other.block == block && other.dst(tile) <= range && other.team == team){
                configure(other.pos());
                other.configure(pos());
                return false;
            }

            return true;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(link);
            write.f(rotation);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            link = read.i();
            rotation = read.f();
        }
    }
}
