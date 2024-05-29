package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.JoinResult;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.Titles;
import com.arkflame.mineclans.utils.MelodyUtil;
import com.arkflame.mineclans.utils.MelodyUtil.Melody;

public class FactionsJoinCommand {
    public static void onCommand(Player player, ModernArguments args) {
        ConfigWrapper messages = MineClans.getInstance().getMessages();
        String factionName = args.getText(1);
        JoinResult joinResult = MineClans.getInstance().getAPI().join(player, factionName);
        Faction faction = joinResult.getFaction();
        String basePath = "factions.join.";

        switch (joinResult.getState()) {
            case ALREADY_HAVE_FACTION:
                player.sendMessage(messages.getText(basePath + "already_in_faction"));
                break;
            case NOT_INVITED:
                player.sendMessage(messages.getText(basePath + "not_invited"));
                break;
            case NO_FACTION:
                player.sendMessage(messages.getText(basePath + "no_faction"));
                break;
            case NULL_NAME:
                player.sendMessage(messages.getText(basePath + "usage"));
                break;
            case SUCCESS:
                Titles.sendTitle(player,
                        messages.getText("factions.join.title").replace("%faction%", faction.getName()),
                        messages.getText("factions.join.subtitle").replace("%faction%", faction.getName()),
                        10, 20, 10);
                MelodyUtil.playMelody(MineClans.getInstance(), player, Melody.FACTION_JOIN_MELODY);
                player.sendMessage(messages.getText(basePath + "success")
                        .replace("%faction%", factionName));
                break;
            default:
                break;
        }
    }
}
