package degradation.content;

import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import degradation.world.blocks.defence.turrets.HealTurret;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.LaserBoltBulletType;
import mindustry.gen.Sounds;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.world.Block;
import degradation.world.blocks.defence.ShardWall;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.ShallowLiquid;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.with;

public class DTBlocks {
    public static Block
    //environment
            iceWater,greenIce,
            greenIceWall,
    //walls
            iridiumWall, iridiumWallLarge,
    //storage
            corePedestal,
    //heat
            heatConduit,
    //turrets
            holy;
    public static void load() {
        //environment
        greenIce = new Floor("green-ice"){{
            dragMultiplier = 0.35f;
            speedMultiplier = 0.9f;
            attributes.set(Attribute.water, 0.4f);
            albedo = 0.65f;
        }};
        iceWater = new Floor("ice-water"){{
            speedMultiplier = 0.5f;
            variants = 3;
            status = StatusEffects.wet;
            statusDuration = 90f;
            liquidDrop = Liquids.water;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.9f;
            supportsOverlay = true;
        }};
        greenIceWall = new StaticWall("green-ice-wall"){{
            greenIce.asFloor().wall = this;
            albedo = 0.6f;
        }};

        //defence
        iridiumWall = new ShardWall("iridium-wall"){{
            shardChance = 0.1f;
            size = 1;
            health = 700;
            requirements(Category.defense, with(DTItems.iridium, 8));
        }};
        iridiumWallLarge = new ShardWall("iridium-wall-large"){{
            shardChance = 0.1f;
            size = 2;
            health = 2800;
            requirements(Category.defense, with(DTItems.iridium, 24));
        }};
        //storage
        corePedestal = new CoreBlock("core-pedestal"){
            {
            requirements(Category.effect, BuildVisibility.editorOnly, with(DTItems.iron, 1300));
            alwaysUnlocked = true;

            isFirstTier = true;
            unitType = DTUnitTypes.separate;
            health = 1300;
            itemCapacity = 4000;
            size = 3;

            unitCapModifier = 8;
        }};
        /*heatConduit = new HeatConduit("heat-conduit"){{
           requirements(Category.distribution, with(DTItems.iridium, 24));
        }};*/
        //turret
        holy = new HealTurret("holy"){{
            scaledHealth = 260;
            size = 1;
            targetAir = true;
            targetGround = true;
            rotateSpeed = 5;
            coolant = consumeCoolant(0.2f);
            consumePower(2f);
            reload = 10;
            maxAmmo = 10;
            range = 180;
            shootSound = Sounds.sap;
            inaccuracy = 3;
            shootEffect = Fx.shootHeal;
            shootType = new LaserBoltBulletType(5,10){{
                lifetime = 40;
                healPercent = 4;
                status = StatusEffects.corroded;
                frontColor = Color.white;
                backColor = Pal.heal;
                collidesTeam = true;
            }};
            requirements(Category.turret, with(DTItems.iridium, 1));
        }};
    }
}
