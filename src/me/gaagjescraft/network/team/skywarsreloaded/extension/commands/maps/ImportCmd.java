package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.maps;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.enums.ChestPlacementType;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.CoordLoc;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import org.bukkit.*;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ImportCmd extends BaseCmd {
    
    public ImportCmd(String t) {
        type = t;
        forcePlayer = false;
        cmdName = "import";
        alias = new String[]{};
        argLength = 2;
    }

    @Override
    public boolean run() {
        if (!sender.hasPermission("sw.map.import")) {
            sender.sendMessage(Main.c(Main.get().getConfig().getString("no_permission")));
            return true;
        }
        
        String worldName = args[1];
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            sender.sendMessage(Main.c(Main.get().getConfig().getString("invalid_world")));
            return true;
        }
        if (GameMap.getMap(worldName) != null)  {
            sender.sendMessage(Main.c(Main.get().getConfig().getString("arena_already_exists")));
            return true;
        }
        
        sender.sendMessage(Main.c(Main.get().getConfig().getString("import_starting").replace("%map%", worldName)));

        try {
            Field field = GameMap.class.getDeclaredField("arenas");
            field.setAccessible(true);
            ((ArrayList<GameMap>) field.get(new GameMap(worldName))).add(new GameMap(worldName));
        } catch (Exception e) {
            sender.sendMessage(Main.c(Main.get().getConfig().getString("import_error")));
            return true;
        }
        
        GameMap map = GameMap.getMap(worldName); 
        if (map == null) {
            sender.sendMessage(Main.c(Main.get().getConfig().getString("import_error")));
            return true;
        }
        
        map.setEditing(true);
        world.setAutoSave(true);
        
        sender.sendMessage(Main.c(Main.get().getConfig().getString("import_succeeded").replace("%map%", worldName)));
        if (sender instanceof Player) {
            player = (Player) sender;
            player.teleport(new Location(world, 0, 75, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.setGameMode(GameMode.CREATIVE);
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        sender.sendMessage(Main.c(Main.get().getConfig().getString("start_legacy_load_delay")));

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.get(), () -> {
            sender.sendMessage(Main.c(Main.get().getConfig().getString("start_legacy_load_now")));
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
                ((ArrayList<CoordLoc>) f.get(new GameMap(worldName))).clear();
                Field f1 = GameMap.class.getDeclaredField("teamCards");
                f1.setAccessible(true);
                ((ArrayList<CoordLoc>) f1.get(new GameMap(worldName))).clear();
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
                met.invoke(new GameMap(worldName));
            } catch (Exception ignored) {}
            
            if (chests > 0) {
                sender.sendMessage(Main.c(Main.get().getConfig().getString("chests_registered").replace("%amount%", "" + chests)));
            } else {
                sender.sendMessage(Main.c(Main.get().getConfig().getString("no_chests_found")));
            }
            if (spawns > 0) {
                sender.sendMessage(Main.c(Main.get().getConfig().getString("spawns_registered").replace("%amount%", "" + spawns)));
            } else {
                sender.sendMessage(Main.c(Main.get().getConfig().getString("no_spawns_found")));
            }
            sender.sendMessage(Main.c(Main.get().getConfig().getString("import_done_note")));
        }, 100);
        return true;
    }
}
