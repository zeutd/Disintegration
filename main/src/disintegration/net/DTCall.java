package disintegration.net;

import disintegration.world.blocks.defence.RepairDroneStation;
import disintegration.world.blocks.units.ReconstructPlatform;
import mindustry.Vars;
import mindustry.world.Tile;

public class DTCall {
    public static void repairDroneSpawned(Tile tile, int id) {
        if (Vars.net.server() || !Vars.net.active()) {
            RepairDroneStation.repairDroneSpawned(tile, id);
        }

        if (Vars.net.server()) {
            RepairDroneSpawnedCallPacket packet = new RepairDroneSpawnedCallPacket();
            packet.tile = tile;
            packet.id = id;
            Vars.net.send(packet, true);
        }

    }

    public static void reconstructDroneSpawned(Tile tile, int id) {
        if (Vars.net.server() || !Vars.net.active()) {
            ReconstructPlatform.reconstructDroneSpawned(tile, id);
        }

        if (Vars.net.server()) {
            ReconstructDroneSpawnedCallPacket packet = new ReconstructDroneSpawnedCallPacket();
            packet.tile = tile;
            packet.id = id;
            Vars.net.send(packet, true);
        }

    }
}
