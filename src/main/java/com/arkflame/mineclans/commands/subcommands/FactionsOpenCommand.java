package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.OpenResult;
import com.arkflame.mineclans.api.results.OpenResult.OpenResultState;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;

public class FactionsOpenCommand {
    public static void onCommand(Player player, ModernArguments args) {
        ConfigWrapper messages = MineClans.getInstance().getMessages();
        OpenResult openResult = MineClans.getInstance().getAPI().toggleOpen(player);
        OpenResultState state = openResult.getState();
        String basePath = "factions.open.";

        switch (state) {
            case NO_FACTION:
                player.sendMessage(messages.getText(basePath + "no_faction"));
                break;
            case NO_PERMISSION:
                player.sendMessage(messages.getText(basePath + "no_permission"));
                break;
            case SUCCESS:
                boolean open = openResult.isOpen();
                Faction faction = openResult.getFaction();

                if (open) {
                    faction.sendMessage(messages.getText(basePath + "faction_opened").replace("%player%", player.getName()));
                } else {
                    faction.sendMessage(messages.getText(basePath + "faction_closed").replace("%player%", player.getName()));
                }
                break;
            default:
                break;
        }
    }
}
