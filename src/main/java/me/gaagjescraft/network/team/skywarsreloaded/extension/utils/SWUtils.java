package me.gaagjescraft.network.team.skywarsreloaded.extension.utils;

import com.walrusone.skywarsreloaded.game.GameMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SWUtils {

    public static ItemStack validateSign() {
        try {
            return new ItemStack(Material.valueOf("OAK_SIGN"));
        } catch (IllegalArgumentException e) {
            return new ItemStack(Material.valueOf("SIGN"));
        }
    }


    public static LinkedHashMap<GameMap, Integer> getSortedGames(List<GameMap> hm) {
        HashMap<GameMap, Integer> games = new HashMap<>();
        for (GameMap g : hm) {
            if (g.canAddPlayer()) games.put(g, g.getAllPlayers().size());
        }

        // Create a list from elements of HashMap
        List<Map.Entry<GameMap, Integer>> list = new LinkedList<>(games.entrySet());

        // Sort the list
        list.sort(Map.Entry.comparingByValue());

        // put data from sorted list to hashmap
        LinkedHashMap<GameMap, Integer> temp = new LinkedHashMap<GameMap, Integer>();
        for (Map.Entry<GameMap, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static String c(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}
