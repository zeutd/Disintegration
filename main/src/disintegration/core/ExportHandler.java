package disintegration.core;

import arc.ApplicationListener;
import arc.struct.ObjectMap;
import arc.util.Interval;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.SectorInfo;
import mindustry.game.Universe;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.type.*;

import java.util.Arrays;

import static disintegration.DTVars.spaceStations;
import static mindustry.Vars.*;

public class ExportHandler implements ApplicationListener {
    /** refresh period of export in ticks */
    private static final float refreshPeriod = 60;

    private float turnCounter;
    private final transient Interval time = new Interval();

    public ObjectMap<Sector, ObjectMap<Item, SectorInfo.ExportStat>> orbitalExport = new ObjectMap<>();
    public void handleItemOrbitalExport(ItemStack stack, Sector sector){
        handleItemOrbitalExport(stack.item, stack.amount, sector);
    }
    public void handleItemOrbitalExport(Item item, int amount, Sector sector){
        if(orbitalExport.get(sector) == null || orbitalExport.get(sector).get(item) == null) {
            orbitalExport.put(sector, new ObjectMap<>());
            orbitalExport.get(sector).put(item, new SectorInfo.ExportStat());
        }
        orbitalExport.get(sector).get(item).counter += amount;
    }
    @Override
    public void init(){

    }
    @Override
    public void update(){
        if(net.client()) return;
        if(state.isPaused() || !state.isCampaign() || !state.isGame()) return;
        turnCounter += Time.delta;
        if(turnCounter >= turnDuration){
            turnCounter = 0;
            updateItem((int) turnDuration/60);
        }
        //refresh throughput
        if(time.get(refreshPeriod)){

            //refresh export
            orbitalExport.each((sector, map) -> {
                map.each((item, stat) -> {
                    if(!stat.loaded){
                        stat.means.fill(stat.mean);
                        stat.loaded = true;
                    }
                    stat.means.add(Math.max(stat.counter, 0));
                    stat.counter = 0;
                    stat.mean = stat.means.rawMean();
                });
            });
            orbitalExport.each((sector, map) -> {
                for(Item item : content.items()){
                    if(map.containsKey(item)){
                        //export can, at most, be the raw items being produced from factories + the items being taken from the core
                        map.get(item).mean = Math.min(map.get(item).mean, sector.info.rawProduction.get(item).mean + Math.max(-sector.info.production.get(item).mean, 0));
                    }
                }
            });
        }
    }

    public void updateItem(int newSecondsPassed){
        orbitalExport.each((sector, map) -> {
            sector.info.lastImported.clear();
            ItemSeq items = new ItemSeq();
            map.each((item, stat) -> {
                Sector to = spaceStations.find(p -> p.parent == sector.planet).getSector(PlanetGrid.Ptile.empty);
                items.add(item, (int)(stat.mean * newSecondsPassed * sector.getProductionScale()));
                to.info.lastImported.add(items);
                to.info.items.add(item, Math.min((int)(stat.mean * newSecondsPassed), sector.info.storageCapacity - sector.info.items.get(item)));
            });
            map.each((item, stat) -> {
                if(sector.info.items.get(item) <= 0 && sector.info.production.get(item, SectorInfo.ExportStat::new).mean < 0 && stat.mean > 0){
                    //cap export by import when production is negative.
                    stat.mean = Math.min(sector.info.lastImported.get(item) / (float)newSecondsPassed, stat.mean);
                }
            });
        });
    }
}
