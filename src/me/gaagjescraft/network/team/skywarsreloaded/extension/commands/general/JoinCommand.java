package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.general;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.GameType;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.TeamCard;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.SWRServer;
import com.walrusone.skywarsreloaded.utilities.Util;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import me.gaagjescraft.network.team.skywarsreloaded.extension.NoArenaAction;
import me.gaagjescraft.network.team.skywarsreloaded.extension.menus.SingleJoinMenu;
import me.gaagjescraft.network.team.skywarsreloaded.extension.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static me.gaagjescraft.network.team.skywarsreloaded.extension.npcs.NPCHandler.getSortedGames;

public class JoinCommand implements Listener {

    private String c(String a) {
        return ChatColor.translateAlternateColorCodes('&', a);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        method(e.getPlayer(), e, e.getMessage());
    }

    @EventHandler
    public void onCommand(ServerCommandEvent e) {
        if (e.getSender() instanceof Player) {
            method((Player) e.getSender(), e, e.getCommand());
        }
    }

    public void method(Player player, Cancellable e, String message) {

        if (StringUtils.startsWithIgnoreCase(message, "/sw join") || StringUtils.startsWithIgnoreCase(message, "/skywars join") ||
                StringUtils.startsWithIgnoreCase(message, "sw join") || StringUtils.startsWithIgnoreCase(message, "skywars join")) {
            String[] a = message.split(" ");

            if (a.length == 2) {
                NoArenaAction action = NoArenaAction.valueOf(Main.get().getConfig().getString("no_arena_specified_action"));
                if (action == NoArenaAction.OPEN_CUSTOM_JOIN_MENU) {
                    e.setCancelled(true);
                    new SingleJoinMenu().openMenu(player, 1);
                } else if (action == NoArenaAction.OPEN_JOIN_MENU) {
                    e.setCancelled(true);
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
                            return;
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
                            return;
                        }

                        SkyWarsReloaded.getIC().show(player, "joinmenu");
                    } else {
                        new SingleJoinMenu().openMenu(player, 1);
                    }
                } else if (action == NoArenaAction.SEND_MESSAGE) {
                    e.setCancelled(true);
                    player.sendMessage(c(Main.get().getConfig().getString("no_arena_specified_message")));
                }
            } else if (a.length == 3) {
                e.setCancelled(true);
                String name = a[2];

                if (name.equalsIgnoreCase("solo")) {
                    if (player.hasPermission("sw.join.solo")) {
                        int highest = 0;
                        List<GameMap> maps = GameMap.getPlayableArenas(GameType.SINGLE);

                        if (maps.isEmpty()) {
                            player.sendMessage(c(Main.get().getConfig().getString("no_solo_arenas")));
                            return;
                        }
                        player.sendMessage(c(Main.get().getConfig().getString("solo_join")));

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
                            player.sendMessage(c(Main.get().getConfig().getString("joined_arena").replace("%name%", map.getName())));
                        } else {
                            player.sendMessage((new Messaging.MessageFormatter()).format("error.could-not-join2"));
                        }
                    } else {
                        player.sendMessage(c(Main.get().getConfig().getString("no_permission")));
                    }
                    return;
                } else if (name.equalsIgnoreCase("team")) {
                    if (player.hasPermission("sw.join.team")) {
                        List<GameMap> maps = GameMap.getPlayableArenas(GameType.TEAM);

                        if (maps.isEmpty()) {
                            player.sendMessage(c(Main.get().getConfig().getString("no_team_arenas")));
                            return;
                        }
                        player.sendMessage(c(Main.get().getConfig().getString("team_join")));

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
                            player.sendMessage(c(Main.get().getConfig().getString("joined_arena").replace("%name%", map.getName())));
                        } else {
                            player.sendMessage((new Messaging.MessageFormatter()).format("error.could-not-join2"));
                        }
                    } else {
                        player.sendMessage(c(Main.get().getConfig().getString("no_permission")));
                    }
                    return;
                }

                if (player.hasPermission("sw.join.arena")) {
                    // Here i'm getting the name of the arena that has been specified.
                    // this is stored in the a variable we just made
                    // this starts with 0 as you see above, so the third argument is actually the second one.
                    if (SkyWarsReloaded.getCfg().bungeeMode() && SkyWarsReloaded.getCfg().isLobbyServer()) {
                        SWRServer server = null;
                        for (int i = 0; i < SWRServer.getServers().size(); i++) {
                            if (SWRServer.getServers().get(i).getServerName().equals(name)) {
                                server = SWRServer.getServers().get(i);
                            }
                        }

                        if (server != null) {
                            if ((server.getMatchState() == MatchState.WAITINGSTART || server.getMatchState() == MatchState.WAITINGLOBBY) && server.canAddPlayer()) {
                                server.setPlayerCount(server.getPlayerCount() + 1);
                                server.updateSigns();
                                SkyWarsReloaded.get().sendBungeeMsg(player, "Connect", server.getServerName());
                                player.sendMessage(c(Main.get().getConfig().getString("joined_arena").replace("%name%", name)));
                            } else {
                                // sending a message because the map is unplayable
                                player.sendMessage(c(Main.get().getConfig().getString("cannot_join")));
                            }
                        } else {
                            // sending a message because the arena doesn't exist
                            player.sendMessage(c(Main.get().getConfig().getString("invalid_arena")));
                        }
                    } else {
                        GameMap map = null;
                        for (int i = 0; i < GameMap.getMaps().size(); i++) {
                            if (GameMap.getMaps().get(i).getName().equals(name)) {
                                map = GameMap.getMaps().get(i);
                            }
                        }
                        // here i'm creating a new game variable
                        // here i'm checking if the game is valid
                        if (map != null) {
                            // here i'm checking if I can add a player to the game
                            // so if the game is playable (not ingame) and if it's not full
                            if ((map.getMatchState() == MatchState.WAITINGSTART || map.getMatchState() == MatchState.WAITINGLOBBY) && map.canAddPlayer()) {
                                // here i'm actually adding the player to the map. So like joining it
                                boolean b = map.addPlayers((TeamCard) null, player);
                                if (b) {
                                    player.sendMessage(c(Main.get().getConfig().getString("joined_arena").replace("%name%", name)));
                                } else {
                                    player.sendMessage((new Messaging.MessageFormatter()).format("error.could-not-join2"));
                                }
                            } else {
                                // sending a message because the map is unplayable
                                player.sendMessage(c(Main.get().getConfig().getString("cannot_join")));
                            }
                        } else {
                            // sending a message because the arena doesn't exist
                            player.sendMessage(c(Main.get().getConfig().getString("invalid_arena")));
                        }
                    }
                } else {
                    player.sendMessage(c(Main.get().getConfig().getString("no_permission")));
                }
            } else if (a.length > 3) {
                e.setCancelled(true);
                // the specified command length here is more than 3, so I'm sending a message that the player gave too many arguments
                player.sendMessage(c(Main.get().getConfig().getString("too_many_join_arguments")));
            }
        }
    }

}
