package net.starly.shop.event;

import net.milkbowl.vault.economy.Economy;
import net.starly.core.builder.ItemBuilder;
import net.starly.core.data.Config;
import net.starly.core.data.util.Tuple;
import net.starly.shop.ShopMain;
import net.starly.shop.data.ShopType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

import static net.starly.shop.ShopMain.*;
import static net.starly.shop.data.ShopMap.*;
import static net.starly.shop.util.InventoryUtil.*;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (shopMap.containsKey(player) && shopTypeMap.get(player) == ShopType.EDIT) {
            if (event.getClickedInventory() == player.getInventory()) {
                if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) event.setCancelled(true);
                return;
            }

            String name = shopMap.get(player);
            Config shopConfig = new Config("shop/" + name, ShopMain.getPlugin());

            int slot = event.getSlot();

            if (event.getClick() == ClickType.SHIFT_LEFT) { // Item Remove
                if (event.getCurrentItem() == null) return;
                ItemStack item = event.getCurrentItem();
                ItemMeta meta = item.getItemMeta();
                meta.setLore(null);
                item.setItemMeta(meta);
                event.setCurrentItem(item);
                saveItem(name, event.getClickedInventory(), null, event.getSlot());
                return;
            } else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                if (event.getCurrentItem() == null) return;
                event.setCancelled(true);

                Inventory inv = Bukkit.createInventory(null, 9, "가격 변경하기");
                inv.setItem(4, new ItemBuilder(Material.EMERALD).setDisplayName("&a가격 변경하기")
                        .setLore(config.getStringList("lore.edit_price")).build());
                player.openInventory(inv);

                shopTypeMap.put(player, ShopType.EDIT_PRICE);

                Tuple<String, Integer> tuple = new Tuple<>(name, slot);
                editPriceMap.put(player, tuple);

                return;
            } else {
                if (event.getCurrentItem() != null) {
                    event.setCancelled(true);
                    return;
                }
            }
            Bukkit.getScheduler().runTaskAsynchronously(ShopMain.getPlugin(), () -> {
                try {
                    if (event.getCursor() != null) {
                        saveItem(name, event.getClickedInventory(), event.getCurrentItem(), slot);

                        List<String> lore = config.getMessages("lore.edit");
                        lore = lore.stream().map(s -> {
                            if (s.contains("{buy_price}") || s.contains("{sell_price}")) {
                                return s.replace("{buy_price}", "구매불가").replace("{sell_price}", "판매불가");
                            }
                            return s;
                        }).collect(Collectors.toList());
                        ItemStack item = shopConfig.getItemStack(name + ".items." + slot);
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.setLore(lore);
                        item.setItemMeta(itemMeta);
                        event.setCurrentItem(item);
                    }
                } catch (Exception ignored) {
                }
            });
        }
    }

    @EventHandler
    public void onEditPrice(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ShopType shopType = shopTypeMap.get(player);

        if (event.getView().getTitle().equals("가격 변경하기") && shopType == ShopType.EDIT_PRICE) {
            event.setCancelled(true);

            if (event.getClick() == ClickType.LEFT) {
                player.closeInventory();
                shopTypeMap.put(player, ShopType.EDIT_BUY_PRICE);

                player.sendMessage(message.getMessage("messages.shop.startEditBuyPrice"));

            } else if (event.getClick() == ClickType.RIGHT) {
                player.closeInventory();
                shopTypeMap.put(player, ShopType.EDIT_SELL_PRICE);

                player.sendMessage(message.getMessage("messages.shop.startEditSellPrice"));
            }
        }
    }

    @EventHandler
    public void shopPurchase(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        if (shopMap.containsKey(player) && shopTypeMap.get(player) == ShopType.OPEN) {
            event.setCancelled(true);

            if (event.getClickedInventory() == player.getInventory()) return;
            else if (event.getCurrentItem() == null) return;

            ClickType click = event.getClick();

            String name = shopMap.get(player);
            Config shopConfig = new Config("shop/" + name, ShopMain.getPlugin());

            int clickedSlot = event.getSlot();
            ItemStack clickedItem = shopConfig.getItemStack(name + ".items." + clickedSlot);

            Economy economy = getEconomy();
            double balance = economy.getBalance(player);
            int amount = 1;
            if (click.isShiftClick()) amount = 64;
            if (click == ClickType.LEFT || click == ClickType.SHIFT_LEFT) { // Buy
                int price = shopConfig.getInt("prices.buy." + clickedSlot);

                if (price == -1) {
                    player.sendMessage(message.getMessage("errorMessages.shop.cannotBuy"));
                } else if (balance >= price * amount) {
                    if (!hasEnoughSpace(player.getInventory(), clickedItem)) {
                        player.sendMessage(message.getMessage("errorMessages.shop.notEnoughSpace"));
                        return;
                    }

                    economy.withdrawPlayer(player, price * amount);
                    ItemStack item = shopConfig.getItemStack(name + ".items." + clickedSlot);
                    item.setAmount(amount);
                    player.getInventory().addItem(item);
                    player.sendMessage(message.getMessage("messages.shop.buy")
                            .replace("{item}", clickedItem.getItemMeta().getDisplayName().isEmpty() ? clickedItem.getType().toString() : clickedItem.getItemMeta().getDisplayName())
                            .replace("{price}", df.format((long) price * amount))
                            .replace("{amount}", df.format(amount)));
                } else {
                    player.sendMessage(message.getMessage("errorMessages.shop.notEnoughMoney"));
                }
            } else if (click == ClickType.RIGHT || click == ClickType.SHIFT_RIGHT) { // Sell
                int price = shopConfig.getInt("prices.sell." + clickedSlot);
                if (price == -1) {
                    player.sendMessage(message.getMessage("errorMessages.shop.cannotSell"));
                    return;
                } else if (!hasItem(player.getInventory(), new ItemBuilder(clickedItem.getType(), amount).setItemMeta(clickedItem.getItemMeta()).build())) {
                    player.sendMessage(message.getMessage("errorMessages.shop.notEnoughItem"));
                    return;
                }

                economy.depositPlayer(player, price * amount);
                player.getInventory().removeItem(new ItemBuilder(clickedItem.getType(), amount).setItemMeta(clickedItem.getItemMeta()).build());

                player.sendMessage(message.getMessage("messages.shop.sell")
                        .replace("{item}", clickedItem.getItemMeta().getDisplayName().isEmpty() ? clickedItem.getType().toString() : clickedItem.getItemMeta().getDisplayName())
                        .replace("{price}", df.format((long) price * amount))
                        .replace("{amount}", df.format(amount)));
            }
        }
    }
}
