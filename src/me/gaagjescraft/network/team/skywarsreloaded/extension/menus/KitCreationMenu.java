package me.gaagjescraft.network.team.skywarsreloaded.extension.menus;

import com.google.common.collect.Lists;
import com.walrusone.skywarsreloaded.menus.gameoptions.objects.GameKit;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class KitCreationMenu implements Listener {

    private static List<Integer> glassSlots = Lists.newArrayList(0,1,2,3,4,5,6,7,8,
            10,12,17,
            19,21,26,
            27,28,30,35,
            37,39,44,
            45,46,47,48,49,50,51,52,53
            );

    private static HashMap<Player, String> editing = new HashMap<>();
    private static HashMap<Player, ItemStack[]> editingArmor = new HashMap<>();
    private static HashMap<Player, ItemStack[]> editingContent = new HashMap<>();

    public void openMenu(Player player, GameKit kit) {

        Inventory menu = Bukkit.createInventory(null, 54, "Skywars Kit Creator");

        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta bMeta = barrier.getItemMeta();
        bMeta.setDisplayName(ChatColor.RED + "No item");
        bMeta.setLore(Lists.newArrayList(ChatColor.GRAY + "Click an armor item to automatically", ChatColor.GRAY + "set it to this slot"));
        barrier.setItemMeta(bMeta);

        ItemStack glass;
        if (Main.get().isNewVersion()) {
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
            if (armor[i] != null) {
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

        int slot = 13;
        ItemStack[] itemStacks = kit.getInventory();
        for(int i=0;i<36;i++) {
            if (itemStacks[i] != null) {
                //if (!armorList.contains(item)) {
                    menu.setItem(slot, itemStacks[i]);

                    if (slot == 16) {
                        slot = 22;
                    } else if (slot == 25) {
                        slot = 31;
                    } else if (slot == 34) {
                        slot = 40;
                    } else {
                        slot++;
                        if (slot == 43) {
                            break;
                        }
                    }
               // }
            }

        }

        ItemStack icon = kit.getIcon();
        ItemMeta iMeta = icon.getItemMeta();
        iMeta.setDisplayName(ChatColor.GREEN + "Kit icon");
        List<String> iconLore = Lists.newArrayList(ChatColor.GRAY + "This item will be displayed when", ChatColor.GRAY + "the player has permission to select it", "");
        iconLore.addAll(kit.getColorLores());
        iMeta.setLore(iconLore);
        icon.setItemMeta(iMeta);

        ItemStack noIcon = kit.getLIcon();
        ItemMeta niMeta = noIcon.getItemMeta();
        niMeta.setDisplayName(ChatColor.RED + "No permission icon");
        niMeta.setLore(Lists.newArrayList(ChatColor.GRAY + "This item will be displayed when", ChatColor.GRAY + "the player does not have permission to select it", "", ChatColor.translateAlternateColorCodes('&', kit.getLockedLore())));
        noIcon.setItemMeta(niMeta);

        menu.setItem(9, icon);
        menu.setItem(18, noIcon);

        ItemStack status;
        ItemMeta sMeta;
        if (Main.get().isNewVersion()) {
            if (kit.getEnabled()) {
                status = new ItemStack(Material.valueOf("LIME_WOOL"));
                sMeta = status.getItemMeta();
                sMeta.setDisplayName(ChatColor.GREEN + "Kit is enabled");
                sMeta.setLore(Lists.newArrayList(ChatColor.GRAY + "Click to " + ChatColor.RED + "disable" + ChatColor.GRAY +" this kit"));
                status.setItemMeta(sMeta);
            }
            else {
                status = new ItemStack(Material.valueOf("RED_WOOL"));
                sMeta = status.getItemMeta();
                sMeta.setDisplayName(ChatColor.RED + "Kit is disabled");
                sMeta.setLore(Lists.newArrayList(ChatColor.GRAY + "Click to " + ChatColor.GREEN + "enable" + ChatColor.GRAY +" this kit"));
                status.setItemMeta(sMeta);
            }
        }
        else {
            if (kit.getEnabled()) {
                status = new ItemStack(Material.valueOf("WOOL"), 1, (byte) 5);
                sMeta = status.getItemMeta();
                sMeta.setDisplayName(ChatColor.GREEN + "Kit is enabled");
                sMeta.setLore(Lists.newArrayList(ChatColor.GRAY + "Click to " + ChatColor.RED + "disable" + ChatColor.GRAY +" this kit"));
                status.setItemMeta(sMeta);
            }
            else {
                status = new ItemStack(Material.valueOf("WOOL"), 1, (byte)14);
                sMeta = status.getItemMeta();
                sMeta.setDisplayName(ChatColor.RED + "Kit is disabled");
                sMeta.setLore(Lists.newArrayList(ChatColor.GRAY + "Click to " + ChatColor.GREEN + "enable" + ChatColor.GRAY +" this kit"));
                status.setItemMeta(sMeta);
            }
        }

        menu.setItem(36, status);

        player.openInventory(menu);
        editing.put(player, kit.getName());
        editingArmor.put(player, armor);
        editingContent.put(player, itemStacks);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null || !e.getView().getTitle().equals("Skywars Kit Creator") || !editing.containsKey(player)) {
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

        if (inventory == e.getView().getTopInventory()) {


            if (slot == 9) {
                // player clicks the icon slot
                if (e.getCursor() != null && e.getCursor().getType() != Material.AIR) {
                    ItemStack cursor = e.getCursor();

                    ItemStack icon = kit.getIcon();
                    ItemMeta iMeta = icon.getItemMeta();
                    iMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', kit.getColorName()));
                    iMeta.setLore(kit.getColorLores());
                    icon.setItemMeta(iMeta);

                    player.getInventory().addItem(icon);
                    kit.setIcon(cursor);
                    e.setCursor(null);
                    openMenu(player, kit);
                }
            }
            else if (slot == 18) {
                if (e.getCursor() != null && e.getCursor().getType() != Material.AIR) {
                    ItemStack cursor = e.getCursor();

                    ItemStack icon = kit.getLIcon();
                    ItemMeta iMeta = icon.getItemMeta();
                    iMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', kit.getColorName()));
                    iMeta.setLore(Lists.newArrayList(ChatColor.translateAlternateColorCodes('&', kit.getColoredLockedLore())));
                    icon.setItemMeta(iMeta);

                    player.getInventory().addItem(icon);
                    kit.setLIcon(cursor);
                    e.setCursor(null);
                    openMenu(player, kit);
                }
            }
            else if (slot == 36) {
                kit.setEnabled(!kit.getEnabled());
                openMenu(player, kit);
            }

            else if (slot == 11 || slot == 20 || slot == 29 || slot == 38) {

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
                        openMenu(player, kit);
                    }
                }
            }
            else {
                // player is clicking normal contents
                if (e.getCursor() == null || e.getCursor().getType() == Material.AIR) {
                    if (clicked != null && !glassSlots.contains(slot)) {
                        for (int i = 0; i < 36; i++) {
                            if (content[i] != null && content[i].equals(clicked)) {
                                content[i] = null;
                                player.getInventory().addItem(clicked);
                                kit.setInventory(content);
                                break;
                            }
                        }
                        openMenu(player, kit);
                    }
                }
                else {
                    ItemStack cursor = e.getCursor();
                    for (int i = 0; i < 36; i++) {
                        if (content[i] == null) {
                            content[i] = cursor;
                            kit.setInventory(content);
                            e.setCursor(null);
                            break;
                        }
                    }
                    openMenu(player, kit);
                }
            }
        }
        else {
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
                                content[i] = clicked;
                                player.getInventory().setItem(slot, null);
                                kit.setInventory(content);
                                break;
                            }
                        }
                    }
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
        if (editing.containsKey(player)) {
            GameKit.saveKit(GameKit.getKit(editing.get(player)));
            editing.remove(player);
            player.sendMessage(ChatColor.GRAY + "Saving the kit...");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getMessage().startsWith("openkitmenu ")) {
            String kit = e.getMessage().replace("openkitmenu ", "");
            GameKit kitt = GameKit.getKit(kit);
            if (kitt != null) {
                openMenu(e.getPlayer(), kitt);
            }
            else {
                GameKit.newKit(e.getPlayer(),kit);
                openMenu(e.getPlayer(), GameKit.getKit(kit));
                e.getPlayer().getInventory().clear();
            }
        }
    }

}
