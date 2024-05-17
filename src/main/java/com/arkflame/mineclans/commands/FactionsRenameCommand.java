package com.arkflame.mineclans.commands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.RenameResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsRenameCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String newName = args.getText(1);
        RenameResult result = MineClans.getInstance().getAPI().rename(player, newName);

        switch (result.getState()) {
            case ALREADY_EXISTS:
                player.sendMessage("The faction name already exists.");
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
                player.sendMessage("The name is not valid.");
                break;
            default:
                break;
        }
    }
}
