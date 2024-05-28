package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.LeaveResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsLeaveCommand {
    public static void onCommand(Player player, ModernArguments args) {
        LeaveResult leaveResult = MineClans.getInstance().getAPI().leave(player);
        String basePath = "factions.leave.";

        switch (leaveResult.getState()) {
            case FACTION_OWNER:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "faction_owner"));
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
