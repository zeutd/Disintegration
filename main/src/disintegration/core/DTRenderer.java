package disintegration.core;

import arc.ApplicationListener;
import disintegration.graphics.DTShaders;
import disintegration.graphics.g3d.SpaceStationRenderer;
import mindustry.Vars;
import mindustry.graphics.Shaders;

public class DTRenderer implements ApplicationListener {
    public final SpaceStationRenderer spaceStation = new SpaceStationRenderer();

    public DTRenderer(){
        DTShaders.init();
    }
    @Override
    public void update() {
        Vars.renderer.planets.cam.near = 0.4f;
        spaceStation.update();
    }
}
