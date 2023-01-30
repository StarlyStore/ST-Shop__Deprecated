package net.starly.shop.event;

import net.starly.shop.ShopMain;
import net.starly.shop.data.GuiType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

import static net.starly.shop.data.InventoryUtil.saveItem;
import static net.starly.shop.data.ShopMap.guiTypeMap;
import static net.starly.shop.data.ShopMap.shopMap;

public class InventoryDragListener implements Listener {
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (shopMap.containsKey(player) && guiTypeMap.get(player) == GuiType.EDIT) {
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
