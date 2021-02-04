package me.gaagjescraft.network.team.skywarsreloaded.extension.features;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.enums.GameType;
import com.walrusone.skywarsreloaded.events.SkyWarsDeathEvent;
import com.walrusone.skywarsreloaded.events.SkyWarsJoinEvent;
import com.walrusone.skywarsreloaded.events.SkyWarsWinEvent;
import com.walrusone.skywarsreloaded.utilities.Party;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;
import java.util.UUID;

public class AutoRejoinHandler implements Listener {

    public static List<UUID> cancelTeleport = Lists.newArrayList();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        AutoRejoin autoRejoin = AutoRejoin.fromPlayer(e.getPlayer());
        if (autoRejoin == null) return;

        if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL && cancelTeleport.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSkyWarsJoin(SkyWarsJoinEvent e) {
        AutoRejoin autoRejoin = AutoRejoin.fromPlayer(e.getPlayer());
        if (autoRejoin == null) return;

        if (autoRejoin.isOwner(e.getPlayer())) {
            autoRejoin.setType(e.getGame().getTeamSize() == 1 ? GameType.SINGLE : GameType.TEAM);
            autoRejoin.setParty(Party.getParty(e.getPlayer()));
        }
    }

    @EventHandler
    public void onSkyWarsEnd(SkyWarsWinEvent e) {
        for (Player player : e.getGame().getAllPlayers()) {
            AutoRejoin autoRejoin = AutoRejoin.fromPlayer(player);
            if (autoRejoin != null && autoRejoin.isOwner(player))
                doJoinCheck(player);
        }
    }

    @EventHandler
    public void onSkyWarsDeath(SkyWarsDeathEvent e) {
        if (e.getGame().getTeamsleft() == 1) return;
        doJoinCheck(e.getPlayer());
    }

    private void doJoinCheck(Player player) {
        AutoRejoin autoRejoin = AutoRejoin.fromPlayer(player);
        if (autoRejoin == null) return;
        if (autoRejoin.isOwner(player)) {
            player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Finding you a new game within 5 seconds. Please hold on.");
            Bukkit.getScheduler().scheduleSyncDelayedTask(SWExtension.get(), () -> {
                // player is owner of the auto join

                // -1 if unknown error, 0 if no maps, 1 if waiting for other players, 2 if success
                int result = autoRejoin.attemptJoin(false);

                if (result == -1) {
                    player.sendMessage(ChatColor.RED + "Something went wrong with finding you a new game.");
                } else if (result == 0) {
                    if (autoRejoin.getParty() == null)
                        player.sendMessage(ChatColor.RED + "We couldn't find an arena to fit you in.");
                    else
                        player.sendMessage(ChatColor.RED + "We couldn't find an arena to fit you and your party members in.");
                } else if (result == 1) {
                    player.sendMessage(ChatColor.RED + "Waiting for your party members to finish their game before rejoining another one.");
                } else if (result == 2) {
                    player.sendMessage(ChatColor.GREEN + "Successfully found a new game to join!");
                }
            }, 60);
        } else {
            int result = autoRejoin.attemptJoin(false);
            if (result == -1) {
                autoRejoin.getOwner().sendMessage(ChatColor.RED + "Something went wrong with finding you s new game.");
            } else if (result == 0) {
                if (autoRejoin.getParty() == null)
                    autoRejoin.getOwner().sendMessage(ChatColor.RED + "We couldn't find an arena to fit you in.");
                else
                    autoRejoin.getOwner().sendMessage(ChatColor.RED + "We couldn't find an arena to fit you and your party members in.");
            } else if (result == 2) {
                autoRejoin.getOwner().sendMessage(ChatColor.GREEN + "Successfully found a new game to join!");
            }
        }
    }


}
