package net.starly.shop.gui;

import net.starly.core.data.Config;
import net.starly.shop.ShopMain;
import net.starly.shop.data.ShopType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.stream.Collectors;

import static net.starly.shop.ShopMain.*;
import static net.starly.shop.data.ShopMap.shopTypeMap;
import static net.starly.shop.data.ShopMap.shopMap;

public class ShopEdit {
    public static void openInventory(Player player, String shopName) {
        Config shopConfig = new Config("shop/" + shopName, ShopMain.getPlugin());
        if (!shopConfig.isFileExist()) {
            player.sendMessage(message.getMessage("errorMessages.shop.notExist"));
            return;
        }
        Inventory inventory = shopConfig.getInventory(shopName);
        ConfigurationSection section = shopConfig.getConfigurationSection(shopName + ".items");
        for (String key : section.getKeys(false)) {
            ItemStack item = shopConfig.getItemStack(shopName + ".items." + key);
            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
            ItemMeta itemMeta = item.getItemMeta();
            List<String> lore = config.getMessages("lore.edit");
            lore = lore.stream()
                    .map(s -> {
                        if (s.contains("{buy_price}") || s.contains("{sell_price}")) {
                            return s.replace("{buy_price}", shopConfig.getInt("prices.buy." + key) == -1 ? "구매불가" : df.format(shopConfig.getInt("prices.buy." + key)))
                                    .replace("{sell_price}", shopConfig.getInt("prices.sell." + key) == -1 ? "판매불가" : df.format(shopConfig.getInt("prices.sell." + key)));
                        }
                        return s;
                    }).collect(Collectors.toList());
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            inventory.setItem(Integer.parseInt(key), item);
        }
        player.openInventory(inventory);
        shopMap.put(player, shopName);
        shopTypeMap.put(player, ShopType.EDIT);
    }
}
