package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.general;

import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.TeamCard;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import me.gaagjescraft.network.team.skywarsreloaded.extension.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SendCommand implements Listener {

    private String c(String a) {
        return ChatColor.translateAlternateColorCodes('&',a);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (StringUtils.startsWithIgnoreCase(e.getMessage(), "/sw send") || StringUtils.startsWithIgnoreCase(e.getMessage(), "/skywars send")) {
            e.setCancelled(true);
            String[] a = e.getMessage().split(" ");

            if (!e.getPlayer().hasPermission("sw.send")) {
                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("no_permission")));
                return;
            }

            if (a.length==4) {
                Player p = Bukkit.getPlayer(a[2]);
                if (p != null) {
                    String name = a[3];
                    GameMap map = null;
                    for (int i=0;i<GameMap.getMaps().size();i++) {
                        if (GameMap.getMaps().get(i).getName().equals(name)) {
                            map = GameMap.getMaps().get(i);
                        }
                    }

                    if (map != null) {
                        GameMap game = MatchManager.get().getPlayerMap(p);
                        if (game == null) {
                            if (map.getMatchState() == MatchState.WAITINGSTART && map.canAddPlayer()) {
                                boolean b = map.addPlayers((TeamCard)null, e.getPlayer());
                                if (b) {
                                    e.getPlayer().sendMessage(c(Main.get().getConfig().getString("send_arena").replace("%name%", name).replace("%player%",p.getName())));
                                    return;
                                }
                                else {
                                    e.getPlayer().sendMessage((new Messaging.MessageFormatter()).format("error.could-not-join2"));
                                    return;
                                }
                            }

                            e.getPlayer().sendMessage(c(Main.get().getConfig().getString("cannot_join_other").replace("%player%",p.getName())));
                            return;
                        }
                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("already_ingame_other").replace("%player%",p.getName())));
                        return;
                    }

                    e.getPlayer().sendMessage(c(Main.get().getConfig().getString("invalid_arena")));
                    return;
                }
                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("invalid_player")));
                return;
            }

            e.getPlayer().sendMessage(c(Main.get().getConfig().getString("send_incorrect_usage")));
        }
    }
}
