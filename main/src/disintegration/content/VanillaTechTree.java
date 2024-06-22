package disintegration.content;

import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Planets;

import static disintegration.util.DTUtil.addTechNode;

public class VanillaTechTree {
    public static void load() {
        addTechNode(Planets.serpulo.techTree, Blocks.payloadRouter, DTBlocks.payloadLoader);
        addTechNode(Planets.serpulo.techTree, DTBlocks.payloadLoader, DTBlocks.payloadUnloader);
        addTechNode(Planets.serpulo.techTree, DTBlocks.payloadLoader, DTBlocks.payloadConstructor);
        addTechNode(Planets.serpulo.techTree, DTBlocks.payloadConstructor, DTBlocks.largePayloadConstructor);
        addTechNode(Planets.serpulo.techTree, DTBlocks.payloadConstructor, DTBlocks.payloadDeconstructor);
        addTechNode(Planets.serpulo.techTree, Blocks.graphitePress, DTBlocks.spaceStationPanelCompressor);
        addTechNode(Planets.serpulo.techTree, Blocks.graphitePress, DTBlocks.spaceStationPanelCompressorLarge);
        addTechNode(Planets.serpulo.techTree, Items.titanium, DTItems.spaceStationPanel);
        addTechNode(Planets.serpulo.techTree, Blocks.launchPad, DTBlocks.spaceStationLaunchPad);
        addTechNode(Planets.serpulo.techTree, DTBlocks.spaceStationLaunchPad, DTBlocks.spaceLaunchPad);
        addTechNode(Planets.serpulo.techTree, DTBlocks.spaceStationLaunchPad, DTBlocks.orbitalLaunchPad);
        addTechNode(Planets.serpulo.techTree, DTBlocks.spaceLaunchPad, DTBlocks.interplanetaryLaunchPad);

        addTechNode(Planets.erekir.techTree, Blocks.carbideWallLarge, DTBlocks.nitrideWall);
        addTechNode(Planets.erekir.techTree, DTBlocks.nitrideWall, DTBlocks.nitrideWallLarge);
        addTechNode(Planets.erekir.techTree, Blocks.breach, DTBlocks.permeation);
        addTechNode(Planets.erekir.techTree, DTBlocks.permeation, DTBlocks.aegis);
        addTechNode(Planets.erekir.techTree, Blocks.diffuse, DTBlocks.encourage);
        addTechNode(Planets.erekir.techTree, DTBlocks.encourage, DTBlocks.blade);
        addTechNode(Planets.erekir.techTree, DTBlocks.blade, DTBlocks.fracture);
        addTechNode(Planets.erekir.techTree, DTBlocks.aegis, DTBlocks.dissolve);
        addTechNode(Planets.erekir.techTree, Blocks.carbideCrucible, DTBlocks.nitrideSynthesizer);
        addTechNode(Planets.erekir.techTree, Items.carbide, DTItems.nitride);
    }
}
