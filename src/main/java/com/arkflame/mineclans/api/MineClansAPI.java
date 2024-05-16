package com.arkflame.mineclans.api;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.CreateResult.CreateResultState;
import com.arkflame.mineclans.api.DisbandResult.DisbandResultState;
import com.arkflame.mineclans.enums.Rank;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

/*
 * MineClans API
 * 
 * Make sure to access asynchronously
 * 
 */
public class MineClansAPI {
    public Faction getFaction(Player player) {
        return null;
    }

    public Faction getFaction(UUID uuid) {
        return null;
    }

    public Faction getFaction(String name) {
        return null;
    }

    public FactionPlayer getFactionPlayer(Player player) {
        return null;
    }
    
    public FactionPlayer getFactionPlayer(UUID uuid) {
        return null;
    }

    public FactionPlayer getFactionPlayer(String name) {
        return null;
    }

    public void leave(Player player) {
        
    }
    
    public void join(Player player, String factionName) {
        
    }
    
    public void invite(Player player, String toInvite) {
        
    }
    
    public void uninvite(Player player, String toUninvite) {
        
    }
    
    public CreateResult create(Player player, String factionName) {
        if (factionName == null) {
            return new CreateResult(CreateResultState.NULL_NAME, null);
        }

        FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager().getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();
        if (faction != null) {
            return new CreateResult(CreateResultState.ALREADY_HAVE_FACTION, null);
        }

        if (MineClans.getInstance().getFactionManager().getFaction(factionName) != null) {
            return new CreateResult(CreateResultState.FACTION_EXISTS, null);
        }

        // Create the faction
        faction = MineClans.getInstance().getFactionManager().createFaction(player, factionName);

        // Update player's faction
        MineClans.getInstance().getFactionPlayerManager().updateFaction(factionPlayer.getPlayerId(), faction);

        // Update Rank
        MineClans.getInstance().getFactionPlayerManager().updateRank(factionPlayer.getPlayerId(), Rank.LEADER);

        return new CreateResult(CreateResultState.SUCCESS, faction);
    }
    
    public DisbandResult disband(Player player) {
        FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager().getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();
        if (faction == null) {
            return new DisbandResult(DisbandResultState.NO_FACTION);
        }
        if (!faction.getOwner().equals(factionPlayer.getPlayerId())) {
            return new DisbandResult(DisbandResultState.NOT_OWNER);
        }
        MineClans.getInstance().getFactionManager().disbandFaction(faction.getName());
        MineClans.getInstance().getFactionPlayerManager().updateFaction(factionPlayer.getPlayerId(), null);
        return new DisbandResult(DisbandResultState.SUCCESS);
    }
}
