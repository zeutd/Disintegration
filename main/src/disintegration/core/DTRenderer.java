package disintegration.core;

import arc.ApplicationListener;
import arc.graphics.Color;
import arc.math.Mathf;
import disintegration.graphics.g3d.SpaceStationRenderer;
import disintegration.type.SpaceStation;
import mindustry.Vars;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

import static arc.Core.graphics;
import static mindustry.Vars.state;

public class DTRenderer implements ApplicationListener {
    public final SpaceStationRenderer spaceStation = new SpaceStationRenderer();

    public DTRenderer(){

    }
    @Override
    public void update() {
        Vars.renderer.planets.cam.near = 0.4f;
        spaceStation.update();
    }
}
