package me.gaagjescraft.network.team.skywarsreloaded.extension.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SWUtils {

    public static ItemStack validateSign() {
        try {
            return new ItemStack(Material.valueOf("OAK_SIGN"));
        } catch (IllegalArgumentException e) {
            return new ItemStack(Material.valueOf("SIGN"));
        }
    }

}
