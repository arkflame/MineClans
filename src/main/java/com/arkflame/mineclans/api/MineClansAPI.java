package com.arkflame.mineclans.api;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.CreateResult.CreateResultState;
import com.arkflame.mineclans.api.DisbandResult.DisbandResultState;
import com.arkflame.mineclans.api.JoinResult.JoinResultState;
import com.arkflame.mineclans.api.LeaveResult.LeaveResultState;
import com.arkflame.mineclans.api.RenameDisplayResult.RenameDisplayResultState;
import com.arkflame.mineclans.api.RenameResult.RenameResultState;
import com.arkflame.mineclans.enums.Rank;
import com.arkflame.mineclans.managers.FactionManager;
import com.arkflame.mineclans.managers.FactionPlayerManager;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

/*
 * MineClans API
 * 
 * Make sure to access asynchronously
 * 
 */
public class MineClansAPI {
    private final FactionManager factionManager;
    private final FactionPlayerManager factionPlayerManager;

    public MineClansAPI(FactionManager factionManager, FactionPlayerManager factionPlayerManager) {
        this.factionManager = factionManager;
        this.factionPlayerManager = factionPlayerManager;
    }

    public Faction getFaction(Player player) {
        FactionPlayer factionPlayer = factionPlayerManager.getOrLoad(player.getUniqueId());
        return factionPlayer != null ? factionPlayer.getFaction() : null;
    }

    public Faction getFaction(String name) {
        return factionManager.getFaction(name);
    }

    public FactionPlayer getFactionPlayer(Player player) {
        return factionPlayerManager.getOrLoad(player.getUniqueId());
    }

    public FactionPlayer getFactionPlayer(UUID uuid) {
        return factionPlayerManager.getOrLoad(uuid);
    }

    public FactionPlayer getFactionPlayer(String name) {
        if (name != null) {
            return factionPlayerManager.loadFactionPlayerFromDatabase(name);
        }
        return null;
    }

    public LeaveResult leave(Player player) {
        FactionPlayer factionPlayer = factionPlayerManager.getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();

        if (faction == null) {
            return new LeaveResult(LeaveResultState.NO_FACTION, faction);
        }

        if (faction.getOwner().equals(factionPlayer.getPlayerId())) {
            return new LeaveResult(LeaveResultState.FACTION_OWNER, faction);
        }

        factionManager.removePlayerFromFaction(faction.getName(), factionPlayer.getPlayerId());
        factionPlayerManager.updateFaction(factionPlayer.getPlayerId(), null);
        return new LeaveResult(LeaveResultState.SUCCESS, faction);
    }

    public JoinResult join(Player player, String factionName) {
        if (factionName == null) {
            return new JoinResult(JoinResultState.NULL_NAME, null, null);
        }

        FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager().getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();

        if (faction != null) {
            return new JoinResult(JoinResultState.ALREADY_HAVE_FACTION, faction, factionPlayer);
        }

        faction = factionManager.getFaction(factionName);

        if (faction != null) {
            if (faction.getInvited().contains(player.getUniqueId())) {
                factionPlayerManager.updateFaction(player.getUniqueId(), faction);
                factionManager.addPlayerToFaction(factionName, player.getUniqueId());
                factionPlayerManager.updateRank(player.getUniqueId(), Rank.MEMBER);
                factionManager.uninvitePlayerFromFaction(factionName, player.getUniqueId());
                return new JoinResult(JoinResultState.SUCCESS, faction, factionPlayer);
            } else {
                return new JoinResult(JoinResultState.NOT_INVITED, faction, factionPlayer);
            }
        } else {
            return new JoinResult(JoinResultState.NO_FACTION, faction, factionPlayer);
        }
    }

    public InviteResult invite(Player player, String toInvite) {
        FactionPlayer factionPlayer = factionPlayerManager.getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();

        if (faction == null) {
            return new InviteResult(InviteResult.InviteResultState.NO_FACTION);
        }

        if (!faction.getOwner().equals(factionPlayer.getPlayerId())) {
            return new InviteResult(InviteResult.InviteResultState.NOT_OWNER);
        }

        FactionPlayer targetPlayer = factionPlayerManager.loadFactionPlayerFromDatabase(toInvite);

        if (targetPlayer == null) {
            return new InviteResult(InviteResult.InviteResultState.PLAYER_NOT_FOUND);
        }

        UUID targetPlayerId = targetPlayer.getPlayerId();

        if (faction.getMembers().contains(targetPlayerId)) {
            return new InviteResult(InviteResult.InviteResultState.MEMBER_EXISTS);
        }

        if (faction.getInvited().contains(targetPlayerId)) {
            return new InviteResult(InviteResult.InviteResultState.ALREADY_INVITED);
        }

        factionManager.invitePlayerToFaction(faction.getName(), targetPlayerId);
        return new InviteResult(InviteResult.InviteResultState.SUCCESS, targetPlayer);
    }

