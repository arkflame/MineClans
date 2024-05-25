package com.arkflame.mineclans.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.arkflame.mineclans.enums.EventObjectiveType;

import java.util.*;
import java.util.stream.Collectors;

public class ClanEventManager {
    private final JavaPlugin plugin;
    private final Map<String, EventConfig> eventConfigs = new HashMap<>();
    private ClanEvent currentEvent;

    public ClanEventManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfiguration();
    }

    private void loadConfiguration() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        this.eventConfigs.putAll(config.getConfigurationSection("events.custom-events").getValues(false).entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            String path = "events.custom-events." + e.getKey();
                            String name = config.getString(path + ".name");
                            String description = config.getString(path + ".description");
                            Map<EventObjectiveType, Integer> objectives = new HashMap<>();
                            objectives.put(EventObjectiveType.BLOCK_BREAK_DIAMOND, config.getInt(path + ".objectives.diamond_mine"));
                            List<String> commands = config.getStringList(path + ".rewards.commands");
                            int deposit = config.getInt(path + ".rewards.deposit");
                            return new EventConfig(name, description, objectives, commands, deposit);
                        }
                )));
    }

    public ClanEvent getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(ClanEvent currentEvent) {
        this.currentEvent = currentEvent;
    }

    public Map<String, EventConfig> getEventConfigs() {
        return eventConfigs;
    }

    public void startEvent(String eventName) {
        EventConfig config = eventConfigs.get(eventName);
        if (config != null) {
            currentEvent = RandomEventFactory.createEventFromConfig(config);
            currentEvent.startEvent();
        }
    }

    public void stopCurrentEvent() {
        if (currentEvent != null) {
            currentEvent.setActive(false);
            currentEvent = null;
        }
    }
}
