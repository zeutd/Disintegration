package disintegration.entities.abilities;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Shaders;
import mindustry.type.UnitType;

public class DTArmorPlateAbility extends Ability {
    public TextureRegion plateRegion;
    public Color color = Color.valueOf("d1efff");
    public float move = 4;
    public float offset = 10f;

    public float healthMultiplier = 0.2f;
    public float z = Layer.effect;
    protected float warmup;
    @Override
    public void init(UnitType unit){
        super.init(unit);
        if(plateRegion == null){
            plateRegion = Core.atlas.find(unit.name + "-armor", unit.region);
        }
    }

    @Override
    public void update(Unit unit){
        super.update(unit);

        warmup = Mathf.lerpDelta(warmup, unit.isShooting() ? 1f : 0f, 0.1f);
        unit.healthMultiplier += warmup * healthMultiplier;
    }

    @Override
    public void draw(Unit unit){
        if(warmup > 0.001f){
            Draw.draw(z <= 0 ? Draw.z() : z, () -> {
                Tmp.v1.trns(unit.rotation, move * warmup + offset);
                Shaders.armor.region = plateRegion;
                Shaders.armor.progress = warmup;
                Shaders.armor.time = -Time.time / 20f;

                Draw.alpha(warmup);
                Draw.rect(Shaders.armor.region, unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, unit.rotation - 90f);
                Draw.color(color, warmup);
                Draw.shader(Shaders.armor);
                Draw.rect(Shaders.armor.region, unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, unit.rotation - 90f);
                Draw.shader();

                Draw.reset();
            });
        }
    }
}
