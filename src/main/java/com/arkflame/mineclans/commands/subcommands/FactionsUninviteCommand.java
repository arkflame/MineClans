package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.UninviteResult;
import com.arkflame.mineclans.api.results.UninviteResult.UninviteResultState;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.Titles;
import com.arkflame.mineclans.utils.MelodyUtil;
import com.arkflame.mineclans.utils.MelodyUtil.Melody;

public class FactionsUninviteCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String targetPlayerName = args.getText(1);
        String basePath = "factions.uninvite.";

        UninviteResult uninviteResult = MineClans.getInstance().getAPI().uninvite(player, targetPlayerName);
        UninviteResultState state = uninviteResult.getState();
        ConfigWrapper messages = MineClans.getInstance().getMessages();

        switch (state) {
            case NULL_NAME:
                player.sendMessage(messages.getText(basePath + "null_name"));
                break;
            case NO_FACTION:
                player.sendMessage(messages.getText(basePath + "no_faction"));
                break;
            case NO_PERMISSION:
                player.sendMessage(messages.getText(basePath + "no_permission"));
                break;
            case NOT_INVITED:
                player.sendMessage(messages.getText(basePath + "not_invited"));
                break;
            case SUCCESS:
                Titles.sendTitle(player,
                        messages.getText("factions.uninvite.title_uninvited_other").replace("%player%",
                                targetPlayerName),
                        messages.getText("factions.uninvite.subtitle_uninvited_other").replace("%player%",
                                targetPlayerName),
                        10, 20, 10);
                player.sendMessage(messages.getText(basePath + "success"));
                MelodyUtil.playMelody(MineClans.getInstance(), player, Melody.ERROR);
                break;
            case PLAYER_NOT_FOUND:
                player.sendMessage(messages.getText(basePath + "player_not_found"));
                break;
            default:
                break;
        }
    }
}
