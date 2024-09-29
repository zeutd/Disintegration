package disintegration.content;

import mindustry.game.Schematic;
import mindustry.game.Schematics;

public class DTLoadouts {
    public static Schematic
            basicSpacestations,
            basicPedestal,
            basicPillar,
            basicAltar
    ;

    public static void load() {
        basicPedestal = Schematics.readBase64("bXNjaAF4nDWLQQqAIBBFvxltoht0Be/SPlqMOsSAqTjen3IR/8FbPD4srMWc6WFsnlTCwZG1U8L6OTSpXUoGsCTynBTTeRnsUVRy57vRyC6Uxq7+T8AMvr0CKxos");
        basicPillar = Schematics.readBase64("bXNjaAF4nCWLwQqAIBAFnyVd/IP+wX/pHh1WXWLBVFz/n5KYuQ0DC7vCFnoYLpBKPCRn6nCJNXZpQ2oBsGUKnBXLeRnsSVTK4LvTzD7Wzr79H2CmHy+CDhiO");
        basicAltar = Schematics.readBase64("bXNjaAF4nB3Lyw2AIBBF0QeKC63AIijCOoyLASZmEgQz0H/85C5PLhzcgLHQxZgDNYlb7qRYEreocnepBcCUKXBusPthsCZpUjqfSh/7WJU9/RtgAPtmHkGeF7A=");
        basicSpacestations = Schematics.readBase64("bXNjaAF4nCWLQQrAIAwEVy299BV9gC8qPaQaJGBVjP+nStm5DMPCwTlshV6GzRFHZA1d2pBaAOyZHs4Ke90GZxSVMjh1Wtlro8Bex2+hdp4Ps5j7ACLBF6s=");
    }
}
