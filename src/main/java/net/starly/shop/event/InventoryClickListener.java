package net.starly.shop.event;

import net.starly.core.builder.ItemBuilder;
import net.starly.core.data.Config;
import net.starly.shop.ShopMain;
import net.starly.shop.data.GuiType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static net.starly.shop.ShopMain.df;
import static net.starly.shop.data.InventoryUtil.saveItem;
import static net.starly.shop.data.ShopMap.guiTypeMap;
import static net.starly.shop.data.ShopMap.shopMap;
import static net.starly.shop.ShopMain.config;

public class InventoryClickListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (shopMap.containsKey(player) && guiTypeMap.get(player) == GuiType.EDIT) {
            if (event.getClickedInventory() == player.getInventory()) return;

            String name = shopMap.get(player);
            Config shopConfig = new Config("shop/" + name, ShopMain.getPlugin());
            int slot = event.getSlot();

            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.getCurrentItem().setItemMeta(shopConfig.getItemStack(name + ".items." + slot).getItemMeta());
            }

            Bukkit.getScheduler().runTaskAsynchronously(ShopMain.getPlugin(), () -> {
                try {
                    if (event.getCursor() != null) saveItem(name, event.getClickedInventory(), event.getCurrentItem(), slot);
                    else saveItem(name, event.getClickedInventory(), null, slot);
                    List<String> lore = config.getStringList("lore.edit");
                    for (int s = 0; s < lore.size(); s++) {
                        lore.set(s, lore.get(s).replace("{buy_price}", shopConfig.getInt("prices.buy." + slot) == -1 ? "구매불가" : df.format(shopConfig.getInt("prices.buy." + slot) ))
                                .replace("{sell_price}", shopConfig.getInt("prices.sell." + slot) == -1 ? "판매불가" : df.format(shopConfig.getInt("prices.sell." + slot))));
                    }
                    ItemStack item = new ItemBuilder(event.getCurrentItem().getType(), event.getCurrentItem().getAmount())
                            .setDisplayName(event.getCurrentItem().getItemMeta().getDisplayName())
                            .setLore(lore).build();

                    event.setCurrentItem(item);
                } catch (Exception ignored) {
                }
            });
        }
    }
}
