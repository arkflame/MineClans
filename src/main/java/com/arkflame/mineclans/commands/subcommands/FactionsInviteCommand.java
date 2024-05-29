package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.InviteResult;
import com.arkflame.mineclans.api.results.InviteResult.InviteResultState;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.Sounds;
import com.arkflame.mineclans.modernlib.utils.Titles;

public class FactionsInviteCommand {
    public static void onCommand(Player player, ModernArguments args) {
        ConfigWrapper messages = MineClans.getInstance().getMessages();
        if (!args.hasArg(1)) {
            player.sendMessage(messages.getText("factions.invite.usage"));
            return;
        }

        String targetPlayerName = args.getText(1);
        InviteResult inviteResult = MineClans.getInstance().getAPI().invite(player, targetPlayerName);
        InviteResultState state = inviteResult.getState();
        String basePath = "factions.invite.";

        switch (state) {
            case NO_FACTION:
                player.sendMessage(messages.getText(basePath + "no_faction"));
                break;
            case NO_PERMISSION:
                player.sendMessage(messages.getText(basePath + "no_permission"));
                break;
            case MEMBER_EXISTS:
                player.sendMessage(messages.getText(basePath + "member_exists"));
                break;
            case ALREADY_INVITED:
                player.sendMessage(messages.getText(basePath + "already_invited"));
                break;
            case SUCCESS:
                Titles.sendTitle(player,
                        messages.getText("factions.invite.title_invited_other").replace("%player%", targetPlayerName),
                        messages.getText("factions.invite.subtitle_invited_other").replace("%player%",
                                targetPlayerName),
                        10, 20, 10);
                player.sendMessage(messages.getText(basePath + "success"));
                inviteResult.getPlayer().getPlayer().sendMessage(messages.getText(basePath + "invite_message"));
                break;
            case PLAYER_NOT_FOUND:
                player.sendMessage(messages.getText(basePath + "player_not_found"));
                break;
            default:
                break;
        }
    }
}
