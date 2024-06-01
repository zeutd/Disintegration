package disintegration.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.EnumSet;
import arc.util.Eachable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.content.DTBlocks;
import disintegration.util.MathDef;
import disintegration.util.WorldDef;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.power.SolarGenerator;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.indexer;
import static mindustry.Vars.tilesize;

public class SpaceSolarGenerator extends SolarGenerator{
    public TextureRegion topRegion;
    public TextureRegion panelRegion;

    public float expandSpeed;

    public Floor[] spaceFloor;

    public Rect spaceRect;

    public float detectTime = 60 * 5f;

    public SpaceSolarGenerator(String name) {
        super(name);
        flags = EnumSet.of(BlockFlag.generator);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        x *= tilesize;
        y *= tilesize;

        x += offset;
        y += offset;

        x -= Geometry.d8edge(rotation).x * tilesize / 2f;
        y -= Geometry.d8edge(rotation).y * tilesize / 2f;

        Rect rect = getRect(Tmp.r1, x, y, rotation);

        Drawf.dashRect(valid ? Pal.accent : Pal.remove, rect);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return !indexer.getFlagged(team, BlockFlag.generator).contains(b -> {
            if (b instanceof SpaceSolarGeneratorBuild build) {
                Rect rect = getRect(Tmp.r1, tile.worldx() - Geometry.d8edge(rotation).x * tilesize / 2f, tile.worldy() - Geometry.d8edge(rotation).y * tilesize / 2f, rotation);
                return getRect(Tmp.r2, build.x - Geometry.d8edge(build.rotation).x * tilesize / 2f, build.y - Geometry.d8edge(build.rotation).y * tilesize / 2f, build.rotation).overlaps(rect);
            }
            return false;
        });
    }

    @Override
    public void load(){
        super.load();
        topRegion = Core.atlas.find(name + "-top");
        panelRegion = Core.atlas.find(name + "-panel");
    }
    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(topRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.rect(panelRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
    }

    public Rect getRect(Rect rect, float x, float y, int rotation){
        Rect tmpRect = new Rect(spaceRect.x * tilesize, spaceRect.y * tilesize, spaceRect.width * tilesize, spaceRect.height * tilesize);
        tmpRect.getCenter(Tmp.v1);
        float width = rotation == 0 || rotation == 2 ? tmpRect.width : tmpRect.height;
        float height = rotation == 0 || rotation == 2 ? tmpRect.height : tmpRect.width;
        for (int i = 0; i < rotation; i++) {
            Tmp.v1.rotate90(1);
        }
        Tmp.v1.add(x, y);
        return rect.setCentered(Tmp.v1.x, Tmp.v1.y, width, height);
    }

    public Rect getRectTile(Rect rect, float x, float y, int rotation){
        spaceRect.getCenter(Tmp.v1);
        Tmp.v1.set(Mathf.floor(Tmp.v1.x), Mathf.floor(Tmp.v1.y));
        float width = rotation == 0 || rotation == 2 ? spaceRect.width : spaceRect.height;
        float height = rotation == 0 || rotation == 2 ? spaceRect.height : spaceRect.width;
        for (int i = 0; i < rotation; i++) {
            Tmp.v1.rotate90(1);
        }
        Tmp.v1.add(x, y);
        return rect.setCentered(Tmp.v1.x, Tmp.v1.y, width, height);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }

    public class SpaceSolarGeneratorBuild extends SolarGeneratorBuild{
        public float expand = 0f;
        public float e = 1;
        @Override
        public void draw(){
            super.draw();
            float width = panelRegion.width * expand / 4f;
            float height = panelRegion.height / 4f;
            Draw.rect(topRegion, x, y, rotdeg());
            Draw.rect(panelRegion, x, y, width, height, rotdeg());
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if (timer.get(detectTime)){
                Rect rect = getRectTile(Tmp.r1, tileX(), tileY(), rotation);
                rect.getPosition(Tmp.v1);
                e = 1;
                WorldDef.getAreaTile(Tmp.v1, Mathf.round(rect.width), Mathf.round(rect.height)).forEach(t -> {
                    if (t != null) {
                        boolean b = false;
                        for (Floor floor : spaceFloor) {
                            if (t.floor() == floor) {
                                b = true;
                                break;
                            }
                        }
                        if (!b) e -= 1 / spaceRect.area();
                    }
                    else e -= 1 / spaceRect.area();
                });
            }
            expand = Mathf.lerpDelta(expand, 1, expandSpeed);
            productionEfficiency = e;
        }

        @Override
        public void drawSelect(){
            float dx = x -  Geometry.d8edge(rotation).x * tilesize / 2f;
            float dy = y -Geometry.d8edge(rotation).y * tilesize / 2f;

            Rect rect = getRect(Tmp.r1, dx, dy, rotation);

            Drawf.dashRect(Pal.accent, rect);
        }

        @Override
        public void write(Writes write){
            write.f(expand);
        }

        @Override
        public void read(Reads read, byte revisions){
            expand = read.f();
        }
    }
}
