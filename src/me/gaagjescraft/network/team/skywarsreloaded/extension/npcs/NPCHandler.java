package me.gaagjescraft.network.team.skywarsreloaded.extension.npcs;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.GameType;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.TeamCard;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.Util;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import me.gaagjescraft.network.team.skywarsreloaded.extension.menus.SingleJoinMenu;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCTeleportEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.gaagjescraft.network.team.skywarsreloaded.extension.npcs.NPCClickAction.*;

public class NPCHandler implements Listener {

    private static HashMap<Player, Boolean> cooldown = new HashMap<>(); // whether the player is on cooldown for clicking an NPC

    public static HashMap<GameMap, Integer> getSortedGames(List<GameMap> hm) {
        HashMap<GameMap, Integer> games = new HashMap<>();
        for (GameMap g : hm) {
            if (g.canAddPlayer()) games.put(g, g.getAllPlayers().size());
        }

        // Create a list from elements of HashMap
        List<Map.Entry<GameMap, Integer>> list = new LinkedList<>(games.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<GameMap, Integer>>() {
            public int compare(Map.Entry<GameMap, Integer> o1,
                               Map.Entry<GameMap, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<GameMap, Integer> temp = new LinkedHashMap<GameMap, Integer>();
        for (Map.Entry<GameMap, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private String c(String a) {
        return ChatColor.translateAlternateColorCodes('&', a);
    }

    public void createNPC(Location loc, NPCClickAction action) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "LukasIsTheName");
        npc.spawn(loc);

        NPCFile file = new NPCFile();
        file.setLocation(npc.getId(), loc);
        file.setClickAction(npc.getId(), action);
    }

    @EventHandler
    public void onNPCClick(PlayerInteractEntityEvent e) {
        Entity entity = e.getRightClicked();
        NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
        if (npc != null) {
            NPCFile file = new NPCFile();
            if (file.getNPCs().contains(npc.getId())) {
                if (cooldown.getOrDefault(e.getPlayer(), false)) {
                    return;
                }
                cooldown.put(e.getPlayer(), true);
                Bukkit.getScheduler().runTaskLater(Main.get(), () -> cooldown.remove(e.getPlayer()), 20);

                int id = npc.getId();
                NPCClickAction action = file.getClickAction(id);

                if (MatchManager.get().getPlayerMap(e.getPlayer()) != null) {
                    return;
                }

                if (action == RANDOM_JOIN) {
                    List<GameMap> maps = GameMap.getPlayableArenas(GameType.ALL);

                    if (maps.isEmpty()) {
                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("no_solo_arenas")));
                        return;
                    }
                    e.getPlayer().sendMessage(c(Main.get().getConfig().getString("solo_join")));

                    HashMap<GameMap, Integer> sortedMaps = getSortedGames(maps);
                    List<GameMap> keys = Lists.newArrayList(sortedMaps.keySet());
                    GameMap map = keys.get(0);

                    if (sortedMaps.get(map) == 0) {
                        // there is no game with players waiting, selecting a random game
                        map = keys.get(new Random().nextInt(keys.size()));
                    }

                    boolean b = false;
                    if (map != null) {
                        b = map.addPlayers((TeamCard) null, e.getPlayer());
                    }
                    if (b) {
                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("joined_arena").replace("%name%", map.getName())));
                    } else {
                        e.getPlayer().sendMessage(c((new Messaging.MessageFormatter()).format("error.could-not-join2")));
                    }
                } else if (action == RANDOM_TEAM_JOIN) {
                    int highest = 0;
                    List<GameMap> maps = GameMap.getPlayableArenas(GameType.TEAM);

                    if (maps.isEmpty()) {
                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("no_team_arenas")));
                        return;
                    }
                    e.getPlayer().sendMessage(c(Main.get().getConfig().getString("team_join")));

                    HashMap<GameMap, Integer> sortedMaps = getSortedGames(maps);
                    List<GameMap> keys = Lists.newArrayList(sortedMaps.keySet());
                    GameMap map = keys.get(0);

                    if (sortedMaps.get(map) == 0) {
                        // there is no game with players waiting, selecting a random game
                        map = keys.get(new Random().nextInt(keys.size()));
                    }


                    boolean b = false;
                    if (map != null) {
                        b = map.addPlayers((TeamCard) null, e.getPlayer());
                    }
                    if (b) {
                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("joined_arena").replace("%name%", map.getName())));
                    } else {
                        e.getPlayer().sendMessage(c((new Messaging.MessageFormatter()).format("error.could-not-join2")));
                    }
                } else if (action == RANDOM_SOLO_JOIN) {
                    int highest = 0;
                    List<GameMap> maps = GameMap.getPlayableArenas(GameType.SINGLE);

                    if (maps.isEmpty()) {
                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("no_solo_arenas")));
                        return;
                    }
                    e.getPlayer().sendMessage(c(Main.get().getConfig().getString("solo_join")));

                    HashMap<GameMap, Integer> sortedMaps = getSortedGames(maps);
                    List<GameMap> keys = Lists.newArrayList(sortedMaps.keySet());
                    GameMap map = keys.get(0);

                    if (sortedMaps.get(map) == 0) {
                        // there is no game with players waiting, selecting a random game
                        map = keys.get(new Random().nextInt(keys.size()));
                    }


                    boolean b = false;
                    if (map != null) {
                        b = map.addPlayers((TeamCard) null, e.getPlayer());
                    }
                    if (b) {
                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("joined_arena").replace("%name%", map.getName())));
                    } else {
                        e.getPlayer().sendMessage(c((new Messaging.MessageFormatter()).format("error.could-not-join2")));
                    }
                } else if (action == OPEN_CUSTOM_MENU) {
                    new SingleJoinMenu().openMenu(e.getPlayer(), 1);
                } else if (action == OPEN_MENU) {
                    Util.get().playSound(e.getPlayer(), e.getPlayer().getLocation(), SkyWarsReloaded.getCfg().getOpenJoinMenuSound(), 1.0F, 1.0F);
                    if (GameMap.getPlayableArenas(GameType.TEAM).size() == 0) {
                        if (!SkyWarsReloaded.getIC().hasViewers("joinsinglemenu")) {
                            (new BukkitRunnable() {
                                public void run() {
                                    SkyWarsReloaded.getIC().getMenu("joinsinglemenu").update();
                                }
                            }).runTaskLater(SkyWarsReloaded.get(), 5L);
                        }

                        SkyWarsReloaded.getIC().show(e.getPlayer(), "joinsinglemenu");
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

                        SkyWarsReloaded.getIC().show(e.getPlayer(), "jointeammenu");
                        return;
                    }

                    SkyWarsReloaded.getIC().show(e.getPlayer(), "joinmenu");
                }

            }
        }
    }

    @EventHandler
    public void onNPCClick(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if (e.getDamager().getType() == EntityType.PLAYER) {
            Player player = (Player) e.getDamager();
            NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
            if (npc != null) {
                if (cooldown.getOrDefault(player, false)) {
                    return;
                }
                cooldown.put(player, true);
                Bukkit.getScheduler().runTaskLater(Main.get(), () -> cooldown.remove(player), 20);

                NPCFile file = new NPCFile();
                if (file.getNPCs().contains(npc.getId())) {
                    int id = npc.getId();
                    NPCClickAction action = file.getClickAction(id);

                    if (MatchManager.get().getPlayerMap(player) != null) {
                        return;
                    }

                    if (action == RANDOM_JOIN) {
                        int highest = 0;
                        List<GameMap> maps = Lists.newArrayList();
                        maps = GameMap.getPlayableArenas(GameType.ALL);

                        if (maps.isEmpty()) {
                            player.sendMessage(c(Main.get().getConfig().getString("no_solo_arenas")));
                            return;
                        }
                        player.sendMessage(c(Main.get().getConfig().getString("solo_join")));

                        GameMap map = null;

                        for (GameMap mappy : maps) {
                            if (mappy.canAddPlayer() && highest <= mappy.getPlayerCount()) {
                                map = mappy;
                                highest = mappy.getPlayerCount();
                            }
                        }


                        boolean b = false;
                        if (map != null) {
                            b = map.addPlayers((TeamCard) null, player);
                        }
                        if (b) {
                            player.sendMessage(c(Main.get().getConfig().getString("joined_arena").replace("%name%", map.getName())));
                        } else {
                            player.sendMessage(c((new Messaging.MessageFormatter()).format("error.could-not-join2")));
                        }
                    } else if (action == RANDOM_TEAM_JOIN) {
                        int highest = 0;
                        List<GameMap> maps = Lists.newArrayList();
                        maps = GameMap.getPlayableArenas(GameType.TEAM);

                        if (maps.isEmpty()) {
                            player.sendMessage(c(Main.get().getConfig().getString("no_team_arenas")));
                            return;
                        }
                        player.sendMessage(c(Main.get().getConfig().getString("team_join")));

                        GameMap map = null;

                        for (GameMap mappy : maps) {
                            if (mappy.canAddPlayer() && highest <= mappy.getPlayerCount()) {
                                map = mappy;
                                highest = mappy.getPlayerCount();
                            }
                        }


                        boolean b = false;
                        if (map != null) {
                            b = map.addPlayers((TeamCard) null, player);
                        }
                        if (b) {
                            player.sendMessage(c(Main.get().getConfig().getString("joined_arena").replace("%name%", map.getName())));
                        } else {
                            player.sendMessage(c((new Messaging.MessageFormatter()).format("error.could-not-join2")));
                        }
                    } else if (action == RANDOM_SOLO_JOIN) {
                        int highest = 0;
                        List<GameMap> maps = Lists.newArrayList();
                        maps = GameMap.getPlayableArenas(GameType.SINGLE);

                        if (maps.isEmpty()) {
                            player.sendMessage(c(Main.get().getConfig().getString("no_solo_arenas")));
                            return;
                        }
                        player.sendMessage(c(Main.get().getConfig().getString("solo_join")));

                        GameMap map = null;

                        for (GameMap mappy : maps) {
                            if (mappy.canAddPlayer() && highest <= mappy.getPlayerCount()) {
                                map = mappy;
                                highest = mappy.getPlayerCount();
                            }
                        }


                        boolean b = false;
                        if (map != null) {
                            b = map.addPlayers((TeamCard) null, player);
                        }
                        if (b) {
                            player.sendMessage(c(Main.get().getConfig().getString("joined_arena").replace("%name%", map.getName())));
                        } else {
                            player.sendMessage(c((new Messaging.MessageFormatter()).format("error.could-not-join2")));
                        }
                    } else if (action == OPEN_CUSTOM_MENU) {
                        new SingleJoinMenu().openMenu(player, 1);
                    } else if (action == OPEN_MENU) {
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
                    }

                }
            }
        }
    }

    @EventHandler
    public void onTP(NPCTeleportEvent e) {
        NPCFile file = new NPCFile();
        if (file.getNPCs().contains(e.getNPC().getId())) {
            file.setLocation(e.getNPC().getId(), e.getTo());
        }
    }

    @EventHandler
    public void onRemove(NPCRemoveEvent e) {
        NPCFile file = new NPCFile();
        if (file.getNPCs().contains(e.getNPC().getId())) {
            file.getFile().set(e.getNPC().getId() + "", null);
        }
    }

}
