package disintegration.content;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import disintegration.ai.types.RepairDroneAI;
import disintegration.entities.abilities.DTArmorPlateAbility;
import disintegration.gen.entities.EntityRegistry;
import disintegration.gen.entities.PayloadBuildingTetherUnit;
import disintegration.gen.entities.WorldUnit;
import disintegration.gen.entities.Worldc;
import disintegration.graphics.Pal2;
import disintegration.type.unit.WorldUnitType;
import ent.anno.Annotations.EntityDef;
import mindustry.ai.types.BuilderAI;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BombBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.EmpBulletType;
import mindustry.entities.bullet.LaserBoltBulletType;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.RegionPart;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.PowerAmmoType;
import mindustry.type.unit.ErekirUnitType;
import mindustry.type.unit.NeoplasmUnitType;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.world.meta.Env;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.Vars.tilesize;

public class DTUnitTypes {
    public static DrawPart.PartProgress time = p -> Time.time;
    public static DrawPart.PartProgress timeSin = p -> Mathf.absin( 20f, 1f);
    public static @EntityDef({Unitc.class}) UnitType lancet, raven, essence, truth, solve, knife, separate, spaceStationDrone, flyingNeoplasmSquid;
    public static @EntityDef({Unitc.class, BuildingTetherc.class, Payloadc.class}) UnitType repairDrone;
    public static @EntityDef({Unitc.class, Worldc.class}) UnitType physics;
    public static void load(){
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

        lancet = EntityRegistry.content("lancet", UnitEntity.class, name -> new UnitType(name){{
            speed = 2.7f;
            accel = 0.08f;
            drag = 0.04f;
            armor = 2;
            flying = true;
            health = 200;
            engineOffset = 12;
            engineSize = 3.7f;
            hitSize = 11;
            weapons.add(new Weapon(){{
                    y = 10f;
                    x = 0f;
                    reload = 140f;
                    ejectEffect = Fx.hitLancer;
                    bullet = new EmpBulletType(){{
                            float rad = 48f;
                            recoil = 1;
                            shootSound = Sounds.laser;
                            scaleLife = true;
                            lightOpacity = 0.7f;
                            timeIncrease = 1f;
                            timeDuration = 60f * 20f;
                            powerDamageScl = 1f;
                            damage = 9;
                            hitColor = lightColor = Pal2.hyperBlue;
                            lightRadius = 40f;
                            clipSize = 250f;
                            shootEffect = Fx.hitLancer;
                            smokeEffect = Fx.shootBigSmoke2;
                            lifetime = 60f;
                            sprite = "circle-bullet";
                            backColor = Pal2.hyperBlue;
                            frontColor = Color.white;
                            width = height = 9f;
                            shrinkY = 0f;
                            speed = 3f;
                            trailLength = 10;
                            trailWidth = 4.5f;
                            trailColor = Pal2.hyperBlue;
                            trailInterval = 5f;
                            splashDamage = 8f;
                            splashDamageRadius = rad;
                            radius = rad;
                            hitShake = 4f;
                            trailRotation = true;
                            status = DTStatusEffects.electricResonated;
                            hitSound = Sounds.plasmaboom;
                            hitPowerEffect = Fx.hitLancer;
                            chainEffect = Fx.chainLightning;

                            trailEffect = new Effect(16f, e -> {
                                color(Pal2.hyperBlue);
                                for(int s : Mathf.signs){
                                    Drawf.tri(e.x, e.y, 4f, 10f * e.fslope(), e.rotation + 90f*s);
                                }
                            });

                            hitEffect = new Effect(50f, 100f, e -> {
                                e.scaled(7f, b -> {
                                    color(Pal2.hyperBlue, b.fout());
                                    Fill.circle(e.x, e.y, rad);
                                });

                                color(Pal2.hyperBlue);
                                stroke(e.fout() * 3f);
                                Lines.circle(e.x, e.y, rad);

                                int points = 10;
                                float offset = Mathf.randomSeed(e.id, 360f);
                                for(int i = 0; i < points; i++){
                                    float angle = i* 360f / points + offset;
                                    //for(int s : Mathf.zeroOne){
                                    Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 6f, 15f * e.fout(), angle/* + s*180f*/);
                                    //}
                                }

                                Fill.circle(e.x, e.y, 12f * e.fout());
                                color();
                                Fill.circle(e.x, e.y, 6f * e.fout());
                                Drawf.light(e.x, e.y, rad * 1.6f, Pal2.hyperBlue, e.fout());
                            });
                    }};
                }});
            abilities.add(new DTArmorPlateAbility(){});
        }});
        separate = EntityRegistry.content("separate", UnitEntity.class, name -> new UnitType(name){{
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
            weapons.add(new Weapon("disintegration-laser-bolt-mount"){{
                reload = 17f;
                x = 2f;
                y = 0f;
                top = false;
                shootSound = Sounds.lasershoot;
                bullet = new LaserBoltBulletType(2.5f, 10){{
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
        spaceStationDrone = EntityRegistry.content("space-station-drone", UnitEntity.class, name -> new UnitType(name){{
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
                    new RegionPart("-side"){{
                        moveRot = -20f;
                        progress = PartProgress.warmup;
                        mirror = true;
                    }}
            );
            weapons.add(new RepairBeamWeapon(){{
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

                bullet = new BulletType(){{
                    maxRange = 60f;
                }};
            }});
        }});
        //special
        repairDrone = EntityRegistry.content("repair-drone", PayloadBuildingTetherUnit.class, name -> new ErekirUnitType(name){{
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
            weapons.add(new Weapon(){{
                reload = 10f;
                x = 0;
                mirror = false;
                ejectEffect = Fx.none;
                shootSound = Sounds.lasershoot;
                range = 90f;

                bullet = new LaserBoltBulletType(4f, 1){{
                    lifetime = 23f;
                    healPercent = 1f;
                    collidesTeam = true;
                    backColor = Pal.heal;
                    frontColor = Color.white;
                    recoil = 0.5f;
                }};
            }});
        }});
        flyingNeoplasmSquid = EntityRegistry.content("flying-neoplasm-squid", UnitEntity.class, name -> new NeoplasmUnitType(name){{
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
            parts.add(new RegionPart("-tentacle"){{
                moves.add(new PartMove(p -> Mathf.absin( 20f, 1f), 0, 0, 20));
                x = -4f;
                y = 0.5f;
                rotation = -10;
                mirror = true;
            }});
            weapons.add(new Weapon(){{
                minShootVelocity = 0.5f;
                x = 0f;
                shootY = 0f;
                reload = 50f;
                shootCone = 180f;
                ejectEffect = Fx.none;
                inaccuracy = 15f;
                ignoreRotation = true;

                shootSound = Sounds.none;
                bullet = new BombBulletType(27f, 25f){{
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
        physics = EntityRegistry.content("physics", WorldUnit.class, name -> new WorldUnitType(name){{
            speed = 1.5f;
            accel = 0.03f;
            drag = 0.06f;
            flying = true;
            rotateSpeed = 1f;
            health = 70;
            worldWidth = 10;
            worldHeight = 10;
        }});
    }
}
