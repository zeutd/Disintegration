package disintegration.ui.dialogs;

import arc.Core;
import arc.graphics.Camera;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g3d.VertexBatch3D;
import arc.input.KeyCode;
import arc.math.Mat;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.scene.event.ElementGestureListener;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.graphics.Pal;
import mindustry.type.Planet;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.PlanetDialog;

import static arc.math.Mathf.lerp;
import static disintegration.DTVars.exportHandler;
import static disintegration.DTVars.spaceStations;

public class ExportOverviewDialog extends BaseDialog {
    public Camera cam;
    public float scaling = 5f;
    private static final Seq<Vec2> points = new Seq<>();
    public ExportOverviewDialog() {
        super("exportoverview");
        addCloseButton();
        dragged((cx, cy) -> {
            //no multitouch drag
            if(Core.input.getTouches() > 1) return;
            cam.position.add(-cx / Mathf.pow(2, scaling), -cy / Mathf.pow(2, scaling));
        });

        addListener(new InputListener(){
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY){
                if(event.targetActor == ExportOverviewDialog.this){
                    scaling = Mathf.clamp(scaling - amountY / 3, Mathf.log(2, 10/Vars.ui.planet.state.planet.solarSystem.totalRadius), Mathf.log(2, 20f));
                }
                return true;
            }
        });

        addCaptureListener(new ElementGestureListener(){
            float lastZoom = -1f;

            @Override
            public void zoom(InputEvent event, float initialDistance, float distance){
                if(lastZoom < 0){
                    lastZoom = scaling;
                }

                scaling = (initialDistance / distance * lastZoom);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button){
                lastZoom = scaling;
            }
        });
        shown(this::setup);
    }
    void setup(){
        cam = new Camera();
        cam.position.set(0, 0);
    }
    @Override
    public void draw(){
        super.draw();
        cam.resize(Core.graphics.getWidth() / Mathf.pow(2, scaling),
                Core.graphics.getHeight() / Mathf.pow(2, scaling));
        Draw.proj(cam);
        Vars.content.planets().each(p -> {
            if(!(p.solarSystem == Vars.ui.planet.state.planet.solarSystem)) return;
            if(p.accessible || p.solarSystem == p) {
            if(p.parent != null && p.drawOrbit){
                Draw.color(Pal.gray);
                Lines.stroke(0.2f);
                Lines.circle(p.parent.position.x, p.parent.position.z, p.orbitRadius);
            }
                Draw.color(p.iconColor);
                Fill.circle(p.position.x, p.position.z, p.radius);
            }
        });
        exportHandler.interplanetaryExport.each((s, map1) -> {
            map1.each((t, map) -> {
                final float[] min = {0};
                map.each((__, stat) -> {
                    if(stat.mean > min[0]) min[0] = stat.mean;
                });
                if(min[0] < 0.01f) return;
                drawArc(Tmp.v5.set(t.position.x, t.position.z), Tmp.v6.set(s.position.x, s.position.z), Pal.accent, Color.clear, 80, 3 * (int)s.position.dst(t.position));
            });
        });
    }

    public void drawArc(Vec2 a, Vec2 b, Color from, Color to, float timeScale, int pointCount){
        //increase curve height when on opposite side of planet, so it doesn't tunnel through
        points.clear();

        Tmp.v1.set(a).lerp(b, 1f/2f).setLength(lerp(a.len(), b.len(), 1f/2f));
        points.addAll(a, Tmp.v1, b);
        Tmp.bz2.set(points);
        for(int i = 0; i < pointCount + 1; i++){
            float f = i / (float)pointCount;
            float f1 = (i + 1) / (float)pointCount;
            Tmp.c1.set(from).lerp(to, (f + Time.globalTime / timeScale) % 1f);
            Draw.color(Tmp.c1);
            Lines.line(Tmp.bz2.valueAt(Tmp.v3, f).x, Tmp.bz2.valueAt(Tmp.v3, f).y, Tmp.bz2.valueAt(Tmp.v3, f1).x, Tmp.bz2.valueAt(Tmp.v3, f1).y, false);
        }
        Draw.flush();
    }
}
