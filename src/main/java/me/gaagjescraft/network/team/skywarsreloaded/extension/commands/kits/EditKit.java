package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.kits;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.GameKit;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditKit extends BaseCmd {

    public EditKit(String t) {
        type = t;
        forcePlayer = true;
        cmdName = "edit";
        alias = new String[]{"change", "modify"};
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

        SWExtension.getKitMenu().openMenu(player, kit);
        return true;
    }
}
