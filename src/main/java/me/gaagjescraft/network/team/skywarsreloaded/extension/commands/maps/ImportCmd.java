package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.maps;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.game.GameMap;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ImportCmd extends BaseCmd {
    
    public ImportCmd(String t) {
        type = t;
        forcePlayer = false;
        cmdName = "import";
        alias = new String[]{};
        argLength = 2;
    }

    @Override
    public boolean run(CommandSender sender, Player player, String[] args) {
        if (!sender.hasPermission("sw.map.import")) {
            sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("no_permission")));
            return true;
        }
        
        final String worldName = args[1];
        final World world = Bukkit.getWorld(worldName);
        final GameMap gameMap;

        if (world == null) {
            sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("invalid_world")));
            return true;
        }
        if (GameMap.getMap(worldName) != null)  {
            sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("arena_already_exists")));
            return true;
        } else {
            sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("import_starting").replace("%map%", worldName)));
            gameMap = GameMap.addMap(worldName);
        }

        gameMap.setEditing(true);
        world.setAutoSave(true);
        
        sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("import_succeeded").replace("%map%", worldName)));
        if (sender instanceof Player) {
            player = (Player) sender;
            player.teleport(new Location(world, 0, 75, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.setGameMode(GameMode.CREATIVE);
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("start_legacy_load_delay")));

        Bukkit.getScheduler().scheduleSyncDelayedTask(SWExtension.get(), () -> {
            sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("start_legacy_load_now")));

            // Run internal scanning system from the main plugin
            try {
                Method scanMethod = gameMap.getClass().getDeclaredMethod("scanChunksForSkywarsFeatures", CommandSender.class, boolean.class);
                scanMethod.setAccessible(true);
                scanMethod.invoke(gameMap, SWExtension.get().getServer().getConsoleSender(), false);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            // Save the new data
            gameMap.saveArenaData();

            /*int mapSize = SkyWarsReloaded.getCfg().getMaxMapSize();
            int max1 = mapSize / 2;
            int min1 = -mapSize / 2;
            Block min = world.getBlockAt(min1, 0, min1);
            Block max = world.getBlockAt(max1, 0, max1);
            Chunk cMin = min.getChunk();
            Chunk cMax = max.getChunk();

            try {
                Field f = GameMap.class.getDeclaredField("chests");
                f.setAccessible(true);
                ((ArrayList<CoordLoc>) f.get(gameMap)).clear();
                Field f1 = GameMap.class.getDeclaredField("teamCards");
                f1.setAccessible(true);
                ((ArrayList<CoordLoc>) f1.get(gameMap)).clear();
            } catch (Exception ignored) {}

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
                                gameMap.addTeamCard(Lists.newArrayList(new CoordLoc(loc)));
                                spawns++;
                                team++;
                            }
                        } else if (te instanceof Chest) {
                            Chest chest = (Chest) te;
                            gameMap.addChest(chest, ChestPlacementType.NORMAL);
                            chests++;
                        }
                    }
                }
            }

            try {
                new GameMap(worldName).saveArenaData();
            } catch (Exception ignored) {}*/

            int chests = gameMap.getChests().size();
            int spawns = gameMap.getSpawnLocations().size();
            int teams = gameMap.getTeamCards().size();

            FileConfiguration config = SWExtension.get().getConfig();

            if (chests > 0) {
                sender.sendMessage(SWExtension.c(config.getString("chests_registered")
                        .replace("%amount%", "" + chests)));
            } else {
                sender.sendMessage(SWExtension.c(config.getString("no_chests_found")));
            }
            if (spawns > 0) {
                sender.sendMessage(SWExtension.c(config.getString("spawns_registered")
                        .replace("%amount%", "" + spawns)));
            } else {
                sender.sendMessage(SWExtension.c(config.getString("no_spawns_found")));
            }
            sender.sendMessage(SWExtension.c(config.getString("import_done_note")));
        }, 100);
        return true;
    }
}
