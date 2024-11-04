package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.HomeResult;
import com.arkflame.mineclans.api.results.HomeResult.HomeResultState;
import com.arkflame.mineclans.utils.LocationData;

public class FactionsHomeCommand {
    public static void onCommand(Player player) {
        HomeResult result = MineClans.getInstance().getAPI().getHome(player);
        HomeResultState state = result.getState();
        String basePath = "factions.home.";

        switch (state) {
            case SUCCESS:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success"));
                MineClans.runSync(() -> {
                    LocationData locationData = result.getHomeLocation();
                    if (locationData != null) {
                        locationData.teleport(player);
                    }
                });
                break;
            case NO_HOME_SET:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_home_set"));
                break;
            case ERROR:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "error"));
                break;
            case NOT_IN_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "not_in_faction"));
                break;
            default:
                break;
        }
    }
}
