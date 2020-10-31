package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.maps;

import com.walrusone.skywarsreloaded.game.GameMap;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import me.gaagjescraft.network.team.skywarsreloaded.extension.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class NameCommand implements Listener {

    private String c(String a) {
        return ChatColor.translateAlternateColorCodes('&',a);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (StringUtils.startsWithIgnoreCase(e.getMessage(),"/swm name") || StringUtils.startsWithIgnoreCase(e.getMessage(),"/skywarsmap name")) {
            e.setCancelled(true);
            String[] a = e.getMessage().split(" ");

            if (!e.getPlayer().hasPermission("sw.map.name")) {
                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("no_permission")));
                return;
            }

            if (a.length >= 4) {
                String name = a[2];
                GameMap map = null;
                for (int i=0;i<GameMap.getMaps().size();i++) {
                    if (GameMap.getMaps().get(i).getName().equals(name)) {
                        map = GameMap.getMaps().get(i);
                    }
                }

                if (map != null) {
                    StringBuilder b = new StringBuilder();
                    for (int i = 3;i<a.length;i++) {
                        b.append(i > 3 ? " " + a[i] : a[i]);
                    }
                    map.setDisplayName(b.toString());
                    e.getPlayer().sendMessage(c(Main.get().getConfig().getString("display_name_set").replace("%map%",map.getName()).replace("%name%", b.toString())));
                }
                else {
                    e.getPlayer().sendMessage(c(Main.get().getConfig().getString("invalid_arena")));
                }
            }
            else {
                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("too_few_display_name_arguments")));
            }
        }
    }

}
