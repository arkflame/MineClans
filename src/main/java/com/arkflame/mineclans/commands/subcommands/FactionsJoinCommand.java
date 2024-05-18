package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.JoinResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsJoinCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String factionName = args.getText(1);
        JoinResult joinResult = MineClans.getInstance().getAPI().join(player, factionName);

        switch (joinResult.getState()) {
            case ALREADY_HAVE_FACTION:
                player.sendMessage("You are already in a faction.");
                break;
            case NOT_INVITED:
                player.sendMessage("You are not invited to this faction.");
                break;
            case NO_FACTION:
                player.sendMessage("No faction with this name.");
                break;
            case NULL_NAME:
                player.sendMessage("Usage: /factions join <faction>");
                break;
            case SUCCESS:
                player.sendMessage("You have joined the faction " + factionName + ".");
                break;
            default:
                break;
        }
    }
}
