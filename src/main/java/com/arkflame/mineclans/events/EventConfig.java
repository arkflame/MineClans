package com.arkflame.mineclans.events;

import java.util.List;
import java.util.Map;

import com.arkflame.mineclans.enums.EventObjectiveType;

public class EventConfig {
    private final String name;
    private final String description;
    private final Map<EventObjectiveType, Integer> objectives;

    // Rewards
    private final List<String> commands;
    private final int deposit;

    public EventConfig(String name, String description, Map<EventObjectiveType, Integer> objectives, List<String> commands, int deposit) {
        this.name = name;
        this.description = description;
        this.objectives = objectives;
        this.commands = commands;
        this.deposit = deposit;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<EventObjectiveType, Integer> getObjectives() {
        return objectives;
    }

    public List<String> getCommands() {
        return commands;
    }

    public int getDeposit() {
        return deposit;
    }
}
