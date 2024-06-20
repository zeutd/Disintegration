package disintegration.world.blocks.defence.turrets;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Tmp;
import disintegration.world.meta.DTStat;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.defense.turrets.ReloadTurret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.state;

public class ElectricTowerTurret extends ReloadTurret {
    private static final Seq<Healthc> all = new Seq<>();

    public float damage = 1, reload = 100, range = 60, warmupSpeed = 1 / 30f;
    public Effect healEffect = Fx.heal, hitEffect = Fx.hitLaserBlast, damageEffect = Fx.chainLightning;
    public StatusEffect status = StatusEffects.shocked;
    public Sound shootSound = Sounds.plasmaboom;
    public float statusDuration = 60f * 6f;
    public boolean targetGround = true, targetAir = true, hitBuildings = true, hitUnits = true;
    public int maxTargets = 25;
    public float healPercent = 0f;
    public Color color = Pal.lancerLaser;
    public Color heatColor = Pal.turretHeat;

    public float cooldownTime, elevation;

    public TextureRegion heatRegion, baseRegion;

    public ElectricTowerTurret(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        heatRegion = Core.atlas.find(name + "-heat");
        baseRegion = Core.atlas.find(name + "-base", "block-" + size);
    }

    @Override
    public void init() {
        if (elevation < 0) elevation = size / 2f;
        if (cooldownTime < 0f) cooldownTime = reload;

        super.init();
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{baseRegion, region};
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.reload, 60f / reload, StatUnit.perSecond);
        stats.add(Stat.targetsAir, targetAir);
        stats.add(Stat.targetsGround, targetGround);
        stats.add(DTStat.maxLinks, maxTargets);
        stats.add(Stat.damage, damage);
    }

    public class ElectricTowerBuild extends ReloadTurretBuild {

        public float heat, warmup;

        @Override
        public void draw() {
            Draw.rect(baseRegion, x, y);
            Drawf.shadow(region, x - elevation, y - elevation);
            Draw.rect(region, x, y);
            Drawf.additive(heatRegion, heatColor.write(Tmp.c1).a(heat), x, y, 0, Layer.turretHeat);
        }

        protected void updateReload() {
            reloadCounter += delta() * baseReloadSpeed();

            //cap reload for visual reasons
            reloadCounter = Math.min(reloadCounter, reload);
        }

        @Override
        public boolean shouldConsume() {
            return super.shouldConsume() && isShooting() || reloadCounter < reload;
        }

        public boolean isShooting() {
            return !all.isEmpty();
        }

        @Override
        public void updateTile() {
            super.updateTile();
            updateReload();
            if (coolant != null) updateCooling();
            heat = Mathf.approachDelta(heat, 0, 1 / cooldownTime);
            warmup = Mathf.approachDelta(warmup, isShooting() ? 1 : 0, warmupSpeed);

            all.clear();

            if (hitUnits) {
                Units.nearby(null, x, y, range, other -> {
                    if (other.checkTarget(targetAir, targetGround) && other.targetable(team) && (other.team != team || (other.damaged() && healPercent > 0))) {
                        all.add(other);
                    }
                });
            }

            if (hitBuildings && targetGround) {
                Units.nearbyBuildings(x, y, range, b -> {
                    if ((b.team != Team.derelict || state.rules.coreCapture) && (b.team != team || (b.damaged() && healPercent > 0))) {
                        all.add(b);
                    }
                });
            }

            if (!all.isEmpty() && reloadCounter >= reload && warmup >= 0.99) {
                all.sort(h -> h.dst2(x, y));
                int len = Math.min(all.size, maxTargets);
                for (int i = 0; i < len; i++) {
                    Healthc other = all.get(i);

                    //lightning gets absorbed by plastanium
                    var absorber = Damage.findAbsorber(team, x, y, other.getX(), other.getY());
                    if (absorber != null) {
                        other = absorber;
                    }

                    if (((Teamc) other).team() == team) {
                        if (other.damaged()) {
                            other.heal(healPercent / 100f * other.maxHealth());
                            healEffect.at(other);
                            damageEffect.at(x, y, 0f, color, other);
                            hitEffect.at(x, y, angleTo(other), color);

                            if (other instanceof Building b) {
                                Fx.healBlockFull.at(b.x, b.y, 0f, color, b.block);
                            }
                        }
                    } else {
                        if (other instanceof Building b) {
                            b.damage(team, damage * state.rules.unitDamage(team));
                        } else {
                            other.damage(damage * state.rules.unitDamage(team));
                        }
                        if (other instanceof Statusc s) {
                            s.apply(status, statusDuration);
                        }
                        hitEffect.at(other.x(), other.y(), angleTo(other), color);
                        damageEffect.at(x, y, 0f, color, other);
                        hitEffect.at(x, y, angleTo(other), color);
                    }
                }

                shootSound.at(this);

                reloadCounter = 0;

                heat = 1f;
            }
        }
    }
}
