package disintegration.content;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import disintegration.DTVars;
import disintegration.entities.bullet.BlockBulletType;
import disintegration.entities.bullet.ConnectBulletType;
import disintegration.entities.bullet.RandomSpreadBulletType;
import disintegration.entities.unit.weapons.PortableBlockWeapon;
import disintegration.graphics.Pal2;
import disintegration.world.blocks.debug.DPSBlock;
import disintegration.world.blocks.debug.DebugBlock;
import disintegration.world.blocks.defence.RepairDroneStation;
import disintegration.world.blocks.defence.ShardWall;
import disintegration.world.blocks.defence.turrets.ElectricTowerTurret;
import disintegration.world.blocks.effect.FloorBuilder;
import disintegration.world.blocks.environment.ConnectFloor;
import disintegration.world.blocks.laser.LaserDevice;
import disintegration.world.blocks.laser.LaserReactor;
import disintegration.world.blocks.laser.LaserReflector;
import disintegration.world.blocks.power.SpreadGenerator;
import disintegration.world.blocks.production.PortableDrill;
import disintegration.world.blocks.production.Quarry;
import disintegration.world.blocks.temperature.*;
import disintegration.world.draw.DrawAllRotate;
import disintegration.world.draw.DrawFusion;
import disintegration.world.draw.DrawLaser;
import disintegration.world.draw.DrawTemperature;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.effect.WrapEffect;
import mindustry.entities.part.*;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.gen.LegsUnit;
import mindustry.gen.Sounds;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.type.unit.MissileUnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.ShockMine;
import mindustry.world.blocks.defense.turrets.ContinuousLiquidTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.environment.SteamVent;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.ThermalGenerator;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.consumers.ConsumeItemExplode;
import mindustry.world.consumers.ConsumeItemFlammable;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.type.ItemStack.with;

public class DTBlocks {
    //TODO check bundles
    public static Block
    //environment
            iceWater, greenIce, greenFloor, spaceStationFloor,
            ethyleneVent,
            greenIceWall,
    //defence
            repairDroneStation,
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
            electrolyser,
    //power
            neoplasmGenerator,
            excitationReactor,
            stirlingGenerator,
            solarPanel,
    //turrets
            fracture,
            dissolve,
            blade,
            encourage,
            aegis,
            permeation,
            holy,
            sparkover,
            axe,
            ambush,
            voltage,
    //drills
            quarry,
            pressureDrill,
            rockExtractor,
            portableDrill,
    //effect
            blastMine,
    //debug
            debugBlock,
            dpsBlock
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

