package me.gaagjescraft.network.team.skywarsreloaded.extension.commands;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.commands.KitCmdManager;
import com.walrusone.skywarsreloaded.commands.MainCmdManager;
import com.walrusone.skywarsreloaded.commands.MapCmdManager;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.admin.CreateNPCCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.admin.SendCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.kits.CreateKit;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.kits.DeleteKit;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.kits.EditKit;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.kits.ListKit;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.maps.CageTypeCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.maps.ImportCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.maps.NameCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.maps.RenameCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.player.JoinCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.player.LeaveCommand;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.player.SelectCosmeticCmd;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class ExtensionCmdManager {

    public void importCmds() {
        try {
            importMessagesIntoFile();
            loadAdminCmds();
            loadPlayerCmds();
            loadMapCmds();
            loadKitCmds();

            Bukkit.getPluginCommand("skywars").setTabCompleter(new ExtensionCmdTabCompletion());
            Bukkit.getPluginCommand("swmap").setTabCompleter(new ExtensionCmdTabCompletion());
            SWExtension.get().getCommand("autojoin").setExecutor(new AutoJoinCmdManager());
            SWExtension.get().getCommand("leave").setExecutor(new LeaveCommand());
        } catch (Exception ignored) {
        }
    }

    private void loadAdminCmds() throws NoSuchFieldException, IllegalAccessException {
        MainCmdManager cm = (MainCmdManager) Bukkit.getPluginCommand("skywars").getExecutor();
        Field adminCmdsField = cm.getClass().getDeclaredField("admincmds");
        adminCmdsField.setAccessible(true);
        List<BaseCmd> adminCommands = (List<BaseCmd>) adminCmdsField.get(cm);

        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            adminCommands.removeIf(cmd -> cmd.cmdName.equalsIgnoreCase("createnpc"));
            adminCommands.add(new CreateNPCCmd("sw"));
        }
        adminCommands.removeIf(cmd -> cmd.cmdName.equalsIgnoreCase("send"));
        adminCommands.add(new SendCmd("sw"));
        adminCmdsField.set(cm, adminCommands);
    }

    private void loadPlayerCmds() throws NoSuchFieldException, IllegalAccessException {
        MainCmdManager cm = (MainCmdManager) Bukkit.getPluginCommand("skywars").getExecutor();
        Field playerCmdsField = cm.getClass().getDeclaredField("pcmds");
        playerCmdsField.setAccessible(true);
        List<BaseCmd> playerCommands = (List<BaseCmd>) playerCmdsField.get(cm);

        playerCommands.removeIf(cmd -> cmd.cmdName.equalsIgnoreCase("join"));
        playerCommands.add(new JoinCmd("sw"));
        playerCommands.removeIf(cmd -> cmd.cmdName.equalsIgnoreCase("select"));
        playerCommands.add(new SelectCosmeticCmd("sw"));
        playerCmdsField.set(cm, playerCommands);
    }

    private void loadKitCmds() throws NoSuchFieldException, IllegalAccessException {
        KitCmdManager cm = (KitCmdManager) Bukkit.getPluginCommand("swkit").getExecutor();
        Field playerCmdsField = cm.getClass().getDeclaredField("kitcmds");
        playerCmdsField.setAccessible(true);
        List<BaseCmd> playerCommands = (List<BaseCmd>) playerCmdsField.get(cm);

        playerCommands.removeIf(cmd -> cmd.cmdName.equalsIgnoreCase("edit"));
        playerCommands.add(new EditKit("kit"));
        playerCommands.removeIf(cmd -> cmd.cmdName.equalsIgnoreCase("create"));
        playerCommands.add(new CreateKit("kit"));
        playerCommands.removeIf(cmd -> cmd.cmdName.equalsIgnoreCase("delete"));
        playerCommands.add(new DeleteKit("kit"));
        playerCommands.removeIf(cmd -> cmd.cmdName.equalsIgnoreCase("list"));
        playerCommands.add(new ListKit("kit"));
        playerCmdsField.set(cm, playerCommands);
    }

    private void loadMapCmds() throws NoSuchFieldException, IllegalAccessException {
        MapCmdManager cm = (MapCmdManager) Bukkit.getPluginCommand("swmap").getExecutor();
        Field cmdsField = cm.getClass().getDeclaredField("mapcmds");
        cmdsField.setAccessible(true);
        List<BaseCmd> commands = (List<BaseCmd>) cmdsField.get(cm);

        commands.removeIf(cmd -> cmd.cmdName.equalsIgnoreCase("cage"));
        commands.add(new CageTypeCmd("map"));
        commands.removeIf(cmd -> cmd.cmdName.equalsIgnoreCase("import"));
        commands.add(new ImportCmd("map"));
        commands.removeIf(cmd -> cmd.cmdName.equalsIgnoreCase("name"));
        commands.add(new NameCmd("map"));
        commands.removeIf(cmd -> cmd.cmdName.equalsIgnoreCase("rename"));
        commands.add(new RenameCmd("map"));
        cmdsField.set(cm, commands);
    }

    private void importMessagesIntoFile() throws IOException {
        File file = new File(SkyWarsReloaded.get().getDataFolder(), "messages.yml");
        FileConfiguration conf = SkyWarsReloaded.getMessaging().getFile();
        a(conf, "helpList.sw.createnpc", "&a/sw createnpc &e[action] &e- &2Create a Skywars NPC using Citizens");
        a(conf, "helpList.sw.send", "&a/sw send &e[playername] [mapname] &e- &2Send a player to a game");
        a(conf, "helpList.sw.select", "&a/sw select &e[cosmetictype] [cosmetic] &e- &2Select a specific cosmetic");
        a(conf, "helpList.swmap.cage", "&a/swmap cage &e[mapname] [cagetype] &e- &2Set the default cage type of an arena");
        a(conf, "helpList.swmap.import", "&a/swmap import &e[worldname] &e- &2Import an existing world as a skywars arena");
        a(conf, "helpList.swmap.rename", "&a/swmap rename &e[mapname] [new name] &e- &2Rename an arena");
        a(conf, "helpList.swkit.edit", "&a/swkit edit &e[kitname] - &2Edit a kit");
        a(conf, "helpList.swkit.delete", "&a/swkit delete &e[kitname] - &2Permanently delete a kit");

        a(conf, "helpList.autojoin.now", "&a/autojoin now &e- &2Forcefully join a new game.");
        a(conf, "helpList.autojoin.auto", "&a/autojoin auto &e- &2Automatically join a new game for 1 hour long.");
        a(conf, "helpList.autojoin.cancel", "&a/autojoin cancel &e- &2Cancel automatically joining a new game.");
        conf.save(file);
    }

    private void a(FileConfiguration a, String b, Object c) {
        if (!a.contains(b)) {
            a.set(b, c);
        }
    }

}
