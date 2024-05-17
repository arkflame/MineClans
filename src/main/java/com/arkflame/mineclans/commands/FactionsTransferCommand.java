package com.arkflame.mineclans.commands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.TransferResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsTransferCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String newOwnerName = args.getText(1);
        TransferResult transferResult = MineClans.getInstance().getAPI().transfer(player, newOwnerName);

        switch (transferResult.getState()) {
            case MEMBER_NOT_FOUND:
                player.sendMessage("The specified player is not a member of your faction.");
                break;
            case NOT_OWNER:
                player.sendMessage("You must be the faction owner to transfer ownership.");
                break;
            case NO_FACTION:
                player.sendMessage("You are not in a faction.");
                break;
            case NULL_NAME:
                player.sendMessage("You must specify the name of the new owner.");
                break;
            case SUCCESS:
                player.sendMessage("Ownership of the faction has been successfully transferred to " + newOwnerName + ".");
                break;
            default:
                player.sendMessage("An unknown error occurred.");
                break;
        }
    }
}
