package disintegration.world.blocks.debug;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.util.Tmp;
import arclibrary.graphics.g3d.model.Model;
import disintegration.content.DTBlocks;
import disintegration.graphics.DTShaders;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;
import mindustry.graphics.Shaders;
import mindustry.world.Block;

import static arc.Core.graphics;
import static disintegration.graphics.DTShaders.blackHole;

public class ShaderTestBlock extends Block {
    public Shader shader;
    public Model model;
    private static final FrameBuffer buffer = new FrameBuffer();

    public ShaderTestBlock(String name) {
        super(name);
        update = true;
        solid = true;
    }

    @Override
    public void load() {
        super.load();
        //model = DTUtil.loadObj("aaaaaa.obj").first();
        //model.setTransformation(new Mat3D().scl(0));
//        Events.run(EventType.Trigger.drawOver, () -> buffer.blit(shader));
    }

    public class ShaderTestBuild extends Building {
        @Override
        public void draw() {
            /*model.setTranslation(new Vec3(x, y, 0));
            DTVars.renderer3D.models.add((Model) model.cloneModel());*/
            if (shader == blackHole) {
                blackHole.add(x, y, 64, 64);
                return;
            }
            //super.draw();
            if (shader == DTShaders.arc) {
                DTShaders.arc.center = Tmp.v2.set(this);
                DTShaders.arc.time = Mathf.absin(5, 1);
                buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
                buffer.begin(Color.clear);
                super.draw();
                buffer.end();
                buffer.blit(shader);
                return;
            }
            Draw.draw(Layer.flyingUnitLow - 1, () -> {
                shader.apply();
                Draw.shader(shader);
                super.draw();
                Draw.shader();
            });
            /*if (Groups.build.count(b -> b.block == DTBlocks.shaderTestBlock) > 1) return;
            buffer.resize(graphics.getWidth(), graphics.getHeight());
            Draw.draw(Layer.block, () -> buffer.begin(Color.clear));
            Draw.draw(Layer.flyingUnitLow + 2, () -> {
                buffer.end();
                buffer.blit(shader);
            });*/
            //super.draw();
        }
    }
}
