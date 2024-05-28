package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.events.ClanEvent;
import com.arkflame.mineclans.events.ClanEventScheduler;
import com.arkflame.mineclans.events.EventObjective;
import com.arkflame.mineclans.models.Faction;

public class FactionsEventCommand {
    public static void onCommand(Player player) {
        MineClans plugin = MineClans.getInstance();
        ClanEventScheduler eventScheduler = plugin.getClanEventScheduler();
        ClanEvent currentEvent = eventScheduler.getEvent();
        ClanEvent nextEvent = eventScheduler.getNextEvent();
        StringBuilder message = new StringBuilder();

        if (currentEvent != null) {
            String currentEventName = currentEvent.getName();
            String timeLeftFormatted = eventScheduler.getTimeLeftFormatted();
            Faction faction = plugin.getAPI().getFaction(player);

            message.append(ChatColor.GOLD).append("Current Event: ").append(ChatColor.YELLOW).append(currentEventName).append("\n");
            message.append(ChatColor.GOLD).append("Time Remaining: ").append(ChatColor.YELLOW).append(timeLeftFormatted).append("\n");

            if (faction != null) {
                message.append(ChatColor.GOLD).append("Faction Progress: ").append("\n");
                for (EventObjective objective : currentEvent.getObjectives()) {
                    String type = objective.getType().getAction();
                    String progressBar = objective.getProgressBar(faction, 20);
                    String progressPercentage = objective.getProgressPercentage(faction) + "%";
                    message.append(ChatColor.YELLOW).append(type).append(": ").append(ChatColor.GREEN).append(progressBar + " ").append(ChatColor.WHITE).append(progressPercentage).append("\n");
                }
            } else {
                message.append(ChatColor.RED).append("You are not part of any faction.");
            }
        } else if (nextEvent != null) {
            String nextEventName = nextEvent.getName();
            String timeLeftFormatted = eventScheduler.getTimeLeftFormatted();

            message.append(ChatColor.GOLD).append("Next Event: ").append(ChatColor.YELLOW).append(nextEventName).append("\n");
            message.append(ChatColor.GOLD).append("Starts In: ").append(ChatColor.YELLOW).append(timeLeftFormatted).append("\n");
        } else {
            message.append(ChatColor.RED).append("No events are scheduled at the moment.");
        }

        player.sendMessage(message.toString());
    }
}
