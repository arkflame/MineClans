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

        switch (state) {
            case NO_PERMISSION:
                player.sendMessage("You have to be LEADER to disband the faction.");
                break;
            case NO_FACTION:
                player.sendMessage("You have no faction.");
                break;
            case SUCCESS:
                player.sendMessage("Disbanded faction.");
                break;
            default:
                break;
        }
    }
}
