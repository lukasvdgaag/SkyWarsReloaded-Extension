package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.admin;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Messaging;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SendCmd extends BaseCmd {

    public SendCmd(String t) {
        type = t;
        forcePlayer = false;
        cmdName = "send";
        alias = new String[]{};
        argLength = 3;
    }

    @Override
    public boolean run(CommandSender sender, Player player, String[] args) {
        Player p = Bukkit.getPlayer(args[1]);
        GameMap map = GameMap.getMap(args[2]);
        if (p == null) {
            sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("invalid_player")));
            return true;
        }
        if (map == null) {
            sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("invalid_arena")));
            return true;
        }
        if (MatchManager.get().getPlayerMap(p) != null) {
            sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("already_ingame_other").replace("%player%", p.getName())));
            return true;
        }
        if (!map.canAddPlayer()) {
            sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("cannot_join_other").replace("%player%", p.getName())));
            return true;
        }

        boolean b = map.addPlayers(null, p);
        if (b) {
            sender.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("send_arena").replace("%name%", map.getDisplayName()).replace("%player%", p.getName())));
        } else {
            sender.sendMessage((new Messaging.MessageFormatter()).format("error.could-not-join2"));
        }
        return true;
    }
}
