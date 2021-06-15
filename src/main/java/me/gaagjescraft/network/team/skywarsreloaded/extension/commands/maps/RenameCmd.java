package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.maps;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class RenameCmd extends BaseCmd {

    public RenameCmd(String t) {
        type = t;
        forcePlayer = false;
        cmdName = "rename";
        alias = new String[]{};
        argLength = 3;
    }


    @Override
    public boolean run(CommandSender sender, Player player, String[] args) {
        String mapName = args[1];
        GameMap map = GameMap.getMap(mapName);
        if (map == null) {
            sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("invalid_arena")));
            return true;
        }
        if (map.getMatchState() != MatchState.OFFLINE) {
            sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("game_must_be_offline")));
            return true;
        }

        String newMapName = args[2];
        GameMap newMap = GameMap.getMap(newMapName);
        if (newMap != null) {
            sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("arena_already_exists")));
            return true;
        }

        if (map.getCurrentWorld() != null) {
            for (Player p : map.getCurrentWorld().getPlayers()) {
                p.teleport(SkyWarsReloaded.getCfg().getSpawn());
                Bukkit.unloadWorld(newMapName,false); // removing the world folder
            }
        }

        File yamlFile = new File(SkyWarsReloaded.get().getDataFolder(), "mapsData" + File.separator + mapName+".yml");
        yamlFile.renameTo(new File(SkyWarsReloaded.get().getDataFolder(), "mapsData" + File.separator + newMapName+".yml"));

        File folderFile = new File(SkyWarsReloaded.get().getDataFolder(), "maps" + File.separator + mapName);
        folderFile.renameTo(new File(SkyWarsReloaded.get().getDataFolder(), "maps" + File.separator + newMapName));

        sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("renamed_arena").replace("%arena%", newMapName)));
        return true;
    }
}
