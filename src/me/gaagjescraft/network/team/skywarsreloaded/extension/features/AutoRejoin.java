package me.gaagjescraft.network.team.skywarsreloaded.extension.features;

import com.walrusone.skywarsreloaded.events.SkyWarsDeathEvent;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AutoRejoin implements Listener {


    @EventHandler
    public void onSkyWarsDeath(SkyWarsDeathEvent e) {
        GameMap map = e.getGame();
        Player player = e.getPlayer();

        if (MatchManager.get().getPlayerMap(player) != null) {
            // player is in a game

            if (map.getPlayerCard(player) != null) {}
        }
    }


}
