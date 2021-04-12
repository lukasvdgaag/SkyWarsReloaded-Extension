package me.gaagjescraft.network.team.skywarsreloaded.extension.files;

import com.google.common.collect.Lists;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public interface FileManager {

    List<FileManager> all = Lists.newArrayList();

    static void register(FileManager fm) {
        all.add(fm);
    }

    static void registerAll() {
        for (FileManager f : all) {
            f.setup();
            f.getFile().options().copyDefaults(true);
            f.save();
            f.reload();
        }
    }

    static void reloadAll() {
        for (FileManager f : all) {
            f.reload();
        }
    }

    default FileConfiguration getFile() {
        /*
         *
    private static String name = "file.yml";
    private static File f = new File(AdvancedStaff.get().getDataFolder(), name);
    private static FileConfiguration cf = null;
         *
         */
        return null;
    }

    default void save() {
        /*
        try {
            cf.save(f);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("Something went wrong whilst saving " + name);
        }*/
    }

    default void reload() {
        // cf = YamlConfiguration.loadConfiguration(f);
    }

    default void setup() {
        /*
        if (AdvancedStaff.get().getResource(fname) != null) {
            AdvancedStaff.get().saveResource(fname,false);
        }
        else {
            try {
                f.createNewFile();
            } catch (IOException e) {
                //
            }
        }
         */
    }



}
