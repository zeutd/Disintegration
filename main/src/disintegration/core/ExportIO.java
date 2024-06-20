package disintegration.core;

import arc.ApplicationListener;
import arc.struct.ObjectMap;
import arc.util.Log;
import disintegration.DTVars;
import mindustry.Vars;
import mindustry.game.SectorInfo;
import mindustry.type.Item;
import mindustry.type.Planet;
import mindustry.type.Sector;

import java.io.IOException;
import java.io.Writer;

import static disintegration.DTVars.exportHandler;

public class ExportIO implements ApplicationListener {
    final int orbital = 0, space = 1, interplanetary = 2;
    public void read(){
        for (String line : DTVars.exportFi.readString().split("\n")){
            int sign = Integer.parseInt(line.split("=")[0]);
            switch (sign){
                case orbital : {
                    try {
                        String[] data = line.split("=")[1].split("/");
                        Planet planet = Vars.content.planet(data[0]);
                        Sector sector = planet.sectors.get(Integer.parseInt(data[1]));
                        Item item = Vars.content.item(data[2]);
                        float mean = Float.parseFloat(data[3]);

                        if (exportHandler.orbitalExport.get(sector) == null)
                            exportHandler.orbitalExport.put(sector, new ObjectMap<>());
                        if (exportHandler.orbitalExport.get(sector).get(item) == null)
                            exportHandler.orbitalExport.get(sector).put(item, new SectorInfo.ExportStat());
                        exportHandler.orbitalExport.get(sector, new ObjectMap<>()).get(item, new SectorInfo.ExportStat()).means.fill(mean);
                        exportHandler.orbitalExport.get(sector, new ObjectMap<>()).get(item, new SectorInfo.ExportStat()).mean = mean;
                    }catch (Throwable e){
                        Log.err(e);
                    }
                }
                case space : {
                    try {
                        String[] data = line.split("=")[1].split("/");
                        Planet planet = Vars.content.planet(data[0]);
                        Sector sector = planet.sectors.get(Integer.parseInt(data[1]));
                        Item item = Vars.content.item(data[2]);
                        float mean = Float.parseFloat(data[3]);

                        if (exportHandler.spaceExport.get(sector) == null)
                            exportHandler.spaceExport.put(sector, new ObjectMap<>());
                        if (exportHandler.spaceExport.get(sector).get(item) == null)
                            exportHandler.spaceExport.get(sector).put(item, new SectorInfo.ExportStat());
                        exportHandler.spaceExport.get(sector, new ObjectMap<>()).get(item, new SectorInfo.ExportStat()).means.fill(mean);
                        exportHandler.spaceExport.get(sector, new ObjectMap<>()).get(item, new SectorInfo.ExportStat()).mean = mean;
                    }catch (Throwable e){
                        Log.err(e);
                    }
                }

                case interplanetary: {
                    try {
                        String[] data = line.split("=")[1].split("/");
                        Planet from = Vars.content.planet(data[0]);
                        Planet to = Vars.content.planet(data[1]);
                        Item item = Vars.content.item(data[2]);
                        float mean = Float.parseFloat(data[3]);

                        if (exportHandler.interplanetaryExport.get(from) == null)
                            exportHandler.interplanetaryExport.put(from, new ObjectMap<>());
                        if (exportHandler.interplanetaryExport.get(from).get(to) == null)
                            exportHandler.interplanetaryExport.get(from).put(to, new ObjectMap<>());
                        if (exportHandler.interplanetaryExport.get(from).get(to).get(item) == null)
                            exportHandler.interplanetaryExport.get(from).get(to).put(item, new SectorInfo.ExportStat());
                        exportHandler.interplanetaryExport.get(from, new ObjectMap<>()).get(to, new ObjectMap<>()).get(item, new SectorInfo.ExportStat()).means.fill(mean);
                        exportHandler.interplanetaryExport.get(from, new ObjectMap<>()).get(to, new ObjectMap<>()).get(item, new SectorInfo.ExportStat()).mean = mean;
                    }catch (Throwable e){
                        Log.err(e);
                    }
                }
            }
        }
    }

    @Override
    public void exit(){
        Writer writer = DTVars.exportFi.writer(false);
        exportHandler.orbitalExport.each((sector, map) -> {
            map.each((item, stat) -> {
                try {
                    writer.write(orbital + "=");
                    writer.write(sector.planet.name + "/" + sector.id + "/" + item.name + "/" + stat.mean + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        exportHandler.spaceExport.each((sector, map) -> {
            map.each((item, stat) -> {
                try {
                    writer.write(space + "=");
                    writer.write(sector.planet.name + "/" + sector.id + "/" + item.name + "/" + stat.mean + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        exportHandler.interplanetaryExport.each((from, map) -> {
            map.each((to, map1) -> {
                map1.each((item, stat) -> {
                    try {
                        writer.write(interplanetary + "=");
                        writer.write(from.name + "/" + to.name + "/" + item.name + "/" + stat.mean + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        });
        try {
            writer.flush();
            writer.close();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
