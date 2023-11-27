package disintegration.entities;

import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.graphics.DTShaders;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Velc;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.storage.CoreBlock;

import static java.lang.Math.abs;

public class BlackHole implements BlackHolec{
    public float x;
    public float y;
    public int id;
    public int index__all = -1;
    public int index__draw = -1;
    public int index__blackHole = -1;

    public float radius;
    public float force;
    public float attractForce;
    public boolean added;
    public Team team;
    public BlackHole(){
        team = Team.derelict;
    }

    public float cforce(float dst, float radius, float force){
        return (abs(dst / radius) - 1)*(abs(dst / radius) - 1)*force;
    }
    @Override
    public float radius() {
        return radius;
    }

    @Override
    public float force() {
        return force;
    }

    @Override
    public float attractForce(){
        return attractForce;
    }

    @Override
    public void radius(float r) {
        radius = r;
    }

    @Override
    public void force(float f) {
        force = f;
    }

    @Override
    public void attractForce(float f){
        attractForce = f;
    }

    @Override
    public float clipSize() {
        return radius;
    }

    @Override
    public void draw() {
        DTShaders.blackHole.add(x, y, radius, force);
    }

    @Override
    public boolean inFogTo(Team team) {
        return false;
    }

    @Override
    public boolean cheating() {
        return false;
    }

    @Override
    public Team team() {
        return team;
    }

    @Override
    public CoreBlock.CoreBuild closestCore() {
        return Vars.state.teams.closestCore(this.x, this.y, this.team);
    }

    @Override
    public CoreBlock.CoreBuild closestEnemyCore() {
        return Vars.state.teams.closestEnemyCore(this.x, this.y, this.team);
    }

    @Override
    public CoreBlock.CoreBuild core() {
        return this.team.core();
    }

    @Override
    public void team(Team team) {
        this.team = team;
    }

    @Override
    public Floor floorOn() {
        return null;
    }

    @Override
    public Building buildOn() {
        return null;
    }

    @Override
    public boolean onSolid() {
        return false;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public int tileX() {
        return World.toTile(x);
    }

    @Override
    public int tileY() {
        return World.toTile(y);
    }

    @Override
    public Block blockOn() {
        return null;
    }

    @Override
    public Tile tileOn() {
        return null;
    }

    @Override
    public void set(Position position) {
        set(position.getX(), position.getY());
    }

    @Override
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void trns(Position position) {
        trns(position.getX(), position.getY());
    }

    @Override
    public void trns(float x, float y) {
        set(this.x + x, this.y + y);
    }

    @Override
    public void x(float x) {
        this.x = x;
    }

    @Override
    public void y(float y) {
        this.y = y;
    }

    @Override
    public <T extends Entityc> T self() {
        return (T) this;
    }

    @Override
    public <T> T as() {
        return (T) this;
    }

    @Override
    public boolean isAdded() {
        return added;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public boolean serialize() {
        return false;
    }

    @Override
    public int classId() {
        return 40;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public void add() {
        if (!added) {
            //index__all = Groups.all.addIndex(this);
            index__draw = Groups.draw.addIndex(this);
            index__blackHole = DTGroups.blackHole.addIndex(this);

            added = true;
        }
    }

    @Override
    public void afterRead() {

    }

    @Override
    public void id(int id) {
        this.id = id;
    }

    @Override
    public void read(Reads reads) {

    }

    @Override
    public void remove() {
        if (added) {
            //Groups.all.removeIndex(this, index__all);
            //index__all = -1;
            Groups.draw.removeIndex(this, index__draw);
            index__draw = -1;
            DTGroups.blackHole.removeIndex(this, index__blackHole);
            index__blackHole = -1;

            added = false;
        }
    }

    @Override
    public void update() {
        Groups.all.each(e -> e instanceof Velc, e -> {
            Velc v = (Velc) e;
            float dst = Mathf.dst(x, y, v.x(), v.y());
            if(dst < radius){
                float dir = Mathf.atan2(x - v.x(), y - v.y());
                float dstc = cforce(dst, radius, attractForce);
                v.vel().add(Mathf.cos(dir) * dstc * Time.delta, Mathf.sin(dir) * dstc * Time.delta);
            }
        });

    }

    @Override
    public void write(Writes writes) {

    }
}
