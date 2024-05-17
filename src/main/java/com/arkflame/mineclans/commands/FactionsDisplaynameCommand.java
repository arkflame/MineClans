package com.arkflame.mineclans.commands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.RenameDisplayResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.utils.ChatColors;

public class FactionsDisplaynameCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String newName = args.getText(1);
        RenameDisplayResult result = MineClans.getInstance().getAPI().renameDisplay(player, ChatColors.color(newName));

        switch (result.getState()) {
            case DIFFERENT_NAME:
                player.sendMessage("The faction display name cannot be different from the original name.");
                break;
            case NOT_IN_FACTION:
                player.sendMessage("You are not in a faction.");
                break;
            case SUCCESS:
                player.sendMessage("Faction name successfully changed.");
                break;
            case NULL_NAME:
                player.sendMessage("You have to enter a name.");
                break;
            case ERROR:
                player.sendMessage("The displayname is not valid.");
                break;
            default:
                break;
        }
    }
}
