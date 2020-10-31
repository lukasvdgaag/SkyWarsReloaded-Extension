package me.gaagjescraft.network.team.skywarsreloaded.extension.events;

import com.walrusone.skywarsreloaded.events.*;
import me.gaagjescraft.network.team.advancedevents.AdditionsEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AdditionsPlusHandler implements Listener {

    @EventHandler
    public void onDeath(SkyWarsDeathEvent e) {
        AdditionsEvent ea = new AdditionsEvent("skywars","death");
        ea.addPlaceholder("cause",e.getCause().name().toLowerCase());
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.setAuthor("GCNT");
        ea.setDescription("Thrown when a player dies in a game");
        ea.perform(e.getPlayer());
    }

    @EventHandler
    public void onJoin(SkyWarsJoinEvent e) {
        AdditionsEvent ea = new AdditionsEvent("skywars","join");
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.setAuthor("GCNT");
        ea.setDescription("Thrown when a player joins a new game");
        ea.perform(e.getPlayer());
    }

    @EventHandler
    public void onKill(SkyWarsKillEvent e) {
        AdditionsEvent ea = new AdditionsEvent("skywars","kill");
        ea.addPlaceholder("killed",e.getKilled().getName());
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.setAuthor("GCNT");
        ea.setDescription("Thrown when a player kills another player");
        ea.perform(e.getKiller());
    }

    @EventHandler
    public void onLeave(SkyWarsLeaveEvent e) {
        AdditionsEvent ea = new AdditionsEvent("skywars","leave");
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.setAuthor("GCNT");
        ea.setDescription("Thrown when a player leaves a game");
        ea.perform(e.getPlayer());
    }

    @EventHandler
    public void onKitSelect(SkyWarsSelectKitEvent e) {
        AdditionsEvent ea = new AdditionsEvent("skywars","select-kit");
        ea.addPlaceholder("kit",e.getKit().getName());
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.setAuthor("GCNT");
        ea.setDescription("Thrown when a player selects a kit in-game");
        ea.perform(e.getPlayer());
    }

    @EventHandler
    public void onVote(SkyWarsVoteEvent e) {
        AdditionsEvent ea = new AdditionsEvent("skywars","vote");
        ea.addPlaceholder("voted",e.getVote().name().toUpperCase());
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.addPlaceholder("alive",e.getGame().getAlivePlayers().size()+"");
        ea.setAuthor("GCNT");
        ea.setDescription("Thrown when a player votes for an option");
        ea.perform(e.getPlayer());
    }

    @EventHandler
    public void onWin(SkyWarsWinEvent e) {
        AdditionsEvent ea = new AdditionsEvent("skywars","win");
        ea.addPlaceholder("wins",e.getPlayerStat().getWins()+"");
        ea.addPlaceholder("arena",e.getGame().getName());
        ea.setAuthor("GCNT");
        ea.setDescription("Thrown when a player wins a game");
        ea.perform(e.getPlayer());
    }
}
