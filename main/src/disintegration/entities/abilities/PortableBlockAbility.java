package disintegration.entities.abilities;

import arc.audio.Sound;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.input.InputHandler;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;

public class PortableBlockAbility extends Ability {
    public Block unitContent;

    public Effect placeEffect;

    public Sound placeSound;
    @Override
    public void addStats(Table table){}

    @Override
    public void update(Unit u){
        if((u.isPlayer() && u.isShooting) || (u.isCommandable() && u.command().targetPos != null && u.within(u.command().targetPos, tilesize))){
            Tile tile = Vars.world.tile(u.tileX(), u.tileY());
            if(tile != null && tile.block() == Blocks.air && unitContent.canPlaceOn(tile, u.team, 0)) {
                u.remove();
                tile.setBlock(unitContent, u.team);
                placeEffect.at(u.tileX() * tilesize, u.tileY() * tilesize, 0f);
                placeSound.at(u.x, u.y);
                if(u.getPlayer() != null)InputHandler.unitClear(u.getPlayer());
            }
        }
    }
}
