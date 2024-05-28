package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.InviteResult;
import com.arkflame.mineclans.api.results.InviteResult.InviteResultState;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsInviteCommand {
    public static void onCommand(Player player, ModernArguments args) {
        if (!args.hasArg(1)) {
            player.sendMessage(MineClans.getInstance().getMessages().getText("factions.invite.usage"));
            return;
        }

        String targetPlayerName = args.getText(1);
        InviteResult inviteResult = MineClans.getInstance().getAPI().invite(player, targetPlayerName);
        InviteResultState state = inviteResult.getState();
        String basePath = "factions.invite.";

        switch (state) {
            case NO_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_faction"));
                break;
            case NO_PERMISSION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_permission"));
                break;
            case MEMBER_EXISTS:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "member_exists"));
                break;
            case ALREADY_INVITED:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "already_invited"));
                break;
            case SUCCESS:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success"));
                inviteResult.getPlayer().getPlayer().sendMessage(MineClans.getInstance().getMessages().getText(basePath + "invitee_message"));
                break;
            case PLAYER_NOT_FOUND:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "player_not_found"));
                break;
            default:
                break;
        }
    }
}
