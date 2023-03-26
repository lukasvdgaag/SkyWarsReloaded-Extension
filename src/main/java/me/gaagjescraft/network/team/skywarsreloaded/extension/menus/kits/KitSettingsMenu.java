package me.gaagjescraft.network.team.skywarsreloaded.extension.menus.kits;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.GameKit;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import me.gaagjescraft.network.team.skywarsreloaded.extension.utils.SWUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class KitSettingsMenu implements Listener {

    private final static List<Integer> glassSlots = Lists.newArrayList(
            0,1,2,3,4,5,6,7,8,
            9, 11, 13, 15, 17,
            18,19,20,21,22,23,24,25,26,
            27,28,29,30,32,33,34,35
    );
    public static List<Player> viewingSettings = Lists.newArrayList();
    private static HashMap<Player, GameKit> displayNameChanging = new HashMap<>();

    public void openMenu(Player player, GameKit kit) {
        Inventory menu = Bukkit.createInventory(null, 36, "Skywars Kit Creator (" + kit.getFilename() + ")");

        ItemStack glass;
        if (SWExtension.get().isMinecraftNotLegacy()) {
            glass = new ItemStack(Material.valueOf("BLACK_STAINED_GLASS_PANE"));
        }
        else {
            glass = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"),1,(byte)15);
        }
        ItemMeta gMeta = glass.getItemMeta();
        gMeta.setDisplayName(ChatColor.RESET + "");
        gMeta.setLore(Lists.newArrayList());
        glass.setItemMeta(gMeta);

        for (int i : glassSlots) {
            menu.setItem(i,glass);
        }

        ItemStack display = new ItemStack(Material.NAME_TAG);
        ItemMeta dm = display.getItemMeta();
        dm.setDisplayName(ChatColor.AQUA + "Change display name");
        dm.setLore(Lists.newArrayList("",
                ChatColor.GRAY + "Current: " + kit.getColorName(), "",
                ChatColor.GOLD + "Click " + ChatColor.YELLOW + "to change the display name through the chat."));
        dm.addItemFlags(ItemFlag.values());
        display.setItemMeta(dm);
        menu.setItem(10, display);

        List<String> kitLore = kit.getColorLores();
        ItemStack lore = SWUtils.validateSign();
        ItemMeta lm = lore.getItemMeta();
        lm.setDisplayName(ChatColor.AQUA + "Change lore");
        List<String> llore = Lists.newArrayList("");
        if (kit.getLores().size() == 0) {
            llore.add(ChatColor.RED + "This kit currently has no lore!");
        }
        else {
            for (int i = 0; i<kitLore.size();i++) {
                llore.add(ChatColor.YELLOW + ((i+1)+"") + ": " + ChatColor.RESET + kitLore.get(i));
            }
        }
        llore.add("");
        llore.add(ChatColor.YELLOW + "It's currently not possible to edit lores");
        llore.add(ChatColor.YELLOW + "through this menu. Use '/swk lore' instead.");
        lm.setLore(llore);
        lm.addItemFlags(ItemFlag.values());
        lore.setItemMeta(lm);
        menu.setItem(16, lore);

        ItemStack perm = new ItemStack(Material.SHIELD);
        ItemMeta pm = perm.getItemMeta();
        pm.setDisplayName(ChatColor.AQUA + "Require permission: " + (kit.needPermission() ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
        List<String> plore = Lists.newArrayList("");
        if (kit.needPermission()) {
            plore.add(ChatColor.GRAY + "Permission: " + ChatColor.YELLOW + "sw.kit." + kit.getFilename());
            plore.add("");
            plore.add(ChatColor.GOLD + "Click " + ChatColor.YELLOW + "to disable a permission node.");
            plore.add(ChatColor.YELLOW + "Everyone will then have this kit (for free)");
        }
        else {
            plore.add(ChatColor.RED + "No permission is needed.");
            plore.add("");
            plore.add(ChatColor.GOLD + "Click " + ChatColor.YELLOW + "to enable a permission node.");
        }
        pm.setLore(plore);
        pm.addItemFlags(ItemFlag.values());
        perm.setItemMeta(pm);
        menu.setItem(14, perm);

        ItemStack slotAmount = new ItemStack(Material.PAPER);
        slotAmount.setAmount(kit.getPosition()+1);
        ItemMeta sameta = slotAmount.getItemMeta();
        sameta.setDisplayName(ChatColor.AQUA + "Current menu position: " + ChatColor.GREEN + (kit.getPosition()+1));
        sameta.setLore(Lists.newArrayList("", ChatColor.GOLD + "Left click" + ChatColor.YELLOW + " to increase the slot by +1",
                ChatColor.GOLD + "Right click" + ChatColor.YELLOW + " to decrease the slot by -1"));
        slotAmount.setItemMeta(sameta);
        menu.setItem(12, slotAmount);

        ItemStack invMan = new ItemStack(Material.BOOK);
        ItemMeta imm = invMan.getItemMeta();
        imm.setDisplayName(ChatColor.AQUA + "Return to normal editor");
        imm.setLore(Lists.newArrayList(ChatColor.GRAY + "Clicking this item will return you to the",
                ChatColor.GRAY + "normal kit editor menu where you can",
                ChatColor.GRAY + "set the slot, icons and armor.","",
                ChatColor.GOLD + "Click " + ChatColor.YELLOW + "to return to the normal editor."));
        imm.addEnchant(Enchantment.DURABILITY,1,true);
        imm.addItemFlags(ItemFlag.values());
        invMan.setItemMeta(imm);
        menu.setItem(31, invMan);

        player.openInventory(menu);
        viewingSettings.add(player);
        KitCreationMenu.editing.put(player, kit.getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null || !e.getView().getTitle().startsWith("Skywars Kit Creator") || !KitCreationMenu.editing.containsKey(player) || !viewingSettings.contains(player)) {
            return;
        }
        e.setCancelled(true);

        if (e.getClickedInventory() != e.getView().getTopInventory()) return;

        GameKit kit = GameKit.getKit(KitCreationMenu.editing.get(player));
        int slot = e.getRawSlot();

        if (slot == 10) {
            displayNameChanging.put(player, kit);
            player.closeInventory();
            player.sendMessage(ChatColor.AQUA + "You are about to change the display name of the kit " + kit.getColorName());
            player.sendMessage(ChatColor.GRAY + "Type the new display name in the chat. You can use color codes (&).");
            player.sendMessage(ChatColor.RED + "Type \"cancel\" to cancel the display name changing.");
        }
        else if (slot == 14) {
            boolean perm = !kit.needPermission();
            kit.setNeedPermission(perm);
            openMenu(player, kit);
            if (perm) {
                player.sendMessage(ChatColor.GRAY + "The kit " + kit.getColorName() + ChatColor.GRAY + " now requires the permission " + ChatColor.AQUA + "sw.kit." + kit.getFilename() + ChatColor.GRAY + " to be unlocked.");
            }
            else {
                player.sendMessage(ChatColor.GRAY + "The kit " + kit.getColorName() + ChatColor.GRAY + " now no longer requires a permission to be unlocked.");
            }
        }
        else if (slot == 12) {
            int pos = kit.getPosition();
            if (e.isLeftClick() && pos < 53) {
                kit.setPosition(pos + 1);
                openMenu(player, kit);
            } else if (e.isRightClick() && pos > 0) {
                kit.setPosition(pos - 1);
                openMenu(player, kit);
            }
        }
        else if (slot == 31) {
            SWExtension.getKitMenu().openMenu(player, kit);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (KitCreationMenu.editing.containsKey(player) && viewingSettings.contains(player)) {
            GameKit kit = GameKit.getKit(KitCreationMenu.editing.get(player));

            GameKit.saveKit(kit);
            viewingSettings.remove(player);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if (displayNameChanging.containsKey(e.getPlayer())) {
            e.setCancelled(true);
            GameKit kit = displayNameChanging.get(e.getPlayer());
            if (kit == null) {
                e.getPlayer().sendMessage(ChatColor.RED + "Something went wrong whilst changing the display name: the kit is null.");
                displayNameChanging.remove(e.getPlayer());
                return;
            }

            if (e.getMessage().equalsIgnoreCase("cancel")) {
                e.getPlayer().sendMessage(ChatColor.RED + "We've cancelled the display name changer.");
            }
            else {
                kit.setName(e.getMessage());
                e.getPlayer().sendMessage(ChatColor.GREEN + "We've changed the display name of the kit " + kit.getFilename() + " to " + kit.getColorName());
            }

            displayNameChanging.remove(e.getPlayer());
            openMenu(e.getPlayer(), kit);
        }
    }

}
