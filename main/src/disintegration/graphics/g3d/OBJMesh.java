package disintegration.graphics.g3d;

import arc.graphics.gl.Shader;
import arc.math.geom.Vec3;
import arclibrary.graphics.g3d.model.obj.OBJModel;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.PlanetMesh;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

public class OBJMesh extends PlanetMesh {
    public OBJMesh(Planet planet, OBJModel model, Shader shader) {
        super(planet, model.mesh, shader);
    }

    public OBJMesh() {
    }

    @Override
    public void preRender(PlanetParams params) {
        Shaders.planet.planet = planet;
        Shaders.planet.lightDir.set(planet.solarSystem.position).sub(planet.position).rotate(Vec3.Y, planet.getRotation()).nor();
        Shaders.planet.ambientColor.set(planet.solarSystem.lightColor);
    }
}
