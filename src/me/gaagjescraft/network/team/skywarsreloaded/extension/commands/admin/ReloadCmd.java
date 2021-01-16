package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.admin;

import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.ExtensionCmdManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class ReloadCmd implements Listener {

    @EventHandler
    public void onCommandExecution(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().toLowerCase().startsWith("/skywars reload") || e.getMessage().toLowerCase().startsWith("/sw reload")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(SWExtension.get(), () -> new ExtensionCmdManager().importCmds(), 40);
        }
    }

    @EventHandler
    public void onCommandExecution(ServerCommandEvent e) {
        if (e.getCommand().toLowerCase().startsWith("skywars reload") || e.getCommand().toLowerCase().startsWith("sw reload")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(SWExtension.get(), () -> new ExtensionCmdManager().importCmds(), 40);
        }
    }

}
