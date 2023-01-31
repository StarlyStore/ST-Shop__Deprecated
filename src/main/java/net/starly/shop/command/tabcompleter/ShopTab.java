package net.starly.shop.command.tabcompleter;

import net.starly.core.data.Config;
import net.starly.shop.ShopMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShopTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player)sender;
        if (args.length == 1) {
            if (player.isOp()) return Arrays.asList("생성", "제거", "열기", "리로드", "도움말", "편집");
            else return Arrays.asList("열기", "도움말");
        }

        if (args.length == 2) {
            if (player.isOp()) {
                if (args[0].equals("생성")) return Arrays.asList("<상점 이름>");
                else if (args[0].equals("열기") || args[0].equals("제거") || args[0].equals("편집"))
                    try {
                        return Arrays.asList(new Config("shop/", ShopMain.getPlugin()).getFileNames().toArray(new String[0]));
                    } catch (Exception ignored) {
                    }
            } else {
                if (args[0].equals("열기"))
                    try {
                        return Arrays.asList(new Config("shop/", ShopMain.getPlugin()).getFileNames().toArray(new String[0]));
                    } catch (Exception ignored) {
                    }
            }
        }
        return Collections.emptyList();
    }
}
