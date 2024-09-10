package disintegration.entities;

import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Tmp;
import disintegration.content.DTFx;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Unitc;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class LargeLightning {
    private static final Rand random = new Rand();
    private static final Rect rect = new Rect();
    private static final Seq<Unitc> entities = new Seq<>();
    private static final IntSet hit = new IntSet();
    private static final int maxChain = 8;
    private static final float hitRange = 30f;
    private static boolean bhit = false;
    private static int lastSeed = 0;

    /**
     * Create a lighting branch at a location. Use Team.derelict to damage everyone.
     */
    public static void create(Team team, Color color, float damage, float x, float y, float targetAngle, int length) {
        createLightningInternal(null, null, lastSeed++, team, color, damage, x, y, targetAngle, length);
    }

    /**
     * Create a lighting branch at a location. Uses bullet parameters.
     */
    public static void create(Bullet bullet, BulletType end, Color color, float damage, float x, float y, float targetAngle, int length) {
        createLightningInternal(bullet, end, lastSeed++, bullet.team, color, damage, x, y, targetAngle, length);
    }

    private static void createLightningInternal(@Nullable Bullet hitter, @Nullable BulletType end,  int seed, Team team, Color color, float damage, float x, float y, float rotation, int length) {
        random.setSeed(seed);
        hit.clear();

        BulletType hitCreate = hitter == null || hitter.type.lightningType == null ? Bullets.damageLightning : hitter.type.lightningType;
        Seq<Vec2> lines = new Seq<>();
        bhit = false;

        for (int i = 0; i < length / 2; i++) {
            hitCreate.create(null, team, x, y, rotation, damage * (hitter == null ? 1f : hitter.damageMultiplier()), 1f, 1f, hitter);
            lines.add(new Vec2(x + Mathf.range(3f), y + Mathf.range(3f)));

            if (lines.size > 1) {
                bhit = false;
                Vec2 from = lines.get(lines.size - 2);
                Vec2 to = lines.get(lines.size - 1);
                World.raycastEach(World.toTile(from.getX()), World.toTile(from.getY()), World.toTile(to.getX()), World.toTile(to.getY()), (wx, wy) -> {

                    Tile tile = world.tile(wx, wy);
                    if (tile != null && (tile.build != null && tile.build.isInsulated()) && tile.team() != team) {
                        bhit = true;
                        //snap it instead of removing
                        lines.get(lines.size - 1).set(wx * tilesize, wy * tilesize);
                        return true;
                    }
                    return false;
                });
                if (bhit) break;
            }

            rect.setSize(hitRange).setCenter(x, y);
            entities.clear();
            if (hit.size < maxChain) {
                Units.nearbyEnemies(team, rect, u -> {
                    if (!hit.contains(u.id()) && (hitter == null || u.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround))) {
                        entities.add(u);
                    }
                });
            }

            Unitc furthest = Geometry.findFurthest(x, y, entities);

            if (furthest != null) {
                hit.add(furthest.id());
                x = furthest.x();
                y = furthest.y();
            } else {
                rotation += random.range(20f);
                x += Angles.trnsx(rotation, hitRange / 2f);
                y += Angles.trnsy(rotation, hitRange / 2f);
            }
        }

        if (hitter != null) {
            Tmp.v1.set(lines.get(lines.size - 1));
            end.create(hitter, Tmp.v1.x, Tmp.v1.y, hitter.rotation());
        }

        DTFx.largeLightning.at(x, y, rotation, color, lines);
    }
}