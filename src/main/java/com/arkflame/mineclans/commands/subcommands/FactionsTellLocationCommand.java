package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.FactionChatResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsTellLocationCommand {
    public static void onCommand(Player player, ModernArguments args) {
        Location location = player.getLocation();
        String world = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        String basePath = "factions.tell_location.";

        String message = MineClans.getInstance().getMessages().getText(basePath + "message")
                           .replace("%world%", world)
                           .replace("%x%", String.valueOf(x))
                           .replace("%y%", String.valueOf(y))
                           .replace("%z%", String.valueOf(z));
        FactionChatResult result = MineClans.getInstance().getAPI().sendFactionMessage(player, message);

        switch (result.getState()) {
            case SUCCESS:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success"));
                break;
            case NOT_IN_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "not_in_faction"));
                break;
            case ERROR:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "error"));
                break;
            default:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "unknown_error"));
                break;
        }
    }
}
