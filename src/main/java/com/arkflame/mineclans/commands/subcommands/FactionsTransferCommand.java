package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.TransferResult;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsTransferCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String newOwnerName = args.getText(1);
        String basePath = "factions.transfer.";

        TransferResult transferResult = MineClans.getInstance().getAPI().transfer(player, newOwnerName);

        switch (transferResult.getState()) {
            case MEMBER_NOT_FOUND:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "member_not_found"));
                break;
            case NOT_OWNER:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "not_owner"));
                break;
            case NO_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_faction"));
                break;
            case NULL_NAME:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "null_name"));
                break;
            case SUCCESS:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success")
                                       .replace("%new_owner%", newOwnerName));
                break;
            default:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "unknown_error"));
                break;
        }
    }
}
