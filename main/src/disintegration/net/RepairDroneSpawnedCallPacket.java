package disintegration.net;

import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.world.blocks.defence.RepairDroneStation;
import mindustry.io.TypeIO;
import mindustry.net.Packet;
import mindustry.world.Tile;

public class RepairDroneSpawnedCallPacket extends Packet {
    private byte[] DATA;
    public Tile tile;
    public int id;

    public RepairDroneSpawnedCallPacket() {
        this.DATA = NODATA;
    }

    public void write(Writes WRITE) {
        TypeIO.writeTile(WRITE, this.tile);
        WRITE.i(this.id);
    }

    public void read(Reads READ, int LENGTH) {
        this.DATA = READ.b(LENGTH);
    }

    public void handled() {
        BAIS.setBytes(this.DATA);
        this.tile = TypeIO.readTile(READ);
        this.id = READ.i();
    }

    public void handleClient() {
        RepairDroneStation.repairDroneSpawned(this.tile, this.id);
    }
}
