package me.gaagjescraft.network.team.skywarsreloaded.extension.events;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.events.SkyWarsSelectKitEvent;
import com.walrusone.skywarsreloaded.events.SkyWarsSelectTeamEvent;
import com.walrusone.skywarsreloaded.events.SkyWarsVoteEvent;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.PlayerCard;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import me.gaagjescraft.network.team.skywarsreloaded.extension.files.PlayerFile;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SWEvents implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (MatchManager.get().getSpectatorMap(p) != null) {
            if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE)) {
                World w = e.getFrom().getWorld();
                if (!e.getTo().getWorld().getName().equals(w.getName())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onJoin(SkyWarsSelectTeamEvent e) {
        boolean yep = false;
        PlayerFile pf = new PlayerFile(e.getPlayer());
        GameMap map = e.getGame();
        PlayerCard card = map.getPlayerCard(e.getPlayer());

        if (card == null) return;

        if (Main.get().getConfig().getBoolean("enable_autoselect_game_options", true)) {
            if (e.getPlayer().hasPermission("sw.chestvote")) {
                if (SkyWarsReloaded.getCfg().isChestVoteEnabled()) {
                    if (e.getPlayer().hasPermission("sw.autovote.chest")) {
                        if (pf.getLatestChestVote() != null && map.getChestOption().getVote(card) == null) {
                            e.getGame().getChestOption().setCard(e.getGame().getPlayerCard(e.getPlayer()), pf.getLatestChestVote());
                            yep = true;
                        }
                    }
                }
            }
            if (e.getPlayer().hasPermission("sw.healthvote")) {
                if (SkyWarsReloaded.getCfg().isHealthVoteEnabled()) {
                    if (e.getPlayer().hasPermission("sw.autovote.health")) {
                        if (pf.getLatestHealthVote() != null && map.getHealthOption().getVote(card) == null) {
                            e.getGame().getHealthOption().setCard(e.getGame().getPlayerCard(e.getPlayer()), pf.getLatestHealthVote());
                            yep = true;
                        }
                    }
                }
            }
            if (e.getPlayer().hasPermission("sw.timevote")) {
                if (SkyWarsReloaded.getCfg().isChestVoteEnabled()) {
                    if (e.getPlayer().hasPermission("sw.autovote.time")) {
                        if (pf.getLatestTimeVote() != null && map.getTimeOption().getVote(card) == null) {
                            e.getGame().getTimeOption().setCard(e.getGame().getPlayerCard(e.getPlayer()), pf.getLatestTimeVote());
                            yep = true;
                        }
                    }
                }
            }
            if (e.getPlayer().hasPermission("sw.weathervote")) {
                if (SkyWarsReloaded.getCfg().isChestVoteEnabled()) {
                    if (e.getPlayer().hasPermission("sw.autovote.weather")) {
                        if (pf.getLatestWeatherVote() != null && map.getWeatherOption().getVote(card) == null) {
                            e.getGame().getWeatherOption().setCard(e.getGame().getPlayerCard(e.getPlayer()), pf.getLatestWeatherVote());
                            yep = true;
                        }
                    }
                }
            }
            if (e.getPlayer().hasPermission("sw.modifiervote")) {
                if (SkyWarsReloaded.getCfg().isChestVoteEnabled()) {
                    if (e.getPlayer().hasPermission("sw.autovote.modifier")) {
                        if (pf.getLatestModifierVote() != null && map.getModifierOption().getVote(card) == null) {
                            e.getGame().getModifierOption().setCard(e.getGame().getPlayerCard(e.getPlayer()), pf.getLatestModifierVote());
                            yep = true;
                        }
                    }
                }
            }
        }
        if (Main.get().getConfig().getBoolean("enable_autoselect_kit", true)) {
            if (e.getPlayer().hasPermission("sw.autovote.kit")) {
                if (pf.getLatestKit() != null && card.getKitVote() == null) {
                    e.getGame().setKitVote(e.getPlayer(), pf.getLatestKit());
                    yep = true;
                }
            }
        }

        if (yep) {
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Main.get().getConfig().getString("autovotes_applied")));
        }
    }

    @EventHandler
    public void onVote(SkyWarsVoteEvent e) {
        PlayerFile pf = new PlayerFile(e.getPlayer());
        if (e.getVote().name().contains("TIME")) {
            pf.setLatestTimeVote(e.getVote());
        } else if (e.getVote().name().contains("CHEST")) {
            pf.setLatestChestVote(e.getVote());
        } else if (e.getVote().name().contains("WEATHER")) {
            pf.setLatestWeatherVote(e.getVote());
        } else if (e.getVote().name().contains("MODIFIER")) {
            pf.setLatestModifierVote(e.getVote());
        } else if (e.getVote().name().contains("HEALTH")) {
            pf.setLatestHealthVote(e.getVote());
        }
    }

    @EventHandler
    public void onKit(SkyWarsSelectKitEvent e) {
        PlayerFile pf = new PlayerFile(e.getPlayer());
        pf.setLatestKit(e.getKit());
    }

}
