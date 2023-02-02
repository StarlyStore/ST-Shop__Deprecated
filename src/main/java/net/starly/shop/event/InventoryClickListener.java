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
                event.getCurrentItem().setItemMeta(shopConfig.getItemStack(name + ".items." + slot).getItemMeta());
                saveItem(name, event.getClickedInventory(), null, event.getSlot());
                return;
            } else if (event.getClick() == ClickType.SHIFT_RIGHT) {
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
                                return s.replace("{buy_price}", shopConfig.getInt("prices.buy." + slot) == -1 ? "구매불가" : df.format(shopConfig.getInt("prices.buy." + slot))).replace("{sell_price}", shopConfig.getInt("prices.sell." + slot) == -1 ? "판매불가" : df.format(shopConfig.getInt("prices.sell." + slot)));
                            }
                            return s;
                        }).collect(Collectors.toList());
                        ItemStack item = new ItemBuilder(event.getCurrentItem().getType(), event.getCurrentItem().getAmount()).setDisplayName(event.getCurrentItem().getItemMeta().getDisplayName()).setLore(lore).build();
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

            if (event.getClick().isLeftClick()) {
                player.closeInventory();
                shopTypeMap.put(player, ShopType.EDIT_BUY_PRICE);

                player.sendMessage(msgConfig.getMessage("messages.shop.startEditBuyPrice"));

            } else if (event.getClick().isRightClick()) {
                player.closeInventory();
                shopTypeMap.put(player, ShopType.EDIT_SELL_PRICE);

                player.sendMessage(msgConfig.getMessage("messages.shop.startEditSellPrice"));
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

            if (click == ClickType.LEFT || click == ClickType.SHIFT_LEFT) { // Buy
                int price = shopConfig.getInt("prices.buy." + clickedSlot);

                if (click == ClickType.SHIFT_LEFT) {
                    amount = 64;
                    price *= 64;
                }

                if (price == -1) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.shop.cannotBuy"));
                } else if (balance >= price) {
                    if (!hasEnoughSpace(player.getInventory(), clickedItem)) {
                        player.sendMessage(msgConfig.getMessage("errorMessages.shop.notEnoughSpace"));
                        return;
                    }

                    economy.withdrawPlayer(player, price);
                    player.getInventory().addItem(new ItemBuilder(clickedItem.getType(), amount).setItemMeta(clickedItem.getItemMeta()).build());
                    player.sendMessage(msgConfig.getMessage("messages.shop.buy")
                            .replace("{item}", clickedItem.getItemMeta().getDisplayName().isEmpty() ? clickedItem.getType().toString() : clickedItem.getItemMeta().getDisplayName())
                            .replace("{price}", df.format(price))
                            .replace("{amount}", df.format(amount)));
                } else {
                    player.sendMessage(msgConfig.getMessage("errorMessages.shop.notEnoughMoney"));
                }
            } else if (click == ClickType.RIGHT || click == ClickType.SHIFT_RIGHT) { // Sell
                int price = shopConfig.getInt("prices.sell." + clickedSlot);

                if (click == ClickType.SHIFT_RIGHT) {
                    amount = 64;
                    price *= 64;
                }
                if (price == -1) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.shop.cannotSell"));
                    return;
                } else if (!hasItem(player.getInventory(), new ItemBuilder(clickedItem.getType(), amount).setItemMeta(clickedItem.getItemMeta()).build())) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.shop.notEnoughItem"));
                    return;
                }

                economy.depositPlayer(player, price);
                player.getInventory().removeItem(new ItemBuilder(clickedItem.getType(), amount).setItemMeta(clickedItem.getItemMeta()).build());

                player.sendMessage(msgConfig.getMessage("messages.shop.sell")
                        .replace("{item}", clickedItem.getItemMeta().getDisplayName().isEmpty() ? clickedItem.getType().toString() : clickedItem.getItemMeta().getDisplayName())
                        .replace("{price}", df.format(price))
                        .replace("{amount}", df.format(amount)));
            }
        }
    }
}
