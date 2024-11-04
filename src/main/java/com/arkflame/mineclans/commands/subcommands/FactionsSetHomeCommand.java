package com.arkflame.mineclans.commands.subcommands;

import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.SetHomeResult;
import com.arkflame.mineclans.utils.LocationData;

public class FactionsSetHomeCommand {
    public static void onCommand(Player player) {
        String basePath = "factions.sethome.";
        Location location = player.getLocation();
        Consumer<String> callback = (serverName) -> {
            SetHomeResult result = MineClans.getInstance().getAPI().setHome(player, new LocationData(location, serverName));

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
        };
        
        if (MineClans.getInstance().getConfig().getBoolean("")) {
            MineClans.getInstance().getBungeeUtil().getCurrentServer(player, callback);
        } else {
            callback.accept(null);
        }
    }
}
