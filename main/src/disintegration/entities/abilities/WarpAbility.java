package disintegration.entities.abilities;

import arc.Core;
import arc.audio.Sound;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

public class WarpAbility extends Ability {
    public float warpRange;
    public float minWarpRange = -1f;
    public float warpReload;
    public @Nullable StatusEffect warpStatus;
    public float warpStatusDuration = -1;
    public float warpDuration = 0;

    public Effect warpEffect = Fx.none;
    public Effect warpTrailEffect = Fx.none;
    public Effect warpChargeEffect = Fx.none;

    public Sound chargeSound;
    public Sound warpSound;
    public float soundVolume;


    public float warpReloadCounter;
    public float warpCounter;
    public boolean warping;

    private final Vec2 dest = new Vec2();

    @Override
    public void addStats(Table t){
        t.add("[lightgray]" + Stat.shootRange.localized() + ": [white]" +  Strings.autoFixed(warpRange / tilesize, 2) + " " + StatUnit.blocks.localized());
        t.row();
        t.add("[lightgray]" + Stat.reload.localized() + ": [white]" + Strings.autoFixed(60f / warpReload, 2) + " " + StatUnit.perSecond.localized());
    }

    @Override
    public void update(Unit u){
        if(warpReloadCounter <= warpReload) {
            warpReloadCounter += Time.delta;
        }
        boolean willWarp = (u.isPlayer() && u.isShooting()) || (!u.isPlayer() && u.isCommandable() && (u.command().targetPos != null));
        if(willWarp && warpReloadCounter >= warpReload) {
            if(u.isPlayer()){
                dest.set(u.aimX(),u.aimY());
            }
            else {
                dest.set(u.command().targetPos);
            }
            if(u.dst(dest) > minWarpRange) {
                warping = true;
                warpReloadCounter = 0f;
                warpChargeEffect.at(u.x, u.y);
                chargeSound.at(u.x, u.y, 0, soundVolume);
                u.apply(StatusEffects.unmoving, warpDuration);
            }
        }
        if (warpCounter <= warpDuration && warping)warpCounter += Time.delta;
        if (warpCounter >= warpDuration && warping){
            warpCounter = 0f;
            dest.sub(u).limit(warpRange).add(u).clamp(0, 0, state.map.width * tilesize, state.map.height * tilesize);
            warp(dest, u);
            warping = false;
        }
    }

    public void warp(Vec2 dest, Unit u){
        if(warpStatusDuration >= 0 && warpStatus != null)u.apply(warpStatus, warpStatusDuration);
        warpSound.at(u.x, u.y, 0, soundVolume);
        warpSound.at(dest.x, dest.y, 0, soundVolume);
        warpEffect.at(u.x, u.y);
        warpEffect.at(dest);
        warpTrailEffect.at(u.x, u.y, 0, dest.cpy());
        u.set(dest);
    }
}
