package me.gaagjescraft.network.team.skywarsreloaded.extension.npcs;

import com.google.common.collect.Lists;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import me.gaagjescraft.network.team.skywarsreloaded.extension.files.FileManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class NPCFile implements FileManager {

    private static FileConfiguration fc = null;
    private String fname = "npcs.yml";
    private File f = new File(SWExtension.get().getDataFolder(), fname);

    @Override
    public void setup() {
        if (!f.exists()) {
            if (SWExtension.get().getResource(fname) != null) {
                SWExtension.get().saveResource(fname, false);
                Bukkit.getConsoleSender().sendMessage("Set up " + fname + " from resource");
            } else {
                try {
                    f.createNewFile();
                    Bukkit.getConsoleSender().sendMessage("Set up " + fname + " as empty file");
                } catch (IOException e) {
                    Bukkit.getConsoleSender().sendMessage("Couldn't set up " + fname);
                }
            }
        }
        fc = YamlConfiguration.loadConfiguration(f);
    }

    @Override
    public void reload() {
        fc = YamlConfiguration.loadConfiguration(f);
    }

    @Override
    public void save() {
        try {
            fc.save(f);
            Bukkit.getConsoleSender().sendMessage("Saved " + fname);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("Something went wrong whilst saving " + fname);
        }
    }

    @Override
    public FileConfiguration getFile() {
        return fc;
    }

    public List<Integer> getNPCs() {
        List<Integer> a = Lists.newArrayList();
        for (String b : getFile().getKeys(false)) {
            a.add((Integer.parseInt(b)));
        }
        return a;
    }

    public void setClickAction(int npcID, NPCClickAction action) {
        getFile().set(npcID+".action",action.name());
        save();
        reload();
    }

    public NPCClickAction getClickAction(int npcID) {
        return NPCClickAction.valueOf(getFile().getString(npcID+".action"));
    }

}
