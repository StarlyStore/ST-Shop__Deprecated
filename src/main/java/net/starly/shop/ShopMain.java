package net.starly.shop;

import net.milkbowl.vault.economy.Economy;
import net.starly.core.bstats.Metrics;
import net.starly.core.data.Config;
import net.starly.shop.command.ShopCmd;
import net.starly.shop.command.tabcompleter.ShopTab;
import net.starly.shop.event.ChatListener;
import net.starly.shop.event.InventoryClickListener;
import net.starly.shop.event.InventoryCloseListener;
import net.starly.shop.event.InventoryDragListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;

public class ShopMain extends JavaPlugin {
    private static Economy economy = null;
    private static JavaPlugin plugin;
    public static Config message, config;
    public static DecimalFormat df = new DecimalFormat("###,###");

    @Override
    public void onEnable() {
        // DEPENDENCY
        if (Bukkit.getPluginManager().getPlugin("ST-Core") == null) {
            Bukkit.getLogger().warning("[" + getDescription().getName() + "] ST-Core 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            Bukkit.getLogger().warning("[" + getDescription().getName() + "] 다운로드 링크 : §fhttp://starly.kr/discord");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else if (!setupEconomy()) {
            Bukkit.getLogger().warning("[" + getDescription().getName() + "] Vault 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            Bukkit.getLogger().warning("[" + getDescription().getName() + "] 다운로드 링크 : §fhttps://www.spigotmc.org/resources/vault.34315/");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        plugin = this;
        new Metrics(this, 17609);

        // CONFIG
        message = new Config("message", this);
        message.loadDefaultConfig();
        message.setPrefix("messages.prefix");
        config = new Config("config", this);
        config.loadDefaultConfig();

        // COMMAND
        Bukkit.getPluginCommand("상점").setExecutor(new ShopCmd());
        Bukkit.getPluginCommand("상점").setTabCompleter(new ShopTab());

        // EVENT
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryDragListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
    }


    // SETUP VAULT ECONOMY
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static Economy getEconomy() {
        return economy;
    }
}