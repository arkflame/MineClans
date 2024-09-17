package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.DisbandResult;
import com.arkflame.mineclans.api.results.DisbandResult.DisbandResultState;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.Titles;
import com.arkflame.mineclans.utils.MelodyUtil;
import com.arkflame.mineclans.utils.MelodyUtil.Melody;

public class FactionsDisbandCommand {
    public static void onCommand(Player player, ModernArguments args) {
        ConfigWrapper messages = MineClans.getInstance().getMessages();
        DisbandResult disbandResult = MineClans.getInstance().getAPI().disband(player);
        DisbandResultState state = disbandResult.getState();
        String basePath = "factions.disband.";
        Faction faction = disbandResult.getFaction();

        switch (state) {
            case NO_PERMISSION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_permission"));
                break;
            case NO_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_faction"));
                break;
            case SUCCESS:
                Titles.sendTitle(player,
                        messages.getText("factions.disband.title").replace("%faction%", faction.getName()),
                        messages.getText("factions.disband.subtitle").replace("%faction%", faction.getName()),
                        10, 20, 10);
                MelodyUtil.playMelody(MineClans.getInstance(), player, Melody.FACTION_DISBAND_MELODY);
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success"));
                break;
            default:
                break;
        }
    }
}
