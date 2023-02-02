package net.starly.shop.command;

import net.starly.core.data.Config;
import net.starly.core.util.StringUtil;
import net.starly.shop.ShopMain;
import net.starly.shop.data.ShopData;
import net.starly.shop.gui.ShopEdit;
import net.starly.shop.gui.ShopOpen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.starly.shop.ShopMain.*;

public class ShopCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length == 0 || args[0].equals("도움말")) {
            if (player.isOp()) msgConfig.getMessages("messages.shop.help").forEach(player::sendMessage);
            else msgConfig.getMessages("messages.shop.main").forEach(player::sendMessage);
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
                    player.sendMessage(msgConfig.getMessage("errorMessages.shop.invalidName"));
                    return true;
                }

                ShopData shopData = new ShopData(shopName);
                if (!shopData.isExist()) {
                    player.sendMessage(msgConfig.getMessage("errorMessages.shop.notExist"));
                    return true;
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
                ShopOpen.openInventory(player, shopName);
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
                ShopEdit.openInventory(player, shopName);
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
                player.sendMessage(msgConfig.getMessage("messages.shop.reload"));
                config.reloadConfig();
                msgConfig.reloadConfig();
                player.sendMessage(msgConfig.getMessage("messages.shop.reloadComplete"));
                return true;
            }
            default: {
                player.sendMessage(msgConfig.getMessage("errorMessages.wrongCommand"));
                return true;
            }
        }
    }
}
