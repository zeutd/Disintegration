package disintegration.entities.abilities;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.input.InputHandler;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;

public class PortableBlockAbility extends Ability {
    public Block unitContent;

    public Effect placeEffect;

    public Sound placeSound;

    @Override
    public void update(Unit u) {
        if ((u.isPlayer() && u.isShooting) || (u.isCommandable() && u.command().targetPos != null && u.within(u.command().targetPos, tilesize))) {
            Tile tile = Vars.world.tile(u.tileX(), u.tileY());
            if (tile != null && tile.block() == Blocks.air && Build.validPlace(unitContent, u.team, u.tileX(), u.tileY(), 0)) {
                u.remove();
                tile.setBlock(unitContent, u.team);
                placeEffect.at(u.tileX() * tilesize, u.tileY() * tilesize, 0f);
                placeSound.at(u.x, u.y);
                if (u.getPlayer() != null) InputHandler.unitClear(u.getPlayer());
            }
        }
    }

    @Override
    public void draw(Unit unit){
        Draw.rect(unit.type.fullIcon, unit.x, unit.y);
    }
}