    public UninviteResult uninvite(Player player, String toUninvite) {
        if (toUninvite == null) {
            return new UninviteResult(UninviteResult.UninviteResultState.NULL_NAME);
        }

        FactionPlayer factionPlayer = factionPlayerManager.getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();

        if (faction == null) {
            return new UninviteResult(UninviteResult.UninviteResultState.NO_FACTION);
        }

        if (!faction.getOwner().equals(factionPlayer.getPlayerId())) {
            return new UninviteResult(UninviteResult.UninviteResultState.NOT_OWNER);
        }

        FactionPlayer targetPlayer = factionPlayerManager.loadFactionPlayerFromDatabase(toUninvite);

        if (targetPlayer == null) {
            return new UninviteResult(UninviteResult.UninviteResultState.PLAYER_NOT_FOUND);
        }

        UUID targetPlayerId = targetPlayer.getPlayerId();

        if (!faction.getInvited().contains(targetPlayerId)) {
            return new UninviteResult(UninviteResult.UninviteResultState.NOT_INVITED);
        }

        faction.uninvitePlayer(targetPlayerId);
        return new UninviteResult(UninviteResult.UninviteResultState.SUCCESS);
    }

    public CreateResult create(Player player, String factionName) {
        if (factionName == null) {
            return new CreateResult(CreateResultState.NULL_NAME, null);
        }

        FactionPlayer factionPlayer = factionPlayerManager.getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();
        if (faction != null) {
            return new CreateResult(CreateResultState.ALREADY_HAVE_FACTION, null);
        }

        if (factionManager.getFaction(factionName) != null) {
            return new CreateResult(CreateResultState.FACTION_EXISTS, null);
        }

        // Create the faction
        faction = factionManager.createFaction(player, factionName);

        // Update player's faction
        factionPlayerManager.updateFaction(factionPlayer.getPlayerId(), faction);

        // Update Rank
        factionPlayerManager.updateRank(factionPlayer.getPlayerId(), Rank.LEADER);

        return new CreateResult(CreateResultState.SUCCESS, faction);
    }

    public DisbandResult disband(Player player) {
        FactionPlayer factionPlayer = factionPlayerManager.getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();
        if (faction == null) {
            return new DisbandResult(DisbandResultState.NO_FACTION);
        }
        if (!faction.getOwner().equals(factionPlayer.getPlayerId())) {
            return new DisbandResult(DisbandResultState.NOT_OWNER);
        }
        factionPlayerManager.updateFaction(factionPlayer.getPlayerId(), null);
        for (UUID uuid : faction.getMembers()) {
            factionPlayerManager.updateFaction(uuid, null);
            factionPlayerManager.updateRank(uuid, Rank.MEMBER);
        }
        factionManager.disbandFaction(faction.getName());
        return new DisbandResult(DisbandResultState.SUCCESS);
    }

    public TransferResult transfer(Player player, String newOwnerName) {
        if (newOwnerName == null) {
            return new TransferResult(TransferResult.TransferResultState.NULL_NAME, null);
        }

        FactionPlayer factionPlayer = factionPlayerManager.getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();

        if (faction == null) {
            return new TransferResult(TransferResult.TransferResultState.NO_FACTION, null);
        }

        UUID oldOwnerId = factionPlayer.getPlayerId();

        if (!faction.getOwner().equals(oldOwnerId)) {
            return new TransferResult(TransferResult.TransferResultState.NOT_OWNER, faction);
        }

        FactionPlayer newOwnerPlayer = factionPlayerManager.loadFactionPlayerFromDatabase(newOwnerName);

        if (newOwnerPlayer == null || !faction.getMembers().contains(newOwnerPlayer.getPlayerId())) {
            return new TransferResult(TransferResult.TransferResultState.MEMBER_NOT_FOUND, faction);
        }

        UUID newOwnerId = newOwnerPlayer.getPlayerId();

        factionManager.updateFactionOwner(faction.getName(), newOwnerId);
        factionPlayerManager.updateRank(newOwnerId, Rank.LEADER);
        factionPlayerManager.updateRank(oldOwnerId, Rank.MEMBER);

        return new TransferResult(TransferResult.TransferResultState.SUCCESS, faction);
    }

    public RenameResult rename(Player player, String newName) {
        if (newName != null) {
            Faction faction = MineClans.getInstance().getFactionManager().getFaction(newName);
            if (faction == null) {
                FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager()
                        .getOrLoad(player.getUniqueId());
                Faction playerFaction = factionPlayer.getFaction();
                if (playerFaction != null) {
                    factionManager.updateFactionName(playerFaction.getName(), newName);
                    return new RenameResult(playerFaction, RenameResultState.SUCCESS);
                } else {
                    return new RenameResult(null, RenameResultState.NOT_IN_FACTION);
                }
            } else {
                return new RenameResult(null, RenameResultState.ALREADY_EXISTS);
            }
        } else {
            return new RenameResult(null, RenameResultState.NULL_NAME);
        }
    }

    public RenameDisplayResult renameDisplay(Player player, String displayName) {
        if (displayName != null) {
            FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager()
                    .getOrLoad(player.getUniqueId());
            Faction playerFaction = factionPlayer.getFaction();
            if (playerFaction != null) {
                factionManager.updateFactionDisplayName(playerFaction.getName(), displayName);
                return new RenameDisplayResult(playerFaction, RenameDisplayResultState.SUCCESS);
            } else {
                return new RenameDisplayResult(null, RenameDisplayResultState.NOT_IN_FACTION);
            }
        } else {
            return new RenameDisplayResult(null, RenameDisplayResultState.NULL_NAME);
        }
    }
}
