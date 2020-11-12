package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.player;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.enums.GameType;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.TeamCard;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.SWRServer;
import com.walrusone.skywarsreloaded.utilities.Util;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import me.gaagjescraft.network.team.skywarsreloaded.extension.NoArenaAction;
import me.gaagjescraft.network.team.skywarsreloaded.extension.menus.SingleJoinMenu;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static me.gaagjescraft.network.team.skywarsreloaded.extension.npcs.NPCHandler.getSortedGames;

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
    public boolean run() {
        GameMap a = MatchManager.get().getPlayerMap(player);
        if (a != null) {
            player.sendMessage(Main.c(Main.get().getConfig().getString("already_ingame")));
            return true;
        }

        if (args.length == 1) {
            NoArenaAction action = NoArenaAction.valueOf(Main.get().getConfig().getString("no_arena_specified_action"));
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
                player.sendMessage(Main.c(Main.get().getConfig().getString("no_arena_specified_message")));
                return true;
            }
            else {
                joinGame(player, GameType.ALL);
                return true;
            }
        }

        else if (args.length == 2) {
            String arena = args[1];
            if (arena.equalsIgnoreCase("solo") || arena.equalsIgnoreCase("single")) {
                if (player.hasPermission("sw.join.solo")) {
                    joinGame(player, GameType.SINGLE);
                } else {
                    player.sendMessage(Main.c(Main.get().getConfig().getString("no_permission")));
                }
                return true;
            }
            else if (arena.equalsIgnoreCase("team") || arena.equalsIgnoreCase("teams")) {
                if (player.hasPermission("sw.join.team")) {
                    joinGame(player, GameType.TEAM);
                } else {
                    player.sendMessage(Main.c(Main.get().getConfig().getString("no_permission")));
                }
                return true;
            }
            else {
                if (player.hasPermission("sw.join.arena")) {
                    if (SkyWarsReloaded.getCfg().bungeeMode() && SkyWarsReloaded.getCfg().isLobbyServer()) {
                        SWRServer server = null;
                        for (int i = 0; i < SWRServer.getServers().size(); i++) {
                            if (SWRServer.getServers().get(i).getServerName().equals(arena)) {
                                server = SWRServer.getServers().get(i);
                            }
                        }

                        if (server != null) {
                            if ((server.getMatchState() == MatchState.WAITINGSTART || server.getMatchState() == MatchState.WAITINGLOBBY) && server.canAddPlayer()) {
                                server.setPlayerCount(server.getPlayerCount() + 1);
                                server.updateSigns();
                                SkyWarsReloaded.get().sendBungeeMsg(player, "Connect", server.getServerName());
                                player.sendMessage(Main.c(Main.get().getConfig().getString("joined_arena").replace("%name%", arena)));
                            } else {
                                // sending a message because the map is unplayable
                                player.sendMessage(Main.c(Main.get().getConfig().getString("cannot_join")));
                            }
                        } else {
                            // sending a message because the arena doesn't exist
                            player.sendMessage(Main.c(Main.get().getConfig().getString("invalid_arena")));
                        }
                    }
                    else {
                        GameMap map = GameMap.getMap(arena);
                        // here i'm creating a new game variable
                        // here i'm checking if the game is valid
                        if (map != null) {
                            // here i'm checking if I can add a player to the game
                            // so if the game is playable (not ingame) and if it's not full
                            if ((map.getMatchState() == MatchState.WAITINGSTART || map.getMatchState() == MatchState.WAITINGLOBBY) && map.canAddPlayer()) {
                                // here i'm actually adding the player to the map. So like joining it
                                boolean b = map.addPlayers((TeamCard) null, player);
                                if (b) {
                                    player.sendMessage(Main.c(Main.get().getConfig().getString("joined_arena").replace("%name%", arena)));
                                } else {
                                    player.sendMessage((new Messaging.MessageFormatter()).format("error.could-not-join2"));
                                }
                            } else {
                                // sending a message because the map is unplayable
                                player.sendMessage(Main.c(Main.get().getConfig().getString("cannot_join")));
                            }
                        } else {
                            // sending a message because the arena doesn't exist
                            player.sendMessage(Main.c(Main.get().getConfig().getString("invalid_arena")));
                        }
                    }
                }
                return true;
            }
        }
        return true;
    }

    private void joinGame(Player player, GameType type) {
        List<GameMap> maps = GameMap.getPlayableArenas(type);
        if (maps.isEmpty()) {
            if (type == GameType.SINGLE) {
                player.sendMessage(Main.c(Main.get().getConfig().getString("no_solo_arenas")));
            }
            else if (type == GameType.TEAM) {
                player.sendMessage(Main.c(Main.get().getConfig().getString("no_team_arenas")));
            }
            else {
                player.sendMessage(Main.c(Main.get().getConfig().getString("no_arenas_found")));
            }
            return;
        }

        if (type == GameType.SINGLE) {
            player.sendMessage(Main.c(Main.get().getConfig().getString("solo_join")));
        }
        else if (type == GameType.TEAM) {
            player.sendMessage(Main.c(Main.get().getConfig().getString("team_join")));
        }
        else {
            player.sendMessage(Main.c(Main.get().getConfig().getString("join_random_arena")));
        }

        HashMap<GameMap, Integer> sortedMaps = getSortedGames(maps);
        List<GameMap> keys = Lists.newArrayList(sortedMaps.keySet());
        GameMap map = keys.get(0);

        if (sortedMaps.get(map) == 0) {
            // there is no game with players waiting, selecting a random game
            map = keys.get(new Random().nextInt(keys.size()));
        }


        boolean b = false;
        if (map != null) {
            b = map.addPlayers((TeamCard) null, player);
        }
        if (b) {
            player.sendMessage(Main.c(Main.get().getConfig().getString("joined_arena").replace("%name%", map.getName())));
        } else {
            player.sendMessage((new Messaging.MessageFormatter()).format("error.could-not-join2"));
        }
    }

}
