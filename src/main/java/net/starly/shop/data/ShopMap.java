package net.starly.shop.data;


import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ShopMap {
    public static Map<Player, String> shopMap = new HashMap<>();

    public static Map<Player, GuiType> guiTypeMap = new HashMap<>();
}
