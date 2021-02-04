package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.kits;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import org.bukkit.ChatColor;

public class ListKit extends BaseCmd {

    public ListKit(String t) {
        type = t;
        forcePlayer = true;
        cmdName = "list";
        alias = new String[]{};
        argLength = 1;
    }

    @Override
    public boolean run() {
        boolean result = SWExtension.getKitListMenu().openMenu(player, 1);
        if (!result)
            player.sendMessage(ChatColor.RED + "Something went wrong whilst opening the kit list menu.");
        return true;
    }
}
