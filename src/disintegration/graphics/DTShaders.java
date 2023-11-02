package disintegration.graphics;

import arc.Core;
import arc.files.Fi;
import arc.graphics.gl.Shader;
import arc.math.geom.Vec2;
import disintegration.DTVars;

public class DTShaders {
    public static BlackHoleShader blackHole;

    public static void init(){
        blackHole = new BlackHoleShader();
        //((ShaderTestBlock) DTBlocks.shaderTestBlock).shader = blackHole;
    }
    public static class BlackHoleShader extends Shader {
        public Vec2 pos;
        public BlackHoleShader() {
            super(Core.files.internal("shaders/screenspace.vert"), getDTShaderFi("blackHole.frag"));
        }
        @Override
        public void apply(){
            setUniformf("v_pos", pos);
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
        }
    }

    public static Fi getDTShaderFi(String file){
        return DTVars.DTModFile.child("shaders").child(file);
    }
}