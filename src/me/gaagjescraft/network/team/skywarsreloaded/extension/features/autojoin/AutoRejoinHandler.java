package me.gaagjescraft.network.team.skywarsreloaded.extension.features.autojoin;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.enums.GameType;
import com.walrusone.skywarsreloaded.events.SkyWarsDeathEvent;
import com.walrusone.skywarsreloaded.events.SkyWarsJoinEvent;
import com.walrusone.skywarsreloaded.events.SkyWarsWinEvent;
import com.walrusone.skywarsreloaded.utilities.Party;
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
        if (e.getGame().getTeamsLeft() == 1) return;
        doJoinCheck(e.getPlayer());
    }

    private void doJoinCheck(Player player) {
        AutoRejoin autoRejoin = AutoRejoin.fromPlayer(player);
        if (autoRejoin == null) return;
        autoRejoin.attemptJoin(false);
    }


}
