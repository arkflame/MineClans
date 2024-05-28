package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.UninviteResult;
import com.arkflame.mineclans.api.results.UninviteResult.UninviteResultState;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsUninviteCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String targetPlayerName = args.getText(1);
        String basePath = "factions.uninvite.";

        UninviteResult uninviteResult = MineClans.getInstance().getAPI().uninvite(player, targetPlayerName);
        UninviteResultState state = uninviteResult.getState();

        switch (state) {
            case NULL_NAME:   
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "null_name"));
                break;
            case NO_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_faction"));
                break;
            case NO_PERMISSION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_permission"));
                break;
            case NOT_INVITED:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "not_invited"));
                break;
            case SUCCESS:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success"));
                break;
            case PLAYER_NOT_FOUND:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "player_not_found"));
                break;
            default:
                break;
        }
    }
}
