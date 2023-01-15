package disintegration.entities.bullet;

import arc.graphics.g2d.Draw;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.Tile;

import static mindustry.Vars.world;

public class BlockBulletType extends ArtilleryBulletType {
    public Block bulletContent;

    public boolean rotateSprite;

    public Effect bulletBuildEffect = Fx.mine;

    public BlockBulletType(){
        this(Blocks.air);
    }
    public BlockBulletType(Block bulletContent){
        super();
        this.bulletContent = bulletContent;
        width = height = bulletContent.size * 8;
    }

    @Override
    public void draw(Bullet b){
        drawParts(b);
        drawTrail(b);
        Draw.z(Layer.bullet - 0.1f);
        float shrink = shrinkInterp.apply(b.fout());
        float height = this.height * ((1f - shrinkY) + shrinkY * shrink);
        float width = this.width * ((1f - shrinkX) + shrinkX * shrink);
        Draw.rect(bulletContent.uiIcon, b.x, b.y, width, height, rotateSprite ? b.rotation() : 0);
    }

    @Override
    public void despawned(Bullet b){
        Tile tile = world.tile(b.tileX(), b.tileY());
        if (tile != null && Build.validPlace(bulletContent, b.team, tile.x, tile.y, 0, true)) {
            tile.setBlock(bulletContent, b.team);
            bulletBuildEffect.at(tile.x, tile.y, 0, bulletContent);
        }
    }
}
