package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.autojoin;

import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.BaseCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.features.AutoRejoin;
import org.bukkit.ChatColor;

public class AutoJoinCancelCmd extends BaseCmd {

    public AutoJoinCancelCmd(String t) {
        type = t;
        forcePlayer = true;
        cmdName = "cancel";
        alias = new String[]{};
        argLength = 1;
    }

    @Override
    public boolean run() {
        AutoRejoin autoRejoin = AutoRejoin.fromPlayer(player);
        if (autoRejoin == null) {
            player.sendMessage(ChatColor.RED + "You don't have auto join enabled.");
            return true;
        } else {
            if (!autoRejoin.isOwner(player)) {
                player.sendMessage(ChatColor.RED + "You must be the party leader to do this.");
                return true;
            }

            player.sendMessage(ChatColor.RED + "You cancelled auto joining.");
            AutoRejoin.autoRejoins.remove(autoRejoin);
        }
        return true;
    }

}
