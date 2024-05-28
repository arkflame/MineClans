package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.RankChangeResult;
import com.arkflame.mineclans.api.results.RankChangeResult.RankChangeResultType;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsPromoteCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String playerName = args.getText(1);
        RankChangeResult promoteResult = MineClans.getInstance().getAPI().promote(player, playerName);
        RankChangeResultType resultType = promoteResult.getResultType();
        String basePath = "factions.promote.";

        switch (resultType) {
            case SUPERIOR_RANK:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "superior_rank"));
                break;
            case PLAYER_NOT_FOUND:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "player_not_found"));
                break;
            case NOT_IN_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "not_in_faction"));
                break;
            case NO_PERMISSION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_permission"));
                break;
            case CANNOT_PROMOTE:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "cannot_promote"));
                break;
            case CANNOT_PROMOTE_TO_LEADER:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "cannot_promote_to_leader"));
                break;
            case SUCCESS:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success"));
                break;
            default:
                break;
        }
    }
}
