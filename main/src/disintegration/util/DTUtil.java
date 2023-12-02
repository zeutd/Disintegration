package disintegration.util;

import arc.files.Fi;
import arc.struct.Seq;
import disintegration.DTVars;

import java.io.File;
import java.io.IOException;

public class DTUtil {
    public static <T> Seq<T> listItem(Seq<T> array){
        Seq<T> result = new Seq<>();
        array.forEach(a -> {
            if(!result.contains(a) && a != null){
                result.add(a);
            }
        });
        return result;
    }

    public static Fi getFiChild(Fi Parent, String name) {
        Fi file = Parent.child(name);
        if (!file.exists()) file.mkdirs();
        return file;
    }

    public static Fi createFi(Fi Parent, String name){
        Fi file = new Fi(new File(Parent.file(), name));
        if (!file.exists()) {
            try {
                file.file().createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return file;
    }

    public static String name(String name){
        return DTVars.modName + "-" + name;
    }
}
