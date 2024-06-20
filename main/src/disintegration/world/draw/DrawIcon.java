package disintegration.world.draw;

import arc.graphics.Pixmap;
import arc.graphics.g2d.PixmapRegion;
import arc.graphics.g2d.TextureRegion;
import mindustry.game.Team;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

import static arc.Core.atlas;

public class DrawIcon extends DrawBlock {
    public TextureRegion region;
    public PixmapRegion teamRegion;
    public Pixmap teamIcon;

    public String suffix;

    public boolean drawTeam;

    public DrawIcon(String suffix) {
        this.suffix = suffix;
    }

    public DrawIcon(String suffix, boolean drawTeam) {
        this.suffix = suffix;
        this.drawTeam = drawTeam;
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return new TextureRegion[]{region};
    }

    @Override
    public void load(Block block) {
        if (suffix != null) {
            region = atlas.find(block.name + suffix);
        }
        if (drawTeam) {
            teamRegion = atlas.getPixmap(block.teamRegion);
            teamIcon = new Pixmap(teamRegion.width, teamRegion.height);
            for (int x = 0; x < teamRegion.width; x++) {
                for (int y = 0; y < teamRegion.height; y++) {
                    int color = teamRegion.getRaw(x, y);
                    int index = color == 0xffffffff ? 0 : color == 0xdcc6c6ff ? 1 : color == 0x9d7f7fff ? 2 : -1;
                    teamIcon.setRaw(x, y, index == -1 ? teamRegion.getRaw(x, y) : Team.sharded.palettei[index]);
                }
            }
            region.texture.draw(teamIcon);
        }
    }
}
