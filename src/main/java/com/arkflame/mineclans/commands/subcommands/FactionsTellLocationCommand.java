package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.ChatColor;
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

        String message = "My location is " + world + ": " + x + ", " + y + ", " + z;
        FactionChatResult result = MineClans.getInstance().getAPI().sendFactionMessage(player, message);

        switch (result.getState()) {
            case SUCCESS:
                player.sendMessage(ChatColor.GREEN + "Location shared with your faction.");
                break;
            case NOT_IN_FACTION:
                player.sendMessage(ChatColor.RED + "You are not in a faction.");
                break;
            case ERROR:
                player.sendMessage(ChatColor.RED + "An error occurred while sending the message.");
                break;
            default:
                player.sendMessage(ChatColor.RED + "An unknown error occurred.");
                break;
        }
    }
}
