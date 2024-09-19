package disintegration.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.graphics.Layer;

public class LaserLightning {
    private static int lastId = 0;
    private static final Rand random = new Rand();
    public static final Seq<LaserLightningData> datas = new Seq<>();
    public static class LaserLightningData{
        public Vec2 start, end;
        public Color color;
        public long seed;
        public int id;
        public float interval, interval2, speed, mag;
        public Seq<Vec2> points = new Seq<>();
        public Seq<Vec2> targets = new Seq<>();
        public LaserLightningData(Vec2 start, Vec2 end, Color color, long seed, float interval, float interval2, float speed, float mag){
            this.start = start;
            this.end = end;
            this.color = color;
            this.seed = seed;
            this.interval = interval;
            this.interval2 = interval2;
            this.speed = speed;
            this.mag = mag;
            this.id = lastId++;
            float len = start.dst(end);
            for(float is = 0; is <= 1 / interval; is += 1){
                points.add(new Vec2((is + 0.5f) * len * interval, 0));
                targets.add(new Vec2((is + 0.5f) * len * interval, 0));
            }
        }

        public void draw(){
            float len = start.dst(end);
            Draw.draw(Layer.effect, () -> {
                Lines.stroke(1, color);
                Draw.trans(Tmp.m1.idt().rotate(start.angleTo(end)).translate(start));
                //Lines.line(0, 0, len, 0);
                Tmp.v5.set(start);
                random.setSeed(seed);
                for (int is = 0; is <= 1 / interval; is += 1){
                    float i = is * interval;
                    Tmp.bz2.set(Tmp.v2.set(i * len, 0), points.get(is), Tmp.v3.set((i + interval) * len, 0));
                    for (float j = 0; j <= 1; j += interval2){
                        Tmp.bz2.valueAt(Tmp.v4, j);
                        Tmp.v1.trns(Time.time * random.range(30f), 1f);
                        Tmp.v4.add(Tmp.v1);
                        Lines.line(Tmp.v5.x, Tmp.v5.y, Tmp.v4.x, Tmp.v4.y, false);
                        Fill.circle(Tmp.v4.x, Tmp.v4.y, Lines.getStroke() / 2f);
                        Tmp.v5.set(Tmp.v4);
                    }
                }
                Draw.trans(Tmp.m1.idt());
            });
        }
        public void update(){
            float len = start.dst(end);
            for(int is = 0; is <= 1 / interval; is += 1){
                Vec2 point = points.get(is);
                Vec2 target = targets.get(is);
                point.approachDelta(target, speed);
                if(point.within(target, 0.1f)){
                    random.setSeed((long) Time.time + seed + is * 100L);
                    point.set(random.random(is * len * interval, (is + 0.5f) * len * interval), random.range(mag));
                    target.set(random.random(is * len * interval, (is + 0.5f) * len * interval), random.range(mag));
                }
            }
        }
    }
    /*public static void draw(Vec2 start, Vec2 end, Color color, long seed, float interval, float interval2, float speed, float mag){
        float len = start.dst(end);
        random.setSeed(seed);
        Draw.draw(Layer.effect, () -> {
            Lines.stroke(1, color);
            Draw.trans(Tmp.m1.idt().rotate(start.angleTo(end)).translate(start));
            //Lines.line(0, 0, len, 0);
            Lines.beginLine();
            for (float i = 0; i <= 1; i += interval){
                if(i == 0) {
                    Tmp.v2.set(Tmp.v1.set((i + interval / 2) * len, 0));
                }
                if(Tmp.v1.within(Tmp.v2, 0.01f)){
                    random.setSeed((int)Time.time);
                    Tmp.v1.set(random.random(i * len, (i + interval) * len), random.range(mag));
                    Tmp.v1.set(random.random(i * len, (i + interval) * len), random.range(mag));
                }
                random.setSeed(seed);
                Tmp.v1.approachDelta(Tmp.v2, speed);
                //Tmp.bz2.set(Tmp.v2.set(i * len, 0), Tmp.v1, Tmp.v3.set((i + interval) * len, 0));
                for (float j = 0; j <= 1; j += interval2){
                    //Lines.linePoint(Tmp.bz2.valueAt(Tmp.v4, j));
                }
                Lines.linePoint(i * len, 0);
                Lines.linePoint(Tmp.v2.x, Tmp.v2.y);
            }
            Lines.endLine();
            Draw.trans(Tmp.m1.idt());
        });
    }*/
    public static void draw(){
        datas.each(LaserLightningData::draw);
        datas.clear();
    }
}
