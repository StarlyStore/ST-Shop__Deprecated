package net.starly.shop.util;

import net.starly.core.data.Config;
import net.starly.shop.ShopMain;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryUtil {
    public static void saveItem(String name, Inventory value, ItemStack itemStack, int slot) {
        Config config = new Config("shop/" + name, ShopMain.getPlugin());
        config.setString(name + ".title", config.getString(name + ".title"));
        config.setInt(name + ".size", value.getSize());
        if (itemStack != null) {
            config.setItemStack(name + ".items." + slot, itemStack);
            config.setInt("prices.buy." + slot, -1);
            config.setInt("prices.sell." + slot, -1);
            config.saveConfig();
        } else {
            config.getConfig().set(name + ".items." + slot, null);
            config.getConfig().set("prices.buy." + slot, null);
            config.getConfig().set("prices.sell." + slot, null);
            config.saveConfig();
        }

        if (config.getConfigurationSection(name + ".items") == null) {
            config.setObject(name + ".items", new HashMap<>());
        }

        config.saveConfig();
    }

    public static Boolean hasEnoughSpace(Inventory inventory, ItemStack toAdd) {
        int freeSpace = 0;
        for (ItemStack item : inventory) {
            if (item == null) {
                freeSpace += toAdd.getMaxStackSize();
            } else if (item == toAdd) {
                freeSpace += toAdd.getMaxStackSize() - item.getAmount();
            }
        }
        freeSpace -= toAdd.getMaxStackSize() * 5;
        return toAdd.getAmount() <= freeSpace;
    }

    public static Boolean hasItem(Inventory inventory, ItemStack itemStack) {
        int amount = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.isSimilar(itemStack)) amount += item.getAmount();
        }
        return amount >= itemStack.getAmount();
    }
}
