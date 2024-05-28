package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.events.ClanEvent;
import com.arkflame.mineclans.events.ClanEventScheduler;
import com.arkflame.mineclans.events.EventObjective;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.commands.ModernArguments;

public class FactionsEventCommand {

    public static void onCommand(Player player, ModernArguments args) {
        MineClans plugin = MineClans.getInstance();
        ClanEventScheduler eventScheduler = plugin.getClanEventScheduler();

        if (args.hasArg(1)) {
            if (args.getText(1).equalsIgnoreCase("start")) {
                if (player.hasPermission("mineclans.events.start")) {
                    startEvent(player, eventScheduler);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to start events.");
                }
                return;
            } else if (args.getText(1).equalsIgnoreCase("end")) {
                if (player.hasPermission("mineclans.events.end")) {
                    endEvent(player, eventScheduler);
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to end events.");
                }
                return;
            }
        } else {
            showEventStatus(player, eventScheduler, plugin);
            return;
        }

        return;
    }

    private static void showEventStatus(Player player, ClanEventScheduler eventScheduler, MineClans plugin) {
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

    private static void startEvent(Player player, ClanEventScheduler eventScheduler) {
        ClanEvent nextEvent = eventScheduler.getNextEvent();
        if (nextEvent != null) {
            eventScheduler.startEvent();
            player.sendMessage(ChatColor.GREEN + "The event " + nextEvent.getName() + " has started!");
        } else {
            player.sendMessage(ChatColor.RED + "No event is scheduled to start.");
        }
    }

    private static void endEvent(Player player, ClanEventScheduler eventScheduler) {
        ClanEvent currentEvent = eventScheduler.getEvent();
        if (currentEvent != null) {
            MineClans.getInstance().getClanEventManager().stopCurrentEvent();
            player.sendMessage(ChatColor.GREEN + "The event " + currentEvent.getName() + " has ended!");
        } else {
            player.sendMessage(ChatColor.RED + "No event is currently running.");
        }
    }
}
