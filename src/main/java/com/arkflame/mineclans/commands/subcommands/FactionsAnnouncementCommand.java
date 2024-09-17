package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.AnnouncementResult;
import com.arkflame.mineclans.api.results.AnnouncementResult.AnnouncementResultState;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.utils.Titles;

public class FactionsAnnouncementCommand {
    public static void onCommand(Player player, ModernArguments args) {
        ConfigWrapper messages = MineClans.getInstance().getMessages();
        
        // Replacing DisbandResult with DiscordResult
        AnnouncementResult announcementResult = MineClans.getInstance().getAPI().setAnnouncement(player, String.join(" ", args.getArgs(1)));
        AnnouncementResultState state = announcementResult.getState();
        String basePath = "factions.announcement.";
        Faction faction = announcementResult.getFaction();

        switch (state) {
            case ERROR:
            case NO_ANNOUNCEMENT:
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "invalid_announcement"));
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
                        messages.getText(basePath + "subtitle").replace("%announcement%", faction.getAnnouncement()),
                        10, 20, 10);
                player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "success"));
                break;
            default:
                break;
        }
    }
}
