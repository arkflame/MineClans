package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.LeaveResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsLeaveCommand {
    public static void onCommand(Player player, ModernArguments args) {
        LeaveResult leaveResult = MineClans.getInstance().getAPI().leave(player);

        switch (leaveResult.getState()) {
            case FACTION_OWNER:
                player.sendMessage("You are the owner. Disband your faction or transfer ownership instead.");
                break;
            case NO_FACTION:
                player.sendMessage("You are not in a faction.");
                break;
            case SUCCESS:
                player.sendMessage("You left your faction.");
                break;
            default:
                break;
        }
    }
}
