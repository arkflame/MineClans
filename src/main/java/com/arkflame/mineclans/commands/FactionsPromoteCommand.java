package com.arkflame.mineclans.commands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.RankChangeResult;
import com.arkflame.mineclans.api.RankChangeResult.RankChangeResultType;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsPromoteCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String playerName = args.getText(1);
        RankChangeResult promoteResult = MineClans.getInstance().getAPI().promote(player, playerName);
        RankChangeResultType resultType = promoteResult.getResultType();

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
            case ADMIN_REQUIRED:
                player.sendMessage("You require ADMIN to promote/demote players.");
                break;
            case CANNOT_PROMOTE:
                player.sendMessage("You cannot promote this player anymore.");
                break;
            case CANNOT_PROMOTE_TO_LEADER:
                player.sendMessage("Cannot promote player to leader.");
                break;
            case SUCCESS:
                player.sendMessage("Player promoted successfully.");
                break;
            default:
                break;
        }
    }
}
