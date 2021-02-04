package me.gaagjescraft.network.team.skywarsreloaded.extension.commands;

import com.walrusone.skywarsreloaded.utilities.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCmd {
    public CommandSender sender;
    public String[] args;
    public String[] alias;
    public String cmdName;
    public int argLength = 0;
    public boolean forcePlayer = true;
    public Player player;
    public String type;
    public int maxArgs = -1;

    public BaseCmd() {
    }

    void processCmd(CommandSender s, String[] arg) {
        sender = s;
        args = arg;

        if (forcePlayer) {
            if (!(s instanceof Player)) {
                sender.sendMessage(new Messaging.MessageFormatter().format("error.must-be-player"));
                return;
            }
            player = ((Player) s);
        }


        if (!s.hasPermission("sw." + type + "." + cmdName)) {
            sender.sendMessage(new Messaging.MessageFormatter().format("error.cmd-no-perm"));
        } else if ((maxArgs == -1 && argLength > arg.length) || (maxArgs!=-1 && arg.length > maxArgs)) {
            s.sendMessage(ChatColor.DARK_RED + "Wrong usage: " + new Messaging.MessageFormatter().format("helpList." + type + "." + cmdName));
        } else {
            boolean returnVal = run();
            if (!returnVal) {
                s.sendMessage(ChatColor.DARK_RED + "Wrong usage: " + new Messaging.MessageFormatter().format("helpList." + type + "." + cmdName));
            }
        }
    }

    public String getType() {
        return type;
    }

    public abstract boolean run();
}
