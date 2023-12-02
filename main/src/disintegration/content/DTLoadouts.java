package disintegration.content;

import mindustry.game.Schematic;
import mindustry.game.Schematics;

public class DTLoadouts {
    public static Schematic
    basicSpacestations,
    basicPedestal;

    public static void load(){
        basicSpacestations = Schematics.readBase64("bXNjaAF4nCWLQQrAIAwEVy299BV9gC8qPaQaJGBVjP+nStm5DMPCwTlshV6GzRFHZA1d2pBaAOyZHs4Ke90GZxSVMjh1Wtlro8Bex2+hdp4Ps5j7ACLBF6s=");
    }
}
