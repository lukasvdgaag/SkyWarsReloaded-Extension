package me.gaagjescraft.network.team.skywarsreloaded.extension.files;

import com.walrusone.skywarsreloaded.enums.Vote;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.GameKit;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import me.gaagjescraft.network.team.skywarsreloaded.extension.features.AutoRejoinType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class PlayerFile implements FileManager {

    private static HashMap<Player, Long> autoJoinTimes = new HashMap<>();
    private static HashMap<Player, AutoRejoinType> autoJoinTypes= new HashMap<>();
    private static FileConfiguration fc = null;
    private String fname = "players.yml";
    private File f = new File(Main.get().getDataFolder(), fname);
    private Player player;

    public PlayerFile() { }

    public PlayerFile(Player p) {
        this.player = p;
    }

    @Override
    public void setup() {
        if (!f.exists()) {
            if (Main.get().getResource(fname) != null) {
                Main.get().saveResource(fname, false);
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

    public Vote getLatestChestVote() {
        if (getFile().contains(player.getUniqueId().toString()+".chestVote")) {
            return Vote.valueOf(getFile().getString(player.getUniqueId().toString()+".chestVote"));
        }
        return null;
    }
    public void setLatestChestVote(Vote v) {
        getFile().set(player.getUniqueId().toString()+".chestVote",v.toString());
        save();
        reload();
    }

    public Vote getLatestHealthVote() {
        if (getFile().contains(player.getUniqueId().toString()+".healthVote")) {
            return Vote.valueOf(getFile().getString(player.getUniqueId().toString()+".healthVote"));
        }
        return null;
    }
    public void setLatestHealthVote(Vote v) {
        getFile().set(player.getUniqueId().toString()+".healthVote",v.toString());
        save();
        reload();
    }

    public Vote getLatestModifierVote() {
        if (getFile().contains(player.getUniqueId().toString()+".modifierVote")) {
            return Vote.valueOf(getFile().getString(player.getUniqueId().toString()+".modifierVote"));
        }
        return null;
    }
    public void setLatestModifierVote(Vote v) {
        getFile().set(player.getUniqueId().toString()+".modifierVote",v.toString());
        save();
        reload();
    }

    public Vote getLatestTimeVote() {
        if (getFile().contains(player.getUniqueId().toString()+".timeVote")) {
            return Vote.valueOf(getFile().getString(player.getUniqueId().toString()+".timeVote"));
        }
        return null;
    }
    public void setLatestTimeVote(Vote v) {
        getFile().set(player.getUniqueId().toString()+".timeVote",v.toString());
        save();
        reload();
    }

    public Vote getLatestWeatherVote() {
        if (getFile().contains(player.getUniqueId().toString()+".weatherVote")) {
            return Vote.valueOf(getFile().getString(player.getUniqueId().toString()+".weatherVote"));
        }
        return null;
    }
    public void setLatestWeatherVote(Vote v) {
        getFile().set(player.getUniqueId().toString()+".weatherVote",v.toString());
        save();
        reload();
    }

    public GameKit getLatestKit() {
        if (getFile().contains(player.getUniqueId().toString() + ".kit")) {
            return GameKit.getKit(getFile().getString(player.getUniqueId().toString()+".kit"));
        }
        return null;
    }
    public void setLatestKit(GameKit kit) {
        getFile().set(player.getUniqueId().toString()+".kit",kit.getName());
        save();
        reload();
    }

    public void enableAutoJoinMode(AutoRejoinType arg0) {
        // this will enable auto-join mode from the current time and will last for x time (configurable in the config)
        // this will also override the set time if it has already been enabled.

        long time = Calendar.getInstance().getTimeInMillis();
        autoJoinTimes.put(player,time);
        autoJoinTypes.put(player, arg0);
    }

    public void disableAutoJoinMode() {
        autoJoinTimes.remove(player);
        autoJoinTypes.remove(player);
    }

    public boolean canAutoJoin() {
        if (autoJoinTimes.containsKey(player) && autoJoinTimes.containsKey(player)) {
            long lastingTime = Main.get().getConfig().getInt("autojoin_lasting_time",30) * 60000;
            long startTime = autoJoinTimes.get(player);
            long endTime = startTime+lastingTime;
            long currentTime = Calendar.getInstance().getTimeInMillis();

            return endTime >= currentTime;
        }

        return false;
    }

    public AutoRejoinType getAutoJoinType() {
        return autoJoinTypes.getOrDefault(player, null);
    }

}
