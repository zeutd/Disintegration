package disintegration.content;

import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.game.Objectives;

import static disintegration.content.DTBlocks.*;
import static disintegration.content.DTItems.*;
import static disintegration.content.DTUnitTypes.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.*;
import static mindustry.content.TechTree.*;

public class OmurloTechTree {

    public static void load() {
        DTPlanets.omurlo.techTree = nodeRoot("omurlo", corePedestal, () -> {
            node(ironConveyor, () -> {
                node(ironJunction, () -> {
                    node(ironRouter, () -> {
                        node(ironSorter, () -> {
                            node(invertedIronSorter);
                            node(alternateOverflowGate, () -> {
                                node(unloader);
                            });
                        });
                        node(ironItemBridge, () -> {
                            node(iridiumConveyor, () -> {
                                node(payloadConveyor, () -> {
                                    node(payloadRouter, () -> {
                                        node(DTBlocks.payloadLoader, () -> {
                                            node(DTBlocks.payloadUnloader);
                                            node(payloadConstructor, () -> {
                                                node(largePayloadConstructor);
                                                node(payloadDeconstructor);
                                            });
                                        });
                                    });
                                    node(payloadTeleporter);
                                    node(payloadAccelerator, () -> {
                                        node(payloadDecelerator);
                                        node(magnetizedPayloadRail, () -> {
                                            node(magnetizedPayloadRailShort);
                                            node(payloadForkLeft);
                                            node(payloadForkRight, () -> {
                                                node(payloadSeparator);
                                                node(payloadCross);
                                            });
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
            node(corePillar, () -> {
                node(coreAltar);
            });
            node(pressureDrill, () -> {
                node(graphiteCompressor, () -> {
                    node(rockExtractor, () -> {
                        node(siliconRefiner, () -> {
                            node(kiln, () -> {
                                node(burningHeater, () -> {
                                    node(heatConduit, () -> {
                                        node(thermalHeater);
                                        node(boiler);
                                    });
                                });
                                node(steelSmelter, () -> {
                                    node(steelBlastFurnace);
                                });
                                node(conductionAlloySmelter, () -> {
                                    node(airCompressor);
                                    node(magnetismAlloySmelter);
                                });
                                node(melter, () -> {
                                    node(centrifuge);
                                    node(surgeFurnace, () -> {

                                    });
                                });
                            });
                        });
                    });
                    node(stiffDrill, () -> {
                        node(electrolyser);
                        node(algalPond, () -> {
                            node(blastCompoundMixer);
                        });
                        node(cuttingDrill);
                        node(quarry);
                    });
                });
                node(ventTurbine, () -> {
                    node(ironPowerNode, () -> {
                        node(ironPowerNodeLarge, () -> {
                            node(powerDriver);
                        });
                    });
                    node(powerCapacitor, () -> {

                    });
                    node(repairer);
                    node(turbineGenerator, Seq.with(new Objectives.Research(boiler)), () -> {
                        node(arcReactor, () -> {
                            node(tokamakFusionReactor);
                        });
                    });
                    node(solarPanel, () -> {
                        node(rotateSolarPanel);
                    });
                });
                node(gearPump, () -> {
                    node(conduit, () -> {
                        node(liquidJunction, () -> {
                            node(liquidRouter, () -> {
                                node(bridgeConduit, () -> {
                                    node(magnetizedConduit, () -> {
                                        node(centrifugalPump);
                                    });
                                    node(liquidContainer, () -> {
                                        node(liquidTank);
                                        node(liquidCellPacker, () -> {
                                            node(liquidCellUnpacker);
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
            node(shard, () -> {
                node(ironWall, () -> {
                    node(ironWallLarge, () -> {
                        node(steelWall, () -> {
                            node(steelWallLarge);
                        });
                        node(conductionAlloyWall, () -> {
                            node(conductionAlloyWallLarge, () -> {
                                node(projectorWall, () -> {
                                    node(circleForceProjector);
                                });
                            });
                        });
                        node(iridiumWall, () -> {
                            node(iridiumWallLarge, () -> {
                                node(surgeWall, () -> {
                                    node(surgeWallLarge);
                                });
                            });
                        });
                    });
                });
                node(aerolite, () -> {
                    node(axe, () -> {
                        node(torch, () -> {
                            node(blaze);
                            node(condense);
                        });
                    });
                });
                node(twist, () -> {
                    node(awake, () -> {
                        node(regeneration, () -> {
                            node(holy);
                        });
                        node(focus, () -> {
                            node(laserDefenceTower);
                            node(voltage, () -> {
                                node(franklinism, () -> {
                                    node(amperage);
                                });
                            });
                            node(sparkover);
                        });
                    });
                });
            });
            node(unitFabricator, () -> {
                node(verity, () -> {
                    node(truth, () -> {
                        node(solve, () -> {
                            node(essence, () -> {
                                node(axiom);
                            });
                        });
                    });
                });
                node(lancet, () -> {
                    node(talwar, () -> {
                        node(estoc, () -> {
                            node(spear, () -> {
                                node(epee);
                            });
                        });
                    });
                });
                node(assist, () -> {
                    node(strike, () -> {
                        node(coverture, () -> {
                            node(attack, () -> {
                                node(devastate);
                            });
                        });
                    });
                });
                node(colibri, () -> {
                    node(albatross, () -> {
                        node(crane, () -> {
                            node(eagle, () -> {
                                node(phoenix);
                            });
                        });
                    });
                });
                node(converge, () -> {
                    node(cover, () -> {
                        node(protect, () -> {
                            node(defend, () -> {
                                node(harbour);
                            });
                        });
                    });
                });
                node(additiveRefabricator, () -> {
                    node(multiplicativeRefabricator, () -> {
                        node(exponentialRefabricatingPlatform, () -> {
                            node(tetrativeRefabricatingPlatform);
                        });
                    });
                });
            });
            nodeProduce(iron, () -> {
                nodeProduce(coal, () -> {
                    nodeProduce(graphite, () -> {});
                    nodeProduce(blastCompound, () -> {});
                    nodeProduce(Items.sand, () -> {
                        nodeProduce(silicon, () -> {});
                        nodeProduce(scrap, () -> {
                            nodeProduce(Liquids.slag, () -> {});
                        });
                    });
                });
                nodeProduce(lead, () -> {
                    nodeProduce(metaglass, () -> {});
                    nodeProduce(steel, () -> {});
                    nodeProduce(silver, () -> {
                        nodeProduce(conductionAlloy, () -> {
                            nodeProduce(iridium, () -> {
                                nodeProduce(magnetismAlloy, () -> {});
                                nodeProduce(surgeAlloy, () -> {});
                            });
                        });
                    });
                });
                nodeProduce(Liquids.water, () -> {
                    nodeProduce(DTLiquids.steam, () -> {});
                    nodeProduce(Liquids.hydrogen, () -> {});
                    nodeProduce(DTLiquids.oxygen, () -> {});
                    nodeProduce(DTLiquids.algalWater, () -> {});
                });
            });
            node(DTSectorPresets.landingArea);
        });
    }
}
