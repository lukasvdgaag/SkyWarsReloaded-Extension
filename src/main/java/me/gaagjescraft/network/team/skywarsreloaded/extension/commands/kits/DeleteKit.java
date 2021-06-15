package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.kits;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.GameKit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class DeleteKit extends BaseCmd {

    public DeleteKit(String t) {
        type = t;
        forcePlayer = true;
        cmdName = "delete";
        alias = new String[]{"remove"};
        argLength = 2;
    }

    @Override
    public boolean run(CommandSender sender, Player player, String[] args) {
        String kitname = args[1];

        GameKit kit = GameKit.getKit(kitname);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "There's no kit with that name.");
            return true;
        }

        File file = new File(SkyWarsReloaded.get().getDataFolder(), "kits" + File.separator + kit.getFilename() + ".yml");
        boolean result = file.delete();
        if (!result) {
            sender.sendMessage(ChatColor.RED + "We failed to delete the Skywars kit file for the kit " + kit.getName());
            return true;
        }
        GameKit.getKits().remove(kit);
        sender.sendMessage(ChatColor.GREEN + "Successfully removed the kit " + kitname + ".");
        return true;
    }
}
