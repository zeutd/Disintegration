package disintegration.tools;

import arc.Core;
import arc.Settings;
import arc.assets.AssetManager;
import arc.mock.MockApplication;
import arc.mock.MockFiles;
import arc.util.Log;
import arc.util.Time;
import mindustry.async.AsyncCore;
import mindustry.core.ContentLoader;
import mindustry.core.FileTree;
import mindustry.core.GameState;
import mindustry.mod.Mods;

import static mindustry.Vars.*;

public class SpriteGenerator {
    public static void generate(String name, Runnable run){
        Time.mark();
        run.run();
        Log.info("&ly[Generator]&lc Time to generate &lm@&lc: &lg@&lcms", name, Time.elapsed());
    }
    public static void main(String[] __){
        /*Log.logger = new Log.DefaultLogHandler();

        headless = true;
        Core.app = new MockApplication(){
            @Override
            public void post(Runnable runnable){
                runs.post(runnable);
            }
        };
        Core.files = new MockFiles();
        Core.assets = new AssetManager(tree = new FileTree());
        Core.settings = new Settings();

        asyncCore = new AsyncCore();
        state = new GameState();
        mods = new Mods();

        content = new ContentLoader();
        content.createBaseContent();

        meta = new ModMeta(){{ name = "unity"; }};
        mod = new LoadedMod(null, null, unity, Tools.class.getClassLoader(), meta);

        Reflect.<Seq<LoadedMod>>get(Mods.class, mods, "mods").add(mod);
        Reflect.<ObjectMap<Class<?>, ModMeta>>get(Mods.class, mods, "metas").put(Unity.class, meta);

        content.setCurrentMod(mod);
        unity.loadContent();
        content.setCurrentMod(null);

        Log.logger = new DefaultLogHandler();
        loadLogger();
*/
    }
}
