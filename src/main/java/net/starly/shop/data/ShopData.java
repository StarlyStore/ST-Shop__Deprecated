package net.starly.shop.data;

import net.starly.core.data.Config;
import net.starly.shop.ShopMain;
import org.bukkit.Bukkit;

public class ShopData {
    private final Config config;
    private final String name;

    public ShopData(String shopName) {
        this.config = new Config("shop/" + shopName, ShopMain.getPlugin());
        this.name = shopName;
    }

    public void create() {
        config.setInventory(name, Bukkit.createInventory(null, 54, name), name);
        config.saveConfig();
    }

    public void delete() {
        config.delete();
    }

    public Boolean isExist() {
        return config.isFileExist();
    }
}
