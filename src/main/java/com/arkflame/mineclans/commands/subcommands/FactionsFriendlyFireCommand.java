package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.FriendlyFireResult;
import com.arkflame.mineclans.api.results.FriendlyFireResult.FriendlyFireResultState;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsFriendlyFireCommand {
    public static void onCommand(Player player, ModernArguments args) {
        FriendlyFireResult result = MineClans.getInstance().getAPI().toggleFriendlyFire(player);
        FriendlyFireResultState state = result.getState();
        String basePath = "factions.friendly_fire.";

        switch (state) {
            case ENABLED:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "enabled"));
                break;
            case NO_PERMISSION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_permission"));
                break;
            case DISABLED:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "disabled"));
                break;
            case NOT_IN_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "not_in_faction"));
                break;
            case ERROR:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "error"));
                break;
            default:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "unknown_error"));
                break;
        }
    }
}
