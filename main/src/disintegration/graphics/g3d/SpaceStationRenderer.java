package disintegration.graphics.g3d;

import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.g3d.VertexBatch3D;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import disintegration.content.DTPlanets;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.graphics.Pal;

import static arc.math.Mathf.lerp;
import static disintegration.DTVars.exportHandler;
import static disintegration.DTVars.spaceStations;

public class SpaceStationRenderer{
    private static final Seq<Vec2> points = new Seq<>();
    private static final Vec2 aP = new Vec2(), bP = new Vec2();
    public void update(){

    }

    public void drawExportLines(){
        exportHandler.interplanetaryExport.each((s, map1) -> {
            map1.each((t, map) -> {
                final float[] min = {0};
                map.each((__, stat) -> {
                    if(stat.mean > min[0]) min[0] = stat.mean;
                });
                if(min[0] < 0.01f) return;
                aP.set(t.position.x, t.position.z);
                bP.set(s.position.x, s.position.z);
                drawArc(Vars.renderer.planets.batch, aP, bP, Pal.accent, Color.clear, 80, 3 * (int)s.position.dst(t.position));
            });
        });
    }
    public void drawArc(VertexBatch3D batch, Vec2 a, Vec2 b, Color from, Color to, float timeScale, int pointCount){
        //increase curve height when on opposite side of planet, so it doesn't tunnel through
        points.clear();
        Tmp.v1.set(a).setAngle(Mathf.slerp(a.angle(), b.angle(), 1f/3f)).setLength(lerp(a.len(), b.len(), 1f/3f) * 1.1f);
        Tmp.v2.set(a).setAngle(Mathf.slerp(a.angle(), b.angle(), 2f/3f)).setLength(lerp(a.len(), b.len(), 2f/3f) * 1.1f);
        points.addAll(a, Tmp.v1, Tmp.v2, b);
        Tmp.bz2.set(points);
        for(int i = 0; i < pointCount + 1; i++){
            float f = i / (float)pointCount;
            Tmp.c1.set(from).lerp(to, (f + Time.globalTime / timeScale) % 1f);
            batch.color(Tmp.c1);
            Tmp.bz2.valueAt(Tmp.v3, f);
            batch.vertex(Tmp.v31.set(Tmp.v3.x, 0, Tmp.v3.y));
        }
        batch.flush(Gl.lineStrip);
    }
}
