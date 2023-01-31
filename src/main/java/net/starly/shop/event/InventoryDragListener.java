package net.starly.shop.event;

import net.starly.shop.ShopMain;
import net.starly.shop.data.ShopType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

import static net.starly.shop.util.InventoryUtil.saveItem;
import static net.starly.shop.data.ShopMap.shopTypeMap;
import static net.starly.shop.data.ShopMap.shopMap;

public class InventoryDragListener implements Listener {
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (shopMap.containsKey(player) && shopTypeMap.get(player) == ShopType.EDIT) {
            if (event.getInventory() == player.getInventory()) return;
            String name = shopMap.get(player);
            Bukkit.getScheduler().runTaskAsynchronously(ShopMain.getPlugin(), () -> {
                for (int i : event.getInventorySlots()) {
                    saveItem(name, event.getInventory(), event.getInventory().getItem(i), i);
                }
            });
        }
    }
}
