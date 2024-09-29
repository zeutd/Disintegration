package disintegration.world.blocks.payload;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Time;
import arc.util.Tmp;
import disintegration.content.DTFx;
import disintegration.util.DrawDef;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.PayloadMassDriver;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class PayloadTeleporter extends PayloadBlock {
    public TextureRegion teleporterRegion;
    public TextureRegion portalRegion;
    public float rotateSpeed = 2f;
    public float levitation = 0.1f;
    public float levitateSpeed = 0.04f;
    public float thickness = 0f;
    public float spacing = 1f;
    public float maxPayloadSize = 5;
    public Effect portalEffect = DTFx.portalEffect;
    public Effect teleportEffect = DTFx.teleportEffect;
    public float effectChance = 0.6f;
    public float effectRange = 28f;
    public float range = 1100f;
    public float reload = 30f;
    public PayloadTeleporter(String name) {
        super(name);
        rotateDraw = false;
        rotate = true;

        config(Point2.class, (PayloadTeleporterBuild tile, Point2 point) -> tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
        config(Integer.class, (PayloadTeleporterBuild tile, Integer point) -> tile.link = point);
    }
    @Override
    public void load(){
        super.load();
        configurable = true;
        teleporterRegion = Core.atlas.find(name + "-teleporter");
        portalRegion = Core.atlas.find(name + "-portal");
    }
    public class PayloadTeleporterBuild extends PayloadBlockBuild<Payload> {
        public int link = -1;
        public float charge = 0;
        public float height = 0;
        @Override
        public void draw(){
            super.draw();
            Draw.rect(outRegion, x, y, rotdeg());
            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                }
            }
            Draw.alpha(0.25f);
            Draw.z(Layer.flyingUnit + 1);
            for (int i = 0; i < 4; i++) {
                DrawDef.rect3d(portalRegion, x, y, Time.time * rotateSpeed * i + i * 90, height + Mathf.absin(10f, 0.01f));
            }
            Draw.reset();
            for (float i = thickness; i >= 0; i -= spacing) {
                DrawDef.rect3d(teleporterRegion, x, y, 0, height - i + Mathf.absin(10f, 0.01f));
            }
            Draw.color(Pal.shadow);
            Draw.z(Layer.blockOver);
            Draw.rect(teleporterRegion, x - (height + Mathf.absin(10f, 0.01f)) * 320, y - (height + Mathf.absin(10f, 0.01f)) * 320);
            Draw.reset();
            Draw.z(Layer.blockOver);

            drawPayload();
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return super.acceptPayload(source, payload) && payload.size() <= maxPayloadSize * tilesize;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            height = Mathf.lerpDelta(height, levitation, levitateSpeed);
            if(portalEffect != null && Mathf.chanceDelta(effectChance)) {
                Tmp.v1.setToRandomDirection().scl(Mathf.range(effectRange));
                portalEffect.at(Tmp.v1.x + x, Tmp.v1.y + y);
            }
            if(!linkValid()){
                moveOutPayload();
                charge = 0;
            }
            else if (moveInPayload()) {
                charge += Time.delta;
                if(charge >= reload){
                    Building link = world.build(this.link);
                    var other = (PayloadTeleporterBuild)link;
                    if(other.acceptPayload(this, payload)) {
                        other.handlePayload(this, payload);
                        other.payVector.set(0, 0);
                        teleportEffect.at(x, y, 0, payload.content());
                        teleportEffect.at(other.x, other.y, 0, payload.content());
                        payload = null;
                        charge = 0;
                    }
                }
            }
        }

        @Override
        public void drawConfigure(){
            float sin = Mathf.absin(Time.time, 6f, 1f);

            Draw.color(Pal.accent);
            Lines.stroke(1f);
            Drawf.circles(x, y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.accent);

            if(linkValid()){
                Building target = world.build(link);
                Drawf.circles(target.x, target.y, (target.block().size / 2f + 1) * tilesize + sin - 2f, Pal.place);
                Drawf.arrow(x, y, target.x, target.y, size * tilesize + sin, 4f + sin);
            }

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
                return false;
            }else if(other.block instanceof PayloadTeleporter && other.dst(tile) <= range && other.team == team){
                configure(other.pos());
                return false;
            }

            return true;
        }

        protected boolean linkValid(){
            return link != -1 && world.build(this.link) instanceof PayloadTeleporterBuild other && other.block == block && other.team == team && within(other, range);
        }
    }
}
