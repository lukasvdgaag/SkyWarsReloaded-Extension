package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.maps;

import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.cages.CageType;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import me.gaagjescraft.network.team.skywarsreloaded.extension.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;

public class CageTypeCommand implements Listener {

    private String c(String a) {
        return ChatColor.translateAlternateColorCodes('&',a);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (StringUtils.startsWithIgnoreCase(e.getMessage(), "/swm cage") || StringUtils.startsWithIgnoreCase(e.getMessage(),"/skywarsmap cage")) {
            e.setCancelled(true);
            String[] a = e.getMessage().split(" ");

            if (!e.getPlayer().hasPermission("sw.map.cage")) {
                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("no_permission")));
                return;
            }

            if (a.length >= 3) {
                String name = a[2];
                GameMap map = null;
                for (int i = 0; i < GameMap.getMaps().size(); i++) {
                    if (GameMap.getMaps().get(i).getName().equals(name)) {
                        map = GameMap.getMaps().get(i);
                    }
                }

                if (map != null) {
                    String cageType = a[3];
                    try {
                        map.setCage(CageType.valueOf(cageType.toUpperCase()));
                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("cagetype_set").replace("%map%", map.getName()).replace("%type%", cageType)));
                    } catch (Exception e1) {
                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("invalid_cagetype")));
                        e.getPlayer().sendMessage(Arrays.toString(CageType.values()).replace("[", "").replace("]", ""));
                    }
                } else {
                    e.getPlayer().sendMessage(c(Main.get().getConfig().getString("invalid_arena")));
                }
            } else {
                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("too_few_creator_arguments")));
            }
        }
    }

}
