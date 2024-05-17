package com.arkflame.mineclans.commands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.InviteResult;
import com.arkflame.mineclans.api.InviteResult.InviteResultState;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsInviteCommand {
    public static void onCommand(Player player, ModernArguments args) {
        if (!args.hasArg(1)) {
            player.sendMessage("Usage: /factions invite <player>");
            return;
        }

        String targetPlayerName = args.getText(1);
        InviteResult inviteResult = MineClans.getInstance().getAPI().invite(player, targetPlayerName);
        InviteResultState state = inviteResult.getState();

        switch (state) {
            case NO_FACTION:
                player.sendMessage("You have no faction.");
                break;
            case NOT_OWNER:
                player.sendMessage("You are not the owner.");
                break;
            case MEMBER_EXISTS:
                player.sendMessage("Player is already a member.");
                break;
            case ALREADY_INVITED:
                player.sendMessage("Player is already invited.");
                break;
            case SUCCESS:
                player.sendMessage("Player invited successfully.");
                inviteResult.getPlayer().getPlayer().sendMessage("You have been invited to join a faction.");
                break;
            case PLAYER_NOT_FOUND:
                player.sendMessage("Player not found.");
                break;
            default:
                break;
        }
    }
}