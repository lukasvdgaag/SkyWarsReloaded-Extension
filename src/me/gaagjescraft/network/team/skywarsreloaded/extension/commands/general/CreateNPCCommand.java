package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.general;

import com.google.common.collect.Lists;
import me.gaagjescraft.network.team.skywarsreloaded.extension.npcs.NPCClickAction;
import me.gaagjescraft.network.team.skywarsreloaded.extension.npcs.NPCHandler;
import me.gaagjescraft.network.team.skywarsreloaded.extension.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CreateNPCCommand implements Listener {

    private String c(String a) {
        return ChatColor.translateAlternateColorCodes('&',a);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (StringUtils.startsWithIgnoreCase(e.getMessage(), "/sw createnpc") || StringUtils.startsWithIgnoreCase(e.getMessage(), "/skywars createnpc")) {
            String[] a = e.getMessage().split(" ");
            Player p = e.getPlayer();

            if (a.length <= 2) {
                e.setCancelled(true);
                p.sendMessage(c("&cYou didn't give enough arguments. Correct usage: /sw createnpc <action>"));
            }
            else {
                e.setCancelled(true);
                for (NPCClickAction ac : NPCClickAction.values()) {
                    if (a[2].toUpperCase().equals(ac.name())) {
                        new NPCHandler().createNPC(p.getLocation(),ac);
                        p.sendMessage(c("&aYou successfully created a NPC with click action " + ac.name() + ". To change the skin and name, just use Citizens."));
                        return;
                    }
                }
                p.sendMessage(c("&cThere is no Click action with that name. Choose one from below:"));
                p.sendMessage(Lists.newArrayList(NPCClickAction.values()).toString().replace("[","").replace("]",""));
            }
        }
    }

}
