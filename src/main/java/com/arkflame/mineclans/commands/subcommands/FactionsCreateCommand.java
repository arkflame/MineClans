package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.CreateResult;
import com.arkflame.mineclans.api.results.CreateResult.CreateResultState;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.Titles;
import com.arkflame.mineclans.utils.MelodyUtil;
import com.arkflame.mineclans.utils.MelodyUtil.Melody;

public class FactionsCreateCommand {
    public static void onCommand(Player player, ModernArguments args) {
        ConfigWrapper messages = MineClans.getInstance().getMessages();
        String factionName = args.getText(1);
        MineClans mineClans = MineClans.getInstance();
        String basePath = "factions.create.";
        CreateResult createResult = mineClans.getAPI().create(player, factionName);
        Faction faction = createResult.getFaction();
        CreateResultState state = createResult.getState();

        switch (state) {
            case ALREADY_HAVE_FACTION:
                player.sendMessage(messages.getText(basePath + "already_have_faction"));
                break;
            case FACTION_EXISTS:
                player.sendMessage(messages.getText(basePath + "faction_exists"));
                break;
            case NULL_NAME:
                player.sendMessage(messages.getText(basePath + "usage"));
                break;
            case SUCCESS:
                Titles.sendTitle(player,
                        messages.getText("factions.create.title").replace("%faction%", faction.getName()),
                        messages.getText("factions.create.subtitle").replace("%faction%", faction.getName()),
                        10, 20, 10);
                MelodyUtil.playMelody(MineClans.getInstance(), player, Melody.FACTION_CREATE_MELODY);
                player.sendMessage(messages.getText(basePath + "success").replace("%faction%", factionName));
                break;
            case ERROR:
                player.sendMessage(messages.getText(basePath + "error"));
                break;
            default:
                break;
        }
    }
}
