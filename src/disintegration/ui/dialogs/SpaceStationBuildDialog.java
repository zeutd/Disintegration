/*
package disintegration.ui.dialogs;

import arc.struct.ObjectMap;
import disintegration.DTVars;
import disintegration.content.DTItems;
import disintegration.content.DTLoadouts;
import disintegration.type.SpaceStation;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.game.Schematic;
import mindustry.gen.Icon;
import mindustry.type.Item;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SchematicsDialog;

import static arc.Core.bundle;
import static disintegration.DTVars.DTUI;
import static mindustry.Vars.iconSmall;

public class SpaceStationBuildDialog extends BaseDialog {

    public Schematic sch = DTLoadouts.basicSpacestations;

    public int itemNum;

    boolean valid;


    ObjectMap<Sector, Integer> itemCache = new ObjectMap<>();
    Item item = DTItems.spaceStationPanel;

    public SpaceStationBuildDialog() {
        super(bundle.get("spacestation", "Space Stations Menu"));
        shown(this::setup);
    }

    void setup(){
        rebuildItems();
        valid = itemNum >= DTVars.spaceStationRequirement;
        cont.clear();
        buttons.clear();

        addCloseButton();
        cont.table(t -> {
            t.add(new SchematicsDialog.SchematicImage(sch));
            t.row();
            t.table(pt -> {
                pt.image(item.uiIcon).left().size(iconSmall);
                pt.add((itemNum >= DTVars.spaceStationRequirement ? "" : "[scarlet]") + (Math.min(DTVars.spaceStationRequirement, itemNum) + "[lightgray]/" + DTVars.spaceStationRequirement));
            }).left();
        });
        buttons.button("@launch.text", Icon.ok, () -> {
            hide();
            DTUI.spaceStation.hide();
            Vars.ui.planet.show();
            Planet p = DTUI.spaceStation.selectedPlanet;
            removeItem(DTVars.spaceStationRequirement);
            DTVars.spaceStationPlanets.add(p);
            Planet s = Vars.content.getByName(ContentType.planet, p.name + "-space-station");
            if(s != null){
                s.accessible = true;
                s.visible = true;
                DTVars.spaceStations.add((SpaceStation) s);
                return;
            }
            DTVars.spaceStations.add(new SpaceStation(p.name + "-space-station", p));
        }).disabled(b -> !valid);
        cont.row();
        cont.add("@sector.missingresources").visible(() -> !valid);
    }

    public void rebuildItems(){
        itemNum = 0;
        for (Planet p : Vars.content.planets()) {
            for (Sector sector : p.sectors) {
                if (sector.hasBase()) {
                    int cached = sector.items().get(item);
                    itemCache.put(sector, cached);
                    itemNum += cached;
                }
            }
        }
    }
    public void removeItem(int amount){
        int left = amount;
        for (Planet p : Vars.content.planets()) {
            for (Sector sec : p.sectors) {
                if(left <= 0) break;
                int remove = Math.min(sec.items().get(DTItems.spaceStationPanel), left);
                sec.removeItem(DTItems.spaceStationPanel, remove);
                left -= remove;
            }
        }
    }
}
*/
