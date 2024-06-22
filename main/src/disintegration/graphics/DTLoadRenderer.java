package disintegration.graphics;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.util.OS;
import mindustry.graphics.Layer;

import static arc.Core.assets;

public class DTLoadRenderer {
    private int lastLength = -1;
    public StringBuilder assetText = new StringBuilder();

    public void draw() {
        Draw.z(Layer.max);
        if (assets.getLoadedAssets() != lastLength) {
            assetText.setLength(0);
            for (String name : assets.getAssetNames()) {
                assetText
                        .append(name.replace(OS.username, "<<host>>").replace("/", "::")).append("::[]")
                        .append(assets.getAssetType(name).getSimpleName()).append("\n");
            }

            lastLength = assets.getLoadedAssets();
        }
        if (assets.isLoaded("tech")) {
            Font font = assets.get("tech");
            font.getData().setScale(0.5f);
            font.draw(assetText, 0, Core.camera.height * 4);
        }
        Draw.flush();
    }
}
