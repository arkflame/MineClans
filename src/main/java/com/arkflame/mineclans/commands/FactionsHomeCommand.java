package com.arkflame.mineclans.commands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.HomeResult;
import com.arkflame.mineclans.api.HomeResult.HomeResultState;

public class FactionsHomeCommand {
    public static void onCommand(Player player) {
        HomeResult result = MineClans.getInstance().getAPI().getHome(player);
        HomeResultState state = result.getState();

        switch (state) {
            case SUCCESS:
                player.sendMessage("Teleporting home...");
                MineClans.runSync(() -> player.teleport(result.getHomeLocation()));
                break;
            case NO_HOME_SET:
                player.sendMessage("You haven't set a home yet.");
                break;
            case ERROR:
                player.sendMessage("An error occurred while teleporting home.");
                break;
            case NOT_IN_FACTION:
                player.sendMessage("You are not in a faction.");
                break;
            default:
                break;
        }
    }
}
