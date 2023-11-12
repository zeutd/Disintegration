package disintegration.world.blocks.debug;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.geom.Vec2;
import disintegration.content.DTBlocks;
import disintegration.graphics.DTShaders;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;
import mindustry.world.Block;

import static arc.Core.graphics;

public class ShaderTestBlock extends Block {
    public Shader shader;
    private static final FrameBuffer buffer = new FrameBuffer();
    public ShaderTestBlock(String name) {
        super(name);
        update = true;
        solid = true;
    }

    @Override
    public void load(){
        super.load();
//        Events.run(EventType.Trigger.drawOver, () -> buffer.blit(shader));
    }

    public class ShaderTestBuild extends Building {
        @Override
        public void draw(){
            if (shader instanceof DTShaders.BlackHoleShader blackHole){
                blackHole.pos = new Vec2(x, y);
            }
            /*Draw.draw(Layer.flyingUnitLow - 1, () -> {
                shader.apply();
                Draw.shader(shader);
                super.draw();
                Draw.shader();
            });*/
            if(Groups.build.count(b -> b.block == DTBlocks.shaderTestBlock) > 1)return;
            buffer.resize(graphics.getWidth(), graphics.getHeight());
            Draw.draw(Layer.min, () -> buffer.begin(Color.clear));
            Draw.draw(Layer.plans + 1, () -> {
                buffer.end();
                buffer.blit(shader);
            });
            //super.draw();
        }
    }
}
