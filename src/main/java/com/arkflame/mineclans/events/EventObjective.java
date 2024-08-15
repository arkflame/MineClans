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

    private static String repeat(String str, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
    
    public static String generateProgressBar(int progressBars, int remainingBars) {
        String progress = repeat("|", progressBars);
        String remaining = repeat("|", remainingBars);
    
        return ChatColor.GREEN + progress + ChatColor.RED + remaining;
    }    
    
    public String getProgressBar(Faction faction, int length) {
        double percentage = getProgressPercentage(faction);
        int progressBars = (int) (length * (percentage / 100));
        int remainingBars = length - progressBars;
        
        return generateProgressBar(progressBars, remainingBars);
    }
    
    public String getProgress(Faction faction) {
        int score = getScore(faction);
        return score + "/" + targetScore;
    }
}
