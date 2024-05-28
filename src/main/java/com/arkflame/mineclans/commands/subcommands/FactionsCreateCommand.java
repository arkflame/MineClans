package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.CreateResult;
import com.arkflame.mineclans.api.results.CreateResult.CreateResultState;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsCreateCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String factionName = args.getText(1);
        MineClans mineClans = MineClans.getInstance();
        String basePath = "factions.create.";

        CreateResult createResult = mineClans.getAPI().create(player, factionName);
        CreateResultState state = createResult.getState();

        switch (state) {
            case ALREADY_HAVE_FACTION:
                player.sendMessage(mineClans.getMessages().getText(basePath + "already_have_faction"));
                break;
            case FACTION_EXISTS:
                player.sendMessage(mineClans.getMessages().getText(basePath + "faction_exists"));
                break;
            case NULL_NAME:
                player.sendMessage(mineClans.getMessages().getText(basePath + "usage"));
                break;
            case SUCCESS:
                player.sendMessage(mineClans.getMessages().getText(basePath + "success"));
                break;
            case ERROR:
                player.sendMessage(mineClans.getMessages().getText(basePath + "error"));
                break;
            default:
                break;
        }
    }
}
