package me.gaagjescraft.network.team.skywarsreloaded.extension.commands;

import com.walrusone.skywarsreloaded.utilities.Messaging;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.autojoin.AutoJoinAutoCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.autojoin.AutoJoinCancelCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.autojoin.AutoJoinNowCmd;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class AutoJoinCmdManager implements CommandExecutor {

    private static AutoJoinCmdManager kcm;
    private List<BaseCmd> kitcmds = new ArrayList<>();

    //Add New Commands Here
    public AutoJoinCmdManager() {
        kcm = this;
        kitcmds.add(new AutoJoinNowCmd("autojoin"));
        kitcmds.add(new AutoJoinAutoCmd("autojoin"));
        kitcmds.add(new AutoJoinCancelCmd("autojoin"));
    }

    public static List<BaseCmd> getCommands() { return kcm.kitcmds; }

    public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
        if (args.length == 0 || getCommand(args[0]) == null) {
            s.sendMessage(new Messaging.MessageFormatter().format("helpList.header"));
            sendHelp(kitcmds, s);
            s.sendMessage(new Messaging.MessageFormatter().format("helpList.footer"));
        } else getCommand(args[0]).processCmd(s, args);
        return true;
    }

    private void sendHelp(List<BaseCmd> cmds, CommandSender s) {
        for (BaseCmd cmd : cmds) {
            if (s.hasPermission("sw." + cmd.getType() + "." + cmd.cmdName)) {
                s.sendMessage(new Messaging.MessageFormatter().format("helpList.autojoin." + cmd.cmdName));
            }
        }
    }

    public BaseCmd getCommand(String s) {
        return getCmd(kitcmds, s);
    }

    private BaseCmd getCmd(List<BaseCmd> cmds, String s) {
        for (BaseCmd cmd : cmds) {
            if (cmd.cmdName.equalsIgnoreCase(s)) {
                return cmd;
            }
            for (String alias : cmd.alias) {
                if (alias.equalsIgnoreCase(s))
                    return cmd;
            }
        }
        return null;
    }

}
