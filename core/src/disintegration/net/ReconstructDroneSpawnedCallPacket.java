package disintegration.net;

import arc.util.io.Reads;
import arc.util.io.Writes;
import disintegration.world.blocks.units.ReconstructPlatform;
import mindustry.io.TypeIO;
import mindustry.net.Packet;
import mindustry.world.Tile;

public class ReconstructDroneSpawnedCallPacket extends Packet {
    private byte[] DATA;
    public Tile tile;
    public int id;

    public ReconstructDroneSpawnedCallPacket() {
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
        ReconstructPlatform.reconstructDroneSpawned(this.tile, this.id);
    }
}
