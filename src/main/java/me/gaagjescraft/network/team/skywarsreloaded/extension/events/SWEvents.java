package me.gaagjescraft.network.team.skywarsreloaded.extension.events;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.events.SkyWarsJoinEvent;
import com.walrusone.skywarsreloaded.events.SkyWarsSelectKitEvent;
import com.walrusone.skywarsreloaded.events.SkyWarsSelectTeamEvent;
import com.walrusone.skywarsreloaded.events.SkyWarsVoteEvent;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.PlayerCard;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.GameKit;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import me.gaagjescraft.network.team.skywarsreloaded.extension.files.PlayerFile;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
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

    private void doVotes(Player p, GameMap map) {
        boolean yep = false;
        PlayerFile pf = new PlayerFile(p);
        PlayerCard card = map.getPlayerCard(p);

        if (card == null) return;

        FileConfiguration config = SWExtension.get().getConfig();
        if (config.getBoolean("enable_autoselect_game_options", true)) {
            if (p.hasPermission("sw.chestvote")) {
                if (SkyWarsReloaded.getCfg().isChestVoteEnabled()) {
                    if (p.hasPermission("sw.autovote.chest")) {
                        if (pf.getLatestChestVote() != null && map.getChestOption().getVote(card) == null) {
                            map.getChestOption().setCard(map.getPlayerCard(p), pf.getLatestChestVote());
                            yep = true;
                        }
                    }
                }
            }
            if (p.hasPermission("sw.healthvote")) {
                if (SkyWarsReloaded.getCfg().isHealthVoteEnabled()) {
                    if (p.hasPermission("sw.autovote.health")) {
                        if (pf.getLatestHealthVote() != null && map.getHealthOption().getVote(card) == null) {
                            map.getHealthOption().setCard(map.getPlayerCard(p), pf.getLatestHealthVote());
                            yep = true;
                        }
                    }
                }
            }
            if (p.hasPermission("sw.timevote")) {
                if (SkyWarsReloaded.getCfg().isChestVoteEnabled()) {
                    if (p.hasPermission("sw.autovote.time")) {
                        if (pf.getLatestTimeVote() != null && map.getTimeOption().getVote(card) == null) {
                            map.getTimeOption().setCard(map.getPlayerCard(p), pf.getLatestTimeVote());
                            yep = true;
                        }
                    }
                }
            }
            if (p.hasPermission("sw.weathervote")) {
                if (SkyWarsReloaded.getCfg().isChestVoteEnabled()) {
                    if (p.hasPermission("sw.autovote.weather")) {
                        if (pf.getLatestWeatherVote() != null && map.getWeatherOption().getVote(card) == null) {
                            map.getWeatherOption().setCard(map.getPlayerCard(p), pf.getLatestWeatherVote());
                            yep = true;
                        }
                    }
                }
            }
            if (p.hasPermission("sw.modifiervote")) {
                if (SkyWarsReloaded.getCfg().isChestVoteEnabled()) {
                    if (p.hasPermission("sw.autovote.modifier")) {
                        if (pf.getLatestModifierVote() != null && map.getModifierOption().getVote(card) == null) {
                            map.getModifierOption().setCard(map.getPlayerCard(p), pf.getLatestModifierVote());
                            yep = true;
                        }
                    }
                }
            }
        }
        if (config.getBoolean("enable_autoselect_kit", true)) {
            if (p.hasPermission("sw.autovote.kit")) {
                if (pf.getLatestKit() != null && card.getKitVote() == null) {
                    map.setKitVote(p, pf.getLatestKit());
                    yep = true;
                }
            }
            String defaultKit = config.getString("default_kit", "");
            if (!defaultKit.isEmpty() && p.hasPermission("sw.autovote.default")) {
                GameKit selectedKit = map.getSelectedKit(p);
                if (selectedKit == null) {
                    map.setKitVote(p, GameKit.getKit(defaultKit));
                    yep = true;
                }
            }
        }

        if (yep) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("autovotes_applied")));
        }
    }

    @EventHandler
    public void onTeamSelect(SkyWarsSelectTeamEvent e) {
        doVotes(e.getPlayer(), e.getGame());
    }

    @EventHandler
    public void onJoin(SkyWarsJoinEvent e) {
        if (e.getGame().getTeamSize() == 1) {
            doVotes(e.getPlayer(), e.getGame());
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
