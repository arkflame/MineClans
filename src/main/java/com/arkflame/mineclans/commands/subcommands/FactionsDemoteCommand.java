package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.RankChangeResult;
import com.arkflame.mineclans.api.results.RankChangeResult.RankChangeResultType;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsDemoteCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String playerName = args.getText(1);
        RankChangeResult demoteResult = MineClans.getInstance().getAPI().demote(player, playerName);
        RankChangeResultType resultType = demoteResult.getResultType();

        switch (resultType) {
            case SUPERIOR_RANK:
                player.sendMessage("The player is higher or same rank than you.");
                break;
            case PLAYER_NOT_FOUND:
                player.sendMessage("Player not found.");
                break;
            case NOT_IN_FACTION:
                player.sendMessage("Player is not in your faction.");
                break;
            case NO_PERMISSION:
                player.sendMessage("You require LEADER to promote/demote players.");
                break;
            case CANNOT_DEMOTE:
                player.sendMessage("Cannot demote player.");
                break;
            case SUCCESS:
                player.sendMessage("Player demoted successfully.");
                break;
            default:
                break;
        }
    }
}