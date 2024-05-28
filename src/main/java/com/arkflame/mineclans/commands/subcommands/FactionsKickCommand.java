package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.api.results.KickResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.MineClans;

public class FactionsKickCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String playerName = args.getText(1);
        KickResult kickResult = MineClans.getInstance().getAPI().kick(player, playerName);
        String basePath = "factions.kick.";

        switch (kickResult.getState()) {
            case SUCCESS:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success").replace("%player%", playerName));
                break;
            case NOT_IN_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "not_in_faction"));
                break;
            case NOT_MODERATOR:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "not_moderator"));
                break;
            case PLAYER_NOT_FOUND:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "player_not_found"));
                break;
            case SUPERIOR_RANK:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "superior_rank"));
                break;
            case NOT_YOURSELF:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "not_yourself"));
                break;
            default:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "error"));
                break;
        }
    }
}
