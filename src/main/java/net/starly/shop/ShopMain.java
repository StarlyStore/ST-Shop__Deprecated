package net.starly.shop;

import net.starly.core.bstats.Metrics;
import net.starly.core.data.Config;
import net.starly.shop.command.ShopCmd;
import net.starly.shop.command.tabcompleter.ShopTab;
import net.starly.shop.event.InventoryClickListener;
import net.starly.shop.event.InventoryCloseListener;
import net.starly.shop.event.InventoryDragListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;

public class ShopMain extends JavaPlugin {
    private static JavaPlugin plugin;
    public static Config msgConfig, config;
    public static DecimalFormat df = new DecimalFormat("###,###");

    @Override
    public void onEnable() {
        // DEPENDENCY
        if (Bukkit.getPluginManager().getPlugin("ST-Core") == null) {
            Bukkit.getLogger().warning("[" + plugin.getName() + "] ST-Core 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            Bukkit.getLogger().warning("[" + plugin.getName() + "] 다운로드 링크 : &fhttp://starly.kr/discord");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        plugin = this;
        new Metrics(this, 12345);

        msgConfig = new Config("msgConfig", this);
        msgConfig.loadDefaultConfig();
        msgConfig.setPrefix("messages.prefix");

        config = new Config("config", this);
        config.loadDefaultConfig();

        Bukkit.getPluginCommand("상점").setExecutor(new ShopCmd());
        Bukkit.getPluginCommand("상점").setTabCompleter(new ShopTab());

        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryDragListener(), this);
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }
}