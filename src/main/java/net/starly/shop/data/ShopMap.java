package net.starly.shop.data;


import net.starly.core.data.util.Triple;
import net.starly.core.data.util.Tuple;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ShopMap {
    public static Map<Player, String> shopMap = new HashMap<>();

    public static Map<Player, ShopType> shopTypeMap = new HashMap<>();

    public static Map<Player, Tuple<String, Integer>> editPriceMap = new HashMap<>();

    public static Map<Player, Triple<String, ItemStack, Integer>> shopPurchaseMap = new HashMap<>();
}
