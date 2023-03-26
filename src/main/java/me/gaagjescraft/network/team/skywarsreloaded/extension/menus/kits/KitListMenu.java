package me.gaagjescraft.network.team.skywarsreloaded.extension.menus.kits;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.GameKit;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class KitListMenu implements Listener {

    private static HashMap<Player, Integer> openPages = new HashMap<>();
    private static List<Inventory> inventoryPages = Lists.newArrayList();
    private static List<GameKit> gameKits = Lists.newArrayList();

    public KitListMenu() {
        this.reloadPages();
    }

    public void reloadPages() {
        gameKits = GameKit.getKits();

        int kitsSize = gameKits.size();
        int pagesNeeded = (int) Math.ceil(kitsSize / 36d);

        for (int page = 1; page <= pagesNeeded; page++) {
            List<GameKit> kits = gameKits.subList((36 * page) - 36, gameKits.size());

            int size = 54;
            if (kits.size() <= 9) {
                size = 27;
            } else if (kits.size() <= 18) {
                size = 36;
            } else if (kits.size() <= 27) {
                size = 45;
            }
            Inventory menu = Bukkit.createInventory(null, size, "SkyWars Kits");

            for (int i = 0; i < kits.size(); i++) {
                GameKit kit = kits.get(i);

                ItemStack item = kit.getIcon();
                if (item == null) item = new ItemStack(Material.DIRT, 1);
                ItemMeta meta = item.getItemMeta();

                // Kit Name
                String kitName = kit.getName();
                if (kitName == null) meta.setDisplayName(ChatColor.AQUA + "(no-kit-name)");
                else meta.setDisplayName(ChatColor.AQUA + kit.getColorName());

                // Lore
                meta.setLore(Lists.newArrayList(
                        "",
                        ChatColor.YELLOW + "" + ChatColor.BOLD + "Kit Info:",
                        ChatColor.AQUA + "Name: " + ChatColor.GRAY + kit.getFilename(),
                        ChatColor.AQUA + "Permission: " + (kit.needPermission() ? ChatColor.GRAY + "sw.kit." + kit.getFilename() : ChatColor.RED + "No permission needed."),
                        ChatColor.AQUA + "Menu position: " + ChatColor.GRAY + kit.getPosition(),
                        "",
                        ChatColor.GOLD + "Left click " + ChatColor.YELLOW + "to edit this kit.",
                        ChatColor.GOLD + "Right click " + ChatColor.YELLOW + "to preview the kit in your inventory" + ChatColor.GRAY + "*",
                        ChatColor.GRAY + "* " + ChatColor.ITALIC + "Your inventory will be cleared."
                ));
                meta.addItemFlags(ItemFlag.values());
                item.setItemMeta(meta);
                item.setAmount(1);
                menu.setItem(i, item);
            }

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

            for (int i = (size - 18); i < (size - 9); i++) {
                menu.setItem(i, glass);
            }

            ItemStack close = new ItemStack(Material.BOOK);
            ItemMeta cmeta = close.getItemMeta();
            cmeta.setDisplayName(ChatColor.AQUA + "Close");
            close.setItemMeta(cmeta);
            menu.setItem(size - 5, close);

            if (page > 1) {
                ItemStack item = new ItemStack(Material.ARROW);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.AQUA + "Previous page");
                item.setItemMeta(meta);
                menu.setItem(size - 9, item);
            }
            if (page < pagesNeeded) {
                ItemStack item = new ItemStack(Material.ARROW);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.AQUA + "Next page");
                item.setItemMeta(meta);
                menu.setItem(size - 1, item);
            }

            inventoryPages.add(menu);
        }
    }

    public boolean openMenu(Player player, int page) {
        if (inventoryPages.size() == 0) return false;
        if (page - 1 > inventoryPages.size()) {
            player.openInventory(inventoryPages.get(inventoryPages.size() - 1));
            return true;
        }

        player.openInventory(inventoryPages.get(page - 1));
        openPages.put(player, page);
        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (!openPages.containsKey(player)) return;

        e.setCancelled(true);

        if (e.getClickedInventory() == null || !e.getClickedInventory().equals(e.getView().getTopInventory())) return;
        int slot = e.getRawSlot();

        if (slot == e.getClickedInventory().getSize() - 5) {
            player.closeInventory();
            return;
        }

        int page = openPages.getOrDefault(player, 1);
        List<GameKit> kits = gameKits.subList((36 * page) - 36, gameKits.size());
        if (slot >= kits.size()) return;

        GameKit clickedKit = kits.get(slot);
        if (e.isLeftClick()) {
            openPages.remove(player);
            SWExtension.getKitMenu().openMenu(player, clickedKit);
        } else if (e.isRightClick()) {
            player.closeInventory();
            GameKit.giveKit(player, clickedKit);
            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("kit_preview_give").replace("%kit%", clickedKit.getFilename())));
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        openPages.remove((Player) e.getPlayer());
    }

}
