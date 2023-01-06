package me.gaagjescraft.network.team.skywarsreloaded.extension.events;

import com.walrusone.skywarsreloaded.events.*;
import me.gaagjescraft.network.team.advancedevents.AdditionsPlus;
import me.gaagjescraft.network.team.advancedevents.api.AdditionsAPI;
import me.gaagjescraft.network.team.advancedevents.files.events.AdditionsEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AdditionsPlusHandler implements Listener {

    public AdditionsPlusHandler() {
        AdditionsAPI api = AdditionsPlus.getAPI();
        api.getAdditionsEvents().add(new AdditionsEvent("skywars", "death", "GCNT", "Thrown when a player dies in a Skywars game."));
        api.getAdditionsEvents().add(new AdditionsEvent("skywars", "join", "GCNT", "Thrown when a player joins a new game"));
        api.getAdditionsEvents().add(new AdditionsEvent("skywars", "win", "GCNT", "Thrown when a player wins a game"));
        api.getAdditionsEvents().add(new AdditionsEvent("skywars", "kill", "GCNT", "Thrown when a player kills another player"));
        api.getAdditionsEvents().add(new AdditionsEvent("skywars", "leave", "GCNT", "Thrown when a player leaves a game"));
        api.getAdditionsEvents().add(new AdditionsEvent("skywars", "select-kit", "GCNT", "Thrown when a player selects a kit in-game"));
        api.getAdditionsEvents().add(new AdditionsEvent("skywars", "vote", "GCNT", "Thrown when a player votes for an option"));
    }

    @EventHandler
    public void onDeath(SkyWarsDeathEvent e) {
        AdditionsEvent ea = AdditionsPlus.getAPI().getAdditionsEventByName("skywars-death");
        if (ea == null)return;
        ea.addPlaceholder("cause",e.getCause().name().toLowerCase());
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.perform(e.getPlayer());
    }

    @EventHandler
    public void onJoin(SkyWarsJoinEvent e) {
        AdditionsEvent ea = AdditionsPlus.getAPI().getAdditionsEventByName("skywars-join");
        if (ea == null)return;        
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.perform(e.getPlayer());
    }

    @EventHandler
    public void onKill(SkyWarsKillEvent e) {
        AdditionsEvent ea = AdditionsPlus.getAPI().getAdditionsEventByName("skywars-kill");
        if (ea == null)return;
        ea.addPlaceholder("killed",e.getKilled().getName());
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.perform(e.getKiller());
    }

    @EventHandler
    public void onLeave(SkyWarsLeaveEvent e) {
        AdditionsEvent ea = AdditionsPlus.getAPI().getAdditionsEventByName("skywars-leave");
        if (ea == null)return;
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.perform(e.getPlayer());
    }

    @EventHandler
    public void onKitSelect(SkyWarsSelectKitEvent e) {
        AdditionsEvent ea = AdditionsPlus.getAPI().getAdditionsEventByName("skywars-select-kit");
        if (ea == null)return;
        ea.addPlaceholder("kit",e.getKit().getName());
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.perform(e.getPlayer());
    }

    @EventHandler
    public void onVote(SkyWarsVoteEvent e) {
        AdditionsEvent ea = AdditionsPlus.getAPI().getAdditionsEventByName("skywars-vote");
        if (ea == null)return;
        ea.addPlaceholder("voted",e.getVote().name().toUpperCase());
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.perform(e.getPlayer());
    }

    @EventHandler
    public void onWin(SkyWarsWinEvent e) {
        AdditionsEvent ea = AdditionsPlus.getAPI().getAdditionsEventByName("skywars-win");
        if (ea == null)return;
        ea.addPlaceholder("wins",e.getPlayerStat().getWins()+"");
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.perform(e.getPlayer());
    }
}
