package me.gaagjescraft.network.team.skywarsreloaded.extension.menus;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.GameType;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.TeamCard;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import com.walrusone.skywarsreloaded.utilities.SWRServer;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.player.JoinCmd;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class SingleJoinMenu implements Listener {

    private static final String menuName = new Messaging.MessageFormatter().format("menu.joinsinglegame-menu-title");
    private static List<Player> inmenu = Lists.newArrayList();
    private static HashMap<Player, Integer> ppages = new HashMap<>();
    private static HashMap<Player, HashMap<Integer, GameMap>> things = new HashMap<>();
    private static HashMap<Player, HashMap<Integer, SWRServer>> thingsBungee = new HashMap<>();

    private String c(String a) {
        return ChatColor.translateAlternateColorCodes('&', a);
    }

    public void openMenu(Player p, int page) {
        boolean none = false;
        inmenu.add(p);

        int pages = 1;

        List<GameMap> maps = GameMap.getMapsCopy();
        List<SWRServer> servers = SWRServer.getServersCopy();

        int arenaSize = 0;
        if (!SkyWarsReloaded.getCfg().bungeeMode()) {
            if (maps.isEmpty()) {
                none = true;
            }

            if (maps.size() > 36) {
                pages = maps.size() / 36;
                if (maps.size() % 36 > 0) {
                    pages++;
                }
            }

            if (pages > 1 && page > 1 && page <= pages) {
                int start = (36 * page) - 36;
                maps = maps.subList(start, maps.size());
            }
            arenaSize = maps.size();
        } else if (SkyWarsReloaded.getCfg().bungeeMode() && SkyWarsReloaded.getCfg().isLobbyServer()) {
            if (servers.isEmpty()) {
                none = true;
            }

            if (servers.size() > 36) {
                pages = servers.size() / 36;
                if (servers.size() % 36 > 0) {
                    pages++;
                }
            }

            if (pages > 1 && page > 1 && page <= pages) {
                int start = (36 * page) - 36;
                servers = servers.subList(start, servers.size());
            }
            arenaSize = servers.size();
        }

        int b = 54;
        if (arenaSize <= 9) {
            b = 27;
        } else if (arenaSize <= 18) {
            b = 36;
        } else if (arenaSize <= 27) {
            b = 45;
        }

        Inventory gui = Bukkit.createInventory(null, b, menuName);

        int a = 0;
        if (!none) {
            HashMap<Integer, GameMap> hehe = new HashMap<>();
            HashMap<Integer, SWRServer> heheBungee = new HashMap<>();

            for (int i = 0; i < arenaSize; i++) {
                String arena;
                String displayName;
                int teamsize;
                int playercount;
                int maxplayers;
                MatchState matchstate;
                int ingamePlayers;

                if (SkyWarsReloaded.getCfg().bungeeMode() && SkyWarsReloaded.getCfg().isLobbyServer()) {
                    SWRServer server = servers.get(i);
                    arena = server.getServerName();
                    displayName = server.getDisplayName();
                    teamsize = server.getTeamSize();
                    playercount = server.getPlayerCount();
                    maxplayers = server.getMaxPlayers();
                    matchstate = server.getMatchState();
                    ingamePlayers = server.getPlayerCount();

                    heheBungee.put(a, server);
                }
                else {
                    GameMap map = maps.get(i);
                    arena = map.getName();
                    displayName = map.getDisplayName();
                    teamsize = map.getTeamSize();
                    playercount = map.getPlayerCount();
                    maxplayers = map.getMaxPlayers();
                    matchstate = map.getMatchState();
                    ingamePlayers = map.getAlivePlayers().size();

                    hehe.put(a, map);
                }

                if (a >= (b - 18)) {
                    break;
                }
                List<String> lore = Lists.newArrayList();
                if (matchstate != MatchState.OFFLINE) {
                    FileConfiguration conf = YamlConfiguration.loadConfiguration(new File(SkyWarsReloaded.get().getDataFolder(), "messages.yml"));

                    if (matchstate == MatchState.WAITINGSTART || matchstate == MatchState.WAITINGLOBBY) {
                        List<String> l = Lists.newArrayList();
                        for (String s : conf.getStringList("menu.join_menu.lore.waiting-start")) {
                            l.add(c(s)
                                    .replace("{arena}", arena)
                                    .replace("{name}", c(displayName))
                                    .replace("{teamsize}", teamsize + "")
                                    .replace("{playercount}", playercount + "")
                                    .replace("{maxplayers}", maxplayers + "")
                            );
                        }
                        lore = l;
                    } else if (matchstate == MatchState.PLAYING) {
                        List<String> l = Lists.newArrayList();
                        for (String s : conf.getStringList("menu.join_menu.lore.playing")) {
                            l.add(c(s)
                                    .replace("{arena}", arena)
                                    .replace("{name}", c(displayName))
                                    .replace("{teamsize}", teamsize + "")
                                    .replace("{playercount}", playercount + "")
                                    .replace("{maxplayers}", maxplayers + "")
                            );
                        }
                        lore = l;
                    } else if (matchstate == MatchState.ENDING) {
                        List<String> l = Lists.newArrayList();
                        for (String s : conf.getStringList("menu.join_menu.lore.ending")) {
                            l.add(c(s)
                                    .replace("{arena}", arena)
                                    .replace("{name}", c(displayName))
                                    .replace("{teamsize}", teamsize + "")
                                    .replace("{playercount}", playercount + "")
                                    .replace("{maxplayers}", maxplayers + "")
                            );
                        }
                        lore = l;
                    }
                }

                double xy = ((double) (ingamePlayers / (maxplayers == 0 ? 1 : maxplayers)));

                ItemStack gameIcon = SkyWarsReloaded.getNMS().getItemStack(SkyWarsReloaded.getIM().getItem("blockwaiting"), lore, new Messaging.MessageFormatter()
                        .setVariable("arena", arena)
                        .setVariable("name", c(displayName))
                        .setVariable("teamsize", teamsize + "")
                        .setVariable("playercount", playercount + "")
                        .setVariable("maxplayers", maxplayers + "")
                        .format("menu.join_menu.item_title.playing"));
                if (matchstate.equals(MatchState.PLAYING)) {
                    gameIcon = SkyWarsReloaded.getNMS().getItemStack(SkyWarsReloaded.getIM().getItem("blockplaying"), lore, new Messaging.MessageFormatter()
                            .setVariable("arena", arena)
                            .setVariable("name", c(displayName))
                            .setVariable("teamsize", teamsize + "")
                            .setVariable("playercount", playercount + "")
                            .setVariable("maxplayers", maxplayers + "")
                            .format("menu.join_menu.item_title.playing"));
                } else if (matchstate.equals(MatchState.ENDING)) {
                    gameIcon = SkyWarsReloaded.getNMS().getItemStack(SkyWarsReloaded.getIM().getItem("blockending"), lore, new Messaging.MessageFormatter()
                            .setVariable("arena", arena)
                            .setVariable("name", c(displayName))
                            .setVariable("teamsize", teamsize + "")
                            .setVariable("playercount", playercount + "")
                            .setVariable("maxplayers", maxplayers + "")
                            .format("menu.join_menu.item_title.ending"));
                } else if (matchstate == MatchState.WAITINGSTART || matchstate == MatchState.WAITINGLOBBY) {
                    String title = new Messaging.MessageFormatter()
                            .setVariable("arena", arena)
                            .setVariable("name", c(displayName))
                            .setVariable("teamsize", teamsize + "")
                            .setVariable("playercount", playercount + "")
                            .setVariable("maxplayers", maxplayers + "")
                            .format("menu.join_menu.item_title.waiting-start");
                    gameIcon = SkyWarsReloaded.getNMS().getItemStack(SkyWarsReloaded.getIM().getItem("almostfull"), lore, title);
                    if (xy < 0.75) {
                        gameIcon = SkyWarsReloaded.getNMS().getItemStack(SkyWarsReloaded.getIM().getItem("threefull"), lore, title);
                    }
                    if (xy < 0.50) {
                        gameIcon = SkyWarsReloaded.getNMS().getItemStack(SkyWarsReloaded.getIM().getItem("halffull"), lore, title);
                    }
                    if (xy < 0.25) {
                        gameIcon = SkyWarsReloaded.getNMS().getItemStack(SkyWarsReloaded.getIM().getItem("almostempty"), lore, title);
                    }
                }
                gui.setItem(a, gameIcon);
                a++;
            }
            things.put(p, hehe);
            thingsBungee.put(p,heheBungee);
        } else {
            ItemStack item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cNo Free Games"));
            item.setItemMeta(meta);
            gui.setItem(0, item);
        }

        if (page > 1) {
            ItemStack item = new ItemStack(Material.ARROW);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7Previous Page"));
            item.setItemMeta(meta);
            gui.setItem(b - 9, item);
        }
        if (page < pages) {
            ItemStack item = new ItemStack(Material.ARROW);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7Next Page"));
            item.setItemMeta(meta);
            gui.setItem(b - 1, item);
        }

        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cClose"));
        item.setItemMeta(meta);
        gui.setItem(b - 5, item);

        ppages.put(p, page);
        p.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) {
            return;
        }
        if (inmenu.contains((Player) e.getWhoClicked())) {
            e.setCancelled(true);
            if (e.getClickedInventory() != null && e.getClickedInventory().equals(e.getView().getTopInventory())) {
                // player is in the menu

                int slot = e.getRawSlot();
                Player p = (Player) e.getWhoClicked();


                if (SkyWarsReloaded.getCfg().bungeeMode() && SkyWarsReloaded.getCfg().isLobbyServer()) {
                    HashMap<Integer, SWRServer> servers = thingsBungee.get(p);
                    if (servers != null && servers.containsKey(slot)) {
                        SWRServer server = servers.get(slot);
                        server.setPlayerCount(server.getPlayerCount() + 1);
                        server.updateSigns();
                        SkyWarsReloaded.get().sendBungeeMsg(p, "Connect", server.getServerName());
                        p.sendMessage(c(SWExtension.get().getConfig().getString("joined_arena").replace("%name%", server.getServerName())));
                        return;
                    }
                }
                else {
                    HashMap<Integer, GameMap> maps = things.get(p);
                    if (maps != null && maps.containsKey(slot)) {
                        GameMap map = maps.get(slot);
                        JoinCmd.joinGame(p, GameType.ALL,map.getName());
                        return;
                    }
                }

                if (slot == e.getClickedInventory().getSize() - 9) {
                    if (e.getClickedInventory().getItem(slot) != null) {
                        openMenu(p, ppages.get(p) - 1);
                    }
                } else if (slot == e.getClickedInventory().getSize() - 1) {
                    if (e.getClickedInventory().getItem(slot) != null) {
                        openMenu(p, ppages.get(p) + 1);
                    }
                }

            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        inmenu.remove((Player) e.getPlayer());
    }

}
