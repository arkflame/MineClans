package com.arkflame.mineclans.api;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.AddKillResult;
import com.arkflame.mineclans.api.results.CreateResult;
import com.arkflame.mineclans.api.results.DisbandResult;
import com.arkflame.mineclans.api.results.FactionChatResult;
import com.arkflame.mineclans.api.results.FocusResult;
import com.arkflame.mineclans.api.results.FocusResult.FocusResultType;
import com.arkflame.mineclans.api.results.FriendlyFireResult;
import com.arkflame.mineclans.api.results.HomeResult;
import com.arkflame.mineclans.api.results.InviteResult;
import com.arkflame.mineclans.api.results.JoinResult;
import com.arkflame.mineclans.api.results.LeaveResult;
import com.arkflame.mineclans.api.results.RankChangeResult;
import com.arkflame.mineclans.api.results.RenameDisplayResult;
import com.arkflame.mineclans.api.results.RenameResult;
import com.arkflame.mineclans.api.results.SetHomeResult;
import com.arkflame.mineclans.api.results.SetRelationResult;
import com.arkflame.mineclans.api.results.ToggleChatResult;
import com.arkflame.mineclans.api.results.TransferResult;
import com.arkflame.mineclans.api.results.UninviteResult;
import com.arkflame.mineclans.api.results.WithdrawResult;
import com.arkflame.mineclans.api.results.AddKillResult.AddKillResultType;
import com.arkflame.mineclans.api.results.WithdrawResult.WithdrawResultType;
import com.arkflame.mineclans.api.results.CreateResult.CreateResultState;
import com.arkflame.mineclans.api.results.DepositResult;
import com.arkflame.mineclans.api.results.DepositResult.DepositResultType;
import com.arkflame.mineclans.api.results.DisbandResult.DisbandResultState;
import com.arkflame.mineclans.api.results.HomeResult.HomeResultState;
import com.arkflame.mineclans.api.results.JoinResult.JoinResultState;
import com.arkflame.mineclans.api.results.KickResult;
import com.arkflame.mineclans.api.results.KickResult.KickResultType;
import com.arkflame.mineclans.api.results.LeaveResult.LeaveResultState;
import com.arkflame.mineclans.api.results.OpenChestResult;
import com.arkflame.mineclans.api.results.OpenChestResult.OpenChestResultType;
import com.arkflame.mineclans.api.results.RankChangeResult.RankChangeResultType;
import com.arkflame.mineclans.api.results.RenameDisplayResult.RenameDisplayResultState;
import com.arkflame.mineclans.api.results.RenameResult.RenameResultState;
import com.arkflame.mineclans.api.results.SetHomeResult.SetHomeResultState;
import com.arkflame.mineclans.enums.Rank;
import com.arkflame.mineclans.enums.RelationType;
import com.arkflame.mineclans.events.ClanEvent;
import com.arkflame.mineclans.managers.FactionManager;
import com.arkflame.mineclans.managers.FactionPlayerManager;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

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

    public Faction getFaction(UUID id) {
        return factionManager.getFaction(id);
    }

    public FactionPlayer getFactionPlayer(Player player) {
        return factionPlayerManager.getOrLoad(player.getUniqueId());
    }

    public FactionPlayer getFactionPlayer(UUID uuid) {
        return factionPlayerManager.getOrLoad(uuid);
    }

    public FactionPlayer getFactionPlayer(String name) {
        if (name != null) {
            return factionPlayerManager.getOrLoad(name);
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

        if (factionPlayer.getRank().isLowerThan(Rank.MODERATOR)) {
            return new InviteResult(InviteResult.InviteResultState.NO_PERMISSION);
        }

        FactionPlayer targetPlayer = factionPlayerManager.getOrLoad(toInvite);

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

        if (factionPlayer.getRank().isLowerThan(Rank.MODERATOR)) {
            return new UninviteResult(UninviteResult.UninviteResultState.NO_PERMISSION);
        }

        FactionPlayer targetPlayer = factionPlayerManager.getOrLoad(toUninvite);

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

        try {
            // Create the faction
            faction = factionManager.createFaction(player, factionName);

            // Update player's faction
            factionPlayerManager.updateFaction(factionPlayer.getPlayerId(), faction);

            // Update Rank
            factionPlayerManager.updateRank(factionPlayer.getPlayerId(), Rank.LEADER);
        } catch (IllegalArgumentException ex) {
            return new CreateResult(CreateResultState.ERROR, faction);
        }

        return new CreateResult(CreateResultState.SUCCESS, faction);
    }

    public DisbandResult disband(Player player) {
        FactionPlayer factionPlayer = factionPlayerManager.getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();
        if (faction == null) {
            return new DisbandResult(DisbandResultState.NO_FACTION);
        }
        if (factionPlayer.getRank().isLowerThan(Rank.LEADER)) {
            return new DisbandResult(DisbandResultState.NO_PERMISSION);
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

        FactionPlayer newOwnerPlayer = factionPlayerManager.getOrLoad(newOwnerName);

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
        if (newName == null) {
            return new RenameResult(null, RenameResultState.NULL_NAME);
        }

        Faction faction = MineClans.getInstance().getFactionManager().getFaction(newName);
        if (faction != null) {
            return new RenameResult(null, RenameResultState.ALREADY_EXISTS);
        }

        FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager()
                .getOrLoad(player.getUniqueId());
        Faction playerFaction = factionPlayer.getFaction();

        if (playerFaction == null) {
            return new RenameResult(null, RenameResultState.NOT_IN_FACTION);
        }

        if (factionPlayer.getRank().isLowerThan(Rank.LEADER)) {
            return new RenameResult(playerFaction, RenameResultState.NO_PERMISSION);
        }

        try {
            factionManager.updateFactionName(playerFaction.getName(), newName);
        } catch (IllegalArgumentException ex) {
            return new RenameResult(playerFaction, RenameResultState.ERROR);
        }

        return new RenameResult(playerFaction, RenameResultState.SUCCESS);
    }

    public RenameDisplayResult renameDisplay(Player player, String displayName) {
        if (displayName != null) {
            FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager()
                    .getOrLoad(player.getUniqueId());
            Faction playerFaction = factionPlayer.getFaction();
            if (playerFaction != null) {
                try {
                    factionManager.updateFactionDisplayName(playerFaction.getName(), displayName);
                } catch (IllegalArgumentException ex) {
                    return new RenameDisplayResult(playerFaction, RenameDisplayResultState.ERROR);
                }
                return new RenameDisplayResult(playerFaction, RenameDisplayResultState.SUCCESS);
            } else {
                return new RenameDisplayResult(null, RenameDisplayResultState.NOT_IN_FACTION);
            }
        } else {
            return new RenameDisplayResult(null, RenameDisplayResultState.NULL_NAME);
        }
    }

    public ToggleChatResult toggleChat(Player player) {
        FactionPlayer factionPlayer = getFactionPlayer(player.getUniqueId());
        if (factionPlayer == null || factionPlayer.getFaction() == null) {
            return new ToggleChatResult(ToggleChatResult.ToggleChatState.NOT_IN_FACTION);
        }

        factionPlayer.toggleChat();
        if (factionPlayer.isChatEnabled()) {
            return new ToggleChatResult(ToggleChatResult.ToggleChatState.ENABLED);
        } else {
            return new ToggleChatResult(ToggleChatResult.ToggleChatState.DISABLED);
        }
    }

    public FactionChatResult sendFactionMessage(Player player, String message) {
        FactionPlayer factionPlayer = getFactionPlayer(player.getUniqueId());
        if (factionPlayer == null || factionPlayer.getFaction() == null) {
            return new FactionChatResult(FactionChatResult.FactionChatState.NOT_IN_FACTION, message, null,
                    factionPlayer);
        }
    
        Faction faction = factionPlayer.getFaction();
        String chatPrefix = MineClans.getInstance().getMessages().getText("factions.chat.prefix");
        String playerName = player.getName();
        String formattedMessage = chatPrefix.replace("%player%", playerName) + message;
        faction.getMembers().forEach(memberId -> {
            Player member = MineClans.getInstance().getServer().getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage(formattedMessage);
            }
        });
    
        return new FactionChatResult(FactionChatResult.FactionChatState.SUCCESS, message, faction, factionPlayer);
    }    

    public FriendlyFireResult toggleFriendlyFire(Player player) {
        FactionPlayer factionPlayer = getFactionPlayer(player.getUniqueId());
        if (factionPlayer == null || factionPlayer.getFaction() == null) {
            return new FriendlyFireResult(FriendlyFireResult.FriendlyFireResultState.NOT_IN_FACTION);
        }

        if (factionPlayer.getRank().isLowerThan(Rank.COLEADER)) {
            return new FriendlyFireResult(FriendlyFireResult.FriendlyFireResultState.NO_PERMISSION);
        }

        Faction faction = factionPlayer.getFaction();
        boolean friendlyFire = !faction.isFriendlyFire();
        factionManager.updateFriendlyFire(faction.getName(), friendlyFire);

        return new FriendlyFireResult(friendlyFire ? FriendlyFireResult.FriendlyFireResultState.ENABLED
                : FriendlyFireResult.FriendlyFireResultState.DISABLED);
    }

    public SetHomeResult setHome(Player player, Location homeLocation) {
        FactionPlayer factionPlayer = getFactionPlayer(player.getUniqueId());
        if (factionPlayer == null || factionPlayer.getFaction() == null) {
            return new SetHomeResult(SetHomeResultState.NOT_IN_FACTION);
        }

        if (factionPlayer.getRank().isLowerThan(Rank.COLEADER)) {
            return new SetHomeResult(SetHomeResultState.NO_PERMISSION);
        }

        String factionName = factionPlayer.getFaction().getName();
        factionManager.updateHome(factionName, homeLocation);
        return new SetHomeResult(SetHomeResultState.SUCCESS);
    }

    public HomeResult getHome(Player player) {
        FactionPlayer factionPlayer = getFactionPlayer(player.getUniqueId());
        if (factionPlayer == null || factionPlayer.getFaction() == null) {
            return new HomeResult(HomeResultState.NOT_IN_FACTION);
        }

        Location homeLocation = factionPlayer.getFaction().getHome();
        if (homeLocation == null) {
            return new HomeResult(HomeResultState.NO_HOME_SET);
        }

        return new HomeResult(HomeResultState.SUCCESS, homeLocation);
    }

    public SetRelationResult setRelation(Player player, String otherFactionName, String relationName) {
        relationName = relationName.toUpperCase();

        RelationType relationType;
        try {
            relationType = RelationType.valueOf(relationName);
        } catch (IllegalArgumentException e) {
            return new SetRelationResult(SetRelationResult.SetRelationResultState.INVALID_RELATION_TYPE, null, null,
                    null);
        }

        Faction faction = getFaction(player);
        if (faction == null) {
            return new SetRelationResult(SetRelationResult.SetRelationResultState.NO_FACTION, null, null, null);
        }

        Faction otherFaction = getFaction(otherFactionName);
        if (otherFaction == null) {
            return new SetRelationResult(SetRelationResult.SetRelationResultState.OTHER_FACTION_NOT_FOUND, faction,
                    null, null);
        }

        if (faction == otherFaction) {
            return new SetRelationResult(SetRelationResult.SetRelationResultState.SAME_FACTION, faction,
                    otherFaction, null);
        }

        factionManager.updateFactionRelation(faction.getName(), otherFaction.getId(), relationName);
        return new SetRelationResult(SetRelationResult.SetRelationResultState.SUCCESS, faction, otherFaction,
                relationType);
    }

    public RelationType getRelation(Player player, String otherFactionName) {
        // Get the player's faction
        Faction faction = getFaction(player);
        if (faction == null) {
            return RelationType.NEUTRAL; // Player is not in a faction
        }

        // Get the other faction by name
        Faction otherFaction = getFaction(otherFactionName);
        if (otherFaction == null) {
            return RelationType.NEUTRAL; // Other faction not found
        }

        // Get the relation type between the factions
        return factionManager.getEffectiveRelation(faction.getName(), otherFaction.getName());
    }

    public RankChangeResult promote(Player player, String playerName) {
        FactionPlayer target = getFactionPlayer(playerName);
        FactionPlayer sender = getFactionPlayer(player.getUniqueId());

        if (target == null || sender == null) {
            return new RankChangeResult(RankChangeResultType.PLAYER_NOT_FOUND, null);
        }

        if (!target.getFaction().equals(sender.getFaction())) {
            return new RankChangeResult(RankChangeResultType.NOT_IN_FACTION, null);
        }

        if (sender.getRank().isLowerThan(Rank.LEADER)) {
            return new RankChangeResult(RankChangeResultType.NO_PERMISSION, null);
        }

        if (target.getRank().isEqualOrHigherThan(sender.getRank())) {
            return new RankChangeResult(RankChangeResultType.SUPERIOR_RANK, null);
        }

        Rank nextRank = target.getRank().getNext();
        if (nextRank != null) {
            if (nextRank == Rank.LEADER) {
                return new RankChangeResult(RankChangeResultType.CANNOT_PROMOTE_TO_LEADER, null);
            }

            if (nextRank == sender.getRank()) {
                return new RankChangeResult(RankChangeResultType.CANNOT_PROMOTE, null);
            }

            factionPlayerManager.updateRank(target.getPlayerId(), nextRank);
            return new RankChangeResult(RankChangeResultType.SUCCESS, nextRank);
        }

        return new RankChangeResult(RankChangeResultType.CANNOT_PROMOTE, null);
    }

    public RankChangeResult demote(Player player, String playerName) {
        FactionPlayer target = getFactionPlayer(playerName);
        FactionPlayer sender = getFactionPlayer(player.getUniqueId());

        if (target == null || sender == null) {
            return new RankChangeResult(RankChangeResultType.PLAYER_NOT_FOUND, null);
        }

        if (!target.getFaction().equals(sender.getFaction())) {
            return new RankChangeResult(RankChangeResultType.NOT_IN_FACTION, null);
        }

        if (sender.getRank().isLowerThan(Rank.LEADER)) {
            return new RankChangeResult(RankChangeResultType.NO_PERMISSION, null);
        }

        if (target.getRank().isEqualOrHigherThan(sender.getRank())) {
            return new RankChangeResult(RankChangeResultType.SUPERIOR_RANK, null);
        }

        Rank previousRank = target.getRank().getPrevious();
        if (previousRank != null) {
            factionPlayerManager.updateRank(target.getPlayerId(), previousRank);
            return new RankChangeResult(RankChangeResultType.SUCCESS, previousRank);
        }

        return new RankChangeResult(RankChangeResultType.CANNOT_DEMOTE, null);
    }

    public KickResult kick(Player kicker, String playerName) {
        // Check if the kicker is in a faction
        FactionPlayer kickerFactionPlayer = factionPlayerManager.getOrLoad(kicker.getUniqueId());
        if (kickerFactionPlayer == null || kickerFactionPlayer.getFaction() == null) {
            return new KickResult(KickResultType.NOT_IN_FACTION, null, null);
        }

        // Check if the kicker is the owner of the faction
        Faction faction = kickerFactionPlayer.getFaction();
        if (kickerFactionPlayer.getRank().isLowerThan(Rank.MODERATOR)) {
            return new KickResult(KickResultType.NOT_MODERATOR, faction, null);
        }

        // Check if the player to be kicked is a member of the faction
        FactionPlayer playerToKick = factionPlayerManager.getOrLoad(playerName);
        if (playerToKick == null || !faction.getMembers().contains(playerToKick.getPlayerId())) {
            return new KickResult(KickResultType.PLAYER_NOT_FOUND, faction, playerToKick);
        }

        if (kickerFactionPlayer == playerToKick) {
            return new KickResult(KickResultType.NOT_YOURSELF, faction, playerToKick);
        }

        // Check if the player to be kicked is the owner of the faction
        if (playerToKick.getRank().isEqualOrHigherThan(kickerFactionPlayer.getRank())) {
            return new KickResult(KickResultType.SUPERIOR_RANK, faction, playerToKick);
        }

        // Kick the player from the faction
        factionManager.removePlayerFromFaction(faction.getName(), playerToKick.getPlayerId());
        factionPlayerManager.updateFaction(playerToKick.getPlayerId(), null);

        return new KickResult(KickResultType.SUCCESS, faction, playerToKick);
    }

    public OpenChestResult openChest(Player player) {
        FactionPlayer factionPlayer = getFactionPlayer(player);
        Faction faction = factionPlayer.getFaction();
        if (faction == null) {
            return new OpenChestResult(OpenChestResultType.NOT_IN_FACTION, faction, factionPlayer);
        }

        if (factionPlayer.getRank().isLowerThan(Rank.MEMBER)) {
            return new OpenChestResult(OpenChestResultType.NO_PERMISSION, faction, factionPlayer);
        }

        try {
            Inventory chestInventory = faction.getChest();
            player.openInventory(chestInventory);
            return new OpenChestResult(OpenChestResultType.SUCCESS, faction, factionPlayer);
        } catch (Exception e) {
            return new OpenChestResult(OpenChestResultType.ERROR, faction, factionPlayer);
        }
    }

    public FocusResult focus(Player player, String factionName) {
        FactionPlayer factionPlayer = MineClans.getInstance().getFactionPlayerManager().getOrLoad(player.getUniqueId());
        Faction faction = factionPlayer.getFaction();
        if (faction == null) {
            return new FocusResult(FocusResultType.NOT_IN_FACTION);
        }

        if (factionPlayer.getRank().isLowerThan(Rank.RECRUIT)) {
            return new FocusResult(FocusResultType.NO_PERMISSION);
        }

        Faction targetFaction = MineClans.getInstance().getFactionManager().getFaction(factionName);
        if (targetFaction == null) {
            faction.setFocusedFaction(null);
            return new FocusResult(FocusResultType.FACTION_NOT_FOUND);
        }

        if (faction == targetFaction) {
            return new FocusResult(FocusResultType.SAME_FACTION);
        }

        faction.setFocusedFaction(targetFaction.getId());

        return new FocusResult(FocusResultType.SUCCESS);
    }

    public WithdrawResult withdraw(Player player, double amount) {
        if (amount <= 0) {
            return new WithdrawResult(WithdrawResultType.INVALID_AMOUNT, 0); // Invalid amount
        }

        if (!MineClans.getInstance().isVaultHooked()) {
            return new WithdrawResult(WithdrawResultType.NO_ECONOMY, 0); // Vault not hooked
        }

        Economy economy = MineClans.getInstance().getVaultEconomy();

        FactionPlayer factionPlayer = getFactionPlayer(player.getUniqueId());
        if (factionPlayer == null || factionPlayer.getFaction() == null) {
            return new WithdrawResult(WithdrawResultType.NOT_IN_FACTION, 0); // Player is not in a faction
        }

        Faction faction = factionPlayer.getFaction();
        Rank playerRank = factionPlayer.getRank();

        // Check if player rank is above a certain rank
        if (playerRank.isLowerThan(Rank.COLEADER)) {
            return new WithdrawResult(WithdrawResultType.NO_PERMISSION, 0); // Player doesn't have sufficient permission
        }

        // Check if there's enough balance in the faction
        double factionBalance = faction.getBalance();
        if (factionBalance < amount) {
            return new WithdrawResult(WithdrawResultType.INSUFFICIENT_FUNDS, factionBalance); // Not enough balance
        }

        // Assuming your FactionManager class has a method to withdraw currency
        boolean success = factionManager.withdraw(faction.getName(), amount)
                && economy.depositPlayer(player, amount).type == EconomyResponse.ResponseType.SUCCESS;
        if (success) {
            return new WithdrawResult(WithdrawResultType.SUCCESS, amount); // Withdrawal successful
        } else {
            return new WithdrawResult(WithdrawResultType.ERROR, 0); // Error occurred during withdrawal
        }
    }

    public DepositResult deposit(Player player, double amount) {
        if (amount <= 0) {
            return new DepositResult(DepositResultType.INVALID_AMOUNT, 0); // Invalid amount
        }

        if (!MineClans.getInstance().isVaultHooked()) {
            return new DepositResult(DepositResultType.NO_ECONOMY, 0); // Vault not hooked
        }

        Economy economy = MineClans.getInstance().getVaultEconomy();

        FactionPlayer factionPlayer = getFactionPlayer(player.getUniqueId());
        if (factionPlayer == null || factionPlayer.getFaction() == null) {
            return new DepositResult(DepositResultType.NOT_IN_FACTION, 0); // Player is not in a faction
        }

        Faction faction = factionPlayer.getFaction();
        Rank playerRank = factionPlayer.getRank();

        if (playerRank.isLowerThan(Rank.RECRUIT)) {
            return new DepositResult(DepositResultType.NO_PERMISSION, 0); // Player doesn't have sufficient permission
        }

        if (!economy.has(player, amount)) {
            return new DepositResult(DepositResultType.NO_MONEY, 0); // Player doesn't have sufficient permission
        }

        // Assuming your FactionManager class has a method to deposit currency
        boolean success = economy.withdrawPlayer(player, amount).type == EconomyResponse.ResponseType.SUCCESS
                && factionManager.deposit(faction.getName(), amount);
        if (success) {
            return new DepositResult(DepositResultType.SUCCESS, amount); // Deposit successful
        } else {
            return new DepositResult(DepositResultType.ERROR, 0); // Error occurred during deposit
        }
    }

    public AddKillResult addKill(Player player, Player killed) {
        FactionPlayer factionPlayer = getFactionPlayer(player);
        FactionPlayer killedPlayer = getFactionPlayer(killed);

        // Ensure both players exist in the system
        if (factionPlayer == null || killedPlayer == null) {
            return new AddKillResult(AddKillResultType.PLAYER_NOT_FOUND);
        }

        Faction faction = factionPlayer.getFaction();

        if (faction == null) {
            return new AddKillResult(AddKillResultType.NO_FACTION);
        }

        Faction killedFaction = killedPlayer.getFaction();

        // Ignore kills within the same faction
        if (faction.equals(killedFaction)) {
            return new AddKillResult(AddKillResultType.SAME_FACTION);
        }

        // Add kill to the faction player and possibly to the faction
        boolean playerKill = factionPlayer.addKill(killed.getUniqueId());
        boolean factionKill = faction.addKill(killed.getUniqueId());
        if (playerKill || factionKill) {
            if (playerKill) {
                factionPlayerManager.save(factionPlayer);
            }
            if (factionKill) {
                factionManager.saveFactionToDatabase(faction);
            }
            return new AddKillResult(AddKillResultType.SUCCESS);
        }
        return new AddKillResult(AddKillResultType.ALREADY_KILLED);
    }

    public int getKills(Player player) {
        FactionPlayer factionPlayer = getFactionPlayer(player);

        if (factionPlayer == null) {
            return 0;
        }

        return factionPlayer.getKills();
    }

    public ClanEvent getCurrentEvent() {
        return MineClans.getInstance().getClanEventScheduler().getEvent();
    }
}
