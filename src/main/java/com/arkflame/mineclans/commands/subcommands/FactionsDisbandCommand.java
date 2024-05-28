package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.DisbandResult;
import com.arkflame.mineclans.api.results.DisbandResult.DisbandResultState;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsDisbandCommand {
    public static void onCommand(Player player, ModernArguments args) {
        DisbandResult disbandResult = MineClans.getInstance().getAPI().disband(player);
        DisbandResultState state = disbandResult.getState();
        String basePath = "factions.disband.";

        switch (state) {
            case NO_PERMISSION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_permission"));
                break;
            case NO_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_faction"));
                break;
            case SUCCESS:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success"));
                break;
            default:
                break;
        }
    }
}
