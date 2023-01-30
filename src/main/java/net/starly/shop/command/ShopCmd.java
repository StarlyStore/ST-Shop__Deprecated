package net.starly.shop.command;

import net.starly.core.data.Config;
import net.starly.core.util.StringUtil;
import net.starly.shop.ShopMain;
import net.starly.shop.data.GuiType;
import net.starly.shop.data.ShopData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import static net.starly.shop.ShopMain.*;
import static net.starly.shop.data.ShopMap.guiTypeMap;
import static net.starly.shop.data.ShopMap.shopMap;

public class ShopCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length == 0) {
            msgConfig.getMessages("messages.shop.help").forEach(player::sendMessage);
            return true;
        }
        switch (args[0]) {
            case "생성": {
                if (!player.hasPermission("starly.shop.create")) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.noPermission"));
                    return true;
                }

                if (args.length == 1) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.shop.noName"));
                    return true;
                }

                if (args.length != 2) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.wrongCommand"));
                    return true;
                }

                String shopName = args[1];
                if (StringUtil.containsSpecialChar(shopName)) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.shop.invalidName"));
                    return true;
                }

                ShopData shopData = new ShopData(shopName);
                shopData.create();
                player.sendMessage(msgConfig.getMessage("messages.shop.create").replace("{name}", shopName));
                return true;
            }
            case "제거": {
                if (!player.hasPermission("starly.shop.delete")) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.noPermission"));
                    return true;
                }

                if (args.length == 1) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.shop.noName"));
                    return true;
                }

                if (args.length != 2) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.wrongCommand"));
                    return true;
                }

                String shopName = args[1];
                if (StringUtil.containsSpecialChar(shopName)) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.invalidName"));
                    return true;
                }

                ShopData shopData = new ShopData(shopName);
                if (!shopData.isExist()) {
                    player.sendMessage(msgConfig.getMessage("errorMessages."));
                }

                shopData.delete();
                player.sendMessage(msgConfig.getMessage("messages.shop.delete").replace("{name}", shopName));
                return true;
            }
            case "열기": {
                if (!player.hasPermission("starly.shop.open")) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.noPermission"));
                    return true;
                }

                if (args.length == 1) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.shop.noName"));
                    return true;
                }

                if (args.length != 2) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.wrongCommand"));
                    return true;
                }

                String shopName = args[1];
                player.openInventory(new Config("shop/" + shopName, ShopMain.getPlugin()).getInventory(shopName));
                shopMap.put(player, shopName);
                guiTypeMap.put(player, GuiType.OPEN);
                return true;
            }
            case "편집": {
                if (!player.hasPermission("starly.shop.edit")) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.noPermission"));
                    return true;
                }

                if (args.length == 1) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.shop.noName"));
                    return true;
                }

                if (args.length != 2) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.wrongCommand"));
                    return true;
                }

                String shopName = args[1];
                Config shopConfig = new Config("shop/" + shopName, ShopMain.getPlugin());
                Inventory inventory = shopConfig.getInventory(shopName);
                for (int i = 0; i < inventory.getSize(); i++) {
                    ItemStack item = inventory.getItem(i);
                    if (item == null) continue;
                    ItemMeta itemMeta = item.getItemMeta();
                    List<String> lore = config.getMessages("lore.edit");
                    for (int s = 0; s < lore.size(); s++) {
                        lore.set(s, lore.get(s).replace("{buy_price}", shopConfig.getInt("prices.buy." + i) == -1 ? "구매불가" : df.format(shopConfig.getInt("prices.buy." + i) ))
                                .replace("{sell_price}", shopConfig.getInt("prices.sell." + i) == -1 ? "판매불가" : df.format(shopConfig.getInt("prices.sell." + i))));
                    }
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                    inventory.setItem(i, item);
                }
                player.openInventory(inventory);
                shopMap.put(player, shopName);
                guiTypeMap.put(player, GuiType.EDIT);
                return true;
            }
            case "목록": {
                if (!player.hasPermission("starly.shop.list")) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.noPermission"));
                    return true;
                }

                if (args.length != 1) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.wrongCommand"));
                    return true;
                }

                player.sendMessage(String.join("\n", new Config("shop/", ShopMain.getPlugin()).getFileNames().toArray(new String[0])));
                return true;
            }
            case "리로드": {
                if (!player.hasPermission("starly.shop.reload")) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.noPermission"));
                    return true;
                }

                if (args.length != 1) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.wrongCommand"));
                    return true;
                }
                config.reloadConfig();
                msgConfig.reloadConfig();
                return true;
            }
            default: {
                player.sendMessage(msgConfig.getMessage("errorMessages.wrongCommand"));
                return true;
            }
        }
    }
}
