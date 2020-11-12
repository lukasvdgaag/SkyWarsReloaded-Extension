package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.maps;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.game.cages.CageType;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import org.bukkit.ChatColor;

import java.util.Arrays;

public class CageTypeCmd extends BaseCmd {

    public CageTypeCmd(String t) {
        type = t;
        forcePlayer = true;
        cmdName = "cage";
        alias = new String[]{};
        argLength = 3;
    }

    @Override
    public boolean run() {
        String mapname = args[1];
        String cage = args[2];

        GameMap map = GameMap.getMap(mapname);
        if (map == null) {
            player.sendMessage(Main.c(Main.get().getConfig().getString("invalid_arena")));
            return true;
        }

        try {
            CageType type = CageType.valueOf(cage.toUpperCase());
            map.setCage(type);
            player.sendMessage(Main.c(Main.get().getConfig().getString("cagetype_set").replace("%map%", map.getName()).replace("%type%", cage)));
        } catch (Exception e1) {
            player.sendMessage(Main.c(Main.get().getConfig().getString("invalid_cagetype")));
            player.sendMessage(ChatColor.GRAY + Arrays.toString(CageType.values()).replace("[", "").replace("]", "").replace(",", ChatColor.WHITE + "," + ChatColor.GRAY));
        }
        return true;
    }
}
