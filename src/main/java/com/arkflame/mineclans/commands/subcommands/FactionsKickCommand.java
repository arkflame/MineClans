package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.api.results.KickResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.MineClans;

public class FactionsKickCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String playerName = args.getText(1);
        KickResult kickResult = MineClans.getInstance().getAPI().kick(player, playerName);
        
        switch (kickResult.getState()) {
            case SUCCESS:
                player.sendMessage("Player " + playerName + " has been kicked from the faction.");
                break;
            case NOT_IN_FACTION:
                player.sendMessage("You are not in a faction.");
                break;
            case NOT_MODERATOR:
                player.sendMessage("You are not moderator of the faction.");
                break;
            case PLAYER_NOT_FOUND:
                player.sendMessage("The specified player is not a member of your faction.");
                break;
            case SUPERIOR_RANK:
                player.sendMessage("You cannot kick a player with a higher or equal rank.");
                break;
            case NOT_YOURSELF:
                player.sendMessage("You cannot kick yourself.");
                break;
            default:
                player.sendMessage("An error occurred while kicking the player.");
                break;
        }
    }
}
