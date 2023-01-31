package net.starly.shop.gui;

import net.starly.core.data.Config;
import net.starly.shop.ShopMain;
import net.starly.shop.data.ShopType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.starly.shop.ShopMain.*;
import static net.starly.shop.ShopMain.df;
import static net.starly.shop.data.ShopMap.shopMap;
import static net.starly.shop.data.ShopMap.shopTypeMap;

public class ShopOpen {
    public static void openInventory(Player player, String shopName) {
        Config shopConfig = new Config("shop/" + shopName, ShopMain.getPlugin());
        if (!shopConfig.isFileExist()) {
            player.sendMessage(msgConfig.getMessage("errorMessages.shop.notExist"));
            return;
        }
        Inventory inventory = shopConfig.getInventory(shopName);
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) continue;
            ItemMeta itemMeta = item.getItemMeta();
            List<String> itemLore = shopConfig.getItemStack(shopName + ".items." + i).getItemMeta().getLore();
            List<String> lore = itemLore == null ? new ArrayList<>() : itemLore;
            lore.add("");
            lore.addAll(config.getMessages("lore.open"));
            int finalI = i;
            lore = lore.stream()
                    .map(s -> {
                        if (s.contains("{buy_price}") || s.contains("{sell_price}")) {
                            return s.replace("{buy_price}", shopConfig.getInt("prices.buy." + finalI) == -1 ? "구매불가" : df.format(shopConfig.getInt("prices.buy." + finalI)))
                                    .replace("{sell_price}", shopConfig.getInt("prices.sell." + finalI) == -1 ? "판매불가" : df.format(shopConfig.getInt("prices.sell." + finalI)));
                        }
                        return s;
                    }).collect(Collectors.toList());
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            inventory.setItem(i, item);
        }
        player.openInventory(inventory);
        shopMap.put(player, shopName);
        shopTypeMap.put(player, ShopType.OPEN);
    }
}
