package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.autojoin;

import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.BaseCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.features.autojoin.AutoRejoin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoJoinCancelCmd extends BaseCmd {

    public AutoJoinCancelCmd(String t) {
        type = t;
        forcePlayer = true;
        cmdName = "cancel";
        alias = new String[]{};
        argLength = 1;
    }

    @Override
    public boolean run(CommandSender sender, Player player, String[] args) {
        AutoRejoin autoRejoin = AutoRejoin.fromPlayer(player);
        if (autoRejoin == null) {
            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.not_enabled")));
            return true;
        } else {
            if (!autoRejoin.isOwner(player)) {
                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.must_be_leader")));
                return true;
            }

            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.canceled")));
            AutoRejoin.autoRejoins.remove(autoRejoin);
        }
        return true;
    }

}
