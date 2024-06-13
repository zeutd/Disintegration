package disintegration.core;

import arc.ApplicationListener;
import arc.struct.ObjectMap;
import arc.util.Interval;
import arc.util.Log;
import arc.util.Time;
import disintegration.DTVars;
import disintegration.content.DTItems;
import disintegration.type.SpaceStation;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.content.SectorPresets;
import mindustry.game.SectorInfo;
import mindustry.game.Universe;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.type.*;

import java.util.Arrays;

import static disintegration.DTVars.modName;
import static disintegration.DTVars.spaceStations;
import static mindustry.Vars.*;

public class ExportHandler implements ApplicationListener {
    /** refresh period of export in ticks */
    private static final float refreshPeriod = 60;
    private final transient Interval time = new Interval();

    public ObjectMap<Sector, ObjectMap<Item, SectorInfo.ExportStat>> orbitalExport = new ObjectMap<>();
    public ObjectMap<Sector, ObjectMap<Item, SectorInfo.ExportStat>> spaceExport = new ObjectMap<>();
    public ObjectMap<Planet, ObjectMap<Planet, ObjectMap<Item, SectorInfo.ExportStat>>> interplanetaryExport = new ObjectMap<>();
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

    public void handleItemSpaceExport(ItemStack stack, Sector sector){
        handleItemSpaceExport(stack.item, stack.amount, sector);
    }
    public void handleItemSpaceExport(Item item, int amount, Sector sector){
        if(spaceExport.get(sector) == null || spaceExport.get(sector).get(item) == null) {
            spaceExport.put(sector, new ObjectMap<>());
            spaceExport.get(sector).put(item, new SectorInfo.ExportStat());
        }
        spaceExport.get(sector).get(item).counter += amount;
    }

    public void handleItemInterplanetaryExport(ItemStack stack, Planet from, Planet to){
        handleItemInterplanetaryExport(stack.item, stack.amount, from, to);
    }
    public void handleItemInterplanetaryExport(Item item, int amount, Planet from, Planet to){
        if(
                interplanetaryExport.get(from) == null
                || interplanetaryExport.get(from).get(to) == null
                || interplanetaryExport.get(from).get(to).get(item) == null
        ) {
            interplanetaryExport.put(from, new ObjectMap<>());
            interplanetaryExport.get(from).put(to, new ObjectMap<>());
            interplanetaryExport.get(from).get(to).put(item, new SectorInfo.ExportStat());
        }
        interplanetaryExport.get(from).get(to).get(item).counter += amount;
    }
    @Override
    public void init(){

    }
    @Override
    public void update(){
        if(net.client()) return;
        if(state.isPaused() || !state.isCampaign() || !state.isGame()) return;
        //refresh throughput
        if(time.get(refreshPeriod)){
            //refresh export
            orbitalExport.each((sector, map) -> {
                if(sector == state.rules.sector) {
                    map.each((item, stat) -> {
                        if (!stat.loaded) {
                            stat.means.fill(stat.mean);
                            stat.loaded = true;
                        }
                        stat.means.add(Math.max(stat.counter, 0));
                        stat.counter = 0;
                        stat.mean = stat.means.rawMean();
                        stat.mean = Math.min(stat.mean, sector.info.rawProduction.get(item).mean + Math.max(-sector.info.production.get(item).mean, 0));
                    });
                }
            });
            spaceStations.each(station -> {
                if(station.getSector(PlanetGrid.Ptile.empty) == state.rules.sector) {
                    Sector from = station.getSector(PlanetGrid.Ptile.empty);
                    spaceExport.each((sector, map) -> {
                        map.each((item, stat) -> {
                            if (!stat.loaded) {
                                stat.means.fill(stat.mean);
                                stat.loaded = true;
                            }
                            stat.means.add(Math.max(stat.counter, 0));
                            stat.counter = 0;
                            stat.mean = stat.means.rawMean();
                            stat.mean = Math.min(stat.mean, from.info.rawProduction.get(item).mean + Math.max(-from.info.production.get(item).mean, 0));
                        });
                    });
                }
            });
            spaceStations.each(station -> {
                if (station.getSector(PlanetGrid.Ptile.empty) == state.rules.sector) {
                    if(interplanetaryExport.containsKey(station)){
                        interplanetaryExport.get(station).each((to, map1) -> {
                            map1.each((item, stat) -> {
                                if (!stat.loaded) {
                                    stat.means.fill(stat.mean);
                                    stat.loaded = true;
                                }
                                stat.means.add(Math.max(stat.counter, 0));
                                stat.counter = 0;
                                stat.mean = stat.means.rawMean();
                                stat.mean = Math.min(stat.mean, station.getSector(PlanetGrid.Ptile.empty).info.rawProduction.get(item).mean + Math.max(-station.getSector(PlanetGrid.Ptile.empty).info.production.get(item).mean, 0));
                            });
                        });
                    }
                }
            });
        }
    }

    public void updateItem(int newSecondsPassed){
        orbitalExport.each((sector, map) -> {
            ItemSeq items = new ItemSeq();
            Sector to = spaceStations.find(p -> p.parent == sector.planet).getSector(PlanetGrid.Ptile.empty);
            map.each((item, stat) -> {
                items.add(item, (int)(stat.mean * newSecondsPassed * sector.getProductionScale()));
            });
            to.info.lastImported.add(items);
            to.addItems(items);
            map.each((item, stat) -> {
                if(sector.info.items.get(item) <= 0 && sector.info.production.get(item, SectorInfo.ExportStat::new).mean < 0 && stat.mean > 0){
                    //cap export by import when production is negative.
                    stat.mean = Math.min(sector.info.lastImported.get(item) / (float)newSecondsPassed, stat.mean);
                }
            });
        });
        spaceExport.each((sector, map) -> {
            SpaceStation station = spaceStations.find(p -> p.parent == sector.planet);
            if(station != null) {
                Sector from = station.getSector(PlanetGrid.Ptile.empty);
                ItemSeq items = new ItemSeq();
                map.each((item, stat) -> {
                    items.add(item, (int) (stat.mean * newSecondsPassed * sector.getProductionScale()));
                });
                sector.info.lastImported.add(items);
                sector.addItems(items);
                map.each((item, stat) -> {
                    if (sector.info.items.get(item) <= 0 && from.info.production.get(item, SectorInfo.ExportStat::new).mean < 0 && stat.mean > 0) {
                        //cap export by import when production is negative.
                        stat.mean = Math.min(from.info.lastImported.get(item) / (float) newSecondsPassed, stat.mean);
                    }
                });
            }
        });
        interplanetaryExport.each((from, map) -> {
            map.each((to, map1) -> {
                Sector fromSec = from.getSector(PlanetGrid.Ptile.empty);
                Sector toSec = to.getSector(PlanetGrid.Ptile.empty);
                ItemSeq items = new ItemSeq();
                map1.each((item, stat) -> {
                    items.add(item, (int) (stat.mean * newSecondsPassed * fromSec.getProductionScale()));
                });
                toSec.info.lastImported.add(items);
                toSec.addItems(items);
                map1.each((item, stat) -> {
                    if (to.getSector(PlanetGrid.Ptile.empty).info.items.get(item) <= 0 && fromSec.info.production.get(item, SectorInfo.ExportStat::new).mean < 0 && stat.mean > 0) {
                        //cap export by import when production is negative.
                        stat.mean = Math.min(fromSec.info.lastImported.get(item) / (float) newSecondsPassed, stat.mean);
                    }
                });
            });
        });
    }
}
