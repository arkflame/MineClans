package com.arkflame.mineclans.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.ToggleChatResult;
import com.arkflame.mineclans.api.ToggleChatResult.ToggleChatState;

public class FactionsChatCommand {
    public static void onCommand(Player player) {
        ToggleChatResult result = MineClans.getInstance().getAPI().toggleChat(player);
        ToggleChatState state = result.getState();

        switch (state) {
            case ENABLED:
                player.sendMessage(ChatColor.GREEN + "Faction chat enabled.");
                break;
            case DISABLED:
                player.sendMessage(ChatColor.RED + "Faction chat disabled.");
                break;
            case NOT_IN_FACTION:
                player.sendMessage(ChatColor.RED + "You are not in a faction.");
                break;
            default:
                player.sendMessage(ChatColor.RED + "An unknown error occurred.");
                break;
        }
    }
}

