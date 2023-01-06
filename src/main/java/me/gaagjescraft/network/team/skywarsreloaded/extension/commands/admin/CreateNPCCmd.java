package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.admin;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import me.gaagjescraft.network.team.skywarsreloaded.extension.npcs.NPCClickAction;
import me.gaagjescraft.network.team.skywarsreloaded.extension.npcs.NPCHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateNPCCmd extends BaseCmd {

    public CreateNPCCmd(String t) {
        type = t;
        forcePlayer = true;
        cmdName = "createnpc";
        alias = new String[]{};
        argLength = 2;
    }

    @Override
    public boolean run(CommandSender commandSender, Player player, String[] args) {
        for (NPCClickAction ac : NPCClickAction.values()) {
            if (args[1].toUpperCase().equals(ac.name())) {
                new NPCHandler().createNPC(player.getLocation(),ac);
                player.sendMessage(SWExtension.c("&aYou successfully created a NPC with click action " + ac.name() + ". To change the skin and name, or to remove it, use Citizens."));
                return true;
            }
        }
        player.sendMessage(SWExtension.c("&cThere is no Click action with that name. Choose one from below:"));
        player.sendMessage(ChatColor.GRAY + Lists.newArrayList(NPCClickAction.values()).toString().replace("[","").replace("]","").replace(",", ChatColor.WHITE + "," + ChatColor.GRAY));
        return true;
    }
}
