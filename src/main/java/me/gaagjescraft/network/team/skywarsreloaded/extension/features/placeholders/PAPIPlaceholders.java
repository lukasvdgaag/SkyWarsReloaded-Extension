package me.gaagjescraft.network.team.skywarsreloaded.extension.features.placeholders;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.managers.PlayerStat;
import com.walrusone.skywarsreloaded.menus.playeroptions.*;
import com.walrusone.skywarsreloaded.utilities.Party;
import com.walrusone.skywarsreloaded.utilities.SWRServer;
import com.walrusone.skywarsreloaded.utilities.Util;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.gaagjescraft.network.team.skywarsreloaded.extension.features.autojoin.AutoRejoin;
import me.gaagjescraft.network.team.skywarsreloaded.extension.files.PlayerFile;
import me.gaagjescraft.network.team.skywarsreloaded.extension.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PAPIPlaceholders extends PlaceholderExpansion {

    public PAPIPlaceholders() {
        try {
            Optional<PlaceholderExpansion> ex = PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().findExpansionByIdentifier("swr");
            ex.ifPresent(PlaceholderExpansion::unregister);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.register();
    }

    @Override
    public boolean persist() {
        return true;
    }


    @Override
    public String getIdentifier() {
        return "swr";
    }

    @Override
    public String getAuthor() {
        return "GaagjesCraft Network Team";
    }

    @Override
    public String getVersion() {
        return "Extension-1.0.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if (identifier.equalsIgnoreCase("players") ||
                identifier.equalsIgnoreCase("players_solo") ||
                identifier.equalsIgnoreCase("players_team")) {
            int i = 0;
            if (SkyWarsReloaded.getCfg().bungeeMode() && SkyWarsReloaded.getCfg().isLobbyServer()) {
                for (SWRServer s : SWRServer.getServersCopy()) {
                    if (identifier.toLowerCase().contains("team")) {
                        if (s.getTeamSize() > 1) i += s.getPlayerCount();
                    } else if (identifier.toLowerCase().contains("solo")) {
                        if (s.getTeamSize() == 1) i += s.getPlayerCount();
                    } else i += s.getPlayerCount();
                }
            } else {
                for (GameMap s : GameMap.getMapsCopy()) {
                    if (identifier.toLowerCase().contains("team")) {
                        if (s.getTeamSize() > 1) i += s.getPlayerCount();
                    } else if (identifier.toLowerCase().contains("solo")) {
                        if (s.getTeamSize() == 1) i += s.getPlayerCount();
                    } else i += s.getPlayerCount();
                }
            }
            return Integer.toString(i);
        } else if (identifier.toLowerCase().startsWith("players_")) {
            GameMap map = GameMap.getMap(StringUtils.replaceIgnoreCase(identifier, "playing_", ""));
            if (map != null) return Integer.toString(map.getPlayerCount());
            return null;
        } else if (identifier.toLowerCase().startsWith("spectators_")) {
            GameMap map = GameMap.getMap(StringUtils.replaceIgnoreCase(identifier, "spectators_", ""));
            if (map != null) return Integer.toString(map.getSpectators().size());
            return null;
        } else if (identifier.toLowerCase().startsWith("status_")) {
            GameMap map = GameMap.getMap(StringUtils.replaceIgnoreCase(identifier, "status_", ""));
            if (map != null) return map.getMatchState().name();
            return null;
        } else if (identifier.toLowerCase().startsWith("creator_")) {
            GameMap map = GameMap.getMap(StringUtils.replaceIgnoreCase(identifier, "creator_", ""));
            if (map != null) return map.getDesigner();
            return null;
        } else if (identifier.toLowerCase().startsWith("voted_chest_")) {
            GameMap map = GameMap.getMap(StringUtils.replaceIgnoreCase(identifier, "voted_chest_", ""));
            if (map != null)
                if (map.getChestOption() != null) return map.getChestOption().getKey().replace("CHEST", "");
                else return map.getDefaultChestType().name().replace("CHEST", "");
            return null;
        } else if (identifier.toLowerCase().startsWith("voted_weather_")) {
            GameMap map = GameMap.getMap(StringUtils.replaceIgnoreCase(identifier, "voted_weather_", ""));
            if (map != null)
                if (map.getWeatherOption() != null) return map.getWeatherOption().getKey().replace("WEATHER", "");
                else return map.getDefaultWeather().name().replace("WEATHER", "");
            return null;
        } else if (identifier.toLowerCase().startsWith("voted_health_")) {
            GameMap map = GameMap.getMap(StringUtils.replaceIgnoreCase(identifier, "voted_health_", ""));
            if (map != null)
                if (map.getHealthOption() != null) return map.getHealthOption().getKey().replace("HEALTH", "");
                else return map.getDefaultHealth().name().replace("HEALTH", "");
            return null;
        } else if (identifier.toLowerCase().startsWith("voted_modifier_")) {
            GameMap map = GameMap.getMap(StringUtils.replaceIgnoreCase(identifier, "voted_modifier_", ""));
            if (map != null)
                if (map.getModifierOption() != null) return map.getModifierOption().getKey().replace("MODIFIER", "");
                else return map.getDefaultModifier().name().replace("MODIFIER", "");
            return null;
        } else if (identifier.toLowerCase().startsWith("voted_time_")) {
            GameMap map = GameMap.getMap(StringUtils.replaceIgnoreCase(identifier, "voted_time_", ""));
            if (map != null)
                if (map.getTimeOption() != null) return map.getTimeOption().getKey().replace("TIME", "");
                else return map.getDefaultTime().name().replace("TIME", "");
            return null;
        } else if (identifier.toLowerCase().startsWith("timer_")) {
            GameMap map = GameMap.getMap(StringUtils.replaceIgnoreCase(identifier, "timer_", ""));
            if (map != null) return Integer.toString(map.getTimer());
            return null;
        } else if (identifier.toLowerCase().startsWith("timer_formatted_")) {
            GameMap map = GameMap.getMap(StringUtils.replaceIgnoreCase(identifier, "timer_formatted_", ""));
            if (map != null) return Util.get().getFormattedTime(map.getTimer());
            return null;
        }


        if (p == null) {
            return null;
        }

        Party party = Party.getParty(p);
        if (party != null) {
            if (identifier.equalsIgnoreCase("party_name")) {
                return party.getPartyName();
            } else if (identifier.equalsIgnoreCase("party_leader")) {
                return Bukkit.getPlayer(party.getLeader()).getName();
            } else if (identifier.equalsIgnoreCase("party_size")) {
                return Integer.toString(party.getSize());
            }
        }
        if (identifier.equalsIgnoreCase("party_in_one")) {
            if (party != null) return "true";
            else return "false";
        }

        AutoRejoin autojoin = AutoRejoin.fromPlayer(p);
        if (autojoin != null) {
            if (identifier.equalsIgnoreCase("autojoin_owner")) {
                return autojoin.getOwner().getName();
            } else if (identifier.equalsIgnoreCase("autojoin_game_type")) {
                return autojoin.getType().name();
            }
        }
        if (identifier.equalsIgnoreCase("autojoin_enabled")) {
            if (autojoin != null) return "true";
            else return "false";
        }

        PlayerStat stat = SkyWarsReloaded.get().getPlayerStat(p);
        if (identifier.equalsIgnoreCase("wins")) {
            return "" + stat.getWins();
        } else if (identifier.equalsIgnoreCase("losses")) {
            return "" + stat.getLosses();
        } else if (identifier.equalsIgnoreCase("kills")) {
            return "" + stat.getKills();
        } else if (identifier.equalsIgnoreCase("deaths")) {
            return "" + stat.getDeaths();
        } else if (identifier.equalsIgnoreCase("xp")) {
            return "" + stat.getXp();
        } else if (identifier.equalsIgnoreCase("games_played") ||
                identifier.equalsIgnoreCase("games")) {
            return "" + (stat.getWins() + stat.getLosses());
        } else if (identifier.equalsIgnoreCase("level")) {
            return "" + Util.get().getPlayerLevel(p, false);
        } else if (identifier.equalsIgnoreCase("kill_death")) {
            double stat1 = (double) stat.getKills() / (double) stat.getDeaths();
            return String.format("%1$,.2f", stat1);
        } else if (identifier.equalsIgnoreCase("win_loss")) {
            double stat1 = (double) stat.getWins() / (double) stat.getLosses();
            return String.format("%1$,.2f", stat1);
        } else if (identifier.equalsIgnoreCase("game")) {
            GameMap m = MatchManager.get().getPlayerMap(p);
            if (m == null) return "none";
            else return m.getDisplayName();
        } else if (identifier.equalsIgnoreCase("ingame")) {
            if (MatchManager.get().getPlayerMap(p) == null) return "false";
            else return "true";
        } else if (identifier.equalsIgnoreCase("kit")) {
            GameMap m = MatchManager.get().getPlayerMap(p);
            PlayerFile pf = new PlayerFile(p);
            if (m != null) {
                if (m.getSelectedKit(p) != null) return m.getSelectedKit(p).getColorName();
                else if (pf.getLatestKit() != null) return pf.getLatestKit().getColorName();
            }
            return "none";
        } else if (identifier.equalsIgnoreCase("cage") || identifier.equalsIgnoreCase("glasscolor")) {
            PlayerOption option = GlassColorOption.getPlayerOptionByKey(stat.getGlassColor());
            if (option == null) return "none";
            return ChatColor.translateAlternateColorCodes('&', option.getName());
        } else if (identifier.equalsIgnoreCase("killsound")) {
            PlayerOption option = KillSoundOption.getPlayerOptionByKey(stat.getKillSound());
            if (option == null) return "none";
            return ChatColor.translateAlternateColorCodes('&', option.getName());
        } else if (identifier.equalsIgnoreCase("winsound")) {
            PlayerOption option = WinSoundOption.getPlayerOptionByKey(stat.getWinSound());
            if (option == null) return "none";
            return ChatColor.translateAlternateColorCodes('&', option.getName());
        } else if (identifier.equalsIgnoreCase("particle_effect")) {
            PlayerOption option = ParticleEffectOption.getPlayerOptionByKey(stat.getParticleEffect());
            if (option == null) return "none";
            return ChatColor.translateAlternateColorCodes('&', option.getName());
        } else if (identifier.equalsIgnoreCase("projectile_effect")) {
            PlayerOption option = ProjectileEffectOption.getPlayerOptionByKey(stat.getProjectileEffect());
            if (option == null) return "none";
            return ChatColor.translateAlternateColorCodes('&', option.getName());
        } else if (identifier.equalsIgnoreCase("taunt")) {
            PlayerOption option = TauntOption.getPlayerOptionByKey(stat.getTaunt());
            if (option == null) return "none";
            return ChatColor.translateAlternateColorCodes('&', option.getName());
        }

        return null;
    }
}
