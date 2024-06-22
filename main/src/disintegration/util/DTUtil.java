package disintegration.util;

import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import arclibrary.graphics.g3d.model.obj.OBJModel;
import arclibrary.graphics.g3d.model.obj.ObjectModelFactory;
import arclibrary.graphics.g3d.model.obj.ObjectShader;
import disintegration.DTVars;
import disintegration.graphics.DTShaders;
import mindustry.content.TechTree.TechNode;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;

import java.io.File;
import java.io.IOException;

public class DTUtil {
    public static <T> Seq<T> listItem(Seq<T> array) {
        Seq<T> result = new Seq<>();
        array.each(a -> {
            if (!result.contains(a) && a != null) {
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

    public static Fi createFi(Fi Parent, String name) {
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

    public static String name(String name) {
        return DTVars.modName + "-" + name;
    }

    public static Seq<OBJModel> loadObj(String file) {
        Fi objFi = DTVars.modFile.child("models").child(file);
        return ObjectModelFactory.create(objFi, DTShaders.objectShader);
    }

    public static void insertTechNode(TechNode tree, UnlockableContent parent, UnlockableContent content) {
        insertTechNode(tree, parent, content, content.researchRequirements(), null);
    }

    public static void insertTechNode(TechNode tree, UnlockableContent parent, UnlockableContent content, ItemStack[] requirements, Seq<Objectives.Objective> objectives) {
        tree.each(node -> {
            if (node.content == parent) {
                Seq<TechNode> next = node.children;
                TechNode newNode = new TechNode(node, content, requirements);
                if (objectives != null) {
                    newNode.objectives.addAll(objectives);
                }
                newNode.children.addAll(next);
                node.children.clear();
                node.children.add(newNode);
                next.each(n -> n.parent = newNode);
            }
        });
    }

    public static void addTechNode(TechNode tree, UnlockableContent parent, UnlockableContent content) {
        tree.each(node -> {
            if (node.content == parent) {
                TechNode newNode = new TechNode(node, content, content.researchRequirements());
            }
        });
    }
}
