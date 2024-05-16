package com.arkflame.mineclans.commands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.CreateResult;
import com.arkflame.mineclans.api.CreateResult.CreateResultState;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsCreateCommand {
    public static void onCommand(Player player, ModernArguments args) {
        String factionName = args.getText(1);
        CreateResult createResult = MineClans.getInstance().getAPI().create(player, factionName);
        CreateResultState state = createResult.getState();

        switch (state) {
            case ALREADY_HAVE_FACTION:
                player.sendMessage("You already have a faction. Disband it or leave first.");
                break;
            case FACTION_EXISTS:
                player.sendMessage("The faction already exists.");
                break;
            case NULL_NAME:
                player.sendMessage(MineClans.getInstance().getMsg().getText("factions.create.usage"));
                break;
            case SUCCESS:
                player.sendMessage("Created faction.");
                break;
            default:
                break;
        }
    }
}
