package me.gaagjescraft.network.team.skywarsreloaded.extension.features.autojoin;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.GameType;
import com.walrusone.skywarsreloaded.enums.PlayerRemoveReason;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Party;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import me.gaagjescraft.network.team.skywarsreloaded.extension.utils.SWUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AutoRejoin {

    public static List<AutoRejoin> autoRejoins = Lists.newArrayList();

    private Player owner;
    private Party party;
    private GameType type;
    private Date startDate;

    public AutoRejoin(Player owner, Party party, GameType type) {
        this.owner = owner;
        this.party = party;
        this.type = type;
        this.startDate = new Date(Instant.now().toEpochMilli());
        autoRejoins.add(this);

        Bukkit.getScheduler().runTaskLater(SWExtension.get(), () -> {
            autoRejoins.remove(this);
            if (owner != null)
                owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.canceled")));
        }, 60 * 60 * 20);
    }

    public static AutoRejoin fromPlayer(Player player) {
        for (AutoRejoin a : autoRejoins) {
            if (a.getOwner().equals(player)) return a;
            if (a.getParty() != null && a.getParty().getMembers().contains(player.getUniqueId())) return a;
        }
        return null;
    }

    public boolean isOwner(Player player) {
        return owner.equals(player);
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public GameType getType() {
        return type;
    }

    public void setType(GameType type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }


    public void attemptJoin(boolean force) {
        this.attemptJoin(force, 0);
    }

    public void attemptJoin(boolean force, int previousAttempts) {
        if (owner == null) {
            return;
        }

        // Get all available maps
        List<GameMap> maps = GameMap.getPlayableArenas(type);

        // Check maps exist
        if (maps.size() < 1) {
            sendCantJoinNewGame();
            return;
        }

        // Sort the maps by player count
        HashMap<GameMap, Integer> sortedMaps = SWUtils.getSortedGames(maps);
        List<GameMap> keys = Lists.newArrayList(sortedMaps.keySet());


        // Get the game with most players as default
        GameMap map = keys.get(0);

        // Number of players to fit into the map
        int playerAmount = (getParty() == null ? 1 : getParty().getSize());

        // Find a map with enough space
        for (int i = 0; i <= keys.size(); ++i) {
            int totalWithNewPlayers = map.getPlayerCount() + playerAmount;

            if (totalWithNewPlayers <= map.getMaxPlayers()) {
                map = keys.get(i);
                break;
            }
        }

        // No map found, alert user but don't abort. There is a second try later.
        if (map == null) {
            sendCantJoinNewGame();
        }

        // player is alone.
        if (party == null) {
            final GameMap mapFinal = map;

            if (map == null) {
                // Only allow 2 fails total
                if (previousAttempts > 0) return;

                // Haven't failed yet? Try again in a few seconds
                owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.searching_game")));
                Bukkit.getScheduler().scheduleSyncDelayedTask(
                        SWExtension.get(),
                        () -> attemptJoin(force, 1),
                        5 * 20L
                ); // 5 seconds before retry
            } else {
                // Task to rejoin new game
                Runnable joinNewGame = () -> {
                    AutoRejoinHandler.cancelTeleport.add(owner.getUniqueId());
                    SkyWarsReloaded.get().getPlayerManager().removePlayer(owner, PlayerRemoveReason.PLAYER_QUIT_GAME, null, true);
                    AutoRejoinHandler.cancelTeleport.remove(owner.getUniqueId());
                    boolean result = mapFinal.addPlayers(null, owner);

                    if (result)
                        owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.game_found")));
                    else
                        sendCantJoinNewGame();
                };

                Bukkit.getScheduler().scheduleSyncDelayedTask(SWExtension.get(), joinNewGame, 20L); // 1 second later
            }
        }
        // player has party
        else {
            boolean canJoin = false;
            if (!force) {
                for (UUID uid : party.getMembers()) {
                    Player player = Bukkit.getPlayer(uid);
                    if (player == null) {
                        canJoin = true;
                    } else {
                        GameMap pmap = MatchManager.get().getDeadPlayerMap(player);
                        canJoin = MatchManager.get().getPlayerMap(player) == null || map.getSpectators().contains(uid) && !pmap.getAlivePlayers().contains(player);

                        if (!canJoin) {
                            owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.waiting_party")));
                            return;
                        }
                    }
                }
            }

            if (canJoin || force) {
                for (UUID uid : party.getMembers()) {
                    AutoRejoinHandler.cancelTeleport.add(uid);
                    SkyWarsReloaded.get().getPlayerManager().removePlayer(Bukkit.getPlayer(uid), PlayerRemoveReason.PLAYER_QUIT_GAME, null, true);
                    AutoRejoinHandler.cancelTeleport.remove(uid);
                }
                boolean result = map.addPlayers(null, party);
                if (result)
                    owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.game_found")));
                else
                    sendCantJoinNewGame();
            }
        }
    }

    private void sendCantJoinNewGame() {
        if (party == null) {
            owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.no_arena_single")));
        } else {
            for (UUID uid : party.getMembers()) {
                Player player = Bukkit.getPlayer(uid);
                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.no_arena_party")));
            }
        }
    }

}
