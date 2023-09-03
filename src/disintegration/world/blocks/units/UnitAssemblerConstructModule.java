package disintegration.world.blocks.units;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;

import static mindustry.Vars.*;

public class UnitAssemblerConstructModule extends BaseTurret {

    public float buildSpeed = 1f;
    public float buildBeamOffset = 5f;
    public float elevation;
    public float buildCone = 5f;

    public TextureRegion baseRegion;
    public TextureRegion[] baseRegions;
    public TextureRegion topRegion;

    public UnitAssemblerConstructModule(String name) {
        super(name);
        update = true;
        solid = true;
        rotate = true;
        rotateSpeed = 1.5f;
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
        baseRegions = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            baseRegions[i] = Core.atlas.find(name + "-base" + (i + 1));
        }
        topRegion = Core.atlas.find(name + "-base-top");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        var link = getLink(player.team(), x, y, rotation);
        if(link != null){
            link.block.drawPlace(link.tile.x, link.tile.y, link.rotation, true);
        }
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return getLink(team, tile.x, tile.y, rotation) != null;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(baseRegions[plan.rotation], plan.drawx(), plan.drawy());
        Draw.rect(region, plan.drawx(), plan.drawy());
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.remove(Stat.shootRange);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    public @Nullable UnitAssembler.UnitAssemblerBuild getLink(Team team, int x, int y, int rotation){
        var results = Vars.indexer.getFlagged(team, BlockFlag.unitAssembler).<UnitAssembler.UnitAssemblerBuild>as();

        return results.find(b -> b.moduleFits(this, x * tilesize + offset, y * tilesize + offset, rotation));
    }

    public class UnitAssemblerConstructModuleBuild extends BaseTurretBuild {
        public UnitAssembler.UnitAssemblerBuild link;
        public boolean building;

        @Override
        public void draw(){
            Draw.rect(baseRegions[rotation()], x, y);
            Draw.z(Layer.turret);
            Drawf.shadow(region, x - elevation, y - elevation, rotation - 90);
            Draw.rect(region, x, y, rotation - 90);
            if(link == null) return;
            float dest = angleTo(link.getUnitSpawn());
            if(efficiency > 0 && Angles.within(rotation, dest, buildCone)) {
                Draw.reset();
                Draw.z(Layer.buildBeam);
                Draw.color(Pal.accent, Math.min(1f - link.invalidWarmup, link.warmup));
                if (building) {
                    float
                            px = x + Angles.trnsx(rotation, buildBeamOffset),
                            py = y + Angles.trnsy(rotation, buildBeamOffset);

                    Drawf.buildBeam(px, py, link.getUnitSpawn().x, link.getUnitSpawn().y, link.plan().unit.hitSize / 2f);
                }
            }
        }

        @Override
        public void drawSelect(){
            if(link != null){
                Drawf.selected(link, Pal.accent);
            }
        }

        @Override
        public void updateTile(){
            link = getLink(player.team(), tileX(), tileY(), rotation());
            if(link == null) return;
            building = link.shouldConsume();
            if(!link.wasOccupied && efficiency > 0 && Units.canCreate(team, link.plan().unit)){
                float dest = angleTo(link.getUnitSpawn());
                rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta());
                link.progress += edelta() * (buildSpeed - 1f) / link.plan().time * state.rules.unitBuildSpeed(team);
            }
        }

        @Override
        public boolean shouldConsume(){
            return link != null && enabled && link.shouldConsume();
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
