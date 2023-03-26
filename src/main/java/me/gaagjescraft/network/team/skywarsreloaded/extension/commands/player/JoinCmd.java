package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.player;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.enums.GameType;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.enums.PlayerRemoveReason;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Party;
import com.walrusone.skywarsreloaded.utilities.SWRServer;
import com.walrusone.skywarsreloaded.utilities.Util;
import me.gaagjescraft.network.team.skywarsreloaded.extension.NoArenaAction;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import me.gaagjescraft.network.team.skywarsreloaded.extension.menus.SingleJoinMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static me.gaagjescraft.network.team.skywarsreloaded.extension.utils.SWUtils.getSortedGames;

public class JoinCmd extends BaseCmd {

    public JoinCmd(String t) {
        type = t;
        forcePlayer = true;
        cmdName = "join";
        alias = new String[]{"j"};
        argLength = 1;
        maxArgs = 2;
    }

    @Override
    public boolean run(CommandSender sender, Player player, String[] args) {
        GameMap a = MatchManager.get().getPlayerMap(player);
        if (a != null) {
            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("already_ingame")));
            return true;
        }

        if (args.length == 1) {
            NoArenaAction action = NoArenaAction.valueOf(SWExtension.get().getConfig().getString("no_arena_specified_action"));
            if (action == NoArenaAction.OPEN_CUSTOM_JOIN_MENU) {
                new SingleJoinMenu().openMenu(player, 1);
                return true;
            } else if (action == NoArenaAction.OPEN_JOIN_MENU) {

                if (SkyWarsReloaded.getCfg().joinMenuEnabled()) {
                    Util.get().playSound(player, player.getLocation(), SkyWarsReloaded.getCfg().getOpenJoinMenuSound(), 1.0F, 1.0F);
                    if (GameMap.getPlayableArenas(GameType.TEAM).size() == 0) {
                        if (!SkyWarsReloaded.getIC().hasViewers("joinsinglemenu")) {
                            (new BukkitRunnable() {
                                public void run() {
                                    SkyWarsReloaded.getIC().getMenu("joinsinglemenu").update();
                                }
                            }).runTaskLater(SkyWarsReloaded.get(), 5L);
                        }

                        SkyWarsReloaded.getIC().show(player, "joinsinglemenu");
                        return true;
                    }

                    if (GameMap.getPlayableArenas(GameType.SINGLE).size() == 0) {
                        if (!SkyWarsReloaded.getIC().hasViewers("jointeammenu")) {
                            (new BukkitRunnable() {
                                public void run() {
                                    SkyWarsReloaded.getIC().getMenu("jointeammenu").update();
                                }
                            }).runTaskLater(SkyWarsReloaded.get(), 5L);
                        }

                        SkyWarsReloaded.getIC().show(player, "jointeammenu");
                        return true;
                    }

                    SkyWarsReloaded.getIC().show(player, "joinmenu");
                }
                else {
                    new SingleJoinMenu().openMenu(player, 1);
                    return true;
                }
            }
            else if (action == NoArenaAction.SEND_MESSAGE) {
                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("no_arena_specified_message")));
                return true;
            }
            else {
                joinGame(player, GameType.ALL, null);
                return true;
            }
        }

        else if (args.length == 2) {
            String arena = args[1];
            if (arena.equalsIgnoreCase("solo") || arena.equalsIgnoreCase("single")) {
                if (player.hasPermission("sw.join.solo")) {
                    joinGame(player, GameType.SINGLE, null);
                } else {
                    player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("no_permission")));
                }
                return true;
            }
            else if (arena.equalsIgnoreCase("team") || arena.equalsIgnoreCase("teams")) {
                if (player.hasPermission("sw.join.team")) {
                    joinGame(player, GameType.TEAM, null);
                } else {
                    player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("no_permission")));
                }
                return true;
            }
            else {
                if (player.hasPermission("sw.join.arena")) {
                    if (SkyWarsReloaded.getCfg().bungeeMode() && SkyWarsReloaded.getCfg().isLobbyServer()) {
                        SWRServer server = null;
                        for (int i = 0; i < SWRServer.getServersCopy().size(); i++) {
                            if (SWRServer.getServersCopy().get(i).getServerName().equals(arena)) {
                                server = SWRServer.getServersCopy().get(i);
                            }
                        }

                        if (server != null) {
                            if ((server.getMatchState() == MatchState.WAITINGSTART || server.getMatchState() == MatchState.WAITINGLOBBY) && server.canAddPlayer()) {
                                server.setPlayerCount(server.getPlayerCount() + 1);
                                server.updateSigns();
                                SkyWarsReloaded.get().sendBungeeMsg(player, "Connect", server.getServerName());
                                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("joined_arena").replace("%name%", arena)));
                            } else {
                                // sending a message because the map is unplayable
                                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("cannot_join")));
                            }
                        } else {
                            // sending a message because the arena doesn't exist
                            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("invalid_arena")));
                        }
                    }
                    else {
                        GameMap map = GameMap.getMap(arena);
                        if (map != null) {
                            if ((map.getMatchState() == MatchState.WAITINGSTART || map.getMatchState() == MatchState.WAITINGLOBBY) && map.canAddPlayer()) {
                                joinGame(player,GameType.ALL,arena);
                            } else {
                                // sending a message because the map is unplayable
                                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("cannot_join")));
                            }
                        } else {
                            // sending a message because the arena doesn't exist
                            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("invalid_arena")));
                        }
                    }
                }
                return true;
            }
        }
        return true;
    }

    private static void sendNoArenaAvailableMsg(Player player, GameType type) {
        if (type == GameType.SINGLE) {
            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("no_solo_arenas")));
        }
        else if (type == GameType.TEAM) {
            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("no_team_arenas")));
        }
        else {
            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("no_arenas_found")));
        }
    }

    public static void joinGame(Player player, GameType type, @Nullable String arenaName) {
        Party party = Party.getParty(player);
        if (party != null && !party.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.must_be_leader")));
            return;
        }

        List<GameMap> maps = arenaName == null ? GameMap.getPlayableArenas(type) : Lists.newArrayList(GameMap.getMap(arenaName));
        if (maps.isEmpty()) {
            sendNoArenaAvailableMsg(player, type);
            return;
        }
        else if (party != null) {
            maps.removeIf(game-> game.getPlayerCount()+party.getSize() > game.getMaxPlayers());
            if (maps.isEmpty()) {
                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.no_arena_party")));
                return;
            }
            else {
                for (UUID uid : party.getMembers()) {
                    SkyWarsReloaded.get().getPlayerManager().removePlayer(
                            Bukkit.getPlayer(uid),
                            PlayerRemoveReason.PLAYER_QUIT_GAME,
                            EntityDamageEvent.DamageCause.CUSTOM,
                            true);
                }
            }
        }

        if (type == GameType.SINGLE) {
            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("solo_join")));
        }
        else if (type == GameType.TEAM) {
            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("team_join")));
        }
        else {
            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("join_random_arena")));
        }

        HashMap<GameMap, Integer> sortedMaps = getSortedGames(maps);
        List<GameMap> keys = Lists.newArrayList(sortedMaps.keySet());

        if (keys.isEmpty()) {
            sendNoArenaAvailableMsg(player, type);
            return;
        }

        GameMap map = keys.get(0);

        if (sortedMaps.get(map) == 0) {
            // there is no game with players waiting, selecting a random game
            map = keys.get(new Random().nextInt(keys.size()));
        }


        boolean b = false;
        if (map != null) {
            if (party != null) b = map.addPlayers(null, party);
            else b = map.addPlayers(null, player);

            if (b) {
                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("joined_arena").replace("%name%", map.getName())));
                return;
            }
        }

        if (arenaName == null)
            player.sendMessage((new Messaging.MessageFormatter()).format("error.could-not-join2"));

    }

}
