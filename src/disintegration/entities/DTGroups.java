package disintegration.entities;

import mindustry.entities.EntityGroup;

@SuppressWarnings("all")
public class DTGroups {
    public static EntityGroup<BlackHole> blackHole;

    public static void clear(){
        blackHole.clear();
    }

    public static void init(){
        blackHole = new EntityGroup(BlackHole.class, false, false, (e, pos) -> {});
    }

    public static void update(){
        blackHole.update();
    }
}
