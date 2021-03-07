package me.gaagjescraft.network.team.skywarsreloaded.extension.features.autojoin;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.enums.GameType;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Party;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.gaagjescraft.network.team.skywarsreloaded.extension.npcs.NPCHandler.getSortedGames;

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
            if (owner != null) owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.canceled")));
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

    /**
     *
     * @return -1 if unknown error, 0 if no maps, 1 if waiting for other players, 2 if success
     */
    public void attemptJoin(boolean force) {
        if (owner == null) {
            return;
        }

        List<GameMap> maps = GameMap.getPlayableArenas(type);
        if (maps.isEmpty()) {
            if (party == null) owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.no_arena_single")));
            else owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.no_arena_party")));
            return;
        }

        HashMap<GameMap, Integer> sortedMaps = getSortedGames(maps);
        List<GameMap> keys = Lists.newArrayList(sortedMaps.keySet());
        GameMap map = keys.get(0);

        int playerAmount = (getParty() == null ? 1 : getParty().getSize());

        for (int i=0;i<=keys.size();i++) {
            if (map.getPlayerCount() + playerAmount <= map.getMaxPlayers()) {
                break;
            }
            else if (i == keys.size()) {
                if (party == null) owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.no_arena_single")));
                else owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.no_arena_party")));
                return;
            } else {
                map = keys.get(i);
            }
        }

        if (party == null) {
            // player is alone.
            if (map == null) {
                owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.no_arena_single")));
                return;
            }

            final GameMap mapFinal = map;
            owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.searching_game")));
            Bukkit.getScheduler().scheduleSyncDelayedTask(SWExtension.get(), () -> {
                AutoRejoinHandler.cancelTeleport.add(owner.getUniqueId());
                MatchManager.get().playerLeave(owner, EntityDamageEvent.DamageCause.CUSTOM, true, false, true);
                AutoRejoinHandler.cancelTeleport.remove(owner.getUniqueId());
                boolean result = mapFinal.addPlayers(null, owner);

                if (result) owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.game_found")));
                else owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.no_arena_single")));
            },60L);
        }
        else {
            // player has company
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
                    MatchManager.get().playerLeave(Bukkit.getPlayer(uid), EntityDamageEvent.DamageCause.CUSTOM, true, false, true);
                    AutoRejoinHandler.cancelTeleport.remove(uid);
                }
                boolean result = map.addPlayers(null, party);
                if (result) owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.game_found")));
                else owner.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.no_arena_party")));
            }
        }
    }

}