        spaceStationFloor = new ConnectFloor("space-station-floor"){{
            variants = 0;
            blendGroup = Blocks.empty;
        }};
        Blocks.empty.asFloor().blendGroup = spaceStationFloor;

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
        repairDroneStation = new RepairDroneStation("repair-drone-station"){{
            size = 3;
            requirements(Category.defense, ItemStack.with());
        }};

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
                    new DrawLiquidTile(DTLiquids.steam){},
                    new DrawDefault(),
                    new DrawTemperature(Pal2.burn, Pal2.heat, DTVars.temperaturePercent));
            liquidCapacity = 24f;
            regionRotated1 = 1;
            craftTime = 120;
            temperatureConsumes = 3f;

            consumeLiquid(Liquids.water, 12f / 60f);
        }};

        electrolyser = new GenericCrafter("electrolyser"){{
            requirements(Category.crafting, with(Items.silicon, 50, Items.graphite, 40, Items.beryllium, 130, Items.tungsten, 80));
            size = 3;

            researchCostMultiplier = 1.2f;
            craftTime = 10f;
            rotate = true;
            invertFlip = true;

            hasLiquids = true;

            liquidCapacity = 50f;

            consumeLiquid(Liquids.water, 12f / 60f);
            consumePower(1f);

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(Liquids.hydrogen),
                    new DrawAllRotate(),
                    new DrawGlowRegion(){{
                        alpha = 0.7f;
                        color = Color.valueOf("c4bdf3");
                        glowIntensity = 0.3f;
                        glowScale = 6f;
                    }}
            );

            ambientSound = Sounds.electricHum;
            ambientSoundVolume = 0.08f;
            squareSprite = false;

            outputLiquids = LiquidStack.with(DTLiquids.oxygen, 4f / 60, Liquids.hydrogen, 8f / 60);
            liquidOutputDirections = new int[]{4, 2};
        }};
        //power
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
            generateEffect = DTFx.ethyleneGenerate;
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
        solarPanel = new ConsumeGenerator("solar-panel"){{
            requirements(Category.power, with(DTItems.spaceStationPanel, 60));
            powerProduction = 5f;
            rotate = true;
            rotateDraw = false;
            envEnabled = envRequired = Env.space;
            drawer = new DrawMulti(new DrawDefault(), new DrawSideRegion());
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
            shootY = 10f;
            rotateSpeed = 2f;
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
            squareSprite = false;

            scaledHealth = 280;
            range = 310f;
            size = 5;

            coolant = consume(new ConsumeLiquid(Liquids.water, 20f / 60f));
            coolantMultiplier = 2.5f;
            consumePower(60);
            ammoPerShot = 1;

            limitRange(-5f);
        }};
        dissolve = new ContinuousLiquidTurret("dissolve"){{
            requirements(Category.turret, with(Items.graphite, 70, Items.silicon, 80, Items.beryllium, 90));

            reload = 300f;
            shake = 5f;
            range = 200f;
            recoil = 2f;
            squareSprite = false;
            rotateSpeed = 2;
            outlineColor = Pal.darkOutline;

            shootCone = 1;
            size = 5;
            shootY = 15;
            envEnabled |= Env.space;

            scaledHealth = 300;
            shootSound = Sounds.laser;
            float r = range;
            shootWarmupSpeed = 0.05f;
            minWarmup = 0.9f;
            ammo(Liquids.hydrogen, new ContinuousLaserBulletType(100){{
                length = r;
                width = 10;
                hitEffect = Fx.hitMeltdown;
                hitColor = Pal.meltdownHit;
                status = StatusEffects.melting;
                drawSize = 420f;

                incendChance = 0f;
                incendSpread = 0f;
                incendAmount = 0;
                ammoMultiplier = 1f;
            }});
            drawer = new DrawTurret("reinforced-"){{
                parts.addAll(
                        new RegionPart("-side"){{
                            heatProgress = PartProgress.warmup;
                            progress = PartProgress.warmup;
                            mirror = true;
                            moveX = 2f;
                            moveY = -2f;
                        }},
                        new RegionPart("-back"){{
                            heatProgress = PartProgress.warmup;
                            progress = PartProgress.warmup;
                            mirror = true;
                            moveX = 1f;
                            moveY = 1f;
                        }});
            }};
        }};

         blade = new ItemTurret("blade") {{
            requirements(Category.turret, with(Items.graphite, 70, Items.silicon, 80, Items.beryllium, 90));

            reload = 300f;
            shake = 4f;
            range = 360f;
            recoil = 2f;
            rotateSpeed = 3f;

            heatColor = Pal.berylShot.cpy().a(0.9f);

            shootCone = 5;
            size = 3;
            envEnabled |= Env.space;

            scaledHealth = 300;
            shootSound = Sounds.missileSmall;

            shoot = new ShootBarrel(){{
                shots = 4;
                shotDelay = 5;
                barrels = new float[]{
                        5.5f, -5f, 0f,
                        -5.5f, -5f, 0f
                };
                firstShotDelay = 100f;
            }};

            squareSprite = false;
            coolantMultiplier = 6f;
            coolant = consume(new ConsumeLiquid(Liquids.water, 15f / 60f));
            ammo(Items.silicon, new BulletType(){{
                shootEffect = new MultiEffect(Fx.shootBigColor, new WaveEffect(){{
                    colorFrom = colorTo = Pal.berylShot;
                    sizeTo = 15f;
                    lifetime = 12f;
                    strokeFrom = 3f;
                }});

                smokeEffect = new MultiEffect(Fx.shootBigSmoke2, DTFx.shootSmokeMissileBeryl);
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
                    heatColor = Pal.berylShot.cpy().a(0.8f);
                }}, new RegionPart("-side") {{
                    progress = PartProgress.warmup;
                    mirror = true;
                    moveX = 2f;
                    under = true;
                    heatColor = Pal.berylShot.cpy().a(0.8f);
                }});
            }};
        }};

        encourage = new ItemTurret("encourage"){{
            requirements(Category.turret, with(Items.graphite, 70, Items.silicon, 80, Items.beryllium, 90));

            reload = 300f;
            shake = 5f;
            range = 300f;
            recoil = 3f;
            squareSprite = false;

            shootCone = 1;
            size = 3;
            envEnabled |= Env.space;

            scaledHealth = 300;
            shootSound = Sounds.lasershoot;

            coolantMultiplier = 6f;
            coolant = consume(new ConsumeLiquid(Liquids.water, 15f / 60f));
            ammo(Items.tungsten, new BasicBulletType(7, 150){{
                float rad = 20;
                scaleLife = true;
                splashDamage = 250;
                width = height = 15f;
                sprite = "circle-bullet";
                backColor = Pal.redLight;
                frontColor = Color.white;
                shrinkY = 0;
                keepVelocity = true;
                trailColor = Pal.redLight;
                trailLength = 15;
                trailWidth = 5;
                hitSound = Sounds.release;

                reflectable = false;
                splashDamageRadius = rad;
                ammoMultiplier = 1f;
                shootEffect = smokeEffect = new MultiEffect(Fx.shootBigSmoke, DTFx.shootEncourage);
                hitEffect = new Effect(50f, 100f, e -> {
                    e.scaled(7f, b -> {
                        color(Pal.redLight, b.fout());
                        Fill.circle(e.x, e.y, rad);
                    });

                    color(Pal.redLight);
                    stroke(e.fout() * 3f);
                    Lines.circle(e.x, e.y, rad);
                });
                fragBullet = new ShrapnelBulletType(){{
                    damage = 50;
                    length = 15;
                    width = 6;
                    lifetime = 100;
                    toColor = Pal.redLight;
                }};
                ammoPerShot = 6;

                fragBullets = 3;
                fragSpread = 120;
                fragRandomSpread = 0;

                limitRange();
            }});
            drawer = new DrawTurret("reinforced-"){{
                parts.addAll(new RegionPart("-blade"){{
                    heatProgress = PartProgress.recoil.add(-0.1f).clamp();
                    progress = PartProgress.warmup;
                    mirror = true;
                    moveX = 1f;
                    moves.add(new PartMove(PartProgress.recoil, 0f, 0f, -10f));
                    under = true;
                    children.addAll(
                            new RegionPart("-front"){{
                                progress = PartProgress.warmup;
                                mirror = true;
                                under = true;
                                moveX = 2;
                                moveY = -2;
                                moves.add(new PartMove(PartProgress.recoil, 0f, 0f, 0f));
                            }},
                            new RegionPart("-back"){{
                                progress = PartProgress.warmup;
                                mirror = true;
                                under = true;
                                moveX = moveY = -0.5f;
                                moves.add(new PartMove(PartProgress.recoil, 0f, 0f, 0f));
                            }}
                    );
                }});
            }};
        }};

        aegis = new PowerTurret("aegis"){{
            requirements(Category.turret, with(Items.graphite, 70, Items.silicon, 80, Items.beryllium, 90));

            reload = 60f;
            shake = 4f;
            range = 150f;
            recoil = 1f;
            squareSprite = false;

            shootCone = 3;
            size = 2;
            envEnabled |= Env.space;
            targetAir = targetGround = targetHealing = true;

            scaledHealth = 300;
            shootSound = Sounds.lasershoot;

            consumePower(1f);
            shootType = new LaserBoltBulletType(5f, 10f){{
                lifetime = 30f;
                healPercent = 5f;
                collidesTeam = true;
                backColor = Pal.heal;
                frontColor = Color.white;
                shootEffect = Fx.none;
            }};

            shoot = new ShootBarrel(){{
                shots = 4;
                barrels = new float[]{
                        3f, -2f, 0f,
                        -3f, -2f, 0f
                };
                shotDelay = 5f;
            }};
            minWarmup = 0.7f;
            drawer = new DrawTurret("reinforced-"){{
                parts.add(new RegionPart("-side"){{
                    progress = PartProgress.warmup;
                    mirror = true;
                    moveX = 0.8f;
                    moves.add(new PartMove(PartProgress.recoil, 0f, -0.5f, 0f));
                    under = true;
                }});
            }};
            limitRange(2);
        }};

        permeation = new ItemTurret("permeation"){{
            requirements(Category.turret, with(Items.graphite, 70, Items.silicon, 80, Items.beryllium, 90));

            reload = 50f;
            shake = 4f;
            range = 60f;
            recoil = 2f;
            squareSprite = false;

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
            shootSound = Sounds.lasershoot;
            inaccuracy = 3;
            shootEffect = Fx.shootHeal;
            shootType = new LaserBoltBulletType(5,10){{
                lifetime = 40;
                healPercent = 2;
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
        axe = new ItemTurret("axe") {{
            requirements(Category.turret, with(Items.graphite, 70, Items.silicon, 80, DTItems.iron, 90));

            reload = 300f;
            shake = 4f;
            range = 540f;
            recoil = 2f;
            rotateSpeed = 3f;

            shootCone = 6;
            size = 2;
            envEnabled |= Env.space;

            scaledHealth = 300;
            shootSound = Sounds.missileSmall;

            shoot = new ShootBarrel(){{
                shots = 4;
                shotDelay = 10;
                barrels = new float[]{
                        3f, -6f, 0f,
                        -3f, -6f, 0f
                };
            }};

            coolant = consumeCoolant(0.2f);
            ammo(Items.blastCompound, new BulletType(){{
                shootEffect = Fx.shootBig;

                smokeEffect = Fx.shootBigSmoke2;
                shake = 2f;
                speed = 0f;
                keepVelocity = false;
                inaccuracy = 2f;

                spawnUnit = new MissileUnitType("axe-missile"){{
                    trailColor = engineColor = Pal.missileYellow;
                    engineSize = 1.75f;
                    engineOffset = 3.5f;
                    engineLayer = Layer.effect;
                    speed = 4.8f;
                    maxRange = 6f;
                    lifetime = 115;
                    outlineColor = Pal.darkOutline;
                    health = 5;
                    lowAltitude = true;

                    weapons.add(new Weapon(){{
                        shootCone = 360f;
                        mirror = false;
                        reload = 0f;
                        shootOnDeath = true;
                        bullet = new ExplosionBulletType(70f, 25f){{
                            shootEffect = new MultiEffect(Fx.massiveExplosion, new WrapEffect(Fx.dynamicSpikes, Pal.missileYellow, 24f));
                        }};
                    }});
                }};
                ammoPerShot = 5;
            }});
            drawer = new DrawTurret("framed-") {{
                parts.addAll(new RegionPart("-bottom"){},
                        new RegionPart("-display"){{
                            progress = PartProgress.reload;
                            moveY = -4f;
                            moves.add(new PartMove(PartProgress.constant(1), 0, 1, 0));
                        }},
                        new RegionPart("-top"){}
                );
            }};
            limitRange();
        }};
        ambush = new ItemTurret("ambush"){{
            requirements(Category.turret, with(DTItems.iron, 90, Items.graphite, 40));
            ammo(
                    Items.silicon, new ArtilleryBulletType(3f, 1){{
                        knockback = 0.8f;
                        lifetime = 80f;
                        width = height = 15f;
                        collidesTiles = false;
                        fragBullet = new BlockBulletType(){{
                            shrinkY = shrinkX = 0;
                            speed = 3f;
                            damage = 1f;
                            rotateSprite = false;
                            hitEffect = Fx.coreBuildBlock;
                        }};
                        fragOnHit = true;
                        fragOnAbsorb = true;
                        fragBullets = 5;
                        fragLifeMax = 0.2f;
                        fragLifeMin = 0.5f;
                        fragVelocityMax = 0.5f;
                        fragVelocityMin = 0.1f;
                    }}
            );
            targetAir = false;
            reload = 80f;
            size = 2;
            recoil = 2f;
            range = 235f;
            inaccuracy = 1f;
            shootCone = 10f;
            health = 260;
            shootSound = Sounds.bang;
            coolant = consumeCoolant(0.3f);
            limitRange(0f);
        }};
        voltage = new PowerTurret("voltage"){{
            scaledHealth = 260;
            size = 3;
            targetAir = true;
            targetGround = true;
            rotateSpeed = 5;
            coolant = consumeCoolant(0.2f);
            consumePower(5f);
            drawer = new DrawTurret("framed-");
            reload = 420;
            maxAmmo = 10;
            range = 180;
            shootSound = Sounds.laser;
            inaccuracy = 3;
            shootType = new RandomSpreadBulletType(1,120){{
                shrinkY = 0;
                drag = 0.005f;
                lifetime = 360;
                width = height = 15f;
                status = StatusEffects.shocked;
                sprite = "circle-bullet";
                backColor = frontColor = Pal2.lightningWhite;
                intervalChance = 0.05f;
                scaleLife = false;

                hittable = reflectable = false;

                homingPower = 0.02f;
                shootEffect = despawnEffect = hitEffect = new ExplosionEffect(){{
                    waveColor = Pal.regen;
                    waveStroke = 4f;
                    waveRad = 40f;
                }};
                intervalBullet = new ConnectBulletType(){{
                    damage = 40;
                    lifetime = speed = 0;

                    hitColor = Pal2.lightningWhite;
                    chainHitEffect = Fx.lightning;
                    chainEffect = Fx.chainLightning;
                    hitEffect = Fx.none;
                    radius = 50f;
                    chainColor = Pal.lancerLaser;
                    lightning = 2;
                    lightningColor = Pal.lancerLaser;
                    lightningLength = 7;
                }};
                lightning = 5;
                lightningColor = Pal.lancerLaser;
                lightningLength = 8;
            }};
            requirements(Category.turret, with(DTItems.iridium, 1));
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
        rockExtractor = new GenericCrafter("rock-extractor"){{
            requirements(Category.production, ItemStack.with(Items.graphite, 40, DTItems.iron, 50));
            consumePower(0.6f);
            size = 2;
            ambientSound = Sounds.drill;
            updateEffect = Fx.pulverizeSmall;
            craftEffect = Fx.mine;
            craftTime = 80;
            drawer = new DrawMulti(new DrawDefault(), new DrawRegion("-rotator", 1.4f){{spinSprite = true;}}, new DrawRegion("-top"));
            outputItem = new ItemStack(DTItems.stone, 1);
        }};

        portableDrill = new PortableDrill("portable-drill"){{
            hasItems = true;
            hasLiquids = true;
            liquidBoostIntensity = 2.56f;
            consumeLiquid(Liquids.water, 0.02f).boost();
            portableUnitType = new UnitType("portable-drill-unit"){{
                speed = 0.5f;
                legStraightness = 0f;
                legLength = 8f;
                lockLegBase = true;
                legContinuousMove = true;
                legExtension = -4f;
                legBaseOffset = 2f;
                legMaxLength = 1.1f;
                legMinLength = 0.2f;
                legLengthScl = 0.96f;
                legForwardScl = 1.1f;
                legGroupSize = 2;
                rippleScale = 0f;
                rotateSpeed = 0;
                constructor = LegsUnit::create;
                legCount = 4;
                drawCell = true;
                weapons.add(new PortableBlockWeapon(){});
            }};
            requirements(Category.production, with(Items.copper, 12));
        }};
        ((PortableBlockWeapon)((PortableDrill)portableDrill).portableUnitType.weapons.get(0)).unitContent = portableDrill;
        //effect
        blastMine = new ShockMine("blast-mine"){{
            requirements(Category.effect, with(DTItems.iron, 15, Items.silicon, 12));
            hasShadow = false;
            health = 50;
            bullet = new ExplosionBulletType(100, 10){{
                hitEffect = Fx.pointHit;
            }};
            tileDamage = 7f;
            tendrils = 0;
        }};

        ((ItemTurret)ambush).ammoTypes.forEach(b -> {
            ((BlockBulletType)b.value.fragBullet).bulletContent = blastMine;
        });

        for (int i = 1; i <= 9; i+=2) {
            int finalI = i;
            new FloorBuilder("space-station-builder-" + finalI){{
                requirements(Category.effect, with(DTItems.spaceStationPanel, 5 * finalI * finalI));
                size = 1;
                envEnabled = envRequired = Env.space;
                range = finalI;
                rotate = true;
                floorOffset = range + 1;
                rotateDraw = false;
                floor = spaceStationFloor.asFloor();
            }};
        }

        for (int i = 1; i <= 9; i+=2) {
            int finalI = i;
            new FloorBuilder("space-station-breaker-" + finalI){{
                requirements(Category.effect, with(DTItems.spaceStationPanel, 5 * finalI * finalI));
                size = 1;
                envEnabled = envRequired = Env.space;
                range = finalI;
                floor = Blocks.empty.asFloor();
            }};
        }
        //debug
        debugBlock = new DebugBlock("debug-block"){{
            buildVisibility = DTVars.debugMode ? BuildVisibility.shown : BuildVisibility.hidden;
            envEnabled = Env.any;
            requirements(Category.effect, with(), true);
        }};
        dpsBlock = new DPSBlock("dps-block"){{
            size = 5;
            health = 999999;
            envEnabled = Env.any;
            buildVisibility = DTVars.debugMode ? BuildVisibility.shown : BuildVisibility.hidden;
            requirements(Category.effect, with(), true);
        }};
    }
}
