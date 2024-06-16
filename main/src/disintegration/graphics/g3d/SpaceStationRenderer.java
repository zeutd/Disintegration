package disintegration.graphics.g3d;

import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.g3d.VertexBatch3D;
import arc.math.Angles;
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
    private static final Seq<Vec3> points = new Seq<>();
    public void update(){

    }

    public void drawExportLines(){
        spaceStations.each(s -> {
            if(exportHandler.interplanetaryExport.containsKey(s)){
                exportHandler.interplanetaryExport.get(s).each((t, map) -> {
                    final float[] min = {0};
                    map.each((__, stat) -> {
                        if(stat.mean > min[0]) min[0] = stat.mean;
                    });
                    if(min[0] < 0.01f) return;
                    drawArc(Vars.renderer.planets.batch, t.position, s.position, Pal.accent, Color.clear, 80, 3 * (int)s.position.dst(t.position));
                });
            }
        });
    }

    public void drawArc(VertexBatch3D batch, Vec3 a, Vec3 b, Color from, Color to, float timeScale, int pointCount){
        //increase curve height when on opposite side of planet, so it doesn't tunnel through
        points.clear();

        Tmp.v31.set(a).slerp(b, 2f/3f).setLength(lerp(a.len(), b.len(), 1f/3f));
        Tmp.v31.set(a).slerp(b, 1f/3f).setLength(lerp(a.len(), b.len(), 2f/3f));
        points.addAll(a, Tmp.v31, b);
        Tmp.bz3.set(points);
        for(int i = 0; i < pointCount + 1; i++){
            float f = i / (float)pointCount;
            Tmp.c1.set(from).lerp(to, (f + Time.globalTime / timeScale) % 1f);
            batch.color(Tmp.c1);
            batch.vertex(Tmp.bz3.valueAt(Tmp.v32, f));
        }
        batch.flush(Gl.lineStrip);
    }
}
