package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.SetHomeResult;

public class FactionsSetHomeCommand {
    public static void onCommand(Player player) {
        Location homeLocation = player.getLocation();
        SetHomeResult result = MineClans.getInstance().getAPI().setHome(player, homeLocation);
        String basePath = "factions.sethome.";

        switch (result.getState()) {
            case NO_PERMISSION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_permission"));
                break;
            case SUCCESS:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success"));
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
