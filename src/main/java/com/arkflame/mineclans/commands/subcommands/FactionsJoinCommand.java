package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.JoinResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsJoinCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String factionName = args.getText(1);
        JoinResult joinResult = MineClans.getInstance().getAPI().join(player, factionName);
        String basePath = "factions.join.";

        switch (joinResult.getState()) {
            case ALREADY_HAVE_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "already_in_faction"));
                break;
            case NOT_INVITED:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "not_invited"));
                break;
            case NO_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_faction"));
                break;
            case NULL_NAME:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "usage"));
                break;
            case SUCCESS:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success").replace("%faction%", factionName));
                break;
            default:
                break;
        }
    }
}
