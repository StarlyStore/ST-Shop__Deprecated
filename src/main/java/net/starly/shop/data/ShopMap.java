package net.starly.shop.data;

import net.starly.core.data.util.Tuple;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ShopMap {
    public static Map<Player, String> shopMap = new HashMap<>();

    public static Map<Player, ShopType> shopTypeMap = new HashMap<>();

    public static Map<Player, Tuple<String, Integer>> editPriceMap = new HashMap<>();
}
