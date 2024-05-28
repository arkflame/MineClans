package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.ToggleChatResult;
import com.arkflame.mineclans.api.results.ToggleChatResult.ToggleChatState;

public class FactionsChatCommand {
    public static void onCommand(Player player) {
        MineClans mineClans = MineClans.getInstance();
        String basePath = "factions.chat.";

        ToggleChatResult result = mineClans.getAPI().toggleChat(player);
        ToggleChatState state = result.getState();

        switch (state) {
            case ENABLED:
                player.sendMessage(mineClans.getMessages().getText(basePath + "enabled"));
                break;
            case DISABLED:
                player.sendMessage(mineClans.getMessages().getText(basePath + "disabled"));
                break;
            case NOT_IN_FACTION:
                player.sendMessage(mineClans.getMessages().getText(basePath + "not_in_faction"));
                break;
            default:
                player.sendMessage(mineClans.getMessages().getText(basePath + "unknown_error"));
                break;
        }
    }
}
