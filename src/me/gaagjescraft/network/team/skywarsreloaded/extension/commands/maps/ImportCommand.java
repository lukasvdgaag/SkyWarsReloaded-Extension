package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.maps;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.enums.ChestPlacementType;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.CoordLoc;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import me.gaagjescraft.network.team.skywarsreloaded.extension.utils.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ImportCommand implements Listener {

    private String c(String a) {
        return ChatColor.translateAlternateColorCodes('&',a);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) throws Exception {
        if (StringUtils.startsWithIgnoreCase(e.getMessage(), "/swm import") || StringUtils.startsWithIgnoreCase(e.getMessage(), "/skywarsmap import")) {
            e.setCancelled(true);
            String[] a = e.getMessage().split(" ");

            if (!e.getPlayer().hasPermission("sw.map.import")) {
                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("no_permission")));
                return;
            }

            if (a.length == 2) {
                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("must_specify_world")));
                return;
            } else if (a.length == 3) {
                String name = a[2];
                World world = Bukkit.getWorld(name);
                if (world != null) {
                    for (GameMap map : GameMap.getMaps()) {
                        if (map.getName().equals(name)) {
                            e.getPlayer().sendMessage(c(Main.get().getConfig().getString("arena_already_exists")));
                            return;
                        }
                    }

                    e.getPlayer().sendMessage(c(Main.get().getConfig().getString("import_starting").replace("%map%", name)));

                    Field field = GameMap.class.getDeclaredField("arenas");
                    field.setAccessible(true);
                    ((ArrayList<GameMap>) field.get(new GameMap(name))).add(new GameMap(name));

                    GameMap map = GameMap.getMap(name);
                    if (map != null) {
                        map.setEditing(true);
                        world.setAutoSave(true);
                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("import_succeeded").replace("%map%", name)));
                        e.getPlayer().teleport(new Location(world, 0, 75, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        e.getPlayer().setGameMode(GameMode.CREATIVE);
                        e.getPlayer().setAllowFlight(true);
                        e.getPlayer().setFlying(true);
                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("start_legacy_load_delay")));

                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.get(), () -> {
                            e.getPlayer().sendMessage(c(Main.get().getConfig().getString("start_legacy_load_now")));
                            int mapSize = SkyWarsReloaded.getCfg().getMaxMapSize();
                            int max1 = mapSize / 2;
                            int min1 = -mapSize / 2;
                            Block min = world.getBlockAt(min1, 0, min1);
                            Block max = world.getBlockAt(max1, 0, max1);
                            Chunk cMin = min.getChunk();
                            Chunk cMax = max.getChunk();

                            try {
                                Field f = GameMap.class.getDeclaredField("chests");
                                f.setAccessible(true);
                                ((ArrayList<CoordLoc>) f.get(new GameMap(name))).clear();
                                Field f1 = GameMap.class.getDeclaredField("teamCards");
                                f1.setAccessible(true);
                                ((ArrayList<CoordLoc>) f1.get(new GameMap(name))).clear();
                            } catch (Exception e1) {
                            }

                            int chests = 0;
                            int spawns = 0;
                            int team = 1;

                            for (int cx = cMin.getX(); cx < cMax.getX(); cx++) {
                                for (int cz = cMin.getZ(); cz < cMax.getZ(); cz++) {
                                    Chunk currentChunk = world.getChunkAt(cx, cz);
                                    currentChunk.load(true);

                                    for (BlockState te : currentChunk.getTileEntities()) {
                                        if (te instanceof Beacon) {
                                            Beacon beacon = (Beacon) te;
                                            Block block = beacon.getBlock().getRelative(0, -1, 0);

                                            if (!block.getType().equals(Material.GOLD_BLOCK) && !block.getType().equals(Material.IRON_BLOCK)
                                                    && !block.getType().equals(Material.DIAMOND_BLOCK) && !block.getType().equals(Material.EMERALD_BLOCK)) {
                                                Location loc = beacon.getLocation();
                                                map.addTeamCard(loc, team);
                                                spawns++;
                                                team++;
                                            }
                                        } else if (te instanceof Chest) {
                                            Chest chest = (Chest) te;
                                            map.addChest(chest, ChestPlacementType.NORMAL);
                                            chests++;
                                        }
                                    }
                                }
                            }

                            try {
                                Method met = GameMap.class.getDeclaredMethod("saveArenaData");
                                met.setAccessible(true);
                                met.invoke(new GameMap(name));
                            } catch (Exception e1) {
                            }

                            if (chests > 0) {
                                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("chests_registered").replace("%amount%", "" + chests)));
                            } else {
                                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("no_chests_found")));
                            }
                            if (spawns > 0) {
                                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("spawns_registered").replace("%amount%", "" + spawns)));
                            } else {
                                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("no_spawns_found")));
                            }
                            e.getPlayer().sendMessage(c(Main.get().getConfig().getString("import_done_note")));
                        }, 100);
                        return;
                    }
                    e.getPlayer().sendMessage(c(Main.get().getConfig().getString("import_error")));
                    return;
                }
                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("invalid_world")));
            } else if (a.length > 3) {
                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("too_many_import_arguments")));
            }
        }
    }

}
