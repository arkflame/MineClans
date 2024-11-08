package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.HomeResult;
import com.arkflame.mineclans.api.results.HomeResult.HomeResultState;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;

public class FactionsHomeCommand {
    public static void onCommand(Player player) {
        ConfigWrapper messages = MineClans.getInstance().getMessages();
        HomeResult result = MineClans.getInstance().getAPI().getHome(player);
        HomeResultState state = result.getState();
        String basePath = "factions.home.";

        switch (state) {
            case SUCCESS:
                int warmup = MineClans.getInstance().getCfg().getInt("home.warmup", 10);
                player.sendMessage(messages.getText(basePath + "teleporting", "").replace("{time}", String.valueOf(warmup)));
                MineClans.getInstance().getTeleportScheduler().schedule(player, result.getHomeLocation(), warmup);
                break;
            case NO_HOME_SET:
                player.sendMessage(messages.getText(basePath + "no_home_set"));
                break;
            case ERROR:
                player.sendMessage(messages.getText(basePath + "error"));
                break;
            case NOT_IN_FACTION:
                player.sendMessage(messages.getText(basePath + "not_in_faction"));
                break;
            default:
                break;
        }
    }
}
