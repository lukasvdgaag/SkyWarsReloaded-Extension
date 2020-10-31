package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.maps;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import me.gaagjescraft.network.team.skywarsreloaded.extension.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.File;

public class RenameCommand implements Listener {


    private String c(String a) {
        return ChatColor.translateAlternateColorCodes('&',a);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (StringUtils.startsWithIgnoreCase(e.getMessage(), "/swm rename") || StringUtils.startsWithIgnoreCase(e.getMessage(),"/skywarsmap rename")) {
            e.setCancelled(true);
            String[] a = e.getMessage().split(" ");

            if (!e.getPlayer().hasPermission("sw.map.rename")) {
                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("no_permission")));
                return;
            }

            if (a.length >= 4) {
                String name = a[2];
                GameMap map = null;
                for (int i = 0; i < GameMap.getMaps().size(); i++) {
                    if (GameMap.getMaps().get(i).getName().equals(name)) {
                        map = GameMap.getMaps().get(i);
                    }
                }

                if (map != null) {
                    if (map.getMatchState() != MatchState.OFFLINE) {
                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("game_must_be_offline")));
                        return;
                    }
                    String target = a[3];
                    GameMap targetMap = GameMap.getMap(target);
                    if (targetMap!=null) {
                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("arena_already_exists")));
                        return;
                    }

                    if (map.getCurrentWorld() != null) {
                        for (Player p : map.getCurrentWorld().getPlayers()) {
                            p.teleport(SkyWarsReloaded.getCfg().getSpawn());
                        }
                        Bukkit.unloadWorld(target,false); // removing the world folder
                    }

                    File yamlFile = new File(SkyWarsReloaded.get().getDataFolder(), "mapsData" + File.separator + name+".yml");
                    yamlFile.renameTo(new File(SkyWarsReloaded.get().getDataFolder(), "mapsData" + File.separator + target+".yml"));

                    File folderFile = new File(SkyWarsReloaded.get().getDataFolder(), "maps" + File.separator + name);
                    folderFile.renameTo(new File(SkyWarsReloaded.get().getDataFolder(), "maps" + File.separator + target));


                    e.getPlayer().sendMessage(c(Main.get().getConfig().getString("renamed_arena").replace("%arena%", target)));


                } else {
                    e.getPlayer().sendMessage(c(Main.get().getConfig().getString("invalid_arena")));
                    return;
                }
            } else {
                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("too_few_rename_arguments")));
                return;
            }
        }
    }

}
