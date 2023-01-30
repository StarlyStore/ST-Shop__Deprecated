package net.starly.shop.event;

import net.starly.shop.data.GuiType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import static net.starly.shop.data.ShopMap.guiTypeMap;
import static net.starly.shop.data.ShopMap.shopMap;

public class InventoryCloseListener implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        if (shopMap.containsKey(player)) {
            shopMap.remove(player);
            guiTypeMap.remove(player);
        }
    }
}
