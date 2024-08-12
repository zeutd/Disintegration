package disintegration.content;

import arc.struct.Seq;
import arc.util.Log;
import disintegration.util.DTUtil;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;

import static disintegration.content.DTBlocks.*;
import static mindustry.content.TechTree.node;
import static mindustry.content.TechTree.nodeRoot;

public class SpaceStationTechTree {
    public static void load() {
        DTPlanets.omurlo.techTree = nodeRoot("space-station", spaceStationCore, () -> {
            node(orbitalLaunchPad, () -> {
                node(spaceLaunchPad, () -> {
                    node(interplanetaryLaunchPad);
                });
            });
            node(spaceStationBuilders.get(0), () -> {
                node(spaceStationBuilders.get(1), () -> {
                    node(spaceStationBuilders.get(2), () -> {
                        node(spaceStationBuilders.get(3), () -> {
                            node(spaceStationBuilders.get(4));
                        });
                    });
                });
            });
            node(spaceStationBreakers.get(0), () -> {
                node(spaceStationBreakers.get(1), () -> {
                    node(spaceStationBreakers.get(2), () -> {
                        node(spaceStationBreakers.get(3), () -> {
                            node(spaceStationBreakers.get(4));
                        });
                    });
                });
            });
        });
        DTPlanets.omurlo.techTree.objectives = Seq.with(new Objectives.Research(spaceStationLaunchPad));
    }
}
