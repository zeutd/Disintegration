package disintegration.entities.comp;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import disintegration.entities.DTGroups;
import disintegration.gen.entities.BlackHolec;
import disintegration.graphics.DTShaders;
import ent.anno.Annotations.EntityComponent;
import ent.anno.Annotations.EntityDef;
import ent.anno.Annotations.Import;
import ent.anno.Annotations.Replace;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Drawc;
import mindustry.gen.Teamc;

@EntityComponent
@EntityDef({Teamc.class, Drawc.class, BlackHolec.class})
abstract class BlackHoleComp implements Teamc, Drawc, BlackHolec {
    @Import
    float x, y;
    @Import
    Team team;
    public float force, radius;
    public float attractForce, attractRadius;
    public float teamForceMultiplier;
    public float scaledForce;

    /*public static float calculateForce(float dst, float force){
        //return (abs(dst / radius) - 1)*(abs(dst / radius) - 1)*force;
        return Mathf.pow(1 / dst * force, 2);
    }*/

    public float force() {
        return force;
    }

    public float radius() {
        return radius;
    }

    public float attractForce() {
        return attractForce;
    }

    public float attractRadius() {
        return attractRadius;
    }

    public float teamForceMultiplier() {
        return teamForceMultiplier;
    }

    public float scaledForce() {
        return scaledForce;
    }

    public void force(float force) {
        this.force = force;
    }

    public void radius(float radius) {
        this.radius = radius;
    }

    public void attractForce(float attractForce) {
        this.attractForce = attractForce;
    }

    public void attractRadius(float attractRadius) {
        this.attractRadius = attractRadius;
    }

    public void teamForceMultiplier(float teamForceMultiplier) {
        this.teamForceMultiplier = teamForceMultiplier;
    }

    public void scaledForce(float scaledForce) {
        this.scaledForce = scaledForce;
    }

    @Override
    public void draw() {
        DTShaders.blackHole.add(x, y, radius, force);
        //Drawf.dashCircle(x, y, attractRadius, Pal.accent);
    }

    @Override
    public void add() {
        DTGroups.blackHole.add(this);
    }

    @Override
    public void remove() {
        DTGroups.blackHole.remove(this);
    }

    @Replace
    @Override
    public void update() {
        /*Units.nearby(null, x, y, attractRadius, u -> {
            float dst = Mathf.dst(x, y, u.x(), u.y());
            if(dst < minRadius) return;
            float dir = Mathf.atan2(x - u.x(), y - u.y());
            float f = attractForce;
            if(u.team == team) f = attractForce * teamForceMultiplier;
            float dstc = calculateForce(dst, f);
            u.vel().add(Mathf.cos(dir) * dstc / u.type.hitSize, Mathf.sin(dir) * dstc * Time.delta);
        });*/
        Units.nearby(null, x, y, attractRadius, u -> {
            float multiplier = u.team == team ? teamForceMultiplier : 1;
            Vec2 attract = Tmp.v1.trns(u.angleTo(this), attractForce * multiplier + Mathf.sqr(Math.abs(u.dst(this) / attractRadius) - 1) * scaledForce * multiplier);
            u.impulseNet(attract);
        });
        /*Groups.all.each(e -> e instanceof Velc v && Mathf.dst(x, y, v.x(), v.y()) < attractRadius, e -> {
            Velc v = (Velc) e;
            float dst = Mathf.dst(x, y, v.x(), v.y());
            if (dst < attractRadius) {
                float dir = Mathf.atan2(x - v.x(), y - v.y());
                float f = attractForce;
                if (v instanceof Teamc) f = attractForce * teamForceMultiplier;
                float dstc = calculateForce(dst, f);
                v.vel().add(Mathf.cos(dir) * dstc * Time.delta, Mathf.sin(dir) * dstc * Time.delta);
            }
        });*/
    }
}
