package me.gaagjescraft.network.team.skywarsreloaded.extension.menus.kits;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.GameKit;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class ExtendedKitCreationMenu implements Listener {

    private static final List<Integer> glassSlots = Lists.newArrayList(
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            45, 46, 47, 48, 50, 51, 52, 53
    );
    private static final HashMap<Integer, Integer> hotbarSlots = new HashMap<>();
    public static List<Player> viewingExtended = Lists.newArrayList();

    static {
        hotbarSlots.put(46, 0);
        hotbarSlots.put(47, 1);
        hotbarSlots.put(48, 2);
        hotbarSlots.put(49, 3);
        hotbarSlots.put(50, 4);
        hotbarSlots.put(51, 5);
        hotbarSlots.put(52, 6);
        hotbarSlots.put(53, 7);
        hotbarSlots.put(54, 8);
    }

    public void openMenu(Player player, GameKit kit) {
        Inventory menu = Bukkit.createInventory(null, 54, "Skywars Kit Creator (" + kit.getFilename() + ")");

        // setting glass decorations
        ItemStack glass;
        if (SWExtension.get().isMinecraftNotLegacy()) {
            glass = new ItemStack(Material.valueOf("BLACK_STAINED_GLASS_PANE"));
        } else {
            glass = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (byte) 15);
        }
        ItemMeta gMeta = glass.getItemMeta();
        gMeta.setDisplayName(ChatColor.RESET + "");
        gMeta.setLore(Lists.newArrayList());
        glass.setItemMeta(gMeta);

        for (int i : glassSlots) {
            menu.setItem(i, glass);
        }

        ItemStack[] content = kit.getInventory();
        for (int i = 9; i < 36; i++) { // setting normal inventory items
            menu.setItem(i - 9, content[i]);
        }
        for (int i = 0; i < 8; i++) { // setting hotbar items
            menu.setItem(36 + i, content[i]);
        }


        ItemStack invMan = new ItemStack(Material.BOOK);
        ItemMeta imm = invMan.getItemMeta();
        imm.setDisplayName(ChatColor.AQUA + "Return to normal editor");
        imm.setLore(Lists.newArrayList(ChatColor.GRAY + "Clicking this item will return you to the",
                ChatColor.GRAY + "normal kit editor menu where you can",
                ChatColor.GRAY + "set the slot, icons and armor.", "",
                ChatColor.GOLD + "Click " + ChatColor.YELLOW + "to return to the normal editor."));
        imm.addEnchant(Enchantment.DURABILITY, 1, true);
        imm.addItemFlags(ItemFlag.values());
        invMan.setItemMeta(imm);
        menu.setItem(49, invMan);

        player.openInventory(menu);
        viewingExtended.add(player);
        KitCreationMenu.editing.put(player, kit.getName());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null || !e.getView().getTitle().startsWith("Skywars Kit Creator") || !KitCreationMenu.editing.containsKey(player) || !viewingExtended.contains(player) || KitSettingsMenu.viewingSettings.contains(player)) {
            return;
        }

        if (e.getClickedInventory() == e.getView().getTopInventory()) {
            if (glassSlots.contains(e.getRawSlot()) || e.getRawSlot() == 49)
                e.setCancelled(true);
        }

        if (e.getRawSlot() == 49) {
            SWExtension.getKitMenu().openMenu(player, GameKit.getKit(KitCreationMenu.editing.get(player)));
            return;
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (KitCreationMenu.editing.containsKey(player) && viewingExtended.contains(player) && !KitSettingsMenu.viewingSettings.contains(player)) {
            GameKit kit = GameKit.getKit(KitCreationMenu.editing.get(player));

            ItemStack[] items = new ItemStack[54];
            for (int i = 0; i < 26; i++) {
                items[i + 9] = e.getView().getTopInventory().getItem(i);
            }
            for (int i = 36; i < 45; i++) {
                items[i - 36] = e.getView().getTopInventory().getItem(i);
            }
            kit.setInventory(items);

            GameKit.saveKit(kit);
            viewingExtended.remove(player);
        }
    }


}
