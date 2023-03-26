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
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class KitCreationMenu implements Listener {

    public static HashMap<Player, String> editing = new HashMap<>();
    private static final List<Integer> glassSlots = Lists.newArrayList(
            0,1,2,3,4,5,6,7,8,
            10,12,17,
            19,21,26,
            28,30,32,33,34,35,
            37,39,40,41,42,44,
            45,46,47,48,49,50,51,52,53
            );
    private static final HashMap<Integer, Integer> hotbarSlots = new HashMap<>();

    static {
        hotbarSlots.put(13,0);
        hotbarSlots.put(14,1);
        hotbarSlots.put(15,2);
        hotbarSlots.put(16,3);
        hotbarSlots.put(22,4);
        hotbarSlots.put(23,5);
        hotbarSlots.put(24,6);
        hotbarSlots.put(25,7);
        hotbarSlots.put(31,8);
    }
    private static HashMap<Player, ItemStack[]> editingArmor = new HashMap<>();
    private static HashMap<Player, ItemStack[]> editingContent = new HashMap<>();

    public void openMenu(Player player, GameKit kit) {

        Inventory menu = Bukkit.createInventory(null, 54, "Skywars Kit Creator (" + kit.getFilename() + ")");

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta bMeta = barrier.getItemMeta();
        bMeta.setDisplayName(ChatColor.RED + "No item");
        bMeta.setLore(Lists.newArrayList(ChatColor.GRAY + "Click an armor item to automatically", ChatColor.GRAY + "set it to this slot", "",
                ChatColor.GOLD + "Drag an item" + ChatColor.YELLOW +" to this slot to change it.",
                ChatColor.GOLD + "Click" + ChatColor.YELLOW + " this item to remove it from the inventory."));
        barrier.setItemMeta(bMeta);

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

        menu.setItem(11, barrier);
        menu.setItem(20, barrier);
        menu.setItem(29, barrier);
        menu.setItem(38, barrier);

        ItemStack[] armor = kit.getArmor();
        for (int i =0; i<= 3; i++) {
            int slot;
            if (armor.length > i && armor[i] != null) {
                if (i == 0) {
                    slot = 38;
                }
                else if (i == 1) {
                    slot = 29;
                }
                else if (i == 2) {
                    slot = 20;
                }
                else {
                    slot = 11;
                }

                menu.setItem(slot, armor[i]);
            }
        }

        ItemStack[] itemStacks = kit.getInventory();
        for(int key : hotbarSlots.keySet()) {
            int slot = hotbarSlots.get(key);
            if (itemStacks[slot] != null) {
                menu.setItem(key, itemStacks[slot]);
            }
        }

        ItemStack icon = kit.getIcon();
        if (icon == null) icon = new ItemStack(Material.DIRT, 1);
        ItemMeta iMeta = icon.getItemMeta();
        iMeta.setDisplayName(ChatColor.GREEN + "Kit icon");
        List<String> iconLore = Lists.newArrayList(ChatColor.GRAY + "This item will be displayed when", ChatColor.GRAY + "the player has permission to select it", "",
                ChatColor.AQUA + "Lore:");
        iconLore.addAll(kit.getColorLores());
        iconLore.add("");
        iconLore.add(ChatColor.GOLD + "Drag an item" + ChatColor.YELLOW +" to this slot to change it.");
        iMeta.setLore(iconLore);
        icon.setItemMeta(iMeta);

        ItemStack noIcon = kit.getLIcon();
        ItemMeta niMeta = noIcon.getItemMeta();
        niMeta.setDisplayName(ChatColor.RED + "No permission icon");
        niMeta.setLore(Lists.newArrayList(ChatColor.GRAY + "This item will be displayed when", ChatColor.GRAY + "the player does not have permission to select it", "",
                ChatColor.GRAY + "Locked lore:", ChatColor.translateAlternateColorCodes('&', kit.getLockedLore()), "",
                ChatColor.GOLD + "Drag an item" + ChatColor.YELLOW +" to this slot to change it."));
        noIcon.setItemMeta(niMeta);

        menu.setItem(9, icon);
        menu.setItem(18, noIcon);

        ItemStack slotAmount = new ItemStack(Material.WORKBENCH);
        ItemMeta sameta = slotAmount.getItemMeta();
        sameta.setDisplayName(ChatColor.AQUA + "Other Kit Settings");
        sameta.setLore(Lists.newArrayList("",
                ChatColor.GRAY + "Change other kit settings such as the lore,",
                ChatColor.GRAY + "the menu position, display name, and permission node.", "",
                ChatColor.GOLD + "Left click" + ChatColor.YELLOW + " to increase the slot by +1",
                ChatColor.GOLD + "Right click" + ChatColor.YELLOW + " to decrease the slot by -1"));
        slotAmount.setItemMeta(sameta);
        menu.setItem(27, slotAmount);

        ItemStack status;
        ItemMeta sMeta;

        if (kit.getEnabled()) {
            if (SWExtension.get().isMinecraftNotLegacy())
                status = new ItemStack(Material.valueOf("LIME_WOOL"));
            else
                status = new ItemStack(Material.valueOf("WOOL"), 1, (byte) 5);

            sMeta = status.getItemMeta();
            sMeta.setDisplayName(ChatColor.GREEN + "Kit is enabled");
            sMeta.setLore(Lists.newArrayList("", ChatColor.GOLD + "Shift click " + ChatColor.YELLOW + "to " + ChatColor.RED + "disable" + ChatColor.YELLOW +" this kit"));
            status.setItemMeta(sMeta);
        }
        else {
            if (SWExtension.get().isMinecraftNotLegacy())
                status = new ItemStack(Material.valueOf("RED_WOOL"));
            else
                status = new ItemStack(Material.valueOf("WOOL"), 1, (byte)14);

            sMeta = status.getItemMeta();
            sMeta.setDisplayName(ChatColor.RED + "Kit is disabled");
            sMeta.setLore(Lists.newArrayList("",ChatColor.GOLD + "Shift click " + ChatColor.YELLOW + "to " + ChatColor.GREEN + "enable" + ChatColor.YELLOW +" this kit"));
            status.setItemMeta(sMeta);
        }
        menu.setItem(36, status);

        ItemStack invMan = new ItemStack(Material.BOOK);
        ItemMeta imm = invMan.getItemMeta();
        imm.setDisplayName(ChatColor.AQUA + "Extended Inventory Editor");
        imm.setLore(Lists.newArrayList(ChatColor.GRAY + "Clicking this item will bring you to another",
                ChatColor.GRAY + "menu in which you can change each item",
                ChatColor.GRAY + "position in the menu, and also manage",
                ChatColor.GRAY + "other slots than just the hotbar.", "",
                ChatColor.GOLD + "Click " + ChatColor.YELLOW + "to open the extended inventory editor."));
        imm.addEnchant(Enchantment.DURABILITY,1,true);
        imm.addItemFlags(ItemFlag.values());
        invMan.setItemMeta(imm);
        menu.setItem(43, invMan);

        player.openInventory(menu);
        editing.put(player, kit.getName());
        editingArmor.put(player, armor);
        editingContent.put(player, itemStacks);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getInventory() == null || !e.getView().getTitle().startsWith("Skywars Kit Creator") || !editing.containsKey(player) || ExtendedKitCreationMenu.viewingExtended.contains(player) || KitSettingsMenu.viewingSettings.contains(player)) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null || !e.getView().getTitle().startsWith("Skywars Kit Creator") || !editing.containsKey(player) || ExtendedKitCreationMenu.viewingExtended.contains(player) || KitSettingsMenu.viewingSettings.contains(player)) {
            return;
        }

        e.setCancelled(true);

        Inventory inventory = e.getClickedInventory();
        String kitName = editing.get(player);
        GameKit kit = GameKit.getKit(kitName);
        int slot = e.getSlot();
        ItemStack clicked = inventory.getItem(slot);

        ItemStack[] armor = kit.getArmor();
        ItemStack[] content = kit.getInventory();

        if (e.getView().getTopInventory().equals(inventory)) {

            if (slot == 9) {
                // player clicks the icon slot
                if (e.getCursor() != null && e.getCursor().getType() != Material.AIR) {
                    ItemStack cursor = e.getCursor();
                    kit.setIcon(cursor);
                    e.setCursor(null);
                    GameKit.saveKit(kit);
                    openMenu(player, kit);
                }
            }
            else if (slot == 18) {
                // player clicks no permission icon slot
                if (e.getCursor() != null && e.getCursor().getType() != Material.AIR) {
                    ItemStack cursor = e.getCursor();
                    kit.setLIcon(cursor);
                    e.setCursor(null);
                    GameKit.saveKit(kit);
                    openMenu(player, kit);
                }
            }
            else if (slot == 27) {
                GameKit.saveKit(kit);
                SWExtension.getKitSettingsMenu().openMenu(player, kit);
            }
            else if (slot == 36 && e.isShiftClick()) {
                // player clicks toggle kit toggle item slot
                kit.setEnabled(!kit.getEnabled());
                GameKit.saveKit(kit);
                openMenu(player, kit);
            }

            else if (slot == 11 || slot == 20 || slot == 29 || slot == 38) {
                // player clicks change armor item slot
                if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {
                    if (clicked.getType() == Material.BARRIER) { return; }

                    player.getInventory().addItem(clicked);
                    for (int i = 0; i <= 3; i++) {
                        if (armor[i] != null && armor[i].equals(clicked)) {
                            armor[i] = null;
                            kit.setArmor(armor);
                            break;
                        }
                    }
                    for (int i = 0; i < 36; i++) {
                        if (content[i] != null && content[i].equals(clicked)) {
                            content[i] = null;
                            kit.setInventory(content);
                            break;
                        }
                    }
                    GameKit.saveKit(kit);
                    openMenu(player, kit);
                }
                else {
                    ItemStack cursor = e.getCursor();
                    if ((slot==11) || (slot == 20 && cursor.getType().name().contains("CHESTPLATE")) || (slot == 29 && cursor.getType().name().contains("LEGGINGS")) || (slot == 38 && cursor.getType().name().contains("BOOTS"))) {
                        int i = 0;
                        if (slot == 11) {
                            i = 3;
                        }
                        else if (slot == 20) {
                            i = 2;
                        }
                        else if (slot == 29) {
                            i = 1;
                        }

                        if (armor[i] != null) {
                            player.getInventory().addItem(armor[i]);
                        }
                        armor[i] = cursor;
                        kit.setArmor(armor);
                        e.setCursor(null);
                        GameKit.saveKit(kit);
                        openMenu(player, kit);
                    }
                }
            }
            else if (slot == 43) {
                GameKit.saveKit(kit);
                SWExtension.getExtendedKitMenu().openMenu(player, kit);
            }
            else {
                // player clicks quick hotbar editor
                if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) { // player is not dragging an item
                    if (clicked != null && !glassSlots.contains(slot)) { // player is not clicking glass slot
                        int index = hotbarSlots.get(slot);
                        if (content[index] != null) {
                            content[index] = null;
                            player.getInventory().addItem(clicked);
                            kit.setInventory(content);
                        }
                        GameKit.saveKit(kit);
                        openMenu(player, kit);
                    }
                }
                else {
                    if (!glassSlots.contains(slot)) {
                        int index = hotbarSlots.get(slot);
                        ItemStack cursor = e.getCursor();
                        if (content[index] == null) {
                            content[index] = cursor;
                            kit.setInventory(content);
                        } else {
                            content[index] = cursor;
                            player.getInventory().addItem(clicked);
                            kit.setInventory(content);
                        }
                        e.setCursor(null);
                        GameKit.saveKit(kit);
                        openMenu(player, kit);
                    }
                }
            }
        }
        else {
            // player clicks its own inventory
            if (clicked != null) {
                if (e.getClick().isShiftClick()) {
                    String mat = clicked.getType().name();
                    if (mat.contains("HELMET")) {
                        if (armor[3] != null) {
                            player.getInventory().addItem(armor[3]);
                        }
                        armor[3] = clicked;
                        player.getInventory().setItem(slot, null);
                        kit.setArmor(armor);
                    } else if (mat.contains("CHESTPLATE")) {
                        if (armor[2] != null) {
                            player.getInventory().addItem(armor[2]);
                        }
                        armor[2] = clicked;
                        player.getInventory().setItem(slot, null);
                        kit.setArmor(armor);
                    } else if (mat.contains("LEGGINGS")) {
                        if (armor[1] != null) {
                            player.getInventory().addItem(armor[1]);
                        }
                        armor[1] = clicked;
                        player.getInventory().setItem(slot, null);
                        kit.setArmor(armor);
                    } else if (mat.contains("BOOTS")) {
                        if (armor[0] != null) {
                            player.getInventory().addItem(armor[0]);
                        }
                        armor[0] = clicked;
                        player.getInventory().setItem(slot, null);
                        kit.setArmor(armor);
                    } else {
                        for (int i = 0; i < 36; i++) {
                            if (content[i] == null) {
                                if (i > 8) {
                                    player.sendMessage(ChatColor.GRAY + "The first empty slot was " + i + " and therefore placed outside the hotbar. Open the Extended Inventory Editor to view the item.");
                                }
                                content[i] = clicked;
                                player.getInventory().setItem(slot, null);
                                kit.setInventory(content);
                                break;
                            }
                        }
                    }
                    GameKit.saveKit(kit);
                    openMenu(player, kit);
                }
                else {
                    e.setCancelled(false);
                }
            }
            else {
                e.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (editing.containsKey(player) && !ExtendedKitCreationMenu.viewingExtended.contains(player) && !KitSettingsMenu.viewingSettings.contains(player)) {
            GameKit.saveKit(GameKit.getKit(editing.get(player)));
            editing.remove(player);
        }
    }

}
