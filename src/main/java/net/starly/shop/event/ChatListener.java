package net.starly.shop.event;

import net.starly.core.data.Config;
import net.starly.core.data.util.Tuple;
import net.starly.shop.ShopMain;
import net.starly.shop.data.ShopType;
import net.starly.shop.gui.ShopEdit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static net.starly.shop.ShopMain.df;
import static net.starly.shop.ShopMain.message;
import static net.starly.shop.data.ShopMap.*;
public class ChatListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ShopType shopType = shopTypeMap.get(player);

        if (shopType == ShopType.EDIT_BUY_PRICE || shopType == ShopType.EDIT_SELL_PRICE) {
            event.setCancelled(true);
            String msg = event.getMessage();
            int price;

            try {
                price = Integer.parseInt(msg);
            } catch (NumberFormatException e) {
                player.sendMessage(message.getMessage("errorMessages.shop.invalidPrice"));
                return;
            }
            Tuple<String, Integer> tuple = editPriceMap.get(player);
            Config shopConfig = new Config("shop/" + tuple.getA(), ShopMain.getPlugin());
            if (shopType == ShopType.EDIT_BUY_PRICE) {
                shopConfig.setInt("prices.buy." + tuple.getB(), price);
                player.sendMessage(message.getMessage("messages.shop.editBuyPrice").replace("{price}", df.format(price)));
            }

            if (shopType == ShopType.EDIT_SELL_PRICE) {
                shopConfig.setInt("prices.sell." + tuple.getB(), price);
                player.sendMessage(message.getMessage("messages.shop.editSellPrice").replace("{price}", df.format(price)));
            }

            Bukkit.getScheduler().runTask(ShopMain.getPlugin(), () -> ShopEdit.openInventory(player, tuple.getA()));
            editPriceMap.remove(player);
        }
    }
}
