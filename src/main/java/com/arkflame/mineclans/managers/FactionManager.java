package com.arkflame.mineclans.managers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.enums.RelationType;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.Relation;

public class FactionManager {
    // Cache for factions by Name
    private Map<String, Faction> factionCacheByName = new ConcurrentHashMap<>();
    // Cache for factions by ID
    private Map<UUID, Faction> factionCacheByID = new ConcurrentHashMap<>();

    // Get faction from cache or load from database
    public Faction getFaction(String name) {
        if (name == null) {
            return null;
        }
        // Check cache first
        Faction faction = factionCacheByName.get(name);
        if (faction != null) {
            return faction;
        }

        // If not in cache, load from database
        faction = loadFactionFromDatabase(name);
        if (faction != null) {
            factionCacheByName.put(name, faction);
            factionCacheByID.put(faction.getId(), faction);
        }
        return faction;
    }

    // Get faction from cache or load from database
    public Faction getFaction(UUID id) {
        if (id == null) {
            return null;
        }
        // Check cache first
        Faction faction = factionCacheByID.get(id);
        if (faction != null) {
            return faction;
        }

        // If not in cache, load from database
        faction = loadFactionFromDatabase(id);
        if (faction != null) {
            factionCacheByName.put(faction.getName(), faction);
            factionCacheByID.put(faction.getId(), faction);
        }
        return faction;
    }

    public Faction loadFactionFromDatabase(String name) {
        return MineClans.getInstance().getMySQLProvider().getFactionDAO().getFactionByName(name);
    }

    public Faction loadFactionFromDatabase(UUID id) {
        return MineClans.getInstance().getMySQLProvider().getFactionDAO().getFactionById(id);
    }

    // Save a faction to the database
    public void saveFactionToDatabase(Faction faction) {
        MineClans.getInstance().getMySQLProvider().getFactionDAO().insertOrUpdateFaction(faction);
        MineClans.getInstance().getPowerManager().updatePower(faction.getId(), faction.getPower());
    }

    // Remove a faction from the database
    public void removeFactionFromDatabase(Faction faction) {
        MineClans.getInstance().getMySQLProvider().getPowerDAO().removeFaction(faction.getId());
        MineClans.getInstance().getMySQLProvider().getFactionDAO().disbandFaction(faction);
        faction.disbandFaction();
    }

    // Create a new faction
    public Faction createFaction(UUID playerId, String factionName) {
        return createFaction(playerId, factionName, UUID.randomUUID());
    }

    // Create a new faction
    public Faction createFaction(UUID playerId, String factionName, UUID uuid) {
        Faction newFaction = new Faction(uuid, playerId, factionName, factionName);
        factionCacheByName.put(factionName, newFaction);
        return newFaction;
    }

    // Clear all factions from cache
    public void clearFactions() {
        factionCacheByName.clear();
    }

    // Add a player to a faction
    public void addPlayer(String factionName, UUID playerId) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            faction.addMember(playerId);
        }
    }

    // Remove a player from a faction
    public void removePlayer(String factionName, UUID playerId) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            faction.removeMember(playerId);
        }
    }

    // Disband a faction
    public void disbandFaction(String factionName) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            faction.disbandFaction();
            factionCacheByName.remove(factionName);
        }
    }

    // Get a faction's balance
    public double getFactionBalance(String factionName) {
        Faction faction = getFaction(factionName);
        return faction != null ? faction.getBalance() : 0;
    }

    // Set a faction's balance
    public void setFactionBalance(String factionName, double balance) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            faction.setBalance(balance);
        }
    }

    // Update a faction's relation with another faction
    public void updateFactionRelation(String factionName, UUID targetFactionId, String relation) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            faction.setRelation(targetFactionId, new Relation(faction.getId(), targetFactionId, relation));
        }
    }

    // Set chest permissions for a role in a faction
    public void setFactionChestPermission(String factionName, String role, boolean permission) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            faction.setChestPermission(role, permission);
        }
    }

    // Add an invitation to join a faction
    public void invitePlayerToFaction(String factionName, UUID playerId) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            faction.invitePlayer(playerId);
        }
    }

    // Remove an invitation to join a faction
    public void uninvitePlayerFromFaction(String factionName, UUID playerId) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            faction.uninvitePlayer(playerId);
        }
    }

    public void updateFactionOwner(String factionName, UUID ownerId) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            faction.setOwner(ownerId);
        }
    }

    public void updateFactionName(String factionName, String newName) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            Faction existingFaction = getFaction(newName);
            if (existingFaction == null) {
                faction.setName(newName);
                faction.setDisplayName(newName);
                faction.setRenameCooldown();
                factionCacheByName.remove(factionName);
                factionCacheByName.put(newName, faction);
            }
        }
    }

    public void updateFactionDisplayName(String factionName, String displayName) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            faction.setDisplayName(displayName);
        }
    }

    public void updateFriendlyFire(String factionName, boolean friendlyFire) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            faction.setFriendlyFire(friendlyFire);
        }
    }

    public void updateHome(String factionName, Location homeLocation) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            faction.setHome(homeLocation);
        }
    }

    public RelationType getEffectiveRelation(String factionName1, String factionName2) {
        Faction faction1 = getFaction(factionName1);
        Faction faction2 = getFaction(factionName2);
    
        if (faction1 == null || faction2 == null) {
            return RelationType.NEUTRAL; // Default relation if either faction is not found
        }
    
        RelationType relationFrom1To2 = faction1.getRelationType(faction2.getId());
        RelationType relationFrom2To1 = faction2.getRelationType(faction1.getId());
    
        if (relationFrom1To2 == RelationType.ENEMY || relationFrom2To1 == RelationType.ENEMY) {
            return RelationType.ENEMY;
        } else if (relationFrom1To2 == RelationType.NEUTRAL || relationFrom2To1 == RelationType.NEUTRAL) {
            return RelationType.NEUTRAL;
        } else {
            return RelationType.ALLY;
        }
    }
    
    public boolean deposit(String factionName, double amount) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            double currentBalance = faction.getBalance();
            double newBalance = currentBalance + amount;
            faction.setBalance(newBalance);
            return true;
        }
        return false;
    }

    public boolean withdraw(String factionName, double amount) {
        Faction faction = getFaction(factionName);
        if (faction != null) {
            double currentBalance = faction.getBalance();
            double newBalance = currentBalance - amount;
            faction.setBalance(newBalance);
            return true;
        }
        return false;
    }

    public void sendFactionMessage(Faction faction, String message) {
        faction.getMembers().forEach(memberId -> {
            Player member = MineClans.getInstance().getServer().getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage(message);
            }
        });
    }

    public void sendAllianceMessage(Faction faction, String message) {
        Map<UUID, Relation> relations = faction.getRelations();
        for (Map.Entry<UUID, Relation> entry : relations.entrySet()) {
            UUID relatedFactionId = entry.getKey();
            Relation relation = entry.getValue();

            // Check if the relation is an alliance
            if (relation.getRelationType() == RelationType.ALLY) {
                Faction relatedFaction = MineClans.getInstance().getFactionManager().getFaction(relatedFactionId);

                // Only proceed if the faction exists and has online members
                if (relatedFaction != null && relatedFaction.hasOnlineMembers()) {
                    relatedFaction.getMembers().forEach(memberId -> {
                        Player member = MineClans.getInstance().getServer().getPlayer(memberId);
                        if (member != null && member.isOnline()) {
                            member.sendMessage(message);
                        }
                    });
                }
            }
        }
    }
}
