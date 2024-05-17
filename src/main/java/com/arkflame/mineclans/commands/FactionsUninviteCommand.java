package com.arkflame.mineclans.commands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.UninviteResult;
import com.arkflame.mineclans.api.UninviteResult.UninviteResultState;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsUninviteCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String targetPlayerName = args.getText(1);
        UninviteResult uninviteResult = MineClans.getInstance().getAPI().uninvite(player, targetPlayerName);
        UninviteResultState state = uninviteResult.getState();

        switch (state) {
            case NULL_NAME:   
                player.sendMessage("Usage: /factions uninvite <player>");
                break;
            case NO_FACTION:
                player.sendMessage("You have no faction.");
                break;
            case NOT_OWNER:
                player.sendMessage("You are not the owner.");
                break;
            case NOT_INVITED:
                player.sendMessage("Player is not invited.");
                break;
            case SUCCESS:
                player.sendMessage("Player uninvited successfully.");
                break;
            case PLAYER_NOT_FOUND:
                player.sendMessage("Player not found.");
                break;
            default:
                break;
        }
    }
}
