package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.api.results.OpenChestResult;
import com.arkflame.mineclans.MineClans;

public class FactionsChestCommand {
    public static void onCommand(Player player) {
        OpenChestResult result = MineClans.getInstance().getAPI().openChest(player);

        switch (result.getResultType()) {
            case SUCCESS:
                player.sendMessage(ChatColor.GREEN + "You have opened the faction chest.");
                break;
            case NOT_IN_FACTION:
                player.sendMessage(ChatColor.RED + "You are not in a faction.");
                break;
            case NO_PERMISSION:
                player.sendMessage(ChatColor.RED + "You need to be MEMBER to open the faction chest.");
                break;
            case ERROR:
                player.sendMessage(ChatColor.RED + "An error occurred while trying to open the chest.");
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown result.");
                break;
        }
    }
}
