package disintegration.content;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.part.DrawPart;
import mindustry.type.UnitType;

public class DTUnitTypes {
    public static DrawPart.PartProgress time = p -> Time.time;
    public static DrawPart.PartProgress timeSin = p -> Mathf.absin(20f, 1f);
    /*public static @EntityDef({Unitc.class}) UnitType lancet, talwar, estoc, spear, epee, knife, separate, attract, blend, spaceStationDrone;
    public static @EntityDef({Unitc.class, Mechc.class}) UnitType verity, truth, solve, essence, celestial;
    public static @EntityDef({Unitc.class, BuildingTetherc.class, Payloadc.class}) UnitType refabricatingDrone, repairDrone;
    public static @EntityDef(value = {Unitc.class, InnerWorldc.class}, serialize = false) UnitType physics;*/
    public static  UnitType lancet, talwar, estoc, spear, epee, knife, separate, attract, blend, spaceStationDrone;
    public static  UnitType verity, truth, solve, essence, celestial;
    public static  UnitType refabricatingDrone, repairDrone;
    public static  UnitType physics;

    public static void load() {
        /*
        UnitTypes.gamma.weapons.get(0).bullet.homingPower = 0.1f;
        UnitTypes.gamma.weapons.get(0).bullet.homingRange = 1000f;
        UnitTypes.gamma.weapons.get(0).bullet.fragBullets = 2;
        UnitTypes.gamma.weapons.get(0).bullet.fragOnHit = false;
        UnitTypes.gamma.weapons.get(0).bullet.fragBullet = UnitTypes.gamma.weapons.get(0).bullet;
        UnitTypes.gamma.weapons.get(0).bullet.fragVelocityMax = 1f;
        UnitTypes.gamma.weapons.get(0).bullet.fragVelocityMin = 1f;
        UnitTypes.gamma.weapons.get(0).reload = 0f;

        test = UnitTypes.aegires;
        test.abilities.clear();
        test.abilities.add(new EnergyFieldAbility(4f, 0f, 1800f){{
            statusDuration = 60f * 6f;
            maxTargets = 1000;
        }});
        */
        //air-Hyper
        //T1 lancet

        /*lancet = EntityRegistry.content("lancet", UnitEntity.class, name -> new OmurloUnitType(name) {{
            speed = 3.7f;
            accel = 0.08f;
            drag = 0.04f;
            armor = 3;
            flying = true;
            health = 400;
            engineOffset = 4.5f;
            engineSize = 3.7f;
            hitSize = 11;

            weapons.add(new Weapon() {{
                mirror = false;
                y = 2.5f;
                x = 0f;
                top = false;
                reload = 12f;
                ejectEffect = Fx.none;
                recoil = 2f;
                shootSound = Sounds.lasershoot;

                bullet = new LaserBoltBulletType(5.2f, 13) {{
                    lifetime = 30f;
                    backColor = Pal2.hyperBlue;
                    frontColor = Color.white;
                    lightColor = Pal2.hyperBlue;
                    smokeEffect = hitEffect = despawnEffect = DTFx.hitLaserBlue;
                }};
            }});
        }});

        talwar = EntityRegistry.content("talwar", UnitEntity.class, name -> new OmurloUnitType(name) {{
            speed = 2.7f;
            accel = 0.08f;
            drag = 0.04f;
            armor = 2;
            flying = true;
            health = 300;
            engineOffset = 4.5f;
            engineSize = 3.7f;
            hitSize = 9;

            weapons.add(new Weapon() {{
                mirror = false;
                y = 2.5f;
                x = 0f;
                top = false;
                reload = 24f;
                ejectEffect = Fx.none;
                recoil = 2f;
                shootSound = Sounds.missileSmall;
                shoot.shots = 3;
                inaccuracy = 20f;
                bullet = new MissileBulletType(3f, 9f) {{
                    lifetime = 30;
                    backColor = Pal2.hyperBlue;
                    frontColor = Color.white;
                    trailColor = Pal2.hyperBlue;
                }};
            }});
        }});

        estoc = EntityRegistry.content("estoc", UnitEntity.class, name -> new OmurloUnitType(name) {{
            speed = 2f;
            accel = 0.08f;
            drag = 0.04f;
            armor = 2;
            flying = true;
            health = 600;
            engineOffset = 4.5f;
            engineSize = 4.7f;
            hitSize = 15;
            faceTarget = false;
            circleTarget = true;

            weapons.add(new Weapon() {{
                minShootVelocity = 0.75f;
                mirror = false;
                shootY = 0f;
                x = 0f;
                reload = 30f;
                shootCone = 180f;
                ejectEffect = Fx.none;
                inaccuracy = 15f;
                ignoreRotation = true;
                shootSound = Sounds.none;
                bullet = new BombBulletType(120f, 45f) {{
                    width = 20f;
                    height = 24f;
                    hitEffect = Fx.flakExplosion;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;
                    backColor = Pal2.hyperBlue;
                    hitSound = Sounds.release;

                    status = StatusEffects.blasted;
                    statusDuration = 60f;
                }};
            }});
        }});
        spear = EntityRegistry.content("spear", UnitEntity.class, name -> new OmurloUnitType(name) {{
            speed = 1.5f;
            accel = 0.03f;
            drag = 0.06f;
            flying = true;
            rotateSpeed = 1f;
            health = 70;
            lowAltitude = true;
            hitSize = 15f;
            engineOffset = 23f;
            faceTarget = true;
            engineSize = 5f;
            weapons.add(new PointDefenseWeapon("disintegration-point-defense-mount-blue") {{
                x = 16f;
                y = -16f;
                reload = 4f;
                targetInterval = 8f;
                targetSwitchInterval = 8f;

                bullet = new BulletType() {{
                    shootEffect = Fx.sparkShoot;
                    hitEffect = Fx.pointHit;
                    maxRange = 180f;
                    damage = 30f;
                }};
            }});
            weapons.add(new PointDefenseWeapon("disintegration-point-defense-mount-blue") {{
                x = 10f;
                y = -2f;
                reload = 4f;
                targetInterval = 8f;
                targetSwitchInterval = 8f;

                bullet = new BulletType() {{
                    shootEffect = Fx.sparkShoot;
                    hitEffect = Fx.pointHit;
                    maxRange = 180f;
                    damage = 30f;
                }};
            }});
            abilities.addAll(new WarpAbility() {{
                                 chargeSound = Sounds.lasercharge;
                                 warpSound = Sounds.laserblast;
                                 soundVolume = 0.5f;
                                 minWarpRange = 200f;
                                 warpRange = 1000f;
                                 warpReload = 30 * 60f;
                                 warpDuration = 2 * 60f;
                                 warpEffect = DTFx.warpEffect;
                                 warpChargeEffect = DTFx.warpCharge;
                                 warpTrailEffect = DTFx.warpTrail;
                                 warpStatus = DTStatusEffects.electricResonated;
                                 warpStatusDuration = 600f;
                             }},
                    new EnergyFieldAbility(40, 30, 180) {{
                        y = -6.5f;
                        displayHeal = false;
                        healPercent = 0.75f;
                        maxTargets = 25;
                        color = Pal2.hyperBlue;
                        healEffect = DTFx.healBlue;
                    }});
        }});

        epee = EntityRegistry.content("epee", UnitEntity.class, name -> new OmurloUnitType(name) {{
            speed = 0.7f;
            accel = 0.03f;
            drag = 0.03f;
            flying = true;
            rotateSpeed = 1f;
            health = 70;
            lowAltitude = true;
            hitSize = 15f;
            engineOffset = 40f;
            engineSize = 6f;
            faceTarget = false;
            setEnginesMirror(new UnitEngine(20f, -37f, 6f, -45f));
            BulletType missileBulletType = new BulletType() {{
                shootEffect = Fx.shootBig;
                smokeEffect = DTFx.shootSmokeMissileBlue;
                spawnUnit = new MissileUnitType("epee-missile") {{
                    speed = 3.6f;
                    maxRange = 6f;
                    lifetime = 60f * 2;
                    engineColor = trailColor = Pal2.hyperBlue;
                    engineLayer = Layer.effect;
                    engineSize = 3.1f;
                    engineOffset = 10f;
                    rotateSpeed = 3f;
                    trailLength = 18;
                    missileAccelTime = 50f;
                    lowAltitude = true;
                    loopSound = Sounds.missileTrail;
                    loopSoundVolume = 0.6f;
                    deathSound = Sounds.largeExplosion;
                    targetAir = true;

                    fogRadius = 6f;

                    health = 210;

                    weapons.add(new Weapon() {{
                        shootCone = 360f;
                        mirror = false;
                        reload = 1f;
                        deathExplosionEffect = Fx.massiveExplosion;
                        shootOnDeath = true;
                        shake = 10f;
                        bullet = new ExplosionBulletType(500f, 35f) {{
                            hitColor = Pal2.hyperBlue;
                            shootEffect = new MultiEffect(Fx.massiveExplosion, DTFx.epeeExplosion, Fx.scatheLight, new WaveEffect() {{
                                lifetime = 10f;
                                strokeFrom = 4f;
                                sizeTo = 130f;
                            }});

                            collidesAir = true;
                            buildingDamageMultiplier = 0.3f;

                            ammoMultiplier = 1f;
                            fragLifeMin = 0.1f;
                            fragBullets = 7;
                            fragBullet = new ArtilleryBulletType(3.4f, 32) {{
                                buildingDamageMultiplier = 0.3f;
                                drag = 0.02f;
                                hitEffect = Fx.massiveExplosion;
                                despawnEffect = Fx.scatheSlash;
                                knockback = 0.8f;
                                lifetime = 23f;
                                width = height = 18f;
                                collidesTiles = false;
                                splashDamageRadius = 40f;
                                splashDamage = 80f;
                                backColor = trailColor = hitColor = Pal2.hyperBlue;
                                frontColor = Color.white;
                                smokeEffect = Fx.shootBigSmoke2;
                                despawnShake = 7f;
                                lightRadius = 30f;
                                lightColor = Pal2.hyperBlue;
                                lightOpacity = 0.5f;

                                trailLength = 20;
                                trailWidth = 3.5f;
                                trailEffect = Fx.none;
                            }};
                        }};
                    }});

                    abilities.add(new MoveEffectAbility() {{
                        effect = DTFx.epeeMissileTrailSmoke;
                        rotation = 180f;
                        y = -9f;
                        color = Pal2.hyperBlue.cpy().a(0.4f);
                        interval = 7f;
                    }});
                }};
            }};
            weapons.addAll(
                    new Weapon() {{
                        shootSound = Sounds.missileLaunch;
                        shootCone = 180f;
                        x = 15;
                        y = 12;
                        reload = 240f;
                        rotate = true;
                        rotationLimit = 10f;
                        bullet = missileBulletType;
                    }},
                    new Weapon() {{
                        shootSound = Sounds.missileLaunch;
                        shootCone = 180f;
                        x = 21;
                        y = 0;
                        reload = 200f;
                        rotate = true;
                        rotationLimit = 10f;
                        bullet = missileBulletType;
                    }},
                    new Weapon() {{
                        shootSound = Sounds.missile;
                        shootCone = 180f;
                        x = 27;
                        y = -7;
                        inaccuracy = 3f;
                        shoot = new ShootBarrel() {{
                            shots = 10;
                            shotDelay = 3f;
                            barrels = new float[]{
                                    3, 3, 0,
                                    0, 6, 0,
                                    6, 0, 0,
                            };
                        }};
                        reload = 100f;
                        rotate = true;
                        baseRotation = -45f;
                        rotationLimit = 10f;
                        bullet = new MissileBulletType(2.5f, 20f) {{
                            lifetime = 100;
                            backColor = Pal2.hyperBlue;
                            frontColor = Color.white;
                            trailColor = Pal2.hyperBlue;
                            accel = 0.01f;
                            weaveMag = 2f;
                            weaveScale = 6f;
                        }};
                    }}
            );
        }});

        verity = EntityRegistry.content("verity", MechUnit.class, name -> new OmurloUnitType(name) {{
            canBoost = true;
            boostMultiplier = 1.6f;
            speed = 0.5f;
            hitSize = 8f;
            health = 150;
            weapons.add(new Weapon("disintegration-plasma-gun") {{
                reload = 21f;
                x = 3.5f;
                y = 0f;
                top = false;
                inaccuracy = 10f;
                shoot.shots = 3;
                bullet = new BasicBulletType(2.5f, 9) {{
                    shootSound = Sounds.bolt;
                    sprite = "circle-bullet";
                    width = 6f;
                    height = 6f;
                    shrinkInterp = Interp.zero;
                    shrinkY = 0f;
                    lifetime = 40f;
                    trailLength = 5;
                    trailWidth = 4f;
                    weaveMag = 2f;
                    weaveScale = 5f;
                    trailColor = frontColor = backColor = Pal2.hyperBlue;
                    despawnEffect = hitEffect = Fx.hitLancer;
                }};
            }});
        }});

        truth = EntityRegistry.content("truth", MechUnit.class, name -> new OmurloUnitType(name) {{
            canBoost = true;
            boostMultiplier = 1.6f;
            speed = 0.7f;
            hitSize = 11f;
            health = 320f;
            buildSpeed = 0.9f;
            armor = 4f;
            riseSpeed = 0.07f;

            ammoType = new PowerAmmoType(1300);

            weapons.add(new Weapon("disintegration-plasma-laser") {{
                top = false;
                x = 3.25f;
                shake = 2.2f;
                y = 0.5f;
                shootY = 2.5f;

                reload = 50f;

                ejectEffect = Fx.none;
                recoil = 2.5f;
                shootSound = Sounds.laser;

                bullet = new LaserBulletType() {{
                    recoil = 0.5f;
                    damage = 14f;
                    length = 90f;
                    shootEffect = Fx.lancerLaserShoot;
                }};
            }});
        }});
        solve = EntityRegistry.content("solve", MechUnit.class, name -> new OmurloUnitType(name) {{
            speed = 0.5f;
            hitSize = 10f;
            health = 550;
            armor = 4f;
            ammoType = new ItemAmmoType(Items.coal);

            immunities.add(StatusEffects.burning);

            weapons.add(new Weapon("disintegration-plasma-thrower") {{
                top = false;
                shootSound = Sounds.flame2;
                shootY = 2f;
                reload = 5f;
                recoil = 1f;
                ejectEffect = Fx.none;
                bullet = new BulletType(4.2f, 37f) {{
                    ammoMultiplier = 3f;
                    hitSize = 13f;
                    lifetime = 13f;
                    pierce = true;
                    pierceBuilding = true;
                    pierceCap = 2;
                    statusDuration = 60f * 4;
                    shootEffect = DTFx.shootPlasmaFlame;
                    hitEffect = Fx.hitFlameSmall;
                    despawnEffect = Fx.none;
                    status = StatusEffects.burning;
                    keepVelocity = false;
                    hittable = false;
                }};
            }});
        }});

        essence = EntityRegistry.content("essence", MechUnit.class, name -> new OmurloUnitType(name) {

            {
                speed = 0.36f;
                hitSize = 22f;
                rotateSpeed = 2.1f;
                health = 9000;
                armor = 10f;
                mechFrontSway = 1f;
                ammoType = new ItemAmmoType(DTItems.iridium);

                mechStepParticles = true;
                stepShake = 0.15f;
                singleTarget = true;
                drownTimeMultiplier = 4f;

                weapons.add(
                        new Weapon("disintegration-essence-weapon") {{
                            top = true;
                            y = 1f;
                            x = 15f;
                            shootY = 8f;
                            reload = 5f;
                            recoil = 5f;
                            shake = 2f;
                            ejectEffect = Fx.casing3;
                            shootSound = Sounds.bang;
                            inaccuracy = 3f;
                            alternate = false;

                            bullet = new BasicBulletType(7f, 50) {{
                                width = 9f;
                                height = 21f;
                                lifetime = 25f;
                                shootEffect = Fx.shootBig;
                                trailWidth = 9f / 4f;
                                trailLength = 12;
                                trailColor = backColor.cpy();
                                pierce = true;
                                pierceCap = 2;
                            }};
                        }}
                );
                weapons.add(new PointDefenseWeapon("disintegration-point-defense-mount-blue") {{
                    mirror = true;
                    x = 9f;
                    y = -7f;
                    reload = 7f;
                    targetInterval = 9f;
                    targetSwitchInterval = 10f;

                    bullet = new BulletType() {{
                        shootEffect = Fx.sparkShoot;
                        hitEffect = Fx.pointHit;
                        maxRange = 170f;
                        damage = 17f;
                    }};
                }});
            }
        });

        celestial = EntityRegistry.content("celestial", MechUnit.class, name -> new OmurloUnitType(name) {{
            speed = 0.3f;
            hitSize = 22f;
            rotateSpeed = 2.1f;
            health = 13000;
            armor = 11f;
            mechFrontSway = 1f;
            ammoType = new ItemAmmoType(DTItems.iridium);

            mechStepParticles = true;
            stepShake = 0.15f;
            singleTarget = true;
            drownTimeMultiplier = 4f;
            weapons.add(
                    new Weapon("disintegration-celestial-weapon") {{
                        top = true;
                        y = 0f;
                        x = 20.5f;
                        shootY = 8f;
                        reload = 20f;
                        recoil = 5f;
                        shake = 2f;
                        ejectEffect = Fx.casing3;
                        shootSound = Sounds.shotgun;
                        shoot = new ShootSpread(5, 5);
                        bullet = new BasicBulletType(7f, 30) {{
                            width = 20f;
                            height = 15f;
                            lifetime = 25f;
                            sprite = "circle-bullet";
                            trailWidth = 20f / 4f;
                            trailLength = 10;
                            despawnEffect = Fx.hitBulletBig;
                            trailColor = backColor.cpy();
                            pierce = true;
                            pierceCap = 4;
                            drag = 0.05f;
                            shrinkY = 0f;
                            fragBullets = 2;
                            fragBullet = new BasicBulletType(4.5f, 20) {{
                                width = 10f;
                                height = 10f;
                                pierce = true;
                                pierceBuilding = true;
                                pierceCap = 3;

                                lifetime = 5f;
                                hitEffect = Fx.flakExplosion;
                                splashDamage = 15f;
                                splashDamageRadius = 10f;
                            }};
                        }};
                    }}
            );
        }});

        separate = EntityRegistry.content("separate", UnitEntity.class, name -> new OmurloUnitType(name) {{
            ammoType = new PowerAmmoType(900);
            aiController = BuilderAI::new;
            isEnemy = false;

            lowAltitude = true;
            flying = true;
            mineSpeed = 6.5f;
            mineTier = 1;
            buildSpeed = 0.5f;
            drag = 0.05f;
            speed = 3f;
            rotateSpeed = 15f;
            accel = 0.1f;
            itemCapacity = 30;
            health = 150f;
            engineOffset = 6f;
            hitSize = 8f;
            alwaysUnlocked = true;
            weapons.add(new Weapon("disintegration-laser-bolt-mount") {{
                reload = 17f;
                x = 2f;
                y = 0f;
                top = false;
                shootSound = Sounds.lasershoot;
                bullet = new LaserBoltBulletType(2.5f, 10) {{
                    collidesTeam = true;
                    healPercent = 2;
                    lifetime = 60f;
                    shootEffect = DTFx.hitLaserYellow;
                    smokeEffect = Fx.shootSmallSmoke;
                    despawnEffect = DTFx.hitLaserYellow;
                    hitEffect = DTFx.hitLaserYellow;
                    buildingDamageMultiplier = 0.01f;
                    healColor = Pal.bulletYellow;
                }};
            }});
        }});

        attract = EntityRegistry.content("attract", UnitEntity.class, name -> new OmurloUnitType(name) {{
            ammoType = new PowerAmmoType(900);
            aiController = BuilderAI::new;
            isEnemy = false;

            lowAltitude = true;
            flying = true;
            mineSpeed = 6.5f;
            mineTier = 1;
            buildSpeed = 0.7f;
            drag = 0.05f;
            speed = 4f;
            rotateSpeed = 15f;
            accel = 0.1f;
            itemCapacity = 30;
            health = 150f;
            engineOffset = 6f;
            hitSize = 8f;
            alwaysUnlocked = true;
            weapons.add(new Weapon("disintegration-laser-bolt-mount2") {{
                reload = 17f;
                x = 2.5f;
                y = 0f;
                shootY = 2.5f;
                top = false;
                shootSound = Sounds.lasershoot;
                bullet = new LaserBoltBulletType(3.5f, 15) {{
                    collidesTeam = true;
                    healPercent = 2;
                    lifetime = 60f;
                    shootEffect = DTFx.hitLaserYellow;
                    smokeEffect = Fx.shootSmallSmoke;
                    despawnEffect = DTFx.hitLaserYellow;
                    hitEffect = DTFx.hitLaserYellow;
                    buildingDamageMultiplier = 0.01f;
                    healColor = Pal.bulletYellow;
                }};
            }});
        }});

        float coreFleeRange = 500f;
        spaceStationDrone = EntityRegistry.content("space-station-drone", UnitEntity.class, name -> new UnitType(name) {{
            coreUnitDock = true;
            controller = u -> new BuilderAI(true, coreFleeRange);
            isEnemy = false;
            envDisabled = 0;

            targetPriority = -2;
            lowAltitude = false;
            mineWalls = true;
            mineFloor = true;
            mineHardnessScaling = false;
            flying = true;
            mineSpeed = 6f;
            mineTier = 3;
            buildSpeed = 1.2f;
            drag = 0.03f;
            speed = 4.6f;
            rotateSpeed = 7f;
            accel = 0.09f;
            itemCapacity = 60;
            health = 300f;
            armor = 1f;
            hitSize = 9f;
            engineSize = 2f;
            engineOffset = 4.8f;
            payloadCapacity = 2f * 2f * tilesize * tilesize;
            pickupUnits = false;
            parts.add(
                    new RegionPart("-side") {{
                        moveRot = -20f;
                        progress = PartProgress.warmup;
                        mirror = true;
                    }}
            );
            weapons.add(new RepairBeamWeapon() {{
                widthSinMag = 0.11f;
                reload = 20f;
                x = 0f;
                y = 6.5f;
                rotate = false;
                shootY = 0f;
                beamWidth = 0.7f;
                repairSpeed = 3.1f;
                fractionRepairSpeed = 0.06f;
                aimDst = 0f;
                shootCone = 15f;
                mirror = false;

                targetUnits = true;
                targetBuildings = true;
                autoTarget = false;
                controllable = true;
                laserColor = Pal.techBlue;
                laserTopColor = Pal.regen;
                healColor = Pal.regen;

                bullet = new BulletType() {{
                    maxRange = 60f;
                }};
            }});
        }});
        //special
        refabricatingDrone = EntityRegistry.content("refabricating-drone", PayloadBuildingTetherUnit.class, name -> new OmurloUnitType(name) {{
            controller = u -> new AssemblerAI();

            flying = true;
            drag = 0.06f;
            accel = 0.11f;
            speed = 1.3f;
            health = 90;
            engineSize = 2f;
            engineOffset = 6.5f;
            payloadCapacity = 0f;
            targetable = false;
            bounded = false;

            isEnemy = false;
            hidden = true;
            useUnitCap = false;
            logicControllable = false;
            playerControllable = false;
            allowedInPayloads = false;
            createWreck = false;
            envEnabled = Env.any;
            envDisabled = Env.none;
        }});
        repairDrone = EntityRegistry.content("repair-drone", PayloadBuildingTetherUnit.class, name -> new ErekirUnitType(name) {{
            controller = u -> new RepairDroneAI();
            flying = true;
            drag = 0.06f;
            accel = 0.11f;
            speed = 1.3f;
            health = 90;
            engineSize = 2f;
            engineOffset = 3.75f;

            outlineColor = Pal.darkOutline;
            isEnemy = false;
            hidden = true;
            useUnitCap = false;
            logicControllable = false;
            playerControllable = false;
            allowedInPayloads = false;
            envEnabled = Env.any;
            envDisabled = Env.none;
            payloadCapacity = 0f;
            weapons.add(new Weapon() {{
                reload = 10f;
                x = 0;
                mirror = false;
                ejectEffect = Fx.none;
                shootSound = Sounds.lasershoot;
                range = 90f;

                bullet = new LaserBoltBulletType(4f, 1) {{
                    lifetime = 23f;
                    healPercent = 1f;
                    collidesTeam = true;
                    backColor = Pal.heal;
                    frontColor = Color.white;
                    recoil = 0.5f;
                }};
            }});
        }});
        if (false)
            EntityRegistry.content("flying-neoplasm-squid", UnitEntity.class, name -> new NeoplasmUnitType(name) {{
                speed = 1.5f;
                accel = 0.03f;
                drag = 0.06f;
                flying = true;
                rotateSpeed = 1f;
                health = 70;
                engineSize = 0;
                hitSize = 12;
                itemCapacity = 10;
                omniMovement = false;
                circleTarget = true;
                parts.add(new RegionPart("-tentacle") {{
                    moves.add(new PartMove(p -> Mathf.absin(20f, 1f), 0, 0, 20));
                    x = -4f;
                    y = 0.5f;
                    rotation = -10;
                    mirror = true;
                }});
                weapons.add(new Weapon() {{
                    minShootVelocity = 0.5f;
                    x = 0f;
                    shootY = 0f;
                    reload = 50f;
                    shootCone = 180f;
                    ejectEffect = Fx.none;
                    inaccuracy = 15f;
                    ignoreRotation = true;

                    shootSound = Sounds.none;
                    bullet = new BombBulletType(27f, 25f) {{
                        width = 10f;
                        height = 14f;
                        hitEffect = Fx.blastExplosion;
                        shootEffect = Fx.none;
                        smokeEffect = Fx.none;
                        frontColor = Pal.neoplasm1;
                        backColor = Pal.neoplasm2;
                        puddleLiquid = Liquids.neoplasm;
                        puddleAmount = 20f;
                        puddleRange = 4f;
                        puddles = 3;
                    }};
                }});
            }});
        physics = EntityRegistry.content("physics", InnerWorldUnit.class, name -> new WorldUnitType(name) {{
            speed = 1.5f;
            accel = 0.03f;
            drag = 0.06f;
            flying = true;
            rotateSpeed = 1f;
            health = 70;
            worldWidth = 10;
            worldHeight = 10;
        }});*/
    }
}
