package disintegration.maps.planet;

import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import disintegration.content.DTBlocks;
import disintegration.util.WorldDef;
import mindustry.game.Team;
import mindustry.game.Waves;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.maps.generators.BlankPlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.state;
import static mindustry.Vars.world;

public class SpaceStationGenerator extends BlankPlanetGenerator {
    public Block core = DTBlocks.spaceStationCore;
    @Override
    public void generate(){
        seed = state.rules.sector.planet.id;
        int sx = width/2, sy = height/2;
        rand = new Rand(seed);

        Floor background = DTBlocks.lightSpace.asFloor();
        Floor ground = DTBlocks.spaceStationFloor.asFloor();

        tiles.eachTile(t -> t.setFloor(background));

        WorldDef.getAreaTile(new Vec2(sx, sy), core.size + 4, core.size + 4).forEach(t -> {
            t.setFloor(ground);
        });
        world.tile(sx + core.size / 2 + 3, sy + core.size / 2 + 3).setBlock(core, Team.sharded);

        state.rules.planetBackground = new PlanetParams(){{
            planet = sector.planet;
            zoom = 1f;
            camPos = new Vec3(1.2388899f, 1.6047299f, /*2.4758825f*/0);
        }};

        state.rules.dragMultiplier = 0.7f; //yes, space actually has 0 drag but true 0% drag is very annoying
        state.rules.borderDarkness = false;
        state.rules.waves = true;

        state.rules.showSpawns = true;
        state.rules.spawns = Waves.generate(0.5f, rand, false, true, false);
    }

    @Override
    public int getSectorSize(Sector sector){
        return 800;
    }
}
