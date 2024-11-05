package com.arkflame.mineclans.events;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.enums.EventObjectiveType;

import java.util.*;
import java.util.stream.Collectors;

public class ClanEventManager {
    private final JavaPlugin plugin;
    private final Map<String, EventConfig> eventConfigs = new HashMap<>();

    public ClanEventManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfiguration();
    }

    private void loadConfiguration() {
        FileConfiguration config = plugin.getConfig();
        if (!config.isBoolean("events.enabled")) {
            return;
        }
        plugin.saveDefaultConfig();

        this.eventConfigs
                .putAll(config.getConfigurationSection("events.custom-events").getValues(false).entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> {
                                    String path = "events.custom-events." + e.getKey();
                                    String name = config.getString(path + ".name");
                                    String description = config.getString(path + ".description");

                                    Map<EventObjectiveType, Integer> objectives = new HashMap<>();
                                    ConfigurationSection objectivesSection = config
                                            .getConfigurationSection(path + ".objectives");
                                    if (objectivesSection != null) {
                                        for (String objectiveKey : objectivesSection.getKeys(false)) {
                                            try {
                                                EventObjectiveType objectiveType = EventObjectiveType
                                                        .valueOf(objectiveKey.toUpperCase());
                                                int objectiveValue = objectivesSection.getInt(objectiveKey);
                                                objectives.put(objectiveType, objectiveValue);
                                            } catch (IllegalArgumentException ex) {
                                                // Handle invalid objective type
                                                System.err.println("Invalid objective type: " + objectiveKey);
                                            }
                                        }
                                    }

                                    List<String> commands = config.getStringList(path + ".rewards.commands");
                                    int deposit = config.getInt(path + ".rewards.deposit");
                                    return new EventConfig(name, description, objectives, commands, deposit);
                                })));

    }

    public Map<String, EventConfig> getEventConfigs() {
        return eventConfigs;
    }

    public void startEvent(String eventName) {
        FileConfiguration cfg = plugin.getConfig();
        if (!cfg.isBoolean("events.enabled")) {
            return;
        }
        EventConfig config = eventConfigs.get(eventName);
        if (config != null) {
            ClanEventScheduler clanEventScheduler = MineClans.getInstance().getClanEventScheduler();
            clanEventScheduler.setEvent(RandomEventFactory.createEventFromConfig(config));
            clanEventScheduler.getEvent().startEvent();
        }
    }

    public void stopCurrentEvent() {
        FileConfiguration cfg = plugin.getConfig();
        if (!cfg.isBoolean("events.enabled")) {
            return;
        }
        ClanEventScheduler clanEventScheduler = MineClans.getInstance().getClanEventScheduler();
        ClanEvent currentEvent = clanEventScheduler.getEvent();

        if (currentEvent != null) {
            clanEventScheduler.setEvent(null);
            currentEvent.finish(null);
        }
    }
}
