package disintegration.core;

import arc.ApplicationListener;
import arc.math.Mathf;
import disintegration.graphics.g3d.SpaceStationRenderer;
import mindustry.Vars;

public class DTRenderer implements ApplicationListener {
    public final SpaceStationRenderer spaceStation = new SpaceStationRenderer();

    @Override
    public void update(){
        Vars.renderer.planets.cam.near = 0.4f;
        spaceStation.update();
    }
}
