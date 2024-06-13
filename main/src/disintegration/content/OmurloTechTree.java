package disintegration.content;

import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.game.Objectives;

import static mindustry.content.TechTree.*;
import static disintegration.content.DTBlocks.*;
import static disintegration.content.DTUnitTypes.*;
import static disintegration.content.DTItems.*;
import static mindustry.content.Items.*;
import static mindustry.content.Blocks.*;

public class OmurloTechTree {

    public static void load(){
        DTPlanets.omurlo.techTree = nodeRoot("omurlo", corePedestal, () -> {
            node(ironConveyor, () -> {
                node(ironJunction, () -> {
                    node(ironRouter, () -> {
                        node(ironSorter, () -> {
                            node(invertedIronSorter);
                            node(alternateOverflowGate);
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
            node(corePillar);
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
                                    node(magnetismAlloySmelter);
                                });
                                node(melter, () -> {
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
                    });
                    node(ventTurbine, () -> {
                        node(ironPowerNode, () -> {
                            node(ironPowerNodeLarge);
                        });
                        node(powerCapacitor, () -> {

                        });
                        node(repairer);
                        node(turbineGenerator, Seq.with(new Objectives.Research(boiler)), () -> {});
                        node(solarPanel, () -> {
                            node(rotateSolarPanel);
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
                                node(projectorWall);
                            });
                        });
                        node(iridiumWall, () -> {
                            node(iridiumWallLarge);
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
                        node(regeneration);
                        node(focus, () -> {
                            node(voltage);
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
                                node(celestial);
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
                node(additiveRefabricator, () -> {
                    node(multiplicativeRefabricator, () -> {
                        node(exponentialRefabricatingPlatform, () -> {
                            node(tetrativeRefabricatingPlatform);
                        });
                    });
                });
            });
            node(iron, () -> {
                node(coal, () -> {
                    node(graphite);
                    node(blastCompound);
                    node(Items.sand, () -> {
                        node(silicon);
                        node(scrap, () -> {
                            node(Liquids.slag);
                        });
                    });
                });
                node(lead, () -> {
                    node(metaglass);
                    node(steel);
                    node(silver, () -> {
                        node(conductionAlloy, () -> {
                            node(iridium, () -> {
                                node(magnetismAlloy);
                                node(surgeAlloy);
                            });
                        });
                    });
                });
                node(Liquids.water, () -> {
                    node(DTLiquids.algalWater);
                });
            });
        });
    }
}
