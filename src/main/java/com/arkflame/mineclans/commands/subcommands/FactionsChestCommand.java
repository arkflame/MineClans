package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.OpenChestResult;

public class FactionsChestCommand {
    public static void onCommand(Player player) {
        MineClans mineClans = MineClans.getInstance();
        String basePath = "factions.chest.";

        OpenChestResult result = mineClans.getAPI().openChest(player);

        switch (result.getResultType()) {
            case SUCCESS:
                player.sendMessage(mineClans.getMessages().getText(basePath + "success"));
                break;
            case NOT_IN_FACTION:
                player.sendMessage(mineClans.getMessages().getText(basePath + "not_in_faction"));
                break;
            case NO_PERMISSION:
                player.sendMessage(mineClans.getMessages().getText(basePath + "no_permission"));
                break;
            case ERROR:
                player.sendMessage(mineClans.getMessages().getText(basePath + "error"));
                break;
            default:
                player.sendMessage(mineClans.getMessages().getText(basePath + "unknown_result"));
                break;
        }
    }
}
