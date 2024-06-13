package disintegration.world.blocks.campaign;

import arc.Core;
import arc.Events;
import arc.audio.Sound;
import arc.func.Boolf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.DTVars;
import disintegration.gen.entities.InterplanetaryLaunchPayload;
import disintegration.gen.entities.InterplanetaryLaunchPayloadc;
import disintegration.world.blocks.campaign.InterplanetaryLaunchPad;
import disintegration.type.SpaceStation;
import ent.anno.Annotations;
import mindustry.content.Fx;
import mindustry.content.Planets;
import mindustry.ctype.ContentType;
import mindustry.entities.Effect;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.logic.LAccess;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static disintegration.DTVars.spaceStationPlanets;
import static disintegration.DTVars.spaceStations;
import static mindustry.Vars.*;

public class InterplanetaryLaunchPad extends Block{
    /** Time inbetween launches. */
    public float launchTime = 1f;
    public Sound launchSound = Sounds.none;

    public TextureRegion lightRegion;
    public TextureRegion podRegion;
    public Color lightColor = Color.valueOf("eab678");

    public InterplanetaryLaunchPad(String name){
        super(name);
        hasItems = true;
        solid = true;
        update = true;
        configurable = true;
        flags = EnumSet.of(BlockFlag.launchPad);
    }

    @Override
    public void load(){
        super.load();
        lightRegion = Core.atlas.find(name + "-light");
        podRegion = Core.atlas.find(name + "-pod", "disintegration-space-launchpod-large");
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.launchTime, launchTime / 60f, StatUnit.seconds);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("items", entity -> new Bar(() -> Core.bundle.format("bar.items", entity.items.total()), () -> Pal.items, () -> (float)entity.items.total() / itemCapacity));

        addBar("progress", (InterplanetaryLaunchPadBuild build) -> new Bar(() -> Core.bundle.get("bar.launchcooldown"), () -> Pal.ammo, () -> Mathf.clamp(build.launchCounter / launchTime)));
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return state.isCampaign() && state.rules.planet instanceof SpaceStation;
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    public class InterplanetaryLaunchPadBuild extends Building {

        public Planet dest;

        public int readDestId = -1;
        public float launchCounter;

        @Override
        public boolean shouldConsume(){
            return launchCounter < launchTime;
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.progress) return Mathf.clamp(launchCounter / launchTime);
            return super.sense(sensor);
        }

        @Override
        public void draw(){
            super.draw();

            if(!state.isCampaign()) return;

            if(lightRegion.found()){
                Draw.color(lightColor);
                float progress = Math.min((float)items.total() / itemCapacity, launchCounter / launchTime);
                int steps = 3;
                float step = 1f;

                for(int i = 0; i < 4; i++){
                    for(int j = 0; j < steps; j++){
                        float alpha = Mathf.curve(progress, (float)j / steps, (j+1f) / steps);
                        float offset = -(j - 1f) * step;

                        Draw.color(Pal.metalGrayDark, lightColor, alpha);
                        Draw.rect(lightRegion, x + Geometry.d8edge(i).x * offset, y + Geometry.d8edge(i).y * offset, i * 90);
                    }
                }

                Draw.reset();
            }

            Draw.rect(podRegion, x, y);

            Draw.reset();
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.total() < itemCapacity;
        }

        @Override
        public void updateTile(){
            if(!state.isCampaign() || !(state.rules.sector.planet instanceof SpaceStation)) return;
            if(readDestId != -1){
                dest = content.getByID(ContentType.planet, readDestId);
                readDestId = -1;
            }
            //increment launchCounter then launch when full and base conditions are met
            if((launchCounter += edelta()) >= launchTime && items.total() >= itemCapacity){
                //if there are item requirements, use those.
                consume();
                launchSound.at(x, y);
                InterplanetaryLaunchPayload entity = InterplanetaryLaunchPayload.create();
                items.each((item, amount) -> entity.stacks.add(new ItemStack(item, amount)));
                entity.set(this);
                entity.lifetime(120f);
                entity.team(team);
                entity.to = dest;
                entity.add();
                Fx.launchPod.at(this);
                items.clear();
                Effect.shake(3f, 3f, this);
                launchCounter = 0f;
            }
        }

        @Override
        public void display(Table table){
            super.display(table);

            if(!state.isCampaign() || net.client() || team != player.team()) return;

            table.row();
            table.label(() -> Core.bundle.format("launch.destination",
                    dest == null || !dest.getSector(PlanetGrid.Ptile.empty).hasBase() ? Core.bundle.get("sectors.nonelaunch") :
                            "[accent]" + dest.name)).pad(4).wrap().width(200f).left();
        }

