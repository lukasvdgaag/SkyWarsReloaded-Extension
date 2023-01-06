package me.gaagjescraft.network.team.skywarsreloaded.extension.events;

import com.walrusone.skywarsreloaded.events.*;
import net.gcnt.additionsplus.api.AdditionsAPI;
import net.gcnt.additionsplus.api.AdditionsPlugin;
import net.gcnt.additionsplus.api.objects.AdditionsEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AdditionsPlusHandler implements Listener {

    private final AdditionsPlugin additionsPlugin;
    private final AdditionsAPI additionsApi;

    public AdditionsPlusHandler(AdditionsPlugin plugin) {
        this.additionsPlugin = plugin;
        this.additionsApi = additionsPlugin.getAPI();
    }

    @EventHandler
    public void onDeath(SkyWarsDeathEvent e) {
        AdditionsEvent ea = additionsApi.createCustomEvent(additionsPlugin, "skywars","death", "GCNT", "Thrown when a player dies in a game");
        ea.addPlaceholder("cause",e.getCause().name().toLowerCase());
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.perform(additionsPlugin, e.getPlayer());
    }

    @EventHandler
    public void onJoin(SkyWarsJoinEvent e) {
        AdditionsEvent ea = additionsApi.createCustomEvent(additionsPlugin, "skywars","join", "GCNT", "Thrown when a player joins a new game");
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.perform(additionsPlugin, e.getPlayer());
    }

    @EventHandler
    public void onKill(SkyWarsKillEvent e) {
        AdditionsEvent ea = additionsApi.createCustomEvent(additionsPlugin, "skywars","kill", "GCNT", "Thrown when a player kills another player");
        ea.addPlaceholder("killed",e.getKilled().getName());
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.perform(additionsPlugin, e.getKiller());
    }

    @EventHandler
    public void onLeave(SkyWarsLeaveEvent e) {
        AdditionsEvent ea = additionsApi.createCustomEvent(additionsPlugin, "skywars","leave", "GCNT", "Thrown when a player leaves a game");
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.perform(additionsPlugin, e.getPlayer());
    }

    @EventHandler
    public void onKitSelect(SkyWarsSelectKitEvent e) {
        AdditionsEvent ea = additionsApi.createCustomEvent(additionsPlugin, "skywars","select-kit", "GCNT", "Thrown when a player selects a kit in-game");
        ea.addPlaceholder("kit",e.getKit().getName());
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.perform(additionsPlugin, e.getPlayer());
    }

    @EventHandler
    public void onVote(SkyWarsVoteEvent e) {
        AdditionsEvent ea = additionsApi.createCustomEvent(additionsPlugin, "skywars","vote", "GCNT", "Thrown when a player votes for an option");
        ea.addPlaceholder("voted",e.getVote().name().toUpperCase());
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.perform(additionsPlugin, e.getPlayer());
    }

    @EventHandler
    public void onWin(SkyWarsWinEvent e) {
        AdditionsEvent ea = additionsApi.createCustomEvent(additionsPlugin, "skywars","win", "GCNT", "Thrown when a player wins a game");
        ea.addPlaceholder("wins",e.getPlayerStat().getWins()+"");
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.perform(additionsPlugin, e.getPlayer());
    }
}
