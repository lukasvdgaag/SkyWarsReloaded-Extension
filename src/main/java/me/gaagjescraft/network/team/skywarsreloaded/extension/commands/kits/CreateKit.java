package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.kits;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.GameKit;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateKit extends BaseCmd {

    public CreateKit(String t) {
        type = t;
        forcePlayer = true;
        cmdName = "create";
        alias = new String[]{};
        argLength = 2;
    }

    @Override
    public boolean run(CommandSender sender, Player player, String[] args) {
        String kitname = args[1];

        GameKit kit = GameKit.getKit(kitname);
        if (kit != null) {
            sender.sendMessage(ChatColor.RED + "There's already a kit with that name.");
            return true;
        }

        GameKit.newKit(player, kitname);
        kit = GameKit.getKit(kitname);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "Something went wrong whilst creating the kit.");
            return true;
        }
        kit.setArmor(new ItemStack[4]);
        kit.setInventory(new ItemStack[36]);
        sender.sendMessage(ChatColor.GREEN + "Successfully created the kit " + kitname + ".");
        SWExtension.getKitMenu().openMenu(player, kit);
        return true;
    }
}
