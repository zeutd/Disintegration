package disintegration.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Mat3D;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arclibrary.graphics.Draw3d;
import disintegration.util.MathDef;
import disintegration.world.blocks.production.SolarCrafter;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.world.Block;

import static mindustry.Vars.tilesize;

@SuppressWarnings("all")
public class Heliostat extends Block {
    public TextureRegion baseRegion;
    public TextureRegion panelRegion;
    public TextureRegion panelLightRegion;
    public TextureRegion supportRegion;
    public TextureRegion rayRegion;
    public TextureRegion rayEndRegion;

    public float levitation = 9f;
    public float thickness = 4f;
    public float spacing = 0.3f;

    public float range = 110f;

    public Heliostat(String name) {
        super(name);
        update = true;
        solid = true;
        configurable = true;
        saveConfig = true;
        config(Point2.class, (SolarMirrorBuild tile, Point2 point) -> tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
        config(Integer.class, (SolarMirrorBuild tile, Integer point) -> tile.link = point);
    }

    @Override
    public void init(){
        clipSize = range;
    }

    @Override
    public void load() {
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
        panelRegion = Core.atlas.find(name + "-panel");
        panelLightRegion = Core.atlas.find(name + "-panel-light");
        supportRegion = Core.atlas.find(name + "-support");
        rayRegion = Core.atlas.find(name + "-ray");
        rayEndRegion = Core.atlas.find(name + "-ray-end");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize, y * tilesize, range, Pal.accent);
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{baseRegion, panelRegion};
    }

    public class SolarMirrorBuild extends Building {
        Mat3D mat = new Mat3D();
        public float rot = 0, angle = 0;
        public Vec2 target = new Vec2();
        public int link = -1;

        @Override
        public void updateTile(){
            if(link == -1) return;
            target.lerp(Point2.x(link) - tileX(), Point2.y(link) - tileY(), 0.1f);
            Building other = Vars.world.build(link);
            if(other != null && other instanceof SolarCrafter.SolarCrafterBuild build){
                build.links.addUnique(pos());
            }
        }

        @Override
        public void draw() {
            Draw.rect(baseRegion, x, y);
            Tmp.v1.set(target).add(0, 0.1f).rotate90(-1);
            rot = target.angle();
            Draw.rect(supportRegion, x, y, rot + 90);
            Draw.z(Layer.blockOver);
            for (float i = thickness; i >= 0; i -= spacing) {
                mat.idt();
                mat.translate(0, 0, levitation - i);
                mat.rotate(Tmp.v31.set(Tmp.v1, 0), -Mathf.radDeg * Mathf.atan2(-10, Mathf.dst(Tmp.v1.x, Tmp.v1.y)));
                mat.translate(0, 0, levitation - i);
                Draw3d.rect(mat, panelRegion, x - panelRegion.width / 8f, y - panelRegion.height / 8f, panelRegion.width / 4f, panelRegion.height / 4f, rot);
            }
            MathDef.specularReflection(Tmp.v31.set(target, 10).nor(), new Vec3(0, 0, 1).nor(), Tmp.v32, Tmp.v33);
            float strength = Tmp.v33.nor().dot(Vec3.Z) - 0.3f;
            Draw.alpha(strength);
            Draw3d.rect(mat, panelLightRegion, x - panelRegion.width / 8f, y - panelRegion.height / 8f, panelRegion.width / 4f, panelRegion.height / 4f, rot);
            Lines.stroke(rayRegion.width * 4);
            Draw.scl(1);
            Lines.stroke(rayRegion.height / 4);
            Lines.line(rayRegion, x, y, x + target.x * 8, y + target.y * 8, false);
            Draw.rect(rayEndRegion, x + target.x * 8, y + target.y * 8, target.angle());
            Draw.z(Layer.blockAdditive);
            Draw.color(Pal.shadow);
            Draw3d.rect(mat, panelRegion, x - panelRegion.width / 8f - levitation, y - panelRegion.height / 8f - levitation, panelRegion.width / 4f, panelRegion.height / 4f, rot);
            Draw.rect(supportRegion, x - levitation, y - levitation, rot + 90);
            Draw.reset();
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(this == other){
                if(link == -1) deselect();
                configure(-1);
                Building b = Vars.world.build(link);
                if(b != null && b instanceof SolarCrafter.SolarCrafterBuild build){
                    build.links.removeValue(pos());
                }
                return false;
            }

            if(link == other.pos()){
                configure(-1);
                if(other != null && other instanceof SolarCrafter.SolarCrafterBuild build){
                    build.links.removeValue(pos());
                }
                return false;
            }else if(other instanceof SolarCrafter.SolarCrafterBuild && other.dst(tile) <= range && other.team == team){
                configure(other.pos());
                return false;
            }

            return true;
        }

        @Override
        public void remove(){
            Building b = Vars.world.build(link);
            if(b != null && b instanceof SolarCrafter.SolarCrafterBuild build){
                build.links.removeValue(pos());
            }
            super.remove();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(link);
            TypeIO.writeVec2(write, target);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            link = read.i();
            TypeIO.readVec2(read, target);
        }
    }
}
