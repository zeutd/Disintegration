package degradation.world.blocks.defence.turrets;

import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import degradation.world.meta.DTStat;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Healthc;
import mindustry.gen.Sounds;
import mindustry.gen.Statusc;
import mindustry.gen.Teamc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static arc.Core.atlas;

public class ElectricTowerTurret extends BaseTurret {
    public float damage;
    public float reload;
    public float statusDuration = 20f;

    public  float cooldownTime = 100f;

    public int maxTargets = -1;

    public Effect hitEffect = Fx.hitLaserBlast;
    public Effect damageEffect = Fx.chainLightning;

    public Color lightningColor = Pal.lancerLaser;
    public Color heatColor = Pal.turretHeat;

    public TextureRegion baseRegion;
    public TextureRegion heatRegion;

    public boolean targetAir = true, targetGround = false;

    public Sound shootSound = Sounds.plasmaboom;

    public StatusEffect status = StatusEffects.shocked;
    public ElectricTowerTurret(String name) {
        super(name);
        rotateSpeed = 10f;
        coolantMultiplier = 1f;
        envEnabled |= Env.space;
    }

    @Override
    public void load(){
        super.load();
        this.baseRegion = atlas.find("degradation-framed-block-" + size);
        this.heatRegion = atlas.find(name + "-heat");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.targetsAir, targetAir);
        stats.add(Stat.targetsGround, targetGround);
        if (damage > 0) stats.add(Stat.damage, damage / reload * 60, StatUnit.perSecond);
        if (maxTargets >= 0) stats.add(DTStat.maxLinks, maxTargets, StatUnit.none);
    }
    public class ElectricTowerTurretBuild extends BaseTurretBuild{
        float timer;
        Seq<Healthc> all = new Seq<>();
        float heat;

        public void updateTile() {

            Units.nearby(null, x, y, range, other -> {
                if (other.checkTarget(targetAir, targetGround) && other.targetable(team) && other.team() != team && !all.contains(other) && !other.dead() && !(dst(other.x(),other.y()) > range)) {
                    all.add(other);
                }
            });

            heat = Mathf.approachDelta(heat, 0, 1 / cooldownTime);

            all.sort(h -> h.dst2(x, y));
            int len = all.size;
            if (maxTargets >= 0) len = Math.min(all.size, maxTargets);
            if((timer += Time.delta) >= reload){
                for (int i = 0; i < len; i++) {
                    Healthc other = all.get(i);
                    if (other == null || other.dead() || dst(other.x(),other.y()) > range){
                        all.remove(other);
                        len = all.size;
                        continue;
                    }
                    if (((Teamc) other).team() != team){
                        other.damage(damage);
                        if (other instanceof Statusc s){
                            s.apply(status, statusDuration);
                        }
                        if (hitEffect != null) hitEffect.at(other.x(), other.y(), angleTo(other), lightningColor);
                        if (damageEffect != null) damageEffect.at(x, y, 0f, lightningColor, other);
                        if (hitEffect != null) hitEffect.at(x, y, angleTo(other), lightningColor);
                        if (shootSound != null) shootSound.at(x, y);
                        heat = 1f;
                    }
                }
                timer = 0f;
            }
        }

        public void draw(){
            super.draw();
            Draw.rect(baseRegion, x, y);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f));
            Draw.rect(region, x, y);
            Drawf.additive(heatRegion, heatColor.write(Tmp.c1).a(heat), x, y, 0, Layer.turretHeat);
        }
        @Override
        public boolean shouldConsume(){
            return super.shouldConsume() && !all.isEmpty();
        }
        @Override
        public void write(Writes write){
            super.write(write);

            write.f(timer);
            write.f(heat);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            timer = read.f();
            heat = read.f();
        }
    }
}
