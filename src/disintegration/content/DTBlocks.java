package disintegration.content;

import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import disintegration.DTVars;
import disintegration.graphics.Pal2;
import disintegration.world.blocks.debug.DebugBlock;
import disintegration.world.blocks.debug.SectorResetBlock;
import disintegration.world.blocks.defence.ShardWall;
import disintegration.world.blocks.defence.turrets.ElectricTowerTurret;
import disintegration.world.blocks.effect.FloorBuilder;
import disintegration.world.blocks.environment.ConnectFloor;
import disintegration.world.blocks.laser.LaserDevice;
import disintegration.world.blocks.laser.LaserReactor;
import disintegration.world.blocks.laser.LaserReflector;
import disintegration.world.blocks.power.SpreadGenerator;
import disintegration.world.blocks.production.Quarry;
import disintegration.world.blocks.temperature.*;
import disintegration.world.draw.DrawAllRotate;
import disintegration.world.draw.DrawFusion;
import disintegration.world.draw.DrawLaser;
import disintegration.world.draw.DrawTemperature;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.effect.WrapEffect;
import mindustry.entities.part.*;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.gen.Sounds;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.type.Weapon;
import mindustry.type.unit.MissileUnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.environment.SteamVent;
import mindustry.world.blocks.power.ThermalGenerator;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.consumers.ConsumeItemExplode;
import mindustry.world.consumers.ConsumeItemFlammable;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static arc.graphics.g2d.Draw.color;
import static mindustry.type.ItemStack.with;

