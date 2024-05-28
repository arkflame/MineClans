package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.RankChangeResult;
import com.arkflame.mineclans.api.results.RankChangeResult.RankChangeResultType;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsDemoteCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String playerName = args.getText(1);
        MineClans mineClans = MineClans.getInstance();
        String basePath = "factions.demote.";

        RankChangeResult demoteResult = mineClans.getAPI().demote(player, playerName);
        RankChangeResultType resultType = demoteResult.getResultType();

        switch (resultType) {
            case SUPERIOR_RANK:
                player.sendMessage(mineClans.getMessages().getText(basePath + "superior_rank"));
                break;
            case PLAYER_NOT_FOUND:
                player.sendMessage(mineClans.getMessages().getText(basePath + "player_not_found"));
                break;
            case NOT_IN_FACTION:
                player.sendMessage(mineClans.getMessages().getText(basePath + "not_in_faction"));
                break;
            case NO_PERMISSION:
                player.sendMessage(mineClans.getMessages().getText(basePath + "no_permission"));
                break;
            case CANNOT_DEMOTE:
                player.sendMessage(mineClans.getMessages().getText(basePath + "cannot_demote"));
                break;
            case SUCCESS:
                player.sendMessage(mineClans.getMessages().getText(basePath + "success"));
                break;
            default:
                break;
        }
    }
}
