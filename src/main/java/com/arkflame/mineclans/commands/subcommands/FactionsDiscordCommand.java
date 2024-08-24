package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.DiscordResult;
import com.arkflame.mineclans.api.results.DiscordResult.DiscordResultState;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.Titles;

public class FactionsDiscordCommand {
    public static void onCommand(Player player, ModernArguments args) {
        ConfigWrapper messages = MineClans.getInstance().getMessages();
        
        // Replacing DisbandResult with DiscordResult
        DiscordResult discordResult = MineClans.getInstance().getAPI().setDiscord(player, args.getText(1));
        DiscordResultState state = discordResult.getState();
        String basePath = "factions.discord.";
        Faction faction = discordResult.getFaction();

        switch (state) {
            case ERROR:
            case INVALID_DISCORD_LINK:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "invalid_discord"));
                break;
            case NO_PERMISSION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_permission"));
                break;
            case NO_FACTION:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_faction"));
                break;
            case SUCCESS:
                Titles.sendTitle(player,
                        messages.getText(basePath + "title").replace("%faction%", faction.getName()),
                        messages.getText(basePath + "subtitle").replace("%faction%", faction.getName()),
                        10, 20, 10);
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success"));
                break;
            default:
                break;
        }
    }
}