public class DTBlocks {
    //TODO check bundles
    public static Block
    //environment
            iceWater, greenIce, greenFloor, spaceStationFloor, lightSpace,
            ethyleneVent,
            greenIceWall,
    //walls
            iridiumWall, iridiumWallLarge,
    //storage
            corePedestal,
            spaceStationCore,
    //temperature
            temperatureConduit,
            temperatureSource,
            temperatureVoid,
            burningHeater,
    //laser
            laserDevice,
            laserReflector,
            laserRouter,
            laserSource,
    //factory
            boiler,
    //power
            neoplasmGenerator,
            excitationReactor,
            stirlingGenerator,
    //turrets
            fracture,
            blade,
            permeation,
            holy,
            sparkover,
    //drills
            quarry,
            pressureDrill,
    //effect
    //debug
            debugBlock,
            sectorResetBlock
            ;
    public static void load() {
        //environment
        greenIce = new Floor("green-ice"){{
            dragMultiplier = 0.35f;
            speedMultiplier = 0.9f;
            attributes.set(Attribute.water, 0.4f);
            albedo = 0.65f;
        }};
        greenFloor = new Floor("green-floor"){{
            variants = 0;
            canShadow = false;
            placeableOn = false;
            solid = true;
        }};
        lightSpace = new Floor("light-space"){{
            cacheLayer = CacheLayer.space;
            placeableOn = false;
            solid = true;
            variants = 0;
            canShadow = false;
            albedo = 1f;
            lightRadius = 8f;
            lightColor = Blocks.space.mapColor;
        }};

        spaceStationFloor = new ConnectFloor("space-station-floor"){{
            variants = 0;
            blendGroup = lightSpace;
        }};

        ((Floor)lightSpace).blendGroup = spaceStationFloor;

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

        ethyleneVent = new SteamVent("ethylene-vent"){{
            parent = blendGroup = Blocks.ice;
            effect = DTFx.ethyleneVentSteam;
            attributes.set(Attribute.steam, 1f);
        }};

        //defence
        iridiumWall = new ShardWall("iridium-wall"){{
            shardChance = 0.1f;
            shard = DTBullets.shard;
            size = 1;
            health = 700;
            requirements(Category.defense, with(DTItems.iridium, 8));
        }};
        iridiumWallLarge = new ShardWall("iridium-wall-large"){{
            shardChance = 0.1f;
            shard = DTBullets.shard;
            size = 2;
            health = 2800;
            requirements(Category.defense, with(DTItems.iridium, 24));
        }};
        //storage
        corePedestal = new CoreBlock("core-pedestal"){{
            requirements(Category.effect, BuildVisibility.editorOnly, with(DTItems.iron, 1300));
            alwaysUnlocked = true;

            isFirstTier = true;
            unitType = DTUnitTypes.separate;
            health = 1300;
            itemCapacity = 4000;
            size = 3;

            unitCapModifier = 8;
        }};

        spaceStationCore = new CoreBlock("space-station-core"){{
            requirements(Category.effect, BuildVisibility.editorOnly, with(DTItems.iron, 1300));
            alwaysUnlocked = true;

            isFirstTier = true;
            unitType = DTUnitTypes.spaceStationDrone;
            health = 1300;
            itemCapacity = 4000;
            size = 3;

            unitCapModifier = 16;
        }};
        
        //temperature
        temperatureConduit = new TemperatureConduit("temperature-conduit"){{
            health = 50;
            underBullets = true;
            conductionSpeed = 0.4f;
            temperaturePercent = DTVars.temperaturePercent;
            requirements(Category.distribution, with(DTItems.iron, 2));
        }};

        temperatureSource = new TemperatureSource("temperature-source"){{
            health = 200;
            requirements(Category.distribution, BuildVisibility.sandboxOnly, with());
            alwaysUnlocked = true;
        }};

        temperatureVoid = new TemperatureVoid("temperature-void"){{
            health = 200;
            requirements(Category.distribution, BuildVisibility.sandboxOnly, with());
            alwaysUnlocked = true;
        }};

        burningHeater = new ConsumeTemperatureProducer("burning-heater"){{
            size = 2;
            health = 150;
            temperatureOutput = 3f;
            itemCapacity = 10;

            itemDuration = 120f;

            effectChance = 0.01f;

            productEffect = Fx.generatespark;
            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.06f;

            consume(new ConsumeItemFlammable());
            consume(new ConsumeItemExplode());
            requirements(Category.crafting, with(DTItems.iron, 30, Items.silicon, 20, Items.graphite, 50));
        }};

        laserSource = new LaserDevice("laser-source"){{
            range = 10;
            health = 400;
            laserOutput = 1000;
            drawer = new DrawMulti(new DrawAllRotate(1), new DrawLaser(false));
            requirements(Category.crafting, BuildVisibility.sandboxOnly, with());
        }};

        laserDevice = new LaserDevice("laser-device"){{
           range = 7;
           health = 200;
           laserOutput = 5;
           drawer = new DrawMulti(new DrawAllRotate(1), new DrawLaser(false));
           consumePower(3);
           requirements(Category.crafting, with(DTItems.iron, 30, Items.silicon, 20, Items.graphite, 50));
        }};

        laserReflector = new LaserReflector("laser-reflector"){{
            range = 7;
            split = false;
            rotateDraw = false;
            health = 200;
            drawer = new DrawMulti(new DrawRegion(""), new DrawLaser(true));
            requirements(Category.crafting, with(DTItems.iron, 30, Items.silicon, 20, Items.graphite, 50));
        }};

        laserRouter = new LaserReflector("laser-router"){{
            range = 7;
            split = true;
            rotateDraw = false;
            health = 200;
            drawer = new DrawMulti(new DrawRegion(""), new DrawLaser(true));
            requirements(Category.crafting, with(DTItems.iron, 30, Items.silicon, 20, Items.graphite, 50));
        }};
        //factory
        boiler = new TemperatureCrafter("boiler"){{
            requirements(Category.crafting, with(DTItems.iron, 65, Items.silicon, 40, Items.graphite, 60));
            outputLiquid = new LiquidStack(DTLiquids.steam, 12f / 60f);
            size = 2;
            rotateDraw = false;
            hasPower = true;
            hasItems = true;
            hasLiquids = true;
            outputsLiquid = true;
            envEnabled = Env.any;
            ambientSound = DTSounds.boiler;
            ambientSoundVolume = 0.01f;
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(Liquids.water),
                    new DrawBubbles(Color.valueOf("7693e3")){{
                        sides = 10;
                        recurrence = 3f;
                        spread = 6;
                        radius = 1.5f;
                        amount = 20;
                    }},
                    new DrawLiquidTile(DTLiquids.steam){{drawLiquidLight = true;}},
                    new DrawDefault(),
                    new DrawTemperature(Pal2.burn, Pal2.heat, DTVars.temperaturePercent));
            liquidCapacity = 24f;
            regionRotated1 = 1;
            craftTime = 120;
            temperatureConsumes = 3f;

            consumeLiquid(Liquids.water, 12f / 60f);
        }};
        neoplasmGenerator = new SpreadGenerator("neoplasm-generator"){{
            requirements(Category.power, with(Items.tungsten, 500, Items.carbide, 100, Items.oxide, 150, Items.silicon, 400, Items.phaseFabric, 200));

            size = 4;
            liquidCapacity = 30f;
            squareSprite = false;

            spreadLiquids = new Liquid[]{Liquids.neoplasm};
            spreadMinWarmup = 0.5f;

            consumeLiquid(Liquids.neoplasm, 5f / 60f);

            powerProduction = 900 / 60f;

            ambientSound = Sounds.bioLoop;
            ambientSoundVolume = 0.2f;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(Liquids.neoplasm, 3f),
                    new DrawCircles(){{
                        color = Color.valueOf("feb380").a(0.8f);
                        strokeMax = 3.25f;
                        radius = 55f / 4f;
                        amount = 5;
                        timeScl = 200f;
                    }},

                    new DrawBubbles(Pal.neoplasm2),

                    new DrawCells(){{
                        color = Color.valueOf("c33e2b");
                        particleColorFrom = Color.valueOf("e8803f");
                        particleColorTo = Color.valueOf("8c1225");
                        particles = 50;
                        range = 7f;
                    }},
                    new DrawDefault()
            );
        }};
        //power
        excitationReactor = new LaserReactor("excitation-reactor"){{
            size = 5;
            scaledHealth = 100;
            maxLaser = 100f;
            warmupSpeed = 0.003f;
            powerProduction = 120f;
            coolantPower = 0.5f;

            heating = 0.01f;

            consumeLiquid(DTLiquids.liquidCrystal, heating / coolantPower).update(false);
            liquidCapacity = 3f;
            explosionMinWarmup = 0.5f;

            ambientSound = Sounds.tractorbeam;
            ambientSoundVolume = 0.13f;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawPlasma(), new DrawSoftParticles(){{
                alpha = 0.2f;
                particleRad = 12f;
                particleSize = 9f;
                particleLife = 100f;
                particles = 10;
            }}, new DrawDefault(), new DrawFusion());
            requirements(Category.power, with(DTItems.iron, 300, Items.silicon, 200, Items.graphite, 300, Items.surgeAlloy, 100));
        }};

        stirlingGenerator = new ThermalGenerator("stirling-generator"){{
            requirements(Category.power, with(DTItems.iron, 60));
            attribute = Attribute.steam;
            displayEfficiencyScale = 1f / 9f;
            minEfficiency = 9f - 0.0001f;
            powerProduction = 5f / 9f;
            displayEfficiency = false;
            generateEffect = DTFx.ethylenegenerate;
            effectChance = 0.04f;
            size = 3;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.06f;

            drawer = new DrawMulti(new DrawDefault(), new DrawBlurSpin("-rotator", 0.6f * 9f){{
                blurThresh = 0.01f;
            }});

            hasLiquids = true;
            outputLiquid = new LiquidStack(Liquids.water, 5f / 60f / 9f);
            liquidCapacity = 20f;
            fogRadius = 3;
            researchCost = with(DTItems.iron, 15);
        }};
        //turret

        //TODO balancing
        fracture = new ItemTurret("fracture"){{
            requirements(Category.turret, with(Items.surgeAlloy, 200, Items.oxide, 400, Items.silicon, 400, Items.beryllium, 500));

            ammo(Items.oxide ,new BasicBulletType(){{
                lightOpacity = 0.7f;
                damage = 60;
                hitColor = lightColor = Pal.berylShot;
                lightRadius = 70f;
                clipSize = 250f;
                shootEffect = Fx.hitEmpSpark;
                smokeEffect = Fx.shootBigSmoke;
                lifetime = 60f;
                sprite = "circle-bullet";
                backColor = Pal.berylShot;
                frontColor = Color.white;
                width = height = 12f;
                shrinkY = 0f;
                speed = 5f;
                trailLength = 20;
                trailWidth = 6f;
                trailColor = Pal.berylShot;
                trailInterval = 3f;
                splashDamage = 70f;
                splashDamageRadius = 20f;
                hitShake = 4f;
                trailRotation = true;
                status = StatusEffects.electrified;
                hitSound = Sounds.explosionbig;

                trailEffect = new Effect(16f, e -> {
                    color(Pal.berylShot);
                    for(int s : Mathf.signs){
                        Drawf.tri(e.x, e.y, 4f, 30f * e.fslope(), e.rotation + 90f*s);
                    }
                });
                hitEffect = despawnEffect = DTFx.hitFracture;
            }});

            reload = 17f;
            shootY = 15f;
            rotateSpeed = 5f;
            shootCone = 30f;
            consumeAmmoOnce = true;
            shootSound = Sounds.laser;

            Color haloColor = Pal.berylShot;
            float haloY = -10f, haloRotSpeed = 1.5f;
            var haloProgress = DrawPart.PartProgress.warmup;

            drawer = new DrawTurret("reinforced-"){{
                parts.addAll(new RegionPart("-mouth"){{
                                 moveY = -1.25f;
                                 under = true;
                                 progress = PartProgress.recoil;
                                 heatProgress = PartProgress.recoil.min(PartProgress.warmup);
                                 moves.add(new PartMove(PartProgress.recoil, 0f, -0.5f, 0f));
                             }},
                        new RegionPart("-mid"){{
                            under = true;
                            progress = PartProgress.recoil;
                            heatProgress = PartProgress.recoil.min(PartProgress.warmup);
                            heatColor = Color.green.cpy().a(0.5f);
                        }},
                        new RegionPart("-blade"){{
                            mirror = true;
                            under = true;
                            moveX = 1.75f;
                            moveY = -1f;
                            heatProgress = PartProgress.recoil.min(PartProgress.warmup);
                            heatColor = Color.green.cpy().a(0.5f);
                            moves.add(new PartMove(PartProgress.recoil, 0.4f, -3f, 0f));
                        }},
                        new ShapePart(){{
                            progress = haloProgress;
                            color = haloColor;
                            circle = true;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 2f;
                            radius = 10f;
                            layer = Layer.effect;
                            y = haloY;
                        }},
                        new ShapePart(){{
                            progress = haloProgress;
                            color = haloColor;
                            sides = 3;
                            rotation = 90f;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 2f;
                            radius = 3f;
                            layer = Layer.effect;
                            y = haloY;
                        }},
                        new HaloPart(){{
                            progress = haloProgress;
                            color = haloColor;
                            sides = 3;
                            shapes = 3;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 2f;
                            radius = 3f;
                            haloRadius = 10f + radius/2f;
                            haloRotateSpeed = haloRotSpeed;
                            layer = Layer.effect;
                            y = haloY;
                        }},

                        new HaloPart(){{
                            progress = haloProgress;
                            color = haloColor;
                            tri = true;
                            shapes = 4;
                            triLength = 0f;
                            triLengthTo = 10f;
                            radius = 6f;
                            haloRadius = 9f;
                            haloRotation = 180f;
                            layer = Layer.effect;
                            y = haloY;
                        }},
                        new HaloPart(){{
                            progress = haloProgress;
                            color = haloColor;
                            tri = true;
                            shapes = 4;
                            triLength = 0f;
                            triLengthTo = 3f;
                            radius = 6f;
                            haloRadius = 9f;
                            shapeRotation = 180f;
                            haloRotation = 180f;
                            layer = Layer.effect;
                            y = haloY;
                        }});
            }};

            targetGround = true;
            inaccuracy = 8f;

            shootWarmupSpeed = 0.07f;
            warmupMaintainTime = 15f;
            minWarmup = 0.96f;

            outlineColor = Pal.darkOutline;

            scaledHealth = 280;
            range = 310f;
            size = 5;

            coolant = consume(new ConsumeLiquid(Liquids.water, 20f / 60f));
            coolantMultiplier = 2.5f;
            consumePower(60);
            ammoPerShot = 1;

            limitRange(-5f);
        }};

        blade = new ItemTurret("blade") {{
            requirements(Category.turret, with(Items.graphite, 70, Items.silicon, 80, Items.beryllium, 90));

            reload = 240f;
            shake = 4f;
            range = 360f;
            recoil = 2f;

            heatColor = Pal.berylShot.cpy().a(0.9f);

            shootCone = 0;
            size = 3;
            envEnabled |= Env.space;

            scaledHealth = 300;
            shootSound = Sounds.missileSmall;

            shoot = new ShootBarrel(){{
                shots = 8;
                shotDelay = 5;
                barrels = new float[]{
                        5.5f, -5f, 0f,
                        -5.5f, -5f, 0f
                };
                firstShotDelay = 100f;
            }};

            coolantMultiplier = 6f;
            coolant = consume(new ConsumeLiquid(Liquids.water, 15f / 60f));
            ammo(Items.silicon, new BulletType(){{
                shootEffect = new MultiEffect(Fx.shootBigColor, new WaveEffect(){{
                    colorFrom = colorTo = Pal.berylShot;
                    sizeTo = 15f;
                    lifetime = 12f;
                    strokeFrom = 3f;
                }});

                smokeEffect = Fx.shootBigSmoke2;
                shake = 2f;
                speed = 0f;
                keepVelocity = false;
                inaccuracy = 2f;

                spawnUnit = new MissileUnitType("blade-missile"){{
                    trailColor = engineColor = Pal.berylShot;
                    engineSize = 1.75f;
                    engineOffset = 4f;
                    engineLayer = Layer.effect;
                    speed = 3.7f;
                    maxRange = 6f;
                    lifetime = 60f * 1.5f;
                    outlineColor = Pal.darkOutline;
                    health = 55;
                    lowAltitude = true;

                    parts.add(new FlarePart(){{
                        color1 = Pal.berylShot;
                        progress = PartProgress.life.slope().curve(Interp.pow2In);
                        radius = 0f;
                        radiusTo = 35f;
                        stroke = 3f;
                        rotation = 45f;
                        y = -5f;
                        followRotation = true;
                    }});

                    weapons.add(new Weapon(){{
                        shootCone = 360f;
                        mirror = false;
                        reload = 1f;
                        shootOnDeath = true;
                        bullet = new ExplosionBulletType(140f, 25f){{
                            shootEffect = new MultiEffect(Fx.massiveExplosion, new WrapEffect(Fx.dynamicSpikes, Pal.berylShot, 24f));
                        }};
                    }});
                }};
            }});
            drawer = new DrawTurret("reinforced-") {{
                parts.addAll(new RegionPart("-mid") {{
                    progress = PartProgress.recoil;
                    mirror = false;
                    under = true;
                    //moveY = -0.5f;
                    heatColor = Color.sky.cpy().a(0.8f);
                }}, new RegionPart("-side") {{
                    progress = PartProgress.warmup;
                    mirror = true;
                    moveX = 2f;
                    //moveY = -0.5f;
                    under = true;
                    heatColor = Color.sky.cpy().a(0.8f);
                }});
            }};
        }};

        permeation = new ItemTurret("permeation"){{
            requirements(Category.turret, with(Items.graphite, 70, Items.silicon, 80, Items.beryllium, 90));

            reload = 50f;
            shake = 4f;
            range = 60f;
            recoil = 2f;

            heatColor = Color.sky.cpy().a(0.9f);

            shootCone = 10;
            size = 2;
            envEnabled |= Env.space;

            scaledHealth = 300;
            shootSound = Sounds.shotgun;

            coolantMultiplier = 6f;
            coolant = consume(new ConsumeLiquid(Liquids.water, 15f / 60f));
            ammo(Items.graphite, new ShrapnelBulletType(){{
                length = 50f;
                damage = 85f;
                ammoMultiplier = 5f;
                toColor = Color.sky;
                shootEffect = smokeEffect = Fx.shootBigSmoke;
            }});
            drawer = new DrawTurret("reinforced-"){{
                parts.addAll(new RegionPart("-mid"){{
                    progress = PartProgress.recoil;
                    heatProgress = PartProgress.recoil.add(-0.1f).clamp();
                    mirror = false;
                    under = true;
                    moveY = -0.7f;
                }}, new RegionPart("-side"){{
                    heatProgress = PartProgress.recoil.add(-0.1f).clamp();
                    progress = PartProgress.warmup;
                    mirror = true;
                    moveX = -0.7f;
                    moves.add(new PartMove(PartProgress.warmup, 2f, 0f, 0f));
                    under = true;
                }});
            }};
        }};
        holy = new PowerTurret("holy"){{
            scaledHealth = 260;
            size = 1;
            targetAir = true;
            targetGround = true;
            targetHealing = true;
            rotateSpeed = 5;
            coolant = consumeCoolant(0.2f);
            consumePower(2f);
            drawer = new DrawTurret("framed-");
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
        sparkover = new ElectricTowerTurret("sparkover"){{
            hasPower = true;
            size = 2;
            range = 170f;
            damage = 120f;
            reload = 200f;
            maxTargets = 30;

            targetAir = true;
            targetGround = true;

            consumePower(10f);

            requirements(Category.turret, with(DTItems.iron, 200, Items.silicon, 110, DTItems.iridium, 90));
        }};
        quarry = new Quarry("quarry"){{
           size = 3;
           regionRotated1 = 1;
           itemCapacity = 100;
           acceptsItems = true;
           consumePower(20);
           consumeLiquid(Liquids.hydrogen, 5f / 60f);
           consumeLiquid(Liquids.nitrogen, 6f / 60f).boost();
           requirements(Category.production, with(DTItems.iridium, 1));
        }};
        pressureDrill = new Drill("pressure-drill"){{
            requirements(Category.production, with(DTItems.iron, 12), true);
            tier = 2;
            drillTime = 600;
            size = 2;

            envEnabled ^= Env.space;

            consumeLiquid(Liquids.water, 0.05f).boost();
        }};
        //effect
        for (int i = 1; i <= 9; i+=2) {
            int finalI = i;
            new FloorBuilder("space-station-builder-" + finalI){{
                requirements(Category.effect, with(DTItems.spaceStationPanel, 5 * finalI * finalI));
                size = 1;
                range = finalI;
                rotate = true;
                buildOffset = range;
                rotateDraw = false;
                floor = (Floor)spaceStationFloor;
            }};
        }

        for (int i = 1; i <= 9; i+=2) {
            int finalI = i;
            new FloorBuilder("space-station-breaker-" + finalI){{
                requirements(Category.effect, with(DTItems.spaceStationPanel, 5 * finalI * finalI));
                size = 1;
                range = finalI;
                floor = (Floor)lightSpace;
            }};
        }
        
        //debug
        debugBlock = new DebugBlock("debug-block"){{
            buildVisibility = DTVars.debugMode ? BuildVisibility.shown : BuildVisibility.hidden;
            requirements(Category.effect, with(), true);
        }};
        sectorResetBlock = new SectorResetBlock("sector-reset-block"){{
            buildVisibility = DTVars.debugMode ? BuildVisibility.shown : BuildVisibility.hidden;
            requirements(Category.effect, with(), true);
        }};

    }
}
