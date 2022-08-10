package degradation.world.blocks.heat;

public class HeatConduit/*extends HeatConductor*/{/*
    static final float rotatePad = 6, hpad = rotatePad / 2f / 4f;
    static final float[][] rotateOffsets = {{hpad, hpad}, {-hpad, hpad}, {-hpad, -hpad}, {hpad, -hpad}};

    public final int timerFlow = timers++;
    public TextureRegion[] topRegions;

    public Color botColor = Color.valueOf("565656");

    public TextureRegion[][][] rotateRegions;

    public @Nullable Block bridgeReplacement;

    public HeatConduit(String name){
        super(name);
        update = solid = rotate = true;
        rotateDraw = true;
        size = 1;
        drawer = new DrawHeatOutput();
    }

    public void load(Block block){
        for(int i = 0;i<=4;i++){
            topRegions[i] = Core.atlas.find("degradation-heat-conduit-top-"+i);
        }
        super.load();

        rotateRegions = new TextureRegion[4][2][animationFrames];

        if(renderer != null){
            float pad = rotatePad;
            var frames = renderer.getFluidFrames();

            for(int rot = 0; rot < 4; rot++){
                for(int fluid = 0; fluid < 2; fluid++){
                    for(int frame = 0; frame < animationFrames; frame++){
                        TextureRegion base = frames[fluid][frame];
                        TextureRegion result = new TextureRegion();
                        result.set(base);

                        if(rot == 0){
                            result.setX(result.getX() + pad);
                            result.setHeight(result.height - pad);
                        }else if(rot == 1){
                            result.setWidth(result.width - pad);
                            result.setHeight(result.height - pad);
                        }else if(rot == 2){
                            result.setWidth(result.width - pad);
                            result.setY(result.getY() + pad);
                        }else{
                            result.setX(result.getX() + pad);
                            result.setY(result.getY() + pad);
                        }

                        rotateRegions[rot][fluid][frame] = result;
                    }
                }
            }
        }
        heat = Core.atlas.find(block.name + "-heat");
    }
    public TextureRegion heat, glow, top1, top2;
    public Color heatColor = new Color(1f, 0.22f, 0.22f, 0.8f);
    public float heatPulse = 0.3f, heatPulseScl = 10f;

    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list, Building build){
        int[] bits = Autotiler.getTiling(plan, list);

        if(bits == null) return;

        Draw.scl(bits[1], bits[2]);
        Draw.color(botColor);
        Draw.alpha(0.5f);
        Draw.rect(Core.atlas.find("degradation-heat-conduit-bottom"), plan.drawx(), plan.drawy(), plan.rotation * 90);
        if(build instanceof HeatBlock heater && heater.heat() > 0) {
            Draw.z(Layer.blockAdditive);
            Draw.blend(Blending.additive);
            Draw.color(heatColor, heater.heatFrac() * (heatColor.a * (1f - heatPulse + Mathf.absin(heatPulseScl, heatPulse))));
            if (heat.found()) Draw.rect(heat, build.x, build.y, build.rotdeg());
            Draw.blend();
            Draw.color();
            Draw.color(heatColor, heater.heatFrac() * (heatColor.a * (1f - heatPulse + Mathf.absin(heatPulseScl, heatPulse))));
            if (heat.found()) Draw.rect(heat, build.x, build.y, build.rotdeg());
        }
        Draw.blend();
        Draw.color();
        Draw.rect(topRegions[bits[0]], plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.scl();
    }
    public class HeatConduitBuild extends HeatConductorBuild {

        public Building next(){
            Tile next = tile.nearby(rotation);
            if(next != null && next.build instanceof Conduit.ConduitBuild){
                return next.build;
            }
            return null;
        }
    }
    */
}