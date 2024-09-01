package disintegration.content;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import disintegration.ai.types.RepairDroneAI;
import disintegration.entities.abilities.WarpAbility;
import disintegration.gen.entities.EntityRegistry;
import disintegration.gen.entities.InnerWorldUnit;
import disintegration.gen.entities.InnerWorldc;
import disintegration.gen.entities.PayloadBuildingTetherUnit;
import disintegration.graphics.Pal2;
import disintegration.type.unit.OmurloUnitType;
import disintegration.type.unit.WorldUnitType;
import ent.anno.Annotations;
import mindustry.ai.UnitCommand;
import mindustry.ai.types.AssemblerAI;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.MinerAI;
import mindustry.ai.types.SuicideAI;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.LaserBoltBulletType;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.part.HoverPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootSpread;
import mindustry.entities.units.WeaponMount;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.type.ammo.PowerAmmoType;
import mindustry.type.unit.ErekirUnitType;
import mindustry.type.unit.MissileUnitType;
import mindustry.type.unit.NeoplasmUnitType;
import mindustry.type.weapons.*;
import mindustry.entities.part.DrawPart;
import mindustry.gen.*;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Env;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.tilesize;

public class DTUnitTypes {
    public static DrawPart.PartProgress time = p -> Time.time;
    public static DrawPart.PartProgress timeSin = p -> Mathf.absin(20f, 1f);
    public static @Annotations.EntityDef({Unitc.class}) UnitType
            lancet, talwar, estoc, epee,
            colibri, albatross, crane, eagle, phoenix,
            converge, cover, protect, defend,
            separate, attract, blend, spaceStationDrone;
    public static @Annotations.EntityDef({Unitc.class, Payloadc.class}) UnitType spear, harbour;
    public static @Annotations.EntityDef({Unitc.class, ElevationMovec.class}) UnitType assist, strike, coverture, attack, devastate;
    public static @Annotations.EntityDef({Unitc.class, Mechc.class}) UnitType verity, truth, solve, essence, axiom;
    public static @Annotations.EntityDef({Unitc.class, BuildingTetherc.class, Payloadc.class}) UnitType refabricatingDrone, repairDrone;
    public static @Annotations.EntityDef(value = {Unitc.class, InnerWorldc.class}, serialize = false) UnitType physics;

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
        //region blue-air
        lancet = EntityRegistry.content("lancet", UnitEntity.class, name -> new OmurloUnitType(name) {{
            speed = 3.7f;
            accel = 0.08f;
            drag = 0.04f;
            flying = true;
            health = 70;
            engineOffset = 4.5f;
            engineSize = 3.7f;
            hitSize = 9;

            ammoType = new PowerAmmoType(900);

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
            hitSize = 11;

            ammoType = new ItemAmmoType(Items.graphite);

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
            hitSize = 19;
            faceTarget = false;
            circleTarget = true;
            armor = 5f;
            health = 700;

            ammoType = new ItemAmmoType(Items.graphite);

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
                bullet = new BombBulletType(50f, 45f) {{
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
        spear = EntityRegistry.content("spear", PayloadUnit.class, name -> new OmurloUnitType(name) {{
            speed = 1.5f;
            accel = 0.03f;
            drag = 0.06f;
            flying = true;
            rotateSpeed = 1f;
            health = 7200;
            armor = 9f;
            lowAltitude = true;
            hitSize = 40f;
            engineOffset = 23f;
            faceTarget = true;
            engineSize = 5f;
            payloadCapacity = 16f * 64f;
            ammoType = new PowerAmmoType(900);
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
            health = 22000;
            armor = 13f;
            lowAltitude = true;
            hitSize = 60f;
            engineOffset = 40f;
            engineSize = 6f;
            faceTarget = false;
            ammoType = new ItemAmmoType(DTItems.iridium);
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
        //endregion
        //region red-air
        colibri = EntityRegistry.content("colibri", UnitEntity.class, name -> new OmurloUnitType(name) {{
            speed = 2.7f;
            accel = 0.08f;
            drag = 0.04f;
            flying = true;
            health = 70;
            engineOffset = 0f;
            ammoType = new PowerAmmoType(100);
            targetFlags = new BlockFlag[]{BlockFlag.generator, null};
            hitSize = 4;
            circleTarget = true;

            weapons.add(new Weapon(){{
                reload = 30f;
                ejectEffect = Fx.none;
                shootSound = Sounds.malignShoot;
                bullet = new LaserBulletType(20f){{
                    colors = new Color[]{Pal2.attackRed.cpy().mul(1f, 1f, 1f, 0.4f), Pal2.attackRed, Color.white};
                    length = 80f;
                    hitEffect = Fx.hitLancer;
                    recoil = -5f;
                }};
            }
                @Override
                protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation){
                    super.shoot(unit, mount, shootX, shootY, rotation);
                    Tmp.v1.trns(rotation, 50f);
                    unit.x += Tmp.v1.x;
                    unit.y += Tmp.v1.y;
                }
            });
        }});
        albatross = EntityRegistry.content("albatross", UnitEntity.class, name -> new OmurloUnitType(name) {{
            aiController = SuicideAI::new;
            speed = 2.7f;
            accel = 0.08f;
            drag = 0.04f;
            flying = true;
            health = 140;
            engineOffset = 5.5f;
            ammoType = new PowerAmmoType(1);
            targetFlags = new BlockFlag[]{BlockFlag.generator, null};
            hitSize = 11;

            weapons.add(new Weapon(){{
                reload = 30f;
                shootCone = 180f;
                ejectEffect = Fx.none;
                shootSound = Sounds.plasmaboom;
                mirror = false;
                x = shootY = 0f;
                shootOnDeath = true;
                bullet = new BulletType(0f, 0f){{
                    killShooter = true;
                    instantDisappear = true;
                    fragBullets = 5;
                    fragBullet = new LaserBulletType(10f){{
                        colors = new Color[]{Pal2.attackRed.cpy().mul(1f, 1f, 1f, 0.4f), Pal2.attackRed, Color.white};
                        length = 100f;
                        width = 25f;
                        hitEffect = Fx.hitLancer;
                    }};
                }};
            }});
        }});
        crane = EntityRegistry.content("crane", UnitEntity.class, name -> new OmurloUnitType(name) {{
            speed = 2f;
            accel = 0.08f;
            drag = 0.04f;
            armor = 2;
            flying = true;
            health = 600;
            engineOffset = 10f;
            engineSize = 3f;
            hitSize = 16;
            faceTarget = false;
            circleTarget = true;

            ammoType = new ItemAmmoType(Items.coal);

            weapons.add(new Weapon() {{
                minShootVelocity = 0.75f;
                mirror = false;
                shootY = 0f;
                x = 0f;
                reload = 60f;
                shootCone = 180f;
                ejectEffect = Fx.none;
                inaccuracy = 360f;
                ignoreRotation = true;
                shootSound = Sounds.plasmadrop;
                shoot.shots = 5;
                bullet = new BombBulletType(120f, 45f) {{
                    speed = 1f;
                    velocityRnd = 1f;
                    drag = 0.1f;
                    width = 10f;
                    height = 12f;
                    hitEffect = Fx.flakExplosion;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;
                    backColor = Pal2.attackRed;
                    hitSound = Sounds.plasmaboom;

                    status = StatusEffects.blasted;
                    statusDuration = 60f;
                }};
            }});
        }});
        eagle = EntityRegistry.content("eagle", UnitEntity.class, name -> new OmurloUnitType(name) {{
            flying = true;
            drag = 0.06f;
            speed = 1.1f;
            rotateSpeed = 3.2f;
            accel = 0.1f;
            health = 6000f;
            armor = 4f;
            hitSize = 48f;
            engineOffset = 26f;
            engineSize = 7f;
            ammoType = new PowerAmmoType(300);
            weapons.add(new Weapon() {{
                mirror = false;
                shootY = 0f;
                x = 0f;
                shoot.firstShotDelay = 40;
                shootStatus = StatusEffects.unmoving;
                shootStatusDuration = 30f;
                reload = 180f;
                ejectEffect = Fx.none;
                chargeSound = Sounds.lasercharge2;
                shootSound = Sounds.pulseBlast;
                bullet = new BasicBulletType(10f, 50f) {{
                    sprite = "disintegration-dart";
                    width = 36f;
                    height = 20f;
                    hitEffect = Fx.hitBeam;
                    pierce = true;
                    pierceCap = 6;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;
                    recoil = 10f;
                    chargeEffect = new Effect(40, e -> {
                        color(Pal2.attackRed);
                        stroke(e.finpow() * 1.5f);

                        randLenVectors(e.id, 10, e.foutpowdown() * 17f, (x, y) -> {
                            float ang = Mathf.angle(x, y);
                            lineAngle(e.x + x, e.y + y, ang, 5f * e.finpow());
                        });
                    });
                    frontColor = Pal2.attackRed;
                    backColor = Pal2.attackRed;
                    hitSound = Sounds.plasmaboom;
                    despawnHit = true;
                    trailColor = Pal2.attackRed;
                    trailWidth = 2f;
                    trailLength = 20;
                }};
            }});
        }});
        phoenix = EntityRegistry.content("phoenix", UnitEntity.class, name -> new OmurloUnitType(name) {{
            lowAltitude = false;
            flying = true;
            drag = 0.06f;
            speed = 1.1f;
            rotateSpeed = 3.2f;
            accel = 0.1f;
            health = 20000f;
            armor = 4f;
            hitSize = 60f;
            engineOffset = 37f;
            engineSize = 7f;
            ammoType = new PowerAmmoType(900);
            weapons.add(new Weapon() {{
                shootStatusDuration = 60f * 2f;
                shootStatus = StatusEffects.unmoving;
                parentizeEffects = true;
                shootY = 0f;
                x = 12f;
                reload = 360f;
                shoot.firstShotDelay = DTFx.redLaserCharge.lifetime;
                ejectEffect = Fx.none;
                chargeSound = Sounds.lasercharge2;
                shootSound = Sounds.pulseBlast;
                bullet = new LaserBulletType(){{
                    length = 360f;
                    damage = 280f;
                    width = 45f;
                    recoil = 1f;
                    lifetime = 65f;

                    lightningSpacing = 35f;
                    lightningLength = 5;
                    lightningDelay = 1.1f;
                    lightningLengthRand = 15;
                    lightningDamage = 50;
                    lightningAngleRand = 40f;
                    largeHit = true;
                    lightColor = lightningColor = Pal2.attackRed;

                    chargeEffect = DTFx.redLaserCharge;

                    sideAngle = 15f;
                    sideWidth = 0f;
                    sideLength = 0f;
                    colors = new Color[]{Pal2.attackRed.cpy().a(0.4f), Pal2.attackRed, Color.white};
                }};
            }});
        }});
        //endregion
        //region orange-air
        converge = EntityRegistry.content("converge", UnitEntity.class, name -> new OmurloUnitType(name) {{
            ammoType = new PowerAmmoType(900);
            flying = true;
            speed = 1.5f;
            hitSize = 8f;
            health = 150f;
            controller = u -> new MinerAI();
            drag = 0.06f;
            accel = 0.12f;
            engineSize = 1.8f;
            engineOffset = 8.1f;
            mineTier = 1;
            mineSpeed = 2.5f;
            defaultCommand = UnitCommand.mineCommand;
        }});
        cover = EntityRegistry.content("cover", UnitEntity.class, name -> new OmurloUnitType(name) {{
            defaultCommand = UnitCommand.rebuildCommand;

            flying = true;
            drag = 0.05f;
            speed = 1.4f;
            rotateSpeed = 15f;
            accel = 0.1f;
            range = 130f;
            health = 400;
            buildSpeed = 0.5f;
            engineOffset = 10f;
            hitSize = 12f;
            lowAltitude = true;

            ammoType = new PowerAmmoType(900);

            mineTier = 2;
            mineSpeed = 3.5f;

            abilities.add(new RepairFieldAbility(5f, 60f * 8, 50f){{
                healEffect = DTFx.healYellow;
                activeEffect = DTFx.healWaveDynamicYellow;
            }});

            weapons.add(new Weapon(){{
                top = false;
                y = 0f;
                x = 0f;
                reload = 15f;
                mirror = false;
                ejectEffect = Fx.none;
                recoil = 2f;
                shootSound = Sounds.missile;
                velocityRnd = 0.5f;
                inaccuracy = 15f;

                bullet = new PointBulletType(){{
                    lifetime = 50f;
                    keepVelocity = false;
                    shootEffect = DTFx.shootOrb;
                    smokeEffect = Fx.hitLaser;
                    trailEffect = DTFx.trailYellow;
                    healEffect = DTFx.healYellow;
                    trailSpacing = 6f;
                    hitEffect = despawnEffect = DTFx.shootOrb;
                    hitSound = Sounds.none;

                    healPercent = 5.5f;
                    collidesTeam = true;
                    trailColor = Pal.accent;
                    healColor = Pal.accent;
                }};
            }});
        }});
        protect = EntityRegistry.content("protect", UnitEntity.class, name -> new OmurloUnitType(name) {{
            flying = true;
            drag = 0.05f;
            speed = 1.3f;
            rotateSpeed = 10f;
            accel = 0.1f;
            range = 130f;
            health = 400;
            buildSpeed = 0.5f;
            engineOffset = 12f;
            hitSize = 16f;
            lowAltitude = true;

            ammoType = new PowerAmmoType(900);

            weapons.add(new Weapon(){{
                range = 100;
                top = false;
                mirror = false;
                y = 0f;
                x = 0f;
                reload = 30f;
                ejectEffect = Fx.none;
                recoil = 2f;
                shootSound = Sounds.laser;
                velocityRnd = 0.5f;
                inaccuracy = 15f;

                bullet = new LaserBulletType(100){{
                    recoil = 0.1f;
                    length = 100;
                    colors = new Color[]{Pal.accent.cpy().mul(1f, 1f, 1f, 0.4f), Pal.accent, Color.white};
                }};
            }});
        }});
        defend = EntityRegistry.content("defend", UnitEntity.class, name -> new OmurloUnitType(name) {{
            flying = true;
            drag = 0.05f;
            speed = 1f;
            rotateSpeed = 10f;
            accel = 0.1f;
            range = 130f;
            health = 400;
            buildSpeed = 0.5f;
            engineOffset = 12f;
            engineSize = 7f;
            hitSize = 36f;
            lowAltitude = true;

            ammoType = new PowerAmmoType(900);

            weapons.add(new Weapon(){{
                range = 100;
                top = false;
                mirror = false;
                y = 4f;
                x = 0f;
                reload = 120f;
                ejectEffect = Fx.none;
                shootSound = Sounds.laser;

                bullet = new EmpBulletType(){{
                    recoil = 0.4f;
                    float rad = 80f;
                    radius = rad;
                    damage = 100f;
                    splashDamage = 70f;
                    splashDamageRadius = rad;
                    clipSize = 250f;
                    lifetime = 60f;
                    sprite = "circle-bullet";
                    backColor = Pal.accent;
                    frontColor = Color.white;
                    speed = 5;
                    scaleLife = true;
                    width = height = 12f;
                    shrinkY = 0f;
                    hitSound = Sounds.plasmaboom;
                    hitEffect = new Effect(50f, rad, e -> {
                        e.scaled(10f, b -> {
                            color(Color.white, b.fout());
                            Fill.circle(e.x, e.y, rad);
                        });

                        color(Pal.accent);
                        stroke(e.fout() * 3f);
                        Lines.circle(e.x, e.y, rad);

                        Fill.circle(e.x, e.y, 12f * e.fout());
                        color();
                        Fill.circle(e.x, e.y, 6f * e.fout());
                        Drawf.light(e.x, e.y, rad * 1.6f, Pal.heal, e.fout());
                    });
                }};
            }});
        }});
        harbour = EntityRegistry.content("harbour", PayloadUnit.class, name -> new OmurloUnitType(name) {{
            defaultCommand = UnitCommand.repairCommand;

            flying = true;
            drag = 0.05f;
            speed = 0.3f;
            rotateSpeed = 2f;
            accel = 0.1f;
            range = 130f;
            health = 400;
            buildSpeed = 0.5f;
            engineOffset = 48f;
            engineSize = 7f;
            hitSize = 64f;
            lowAltitude = true;

            ammoType = new PowerAmmoType(900);

            mineTier = 2;
            mineSpeed = 3.5f;

            payloadCapacity = Mathf.sqr(8 * 7);

            abilities.addAll(new RegenAbility(){{
                amount = 300f;
                }},
                new UnitSpawnAbility(cover, 3600, -20, -40),
                new UnitSpawnAbility(cover, 3600, 20, -40),
                new EnergyFieldAbility(50, 50, 100){{
                    color = Pal.accent;
                    healEffect = DTFx.healYellow;
                    healColor = Pal.accent;
                    healPercent = 4;
                }}
            );
        }});
        //endregion
        //region blue-ground
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
        axiom = EntityRegistry.content("axiom", MechUnit.class, name -> new OmurloUnitType(name) {{
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
                    new Weapon("disintegration-axiom-weapon") {{
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
                        bullet = new BasicBulletType(7f, 50) {{
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
        //endregion
        //region red-ground
        assist = EntityRegistry.content("assist", ElevationMoveUnit.class, name -> new OmurloUnitType(name){{
            hovering = true;
            shadowElevation = 0.1f;

            drag = 0.07f;
            speed = 1.8f;
            rotateSpeed = 4f;

            accel = 0.09f;
            health = 600f;
            armor = 1f;
            hitSize = 11f;
            engineOffset = 7f;
            engineSize = 2f;
            itemCapacity = 0;
            useEngineElevation = false;
            researchCostMultiplier = 0f;

            abilities.add(new MoveEffectAbility(0f, -7f, Pal2.attackRed, Fx.missileTrailShort, 4f){{
                teamColor = true;
            }});

            for(float f : new float[]{-3f, 3f}){
                parts.add(new HoverPart(){{
                    x = 3.9f;
                    y = f;
                    mirror = true;
                    radius = 6f;
                    phase = 90f;
                    stroke = 2f;
                    layerOffset = -0.001f;
                    color = Pal2.attackRed;
                }});
            }

            weapons.add(new Weapon("disintegration-assist-weapon"){{
                rotateSpeed = 6f;
                y = 0f;
                x = 0f;
                top = true;
                mirror = false;
                reload = 40f;
                rotate = true;

                bullet = new BasicBulletType(4f, 25){{
                    sprite = "missile-large";
                    smokeEffect = Fx.shootBigSmoke;
                    shootEffect = Fx.shootBigColor;
                    width = 5f;
                    height = 7f;
                    lifetime = 40f;
                    hitSize = 4f;
                    hitColor = backColor = trailColor = Pal2.attackRed;
                    frontColor = Color.white;
                    trailWidth = 1.7f;
                    trailLength = 5;
                    despawnEffect = hitEffect = Fx.hitBulletColor;
                }};
            }});
        }});
        strike = EntityRegistry.content("strike", ElevationMoveUnit.class, name -> new OmurloUnitType(name){{
            hovering = true;
            shadowElevation = 0.1f;

            drag = 0.07f;
            speed = 1.8f;
            rotateSpeed = 4f;

            accel = 0.09f;
            health = 600f;
            armor = 1f;
            hitSize = 11f;
            engineOffset = 9f;
            engineSize = 2f;
            itemCapacity = 0;
            useEngineElevation = false;
            researchCostMultiplier = 0f;

            abilities.add(new MoveEffectAbility(0f, -9f, Pal2.attackRed, Fx.missileTrailShort, 4f){{
                teamColor = true;
            }});

            for(float f : new float[]{-8f, 8f}){
                parts.add(new HoverPart(){{
                    x = 8f;
                    y = f;
                    mirror = true;
                    radius = 10f;
                    phase = 90f;
                    stroke = 2f;
                    layerOffset = -0.001f;
                    color = Pal2.attackRed;
                }});
            }

            weapons.add(new Weapon("disintegration-strike-weapon"){{
                range = 80f;
                rotateSpeed = 6f;
                y = 0f;
                x = 0f;
                shootY = 0f;
                top = true;
                mirror = false;
                reload = 30f;
                rotate = true;

                bullet = new RailBulletType(){{
                    length = 160f;
                    damage = 48f;
                    hitColor = Pal2.attackRed;
                    hitEffect = endEffect = Fx.hitBulletColor;
                    pierceDamageFactor = 0.8f;

                    smokeEffect = Fx.colorSpark;

                    endEffect = new Effect(14f, e -> {
                        color(e.color);
                        Drawf.tri(e.x, e.y, e.fout() * 1.5f, 5f, e.rotation);
                    });

                    shootEffect = new Effect(10, e -> {
                        color(e.color);
                        float w = 1.2f + 7 * e.fout();

                        Drawf.tri(e.x, e.y, w, 30f * e.fout(), e.rotation);
                        color(e.color);

                        for(int i : Mathf.signs){
                            Drawf.tri(e.x, e.y, w * 0.9f, 18f * e.fout(), e.rotation + i * 90f);
                        }

                        Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
                    });

                    lineEffect = new Effect(20f, e -> {
                        if(!(e.data instanceof Vec2 v)) return;

                        color(e.color);
                        stroke(e.fout() * 0.9f + 0.6f);

                        Fx.rand.setSeed(e.id);
                        for(int i = 0; i < 7; i++){
                            Fx.v.trns(e.rotation, Fx.rand.random(8f, v.dst(e.x, e.y) - 8f));
                            Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
                        }

                        e.scaled(14f, b -> {
                            stroke(b.fout() * 1.5f);
                            color(e.color);
                            Lines.line(e.x, e.y, v.x, v.y);
                        });
                    });
                }};
            }});
        }});
        coverture = EntityRegistry.content("coverture", ElevationMoveUnit.class, name -> new OmurloUnitType(name){{
            hovering = true;
            shadowElevation = 0.1f;

            drag = 0.07f;
            speed = 1.8f;
            rotateSpeed = 4f;

            accel = 0.09f;
            health = 600f;
            armor = 1f;
            hitSize = 24f;
            engineOffset = 12f;
            engineSize = 2f;
            itemCapacity = 0;
            useEngineElevation = false;
            researchCostMultiplier = 0f;

            abilities.add(new MoveEffectAbility(0f, -14f, Pal2.attackRed, Fx.missileTrailShort, 4f){{
                teamColor = true;
            }});

            for(float f : new float[]{-10f, 0f, 10f}){
                parts.add(new HoverPart(){{
                    x = 10f;
                    y = f;
                    mirror = true;
                    radius = 10f;
                    phase = 90f;
                    stroke = 2f;
                    layerOffset = -0.001f;
                    color = Pal2.attackRed;
                }});
            }

            weapons.add(new Weapon("disintegration-coverture-weapon"){{
                range = 50f;
                rotateSpeed = 6f;
                y = 0f;
                x = 0f;
                shootY = 12f;
                top = true;
                mirror = false;
                reload = 50f;
                rotate = true;

                bullet = new BasicBulletType(5, 100){{
                    sprite = "missile-large";
                    smokeEffect = Fx.shootBigSmoke;
                    shootEffect = Fx.shootBigColor;
                    width = 15f;
                    height = 17f;
                    lifetime = 40f;
                    hitSize = 4f;
                    hitColor = backColor = trailColor = Pal2.attackRed;
                    frontColor = Color.white;
                    trailWidth = 1.7f;
                    trailLength = 5;
                    fragBullets = 1;
                    fragRandomSpread = 0f;
                    fragBullet = new ShrapnelBulletType(){{
                        lifetime = 20f;
                        damage = 30f;
                        toColor = Pal2.attackRed;
                    }};
                    despawnEffect = hitEffect = Fx.hitBulletColor;
                }};
            }});
        }});
        attack = EntityRegistry.content("attack", ElevationMoveUnit.class, name -> new OmurloUnitType(name){{
            hovering = true;
            shadowElevation = 0.1f;

            drag = 0.07f;
            speed = 1.8f;
            rotateSpeed = 4f;

            accel = 0.09f;
            health = 600f;
            armor = 1f;
            hitSize = 24f;
            engineOffset = 12f;
            engineSize = 2f;
            itemCapacity = 0;
            useEngineElevation = false;

            abilities.add(new MoveEffectAbility(0f, -12f, Pal2.attackRed, Fx.missileTrailShort, 4f){{
                teamColor = true;
            }});

            for(float f : new float[]{-14f, 0f, 14f}){
                parts.add(new HoverPart(){{
                    x = 14f;
                    y = f;
                    mirror = true;
                    radius = 10f;
                    phase = 90f;
                    stroke = 2f;
                    layerOffset = -0.001f;
                    color = Pal2.attackRed;
                }});
            }

            weapons.add(new Weapon("disintegration-attack-weapon"){{
                range = 50f;
                rotateSpeed = 6f;
                y = 0f;
                x = 0f;
                shoot = new ShootBarrel(){{
                    barrels = new float[]{
                            0f,8f,0f,
                            -8f,8f,0f,
                            8f,8f,0f
                    };
                }};
                top = true;
                mirror = false;
                reload = 30f;
                rotate = true;

                bullet = new MissileBulletType(5, 100){{
                    smokeEffect = Fx.shootBigSmoke;
                    shootEffect = Fx.shootBigColor;
                    width = 15f;
                    height = 17f;
                    lifetime = 40f;
                    hitSize = 4f;
                    hitColor = backColor = trailColor = Pal2.attackRed;
                    frontColor = Color.white;
                    trailWidth = 1.7f;
                    trailLength = 5;
                    despawnEffect = hitEffect = Fx.blastExplosion;
                }};
            }});
        }});
        devastate = EntityRegistry.content("devastate", ElevationMoveUnit.class, name -> new OmurloUnitType(name){{
            hovering = true;
            shadowElevation = 0.1f;

            drag = 0.07f;
            speed = 1.8f;
            rotateSpeed = 2f;

            accel = 0.09f;
            health = 600f;
            armor = 1f;
            hitSize = 36f;
            engineOffset = 17f;
            engineSize = 2f;
            itemCapacity = 0;
            useEngineElevation = false;
            researchCostMultiplier = 0f;

            abilities.add(new MoveEffectAbility(0f, -17f, Pal2.attackRed, Fx.missileTrailShort, 4f){{
                teamColor = true;
            }});

            for(float f : new float[]{-14f, 0f, 14f}){
                parts.add(new HoverPart(){{
                    x = 12f;
                    y = f;
                    mirror = true;
                    radius = 12f;
                    phase = 90f;
                    stroke = 2f;
                    layerOffset = -0.001f;
                    color = Pal2.attackRed;
                }});
            }

            weapons.add(new Weapon("disintegration-devastate-weapon"){{
                range = 80f;
                rotateSpeed = 2f;
                y = 0f;
                x = 0f;
                shootY = 12f;
                top = true;
                mirror = false;
                reload = 660f;
                rotate = true;
                parentizeEffects = true;
                shootSound = Sounds.beam;
                continuous = true;

                bullet = new ContinuousLaserBulletType(100){{
                    length = 200f;
                    drawSize = 420f;
                    lifetime = 300f;
                    colors = new Color[]{Pal2.attackRed.cpy().a(0.4f), Pal2.attackRed, Color.white};
                    hitEffect = Fx.hitMeltdown;
                    hitColor = Pal.meltdownHit;
                    status = StatusEffects.melting;
                }};
            }});
        }});
        //endregion
        //region core
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
        //endregion
        //region special
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
        }});
        //endregion
    }
}
