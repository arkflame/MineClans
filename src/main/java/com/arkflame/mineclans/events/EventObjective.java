package com.arkflame.mineclans.events;

import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;

import com.arkflame.mineclans.enums.EventObjectiveType;
import com.arkflame.mineclans.models.Faction;

public class EventObjective {
    private EventObjectiveType type;
    private int targetScore;

    private Map<UUID, Integer> scores;

    public EventObjective(EventObjectiveType type, int targetScore) {
        this.type = type;
        this.targetScore = targetScore;
        this.scores = new ConcurrentHashMap<>();
    }

    public EventObjectiveType getType() {
        return type;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public void resetScores() {
        scores.clear();
    }

    public void increaseScore(Faction faction, int points) {
        scores.put(faction.getId(), getScore(faction) + points);
    }

    public int getScore(Faction faction) {
        return scores.getOrDefault(faction.getId(), 0);
    }

    public static EventObjective[] fromConfig(EventConfig config) {
        Map<EventObjectiveType, Integer> objectivesMap = config.getObjectives();
        int length = objectivesMap.size();
        EventObjective[] objectives = new EventObjective[length];
        int i = 0;
        for (Entry<EventObjectiveType, Integer> objective : objectivesMap.entrySet()) {
            objectives[i++] = new EventObjective(objective.getKey(), objective.getValue());
        }
        return objectives;
    }

    public boolean isCompleted(Faction faction) {
        return getScore(faction) >= targetScore;
    }

    public double getProgressPercentage(Faction faction) {
        int score = getScore(faction);
        return Math.round((double) score / targetScore * 1000) / 10.0;
    }
    
    public String getProgressBar(Faction faction, int length) {
        double percentage = getProgressPercentage(faction);
        int progressBars = (int) (length * (percentage / 100));
        int remainingBars = length - progressBars;
        
        return ChatColor.GREEN + "|".repeat(progressBars) + ChatColor.RED + "|".repeat(remainingBars);
    }
    
    public String getProgress(Faction faction) {
        int score = getScore(faction);
        return score + "/" + targetScore;
    }
}
