package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.FriendlyFireResult;
import com.arkflame.mineclans.api.results.FriendlyFireResult.FriendlyFireResultState;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsFriendlyFireCommand {
    public static void onCommand(Player player, ModernArguments args) {
        FriendlyFireResult result = MineClans.getInstance().getAPI().toggleFriendlyFire(player);
        FriendlyFireResultState state = result.getState();

        switch (state) {
            case ENABLED:
                player.sendMessage(ChatColor.GREEN + "Friendly fire is now enabled in your faction.");
                break;
            case NOT_ADMIN:
                player.sendMessage(ChatColor.RED + "You are not admin of this faction.");
                break;
            case DISABLED:
                player.sendMessage(ChatColor.GREEN + "Friendly fire is now disabled in your faction.");
                break;
            case NOT_IN_FACTION:
                player.sendMessage(ChatColor.RED + "You are not in a faction.");
                break;
            case ERROR:
                player.sendMessage(ChatColor.RED + "An error occurred while toggling friendly fire.");
                break;
            default:
                player.sendMessage(ChatColor.RED + "An unknown error occurred.");
                break;
        }
    }
}
