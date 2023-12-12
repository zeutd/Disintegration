package disintegration.entities;

import disintegration.gen.entities.BlackHolec;
import mindustry.entities.EntityGroup;

@SuppressWarnings("unchecked")
public class DTGroups {
    public static EntityGroup<BlackHolec> blackHole;

    public static void clear(){
        blackHole.clear();
    }

    public static void init(){
        blackHole = new EntityGroup(BlackHolec.class, true, true, (e, pos) -> {});
    }

    public static void update(){

    }
}
