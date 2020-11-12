package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.player;

import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
public class LeaveCommand implements CommandExecutor {

    private String c(String a) {
        return ChatColor.translateAlternateColorCodes('&',a);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("leave")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + "You must be a player to perform this command");
                return true;
            }

            Player p = (Player)commandSender;

            if (!p.hasPermission("sw.quit")) {
                p.sendMessage(c(Main.get().getConfig().getString("no_permission")));
                return true;
            }

            GameMap a = MatchManager.get().getPlayerMap(p);
            if (a == null) {
                p.sendMessage(c(Main.get().getConfig().getString("not_ingame")));
                return true;
            }

            MatchManager.get().playerLeave(p, EntityDamageEvent.DamageCause.CUSTOM,true,false,true);
            p.sendMessage(c(Main.get().getConfig().getString("left_game")));
            return true;
        }
        
        return false;
    }
}
