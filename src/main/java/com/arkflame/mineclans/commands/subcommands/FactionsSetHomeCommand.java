package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.SetHomeResult;
import com.arkflame.mineclans.api.results.SetHomeResult.SetHomeResultState;

public class FactionsSetHomeCommand {
    public static void onCommand(Player player) {
        Location homeLocation = player.getLocation();
        SetHomeResult result = MineClans.getInstance().getAPI().setHome(player, homeLocation);
        SetHomeResultState state = result.getState();

        switch (state) {
            case SUCCESS:
                player.sendMessage("Home set successfully.");
                break;
            case ERROR:
                player.sendMessage("An error occurred while setting home.");
                break;
            case NOT_IN_FACTION:
                player.sendMessage("You are not in any faction.");
                break;
            default:
                break;
        }
    }
}
