package com.arkflame.mineclans.commands.subcommands;

import org.bukkit.configuration.file.FileConfiguration;
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
        String basePath = "factions.event.";
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean("events.enabled")) {
            player.sendMessage(plugin.getMessages().getText(basePath + "disabled"));
            return;
        }
        ClanEventScheduler eventScheduler = plugin.getClanEventScheduler();

        if (args.hasArg(1)) {
            if (args.getText(1).equalsIgnoreCase("start")) {
                if (player.hasPermission("mineclans.events.start")) {
                    startEvent(player, eventScheduler);
                } else {
                    player.sendMessage(plugin.getMessages().getText(basePath + "no_permission_start"));
                }
                return;
            } else if (args.getText(1).equalsIgnoreCase("end")) {
                if (player.hasPermission("mineclans.events.end")) {
                    endEvent(player, eventScheduler);
                } else {
                    player.sendMessage(plugin.getMessages().getText(basePath + "no_permission_end"));
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
        String basePath = "factions.event.";

        if (currentEvent != null) {
            String currentEventName = currentEvent.getName();
            String timeLeftFormatted = eventScheduler.getTimeLeftFormatted();
            Faction faction = plugin.getAPI().getFaction(player);

            message.append(plugin.getMessages().getText(basePath + "current_event")
                    .replace("%event%", currentEventName)
                    .replace("%time%", timeLeftFormatted));

            if (faction != null) {
                message.append("\n").append(plugin.getMessages().getText(basePath + "faction_progress"));
                for (EventObjective objective : currentEvent.getObjectives()) {
                    String type = plugin.getMessages().getText(basePath + "objective_types." + objective.getType().name());
                    String progressBar = objective.getProgressBar(faction, 20);
                    String progressPercentage = objective.getProgressPercentage(faction) + "%";
                    message.append(plugin.getMessages().getText(basePath + "event_objective")
                            .replace("%type%", type)
                            .replace("%progress%", progressBar)
                            .replace("%percentage%", progressPercentage));
                }
            } else {
                message.append(plugin.getMessages().getText(basePath + "not_in_faction"));
            }
        } else if (nextEvent != null) {
            String nextEventName = nextEvent.getName();
            String timeLeftFormatted = eventScheduler.getTimeLeftFormatted();

            message.append(plugin.getMessages().getText(basePath + "next_event")
                    .replace("%event%", nextEventName)
                    .replace("%time%", timeLeftFormatted));
        } else {
            message.append(plugin.getMessages().getText(basePath + "no_events_scheduled"));
        }

        player.sendMessage(message.toString());
    }

    private static void startEvent(Player player, ClanEventScheduler eventScheduler) {
        ClanEvent nextEvent = eventScheduler.getNextEvent();
        String basePath = "factions.event.";
        if (nextEvent != null) {
            eventScheduler.startEvent();
            player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "start_success")
                    .replace("%event%", nextEvent.getName()));
        } else {
            player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_event_scheduled"));
        }
    }

    private static void endEvent(Player player, ClanEventScheduler eventScheduler) {
        ClanEvent currentEvent = eventScheduler.getEvent();
        String basePath = "factions.event.";
        if (currentEvent != null) {
            MineClans.getInstance().getClanEventManager().stopCurrentEvent();
            player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "end_success")
                    .replace("%event%", currentEvent.getName()));
        } else {
            player.sendMessage(MineClans.getInstance().getMessages().getText(basePath + "no_event_running"));
        }
    }
}
