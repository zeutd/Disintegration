package degradation.world.meta;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

/** Describes one type of stat for content. */
public class DTStat{
    public static final Stat
            maxLinks = new Stat("powerConnections", StatCat.function),
            shardChance = new Stat("shardChance"),
            shardDamage = new Stat("shardDamage");
}
