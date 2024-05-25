package com.arkflame.mineclans.events;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.enums.EventObjectiveType;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

public class ClanEvent {
    // Container of static information
    private EventConfig config;

    // Current objectives and scores
    private Map<EventObjectiveType, EventObjective> objectives = new ConcurrentHashMap<>();

    // Is the current event active?
    private boolean isActive;

    public ClanEvent(EventConfig config) {
        addObjectives(EventObjective.fromConfig(config));
        this.config = config;
    }

    public void onFactionKill(FactionPlayer player) {
        increaseScore(EventObjectiveType.FACTION_KILL, player.getFaction());
    }

    public void onBlockBreak(Block block, FactionPlayer player) {
        Material type = block.getType();
        if (type == Material.DIAMOND_ORE) {
            increaseScore(EventObjectiveType.BLOCK_BREAK_DIAMOND, player.getFaction());
        }
        increaseScore(EventObjectiveType.BLOCK_BREAK, player.getFaction());
    }

    public void onMonsterKill(FactionPlayer player) {
        increaseScore(EventObjectiveType.MONSTER_KILL, player.getFaction());
    }

    public void onObjectiveChange(EventObjective objective, Faction faction) {
        for (EventObjective objective2 : objectives.values()) {
            if (!objective2.isCompleted(faction)) {
                return;
            }
        }
        endEvent(faction);
    }

    public void resetScores() {
        for (EventObjective objective : objectives.values()) {
            objective.resetScores();
        }
    }

    public void increaseScore(EventObjectiveType eventObjectiveType, Faction faction, int points) {
        if (faction == null)
            return;
        if (points == 0)
            return;
        EventObjective objective = objectives.get(eventObjectiveType);
        if (objective == null)
            return;
        objective.increaseScore(faction, points);
        // Notify players with a subtitle of progress
        onObjectiveChange(objective, faction);
    }

    public void increaseScore(EventObjectiveType eventObjectiveType, Faction faction) {
        increaseScore(eventObjectiveType, faction, 1);
    }

    public int getScore(EventObjectiveType eventObjectiveType, Faction faction) {
        if (faction == null)
            return 0;
        EventObjective objective = objectives.get(eventObjectiveType);
        if (objective == null)
            return 0;
        return objective.getScore(faction);
    }

    public void addObjective(EventObjective objective) {
        this.objectives.put(objective.getType(), objective);
    }

    public void addObjectives(EventObjective... objectives) {
        for (int i = 0; i < objectives.length; i++) {
            addObjective(objectives[i]);
        }
    }

    public void startEvent() {
        this.isActive = true;
        // Send start title/subtitle/message
    }

    public void endEvent(Faction winner) {
        this.isActive = false;
        // Send end title/subtitle/message
        Bukkit.broadcastMessage("Event ended: " + getName());

        if (winner != null) {
            // Reward winner faction
            rewardWinnerFaction(winner);
        }
    }

    private void rewardWinnerFaction(Faction winnerFaction) {
        // Execute reward commands and deposit for each player in the winning faction
        for (UUID uuid : winnerFaction.getMembers()) {
            FactionPlayer factionPlayer = MineClans.getInstance().getAPI().getFactionPlayer(uuid);
            Player player = factionPlayer.getPlayer();

            if (player != null && player.isOnline()) {
                player.sendTitle("", "Winner: " + winnerFaction.getName(), 10, 70, 20);

                for (String command : config.getCommands()) {
                    String formattedCommand = command.replace("{player}", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCommand);
                }

                if (config.getDeposit() > 0) {
                    // Assuming a method to add deposit exists
                    if (MineClans.getInstance().isVaultHooked()){
                        MineClans.getInstance().getVaultEconomy().depositPlayer(player, config.getDeposit());
                    }
                }
            }
        }
    }

    public boolean isActive() {
        return this.isActive;
    }

    public EventConfig getConfig() {
        return config;
    }

    public String getName() {
        return config.getName();
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public Collection<EventObjective> getObjectives() {
        return objectives.values();
    }
}
