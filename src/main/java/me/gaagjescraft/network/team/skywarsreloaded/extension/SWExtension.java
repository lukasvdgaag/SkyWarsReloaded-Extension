package me.gaagjescraft.network.team.skywarsreloaded.extension;

import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import com.walrusone.skywarsreloaded.managers.PlayerStat;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.ExtensionCmdManager;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.admin.ReloadCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.events.AdditionsPlusHandler;
import me.gaagjescraft.network.team.skywarsreloaded.extension.events.SWEvents;
import me.gaagjescraft.network.team.skywarsreloaded.extension.features.autojoin.AutoRejoinHandler;
import me.gaagjescraft.network.team.skywarsreloaded.extension.features.placeholders.PAPIPlaceholders;
import me.gaagjescraft.network.team.skywarsreloaded.extension.files.FileManager;
import me.gaagjescraft.network.team.skywarsreloaded.extension.files.PlayerFile;
import me.gaagjescraft.network.team.skywarsreloaded.extension.menus.SingleJoinMenu;
import me.gaagjescraft.network.team.skywarsreloaded.extension.menus.kits.ExtendedKitCreationMenu;
import me.gaagjescraft.network.team.skywarsreloaded.extension.menus.kits.KitCreationMenu;
import me.gaagjescraft.network.team.skywarsreloaded.extension.menus.kits.KitListMenu;
import me.gaagjescraft.network.team.skywarsreloaded.extension.menus.kits.KitSettingsMenu;
import me.gaagjescraft.network.team.skywarsreloaded.extension.npcs.NPCFile;
import me.gaagjescraft.network.team.skywarsreloaded.extension.npcs.NPCHandler;
import me.gaagjescraft.network.team.skywarsreloaded.extension.utils.SWUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SWExtension extends JavaPlugin implements Listener {

    private static SWExtension instance;
    private KitCreationMenu kitMenu;
    private ExtendedKitCreationMenu extendedKitCreationMenu;
    private KitListMenu kitListMenu;
    private KitSettingsMenu kitSettingsMenu;

    public static SWExtension get() {
        return instance;
    }

    public static KitCreationMenu getKitMenu() {
        return instance.kitMenu;
    }

    public static KitListMenu getKitListMenu() {
        return instance.kitListMenu;
    }

    public static KitSettingsMenu getKitSettingsMenu() { return instance.kitSettingsMenu; }

    public static ExtendedKitCreationMenu getExtendedKitMenu() { return instance.extendedKitCreationMenu; }

    public static String c(String str) {
        return SWUtils.c(str);
    }

    @Override
    public void onEnable() {
        instance = this;

        // here I'm just checking if you have SkywarsReloaded installed
        Logger logger = Bukkit.getLogger();
        if (!Bukkit.getPluginManager().isPluginEnabled(SkyWarsReloaded.get())) {
            // if not, i'm gonna send a message and gonna disable this plugin as it won't work without
            logger.log(Level.SEVERE, "Couldn't find SkywarsReloaded! Now disabling the plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        try {
            SkyWarsReloaded swr = SkyWarsReloaded.get();
            if (!swr.extensionCompatCheck(this)) return;
            if (!swr.isNewVersion()) return;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Found SkyWarsReloaded, but not the updated version. The updated version is required to run this extension as it has" +
                    " a lot of fixes and new features that are required for this plugin to run. You can get it here: https://www.spigotmc.org/resources/69436/");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        File f = new File(getDataFolder(), "config.yml");
        if (!f.exists()) {
            saveResource("config.yml", false);
            logger.log(Level.INFO, "Created the config.yml");
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
                        "https://gcnt.net/discord \n" +
                        "\n" +
                        "Also, make sure to go to our website to checkout our SkywarsReloaded tutorials!\n" +
                        "http://gcnt.net/skywars/\n" +
                        "\n" +
                        "Please leave a donation :)\n" +
                        "https://gcnt.net/paypal"

        );
        saveConfig();
        reloadConfig();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            logger.log(Level.INFO, "Found PlaceholderAPI. We hooked into it and registered the skywars placeholders");
            logger.log(Level.INFO, "The official Skywars placeholders will be overwritten.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Additions")) {
            String outdatedMessage = "Found AdditionsPlus but it's outdated! Please update your AdditionsPlus plugin.";
            String notPremiumMessage = "Found Additions, but not AdditionsPlus. You need to have AdditionsPlus installed in order to make the Custom Event Integration work.";
            boolean foundAdditionsPlus_v2_7_x = false;
            boolean foundAdditionsPlus_outdated = false;

            try {
                // checking for 2.7.0+
                net.gcnt.additionsplus.api.AdditionsPlugin ap = (net.gcnt.additionsplus.api.AdditionsPlugin) Bukkit.getPluginManager().getPlugin("Additions");
                Bukkit.getPluginManager().registerEvents(new AdditionsPlusHandler(ap), this);
                foundAdditionsPlus_v2_7_x = true;
            } catch (Exception ignored) {
                try {
                    // checking for 2.4.0+
                    Class.forName("net.gcnt.additionsplus.AdditionsPlus");
                    foundAdditionsPlus_outdated = true;
                } catch (Exception ignored2) {}
            }

            if (foundAdditionsPlus_v2_7_x) {
                logger.info("Found AdditionsPlus. Registering the custom events.");
            }
            else if (foundAdditionsPlus_outdated) logger.severe(outdatedMessage);
            else logger.severe(notPremiumMessage);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            Bukkit.getPluginManager().registerEvents(new NPCHandler(), this);
            NPCFile file = new NPCFile();
            file.setup();
            file.save();
            file.reload();
            logger.log(Level.INFO, "Found Citizens. We hooked into it, and you are now able to create Skywars NPCs using the '/sw createnpc <action>' command.");
        }

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

        loadSchedulers();
        new PAPIPlaceholders();
    }

    public void loadSchedulers() {
        final int lobbyboardInterval = getConfig().getInt("lobbyboard_update_interval");
        if (lobbyboardInterval > 0) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                if (SkyWarsReloaded.getCfg().getSpawn() == null || SkyWarsReloaded.getCfg().getSpawn().getWorld() == null ||
                !SkyWarsReloaded.getCfg().lobbyBoardEnabled()) return;
                for (Player p : SkyWarsReloaded.getCfg().getSpawn().getWorld().getPlayers()) {
                    PlayerStat.updateScoreboard(p, "lobbyboard");
                }
            },0, lobbyboardInterval);
        }
    }

    public boolean isMinecraftNotLegacy() {
        return SkyWarsReloaded.getNMS().getVersion() > 13;
    }
}