        @Override
        public void buildConfiguration(Table table){
            if(!state.isCampaign() || net.client() || !(state.rules.sector.planet instanceof SpaceStation)){
                deselect();
                return;
            }
            for(int i = 0; i < content.planets().size; i++){
                Planet planet = content.planets().get(i);
                if(spaceStations.contains(s -> s == planet) && planet != state.rules.planet){
                    table.button(planet.localizedName,
                                    Icon.icons.get(planet.parent.icon + "Small",
                                            Icon.icons.get(planet.parent.icon, Icon.planet)),
                                    Styles.flatTogglet, () -> {
                                        dest = planet;
                                        deselect();
                                    })
                            .width(190).height(40).growX()
                            .update(bb -> bb.setChecked(dest == planet))
                            .with(w -> w.marginLeft(10f))
                            .get().getChildren().get(1)
                            .setColor(planet.parent.iconColor);
                    table.row();
                }
            }
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(launchCounter);
            write.i(dest == null ? -1 : dest.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1){
                launchCounter = read.f();
            }
            readDestId = read.i();
        }
    }

    @Annotations.EntityDef(InterplanetaryLaunchPayloadc.class)
    @Annotations.EntityComponent(base = true)
    static abstract class InterplanetaryLaunchPayloadComp implements Drawc, Timedc, Teamc{
        @Annotations.Import
        float x,y;

        Seq<ItemStack> stacks = new Seq<>();
        transient Interval in = new Interval();
        transient Planet to;

        @Override
        public void draw(){
            float alpha = fout(Interp.pow5Out);
            float scale = (1f - alpha) * 1.3f + 1f;
            float cx = cx(), cy = cy();
            float rotation = fin() * (130f + Mathf.randomSeedRange(id(), 50f));

            Draw.z(Layer.effect + 0.001f);

            Draw.color(Pal.engine);

            float rad = 0.2f + fslope();

            Fill.light(cx, cy, 10, 25f * (rad + scale-1f), Tmp.c2.set(Pal.engine).a(alpha), Tmp.c1.set(Pal.engine).a(0f));

            Draw.alpha(alpha);
            for(int i = 0; i < 4; i++){
                Drawf.tri(cx, cy, 6f, 40f * (rad + scale-1f), i * 90f + rotation);
            }

            Draw.color();

            Draw.z(Layer.weather - 1);

            TextureRegion region = blockOn() instanceof InterplanetaryLaunchPad p ? p.podRegion : Core.atlas.find("disintegration-space-launchpod-large");
            scale *= region.scl();
            float rw = region.width * scale, rh = region.height * scale;

            Draw.alpha(alpha);
            Draw.rect(region, cx, cy, rw, rh, rotation);

            Tmp.v1.trns(225f, fin(Interp.pow3In) * 250f);

            Draw.z(Layer.flyingUnit + 1);
            Draw.color(0, 0, 0, 0.22f * alpha);
            Draw.rect(region, cx + Tmp.v1.x, cy + Tmp.v1.y, rw, rh, rotation);

            Draw.reset();
        }

        float cx(){
            return x + fin(Interp.pow2In) * (12f + Mathf.randomSeedRange(id() + 3, 4f));
        }

        float cy(){
            return y + fin(Interp.pow5In) * (100f + Mathf.randomSeedRange(id() + 2, 30f));
        }

        @Override
        public void update(){
            float r = 3f;
            if(in.get(4f - fin()*2f)){
                Fx.rocketSmoke.at(cx() + Mathf.range(r), cy() + Mathf.range(r), fin());
            }
        }

        @Override
        public void remove(){
            if(!state.isCampaign()) return;

            //actually launch the items upon removal
            if(team() == state.rules.defaultTeam){
                if(to != null && (to.getSector(PlanetGrid.Ptile.empty) != state.rules.sector || net.client())){
                    ItemSeq dest = new ItemSeq();

                    for(ItemStack stack : stacks){
                        dest.add(stack);

                        //update export
                        DTVars.exportHandler.handleItemInterplanetaryExport(stack, state.rules.planet, to);
                        Events.fire(new EventType.LaunchItemEvent(stack));
                    }

                    if(!net.client()){
                        to.getSector(PlanetGrid.Ptile.empty).addItems(dest);
                    }
                }
            }
        }
    }
}
