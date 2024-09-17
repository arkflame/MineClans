package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.api.results.FocusResult;
import com.arkflame.mineclans.api.results.FocusResult.FocusResultType;
import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;

public class FactionsFocusCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String factionName = args.getText(1);
        MineClans mineClans = MineClans.getInstance();
        FocusResult focusResult = mineClans.getAPI().focus(player, factionName);
        FocusResultType type = focusResult.getType();
        String basePath = "factions.focus.";
        ConfigWrapper messages = mineClans.getMessages();

        switch (type) {
            case SUCCESS:
                player.sendMessage(messages.getText(basePath + "success").replace("%faction%", factionName));
                break;
            case NOT_IN_FACTION:
                player.sendMessage(messages.getText(basePath + "not_in_faction"));
                break;
            case FACTION_NOT_FOUND:
                player.sendMessage(messages.getText(basePath + "faction_not_found").replace("%faction%", factionName));
                break;
            case NO_PERMISSION:
                player.sendMessage(messages.getText(basePath + "no_permission"));
                break;
            case SAME_FACTION:
                player.sendMessage(messages.getText(basePath + "same_faction"));
                break;
            default:
                player.sendMessage(messages.getText(basePath + "error"));
                break;
        }
    }
}
