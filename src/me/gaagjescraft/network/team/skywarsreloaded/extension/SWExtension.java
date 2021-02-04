package me.gaagjescraft.network.team.skywarsreloaded.extension;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.ExtensionCmdManager;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.admin.ReloadCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.player.LeaveCommand;
import me.gaagjescraft.network.team.skywarsreloaded.extension.events.AdditionsPlusHandler;
import me.gaagjescraft.network.team.skywarsreloaded.extension.events.SWEvents;
import me.gaagjescraft.network.team.skywarsreloaded.extension.features.AutoRejoinHandler;
import me.gaagjescraft.network.team.skywarsreloaded.extension.files.FileManager;
import me.gaagjescraft.network.team.skywarsreloaded.extension.files.PlayerFile;
import me.gaagjescraft.network.team.skywarsreloaded.extension.menus.SingleJoinMenu;
import me.gaagjescraft.network.team.skywarsreloaded.extension.menus.kits.ExtendedKitCreationMenu;
import me.gaagjescraft.network.team.skywarsreloaded.extension.menus.kits.KitCreationMenu;
import me.gaagjescraft.network.team.skywarsreloaded.extension.menus.kits.KitListMenu;
import me.gaagjescraft.network.team.skywarsreloaded.extension.menus.kits.KitSettingsMenu;
import me.gaagjescraft.network.team.skywarsreloaded.extension.npcs.NPCFile;
import me.gaagjescraft.network.team.skywarsreloaded.extension.npcs.NPCHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class SWExtension extends JavaPlugin implements Listener {

    private static SWExtension m;
    private KitCreationMenu kitMenu;
    private ExtendedKitCreationMenu extendedKitCreationMenu;
    private KitListMenu kitListMenu;
    private KitSettingsMenu kitSettingsMenu;

    public static SWExtension get() {
        return m;
    }

    public static KitCreationMenu getKitMenu() {
        return m.kitMenu;
    }

    public static KitListMenu getKitListMenu() {
        return m.kitListMenu;
    }

    public static KitSettingsMenu getKitSettingsMenu() { return m.kitSettingsMenu; }

    public static ExtendedKitCreationMenu getExtendedKitMenu() { return m.extendedKitCreationMenu; }

    public static String c(String a) {
        return ChatColor.translateAlternateColorCodes('&', a);
    }

    @Override
    public void onEnable() {
        m = this;
        // here I'm just checking if you have SkywarsReloaded installed
        if (!Bukkit.getPluginManager().isPluginEnabled(SkyWarsReloaded.get())) {
            // if not, i'm gonna send a message and gonna disable this plugin as it won't work without
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't find SkywarsReloaded! Now disabling the plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        try {
            SkyWarsReloaded.get().isNewVersion();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Found SkyWarsReloaded, but not the updated version. The updated version is required to run this extension as it has" +
                    " a lot of fixes and new features that are required for this plugin to run. You can get it here: https://www.spigotmc.org/resources/69436/");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        File f = new File(getDataFolder(), "config.yml");
        if (!f.exists()) {
            saveResource("config.yml", false);
            Bukkit.getLogger().log(Level.INFO, "Created the config.yml");
        }

        FileManager.register(new PlayerFile());
        FileManager.registerAll();
        Bukkit.getPluginManager().registerEvents(new SWEvents(), this);
        Bukkit.getPluginManager().registerEvents(new ReloadCmd(), this);

        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        getConfig().options().header(
                "This is a SkywarsReloaded extension created by GaagjesCraft Network Team (GCNT)\n" +
                        "This plugin has been requested by multiple people, so we decided to create it.\n" +
                        "It adds advanced join features to Skywars which allows you to:\n" +
                        "  When performing '/sw join':\n" +
                        "    - Join a random game\n" +
                        "    - Open the join menu\n" +
                        "    - Send a message\n" +
                        "  Or some new commands for joining new games:\n" +
                        "    - '/sw join solo' joins a random solo arena\n" +
                        "    - '/sw join team' joins a random team arena\n" +
                        "    - '/sw join [arena]' joins the specified arena\n" +
                        "\n" +
                        "But not also join features. Also a cool feature to import maps using a command.\n" +
                        "This was a big problem for a lot of people because there was no official way to do it.\n" +
                        "  - '/sw import [world]' turns a normal world into a skywars arena\n" +
                        "\n" +
                        "The value of 'no_arena_specified_action' could be either JOIN_RANDOM, OPEN_JOIN_MENU, or SEND_MESSAGE\n" +
                        "The message of the option 'no_arena_specified_message' will only be sent when you have 'no_arena_specified_action' set to SEND_MESSAGE.\n" +
                        "\n" +
                        "Permissions:\n" +
                        "  - 'sw.join' is used for '/sw join'.\n" +
                        "  - 'sw.join.solo' to use '/sw join solo'\n" +
                        "  - 'sw.join.team' to use '/sw join team'\n" +
                        "  - 'sw.join.arena' to use '/sw join [arena]'\n" +
                        "  - 'sw.map.import' to use '/sw import [world]'\n" +
                        "\n" +
                        "A lot of people wanted these features.\n" +
                        "If you have new features for this plugin, make sure to join our Discord:\n" +
                        "https://discord.gg/r7fmTC \n" +
                        "\n" +
                        "Also, make sure to go to our website to checkout our SkywarsReloaded tutorials!\n" +
                        "http://beta.gaagjescraft.net/skywars/\n" +
                        "\n" +
                        "Please leave a donation :)\n" +
                        "https://paypal.me/gaagjescraft"

        );
        saveConfig();
        reloadConfig();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Bukkit.getLogger().log(Level.INFO, "Found PlaceholderAPI. We hooked into it and registered the skywars placeholders");
            Bukkit.getLogger().log(Level.INFO, "The official Skywars placeholders will be overwritten.");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Additions")) {
            try {
                Class<?> a = Class.forName("me.gaagjescraft.network.team.advancedevents.AdditionsEvent");
            } catch (Exception ea) {
                Bukkit.getLogger().log(Level.INFO, "Found Additions, but not AdditionsPlus. You need to have AdditionsPlus installed in order to make the Custom Event INtegration work.");
            } finally {
                Bukkit.getPluginManager().registerEvents(new AdditionsPlusHandler(), this);
                Bukkit.getLogger().log(Level.INFO, "Found AdditionsPlus. Now registering the custom events.");
            }
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            Bukkit.getPluginManager().registerEvents(new NPCHandler(), this);
            NPCFile file = new NPCFile();
            file.setup();
            file.save();
            file.reload();
            Bukkit.getLogger().log(Level.INFO, "Found Citizens. We hooked into it, and you are now able to create Skywars NPCs using the '/sw createnpc <action>' command.");
        }

        getCommand("leave").setExecutor(new LeaveCommand());

        new ExtensionCmdManager().importCmds();
        Bukkit.getPluginManager().registerEvents(new SingleJoinMenu(), this);
        Bukkit.getPluginManager().registerEvents(new KitCreationMenu(), this);
        Bukkit.getPluginManager().registerEvents(new ExtendedKitCreationMenu(), this);
        Bukkit.getPluginManager().registerEvents(new KitListMenu(), this);
        Bukkit.getPluginManager().registerEvents(new KitSettingsMenu(), this);

        Bukkit.getPluginManager().registerEvents(new AutoRejoinHandler(), this); // todo beta

        kitMenu = new KitCreationMenu();
        extendedKitCreationMenu = new ExtendedKitCreationMenu();
        kitListMenu = new KitListMenu();
        kitSettingsMenu = new KitSettingsMenu();
    }

    public boolean isNewVersion() {
        try {
            Material.valueOf("RED_WOOL");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
