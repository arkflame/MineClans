package com.arkflame.mineclans.api;

import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.AddDeathResult;
import com.arkflame.mineclans.api.results.AddEventsWonResult;
import com.arkflame.mineclans.api.results.AddEventsWonResult.AddEventsWonResultType;
import com.arkflame.mineclans.api.results.AddKillResult;
import com.arkflame.mineclans.api.results.AddKillResult.AddKillResultType;
import com.arkflame.mineclans.api.results.AnnouncementResult;
import com.arkflame.mineclans.api.results.CreateResult;
import com.arkflame.mineclans.api.results.CreateResult.CreateResultState;
import com.arkflame.mineclans.api.results.DepositResult;
import com.arkflame.mineclans.api.results.DepositResult.DepositResultType;
import com.arkflame.mineclans.api.results.DisbandResult;
import com.arkflame.mineclans.api.results.DisbandResult.DisbandResultState;
import com.arkflame.mineclans.api.results.DiscordResult;
import com.arkflame.mineclans.api.results.FactionChatResult;
import com.arkflame.mineclans.api.results.FocusResult;
import com.arkflame.mineclans.api.results.FocusResult.FocusResultType;
import com.arkflame.mineclans.api.results.FriendlyFireResult;
import com.arkflame.mineclans.api.results.HomeResult;
import com.arkflame.mineclans.api.results.HomeResult.HomeResultState;
import com.arkflame.mineclans.api.results.InviteResult;
import com.arkflame.mineclans.api.results.JoinResult;
import com.arkflame.mineclans.api.results.JoinResult.JoinResultState;
import com.arkflame.mineclans.api.results.KickResult;
import com.arkflame.mineclans.api.results.KickResult.KickResultType;
import com.arkflame.mineclans.api.results.OpenChestResult;
import com.arkflame.mineclans.api.results.OpenChestResult.OpenChestResultType;
import com.arkflame.mineclans.api.results.OpenResult;
import com.arkflame.mineclans.api.results.RankChangeResult;
import com.arkflame.mineclans.api.results.RankChangeResult.RankChangeResultType;
import com.arkflame.mineclans.api.results.RenameDisplayResult;
import com.arkflame.mineclans.api.results.RenameDisplayResult.RenameDisplayResultState;
import com.arkflame.mineclans.api.results.RenameResult;
import com.arkflame.mineclans.api.results.RenameResult.RenameResultState;
import com.arkflame.mineclans.api.results.SetHomeResult;
import com.arkflame.mineclans.api.results.SetHomeResult.SetHomeResultState;
import com.arkflame.mineclans.api.results.SetRelationResult;
import com.arkflame.mineclans.api.results.ToggleChatResult;
import com.arkflame.mineclans.api.results.TransferResult;
import com.arkflame.mineclans.api.results.UninviteResult;
import com.arkflame.mineclans.api.results.WithdrawResult;
import com.arkflame.mineclans.api.results.WithdrawResult.WithdrawResultType;
import com.arkflame.mineclans.enums.Rank;
import com.arkflame.mineclans.enums.RelationType;
import com.arkflame.mineclans.events.ClanEvent;
import com.arkflame.mineclans.managers.FactionManager;
import com.arkflame.mineclans.managers.FactionPlayerManager;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.models.Relation;
import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.RedisProvider;
import com.arkflame.mineclans.utils.LocationData;

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
    private final MySQLProvider mySQLProvider;
    private final RedisProvider redisProvider;

    public MineClansAPI(FactionManager factionManager, FactionPlayerManager factionPlayerManager,
            MySQLProvider mySQLProvider, RedisProvider redisProvider) {
        this.factionManager = factionManager;
        this.factionPlayerManager = factionPlayerManager;
        this.mySQLProvider = mySQLProvider;
        this.redisProvider = redisProvider;
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
            return new InviteResult(InviteResult.InviteResultState.PLAYER_NOT_FOUND, targetPlayer, faction);
        }

        UUID targetPlayerId = targetPlayer.getPlayerId();

        if (faction.getMembers().contains(targetPlayerId)) {
            return new InviteResult(InviteResult.InviteResultState.MEMBER_EXISTS, targetPlayer, faction);
        }

        if (faction.getInvited().contains(targetPlayerId)) {
            return new InviteResult(InviteResult.InviteResultState.ALREADY_INVITED, targetPlayer, faction);
        }

        factionManager.invitePlayerToFaction(faction.getName(), targetPlayerId);
        mySQLProvider.getInvitedDAO().addInvitedMember(faction.getId(), targetPlayerId);
        redisProvider.invite(faction.getId(), targetPlayerId);
        return new InviteResult(InviteResult.InviteResultState.SUCCESS, targetPlayer, faction);
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

        factionManager.uninvitePlayerFromFaction(faction.getName(), targetPlayerId);
        mySQLProvider.getInvitedDAO().removeInvitedMember(faction.getId(), targetPlayerId);
        redisProvider.uninvite(faction.getId(), player.getUniqueId());
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
            faction = factionManager.createFaction(player.getUniqueId(), factionName);
            redisProvider.createFaction(faction.getId(), player.getUniqueId(), factionName);

            // Add player to faction
            factionManager.addPlayer(factionName, player.getUniqueId());
            redisProvider.addPlayer(faction.getId(), player.getUniqueId());

            // Save to database
            factionManager.saveFactionToDatabase(faction);

            // Save member to database
            mySQLProvider.getMemberDAO().addMember(faction.getId(), player.getUniqueId());

            // Update player's faction
            factionPlayerManager.updateFaction(factionPlayer.getPlayerId(), faction);
            redisProvider.updateFaction(factionPlayer.getPlayerId(), faction.getName());

            // Update Rank
            factionPlayerManager.updateRank(factionPlayer.getPlayerId(), Rank.LEADER);
            factionPlayerManager.save(factionPlayer);
            mySQLProvider.getRanksDAO().setRank(factionPlayer.getPlayerId(), Rank.LEADER);
            redisProvider.updateRank(factionPlayer.getPlayerId(), Rank.LEADER);
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
        factionPlayerManager.save(factionPlayer);
        for (UUID uuid : faction.getMembers()) {
            factionPlayerManager.updateFaction(uuid, null);
            factionPlayerManager.updateRank(uuid, Rank.RECRUIT);
            factionPlayerManager.save(uuid);
            mySQLProvider.getRanksDAO().setRank(uuid, Rank.RECRUIT);
            redisProvider.updateRank(uuid, Rank.RECRUIT);
            redisProvider.updateFaction(uuid, null);
        }
        factionManager.disbandFaction(faction.getName());
        factionManager.removeFactionFromDatabase(faction);
        MineClans.getInstance().getLeaderboardManager().removeFaction(faction.getId());
        redisProvider.removeFaction(faction.getId());
        return new DisbandResult(DisbandResultState.SUCCESS, faction);
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
        factionManager.saveFactionToDatabase(faction);
        redisProvider.updateFactionOwner(faction.getId(), newOwnerId);
        factionPlayerManager.updateRank(newOwnerId, Rank.LEADER);
        factionPlayerManager.save(newOwnerId);
        mySQLProvider.getRanksDAO().setRank(newOwnerId, Rank.LEADER);
        redisProvider.updateRank(newOwnerId, Rank.LEADER);
        factionPlayerManager.updateRank(oldOwnerId, Rank.RECRUIT);
        factionPlayerManager.save(oldOwnerId);
        mySQLProvider.getRanksDAO().setRank(oldOwnerId, Rank.RECRUIT);
        redisProvider.updateRank(oldOwnerId, Rank.RECRUIT);

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

        FactionPlayer factionPlayer = factionPlayerManager
                .getOrLoad(player.getUniqueId());
        Faction playerFaction = factionPlayer.getFaction();

        if (playerFaction == null) {
            return new RenameResult(null, RenameResultState.NOT_IN_FACTION);
        }

        if (factionPlayer.getRank().isLowerThan(Rank.LEADER)) {
            return new RenameResult(playerFaction, RenameResultState.NO_PERMISSION);
        }

        if (playerFaction.isRenameCooldown()) {
            return new RenameResult(playerFaction, RenameResultState.COOLDOWN);
        }

        try {
            factionManager.updateFactionName(playerFaction.getName(), newName);
            playerFaction.setRenameCooldown();
            factionManager.saveFactionToDatabase(faction);
            redisProvider.updateName(playerFaction.getId(), newName);
        } catch (IllegalArgumentException ex) {
            return new RenameResult(playerFaction, RenameResultState.ERROR);
        }

        return new RenameResult(playerFaction, RenameResultState.SUCCESS);
    }

    public RenameDisplayResult renameDisplay(Player player, String displayName) {
        if (displayName != null) {
            FactionPlayer factionPlayer = factionPlayerManager
                    .getOrLoad(player.getUniqueId());
            Faction playerFaction = factionPlayer.getFaction();
            if (playerFaction != null) {
                try {
                    factionManager.updateFactionDisplayName(playerFaction.getName(), displayName);
                    factionManager.saveFactionToDatabase(playerFaction);
                    redisProvider.updateDisplayName(playerFaction.getId(), displayName);
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
        return new ToggleChatResult(factionPlayer.getChatMode());
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

        // Send faction message
        factionManager.sendFactionMessage(faction, formattedMessage);

        // Send by redis
        redisProvider.sendFactionMessage(faction.getId(), formattedMessage);

        return new FactionChatResult(FactionChatResult.FactionChatState.SUCCESS, message, faction, factionPlayer);
    }

    public FactionChatResult sendAllianceMessage(Player player, String message) {
        FactionPlayer factionPlayer = getFactionPlayer(player.getUniqueId());
        if (factionPlayer == null || factionPlayer.getFaction() == null) {
            return new FactionChatResult(FactionChatResult.FactionChatState.NOT_IN_FACTION, message, null,
                    factionPlayer);
        }

        Faction faction = factionPlayer.getFaction();
        String chatPrefix = MineClans.getInstance().getMessages().getText("factions.chat.prefix_alliance");
        String playerName = player.getName();
        String formattedMessage = chatPrefix.replace("%player%", playerName).replace("%faction%", faction.getName()) + message;

        // Send faction message
        factionManager.sendFactionMessage(faction, formattedMessage);

        // Send alliance message
        factionManager.sendAllianceMessage(faction, formattedMessage);

        // Send by redis
        redisProvider.sendFactionMessage(faction.getId(), formattedMessage);
        redisProvider.sendAllianceMessage(faction.getId(), formattedMessage);

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
        factionManager.saveFactionToDatabase(faction);

        // Send redis update
        redisProvider.updateFriendlyFire(faction.getId(), friendlyFire);

        return new FriendlyFireResult(friendlyFire ? FriendlyFireResult.FriendlyFireResultState.ENABLED
                : FriendlyFireResult.FriendlyFireResultState.DISABLED);
    }

    public SetHomeResult setHome(Player player, LocationData homeLocation) {
        FactionPlayer factionPlayer = getFactionPlayer(player.getUniqueId());
        if (factionPlayer == null || factionPlayer.getFaction() == null) {
            return new SetHomeResult(SetHomeResultState.NOT_IN_FACTION);
        }

        if (factionPlayer.getRank().isLowerThan(Rank.COLEADER)) {
            return new SetHomeResult(SetHomeResultState.NO_PERMISSION);
        }

        Faction faction = factionPlayer.getFaction();
        String factionName = faction.getName();
        factionManager.updateHome(factionName, homeLocation);
        redisProvider.updateHome(faction.getId(), homeLocation);

        // Save changes to the faction
        factionManager.saveFactionToDatabase(faction);

        return new SetHomeResult(SetHomeResultState.SUCCESS);
    }

    public HomeResult getHome(Player player) {
        FactionPlayer factionPlayer = getFactionPlayer(player.getUniqueId());
        if (factionPlayer == null || factionPlayer.getFaction() == null) {
            return new HomeResult(HomeResultState.NOT_IN_FACTION);
        }

        LocationData homeLocation = factionPlayer.getFaction().getHome();
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
                    null, null);
        }

        Faction faction = getFaction(player);
        if (faction == null) {
            return new SetRelationResult(SetRelationResult.SetRelationResultState.NO_FACTION, null, null, null, null);
        }

        Faction otherFaction = getFaction(otherFactionName);
        if (otherFaction == null) {
            return new SetRelationResult(SetRelationResult.SetRelationResultState.OTHER_FACTION_NOT_FOUND, faction,
                    null, null, null);
        }

        if (faction == otherFaction) {
            return new SetRelationResult(SetRelationResult.SetRelationResultState.SAME_FACTION, faction,
                    otherFaction, null, null);
        }

        UUID factionId = faction.getId();
        UUID otherFactionId = otherFaction.getId();
        RelationType otherRelation = otherFaction.getRelationType(factionId);
        Relation currentRelation = faction.getRelation(otherFactionId);

        if (currentRelation != null && currentRelation.getRelationType() == relationType) {
            return new SetRelationResult(SetRelationResult.SetRelationResultState.ALREADY_RELATION, faction,
                    otherFaction, relationType, otherRelation);
        }

        factionManager.updateFactionRelation(faction.getName(), otherFactionId, relationName);
        mySQLProvider.getRelationsDAO().insertOrUpdateRelation(factionId, otherFactionId, relationName);

        // Send update to redis
        redisProvider.updateRelation(factionId, otherFactionId, relationType);
        return new SetRelationResult(SetRelationResult.SetRelationResultState.SUCCESS, faction, otherFaction,
                relationType, otherRelation);
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

        if (!sender.getFaction().equals(target.getFaction())) {
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

            UUID targetPlayerId = target.getPlayerId();

            factionPlayerManager.updateRank(targetPlayerId, nextRank);
            factionPlayerManager.save(target);
            mySQLProvider.getRanksDAO().setRank(targetPlayerId, nextRank);

            // Send update to redis
            redisProvider.updateRank(targetPlayerId, nextRank);
            return new RankChangeResult(RankChangeResultType.SUCCESS, nextRank);
        }

        return new RankChangeResult(RankChangeResultType.CANNOT_PROMOTE, null);
    }

    public RankChangeResult demote(Player senderPlayer, String targetName) {
        FactionPlayer target = getFactionPlayer(targetName);
        FactionPlayer sender = getFactionPlayer(senderPlayer.getUniqueId());

        if (target == null || sender == null) {
            return new RankChangeResult(RankChangeResultType.PLAYER_NOT_FOUND, null);
        }

        if (!sender.getFaction().equals(target.getFaction())) {
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
            UUID targetPlayerId = target.getPlayerId();

            factionPlayerManager.updateRank(targetPlayerId, previousRank);
            factionPlayerManager.save(target);
            mySQLProvider.getRanksDAO().setRank(targetPlayerId, previousRank);

            // Send update to redis
            redisProvider.updateRank(targetPlayerId, previousRank);
            return new RankChangeResult(RankChangeResultType.SUCCESS, previousRank);
        }

        return new RankChangeResult(RankChangeResultType.CANNOT_DEMOTE, null);
    }

    public JoinResult join(Player player, String factionName) {
        if (factionName == null) {
            return new JoinResult(JoinResultState.NULL_NAME, null, null);
        }

        UUID playerId = player.getUniqueId();
        FactionPlayer factionPlayer = factionPlayerManager.getOrLoad(playerId);
        Faction faction = factionPlayer.getFaction();

        if (faction != null) {
            return new JoinResult(JoinResultState.ALREADY_HAVE_FACTION, faction, factionPlayer);
        }

        faction = factionManager.getFaction(factionName);

        if (faction != null) {
            UUID factionId = faction.getId();
            if (faction.isOpen() || faction.isInvited(player)) {
                // Update Faction
                factionPlayerManager.updateFaction(playerId, faction);
                factionPlayerManager.updateRank(playerId, Rank.RECRUIT);
                factionPlayerManager.save(factionPlayer);

                // Update Player
                factionManager.addPlayer(factionName, playerId);
                factionManager.uninvitePlayerFromFaction(factionName, playerId);
                factionManager.saveFactionToDatabase(faction);

                // Update Rank/Members
                mySQLProvider.getRanksDAO().setRank(playerId, Rank.RECRUIT);
                mySQLProvider.getMemberDAO().addMember(factionId,
                        playerId);

                // Send update to redis
                redisProvider.updateFaction(playerId, faction.getName());
                redisProvider.updateRank(playerId, Rank.RECRUIT);
                redisProvider.addPlayer(factionId, playerId);
                redisProvider.uninvite(factionId, playerId);
                return new JoinResult(JoinResultState.SUCCESS, faction, factionPlayer);
            } else {
                return new JoinResult(JoinResultState.NOT_INVITED, faction, factionPlayer);
            }
        } else {
            return new JoinResult(JoinResultState.NO_FACTION, faction, factionPlayer);
        }
    }

    public KickResult kick(String playerName) {
        return kick(null, playerName);
    }

    public KickResult kick(Player kicker, String playerName) {
        // Check if kicker is provided, otherwise skip faction and rank checks
        Faction faction = null;
        FactionPlayer kickerFactionPlayer = null;

        if (kicker != null) {
            kickerFactionPlayer = factionPlayerManager.getOrLoad(kicker.getUniqueId());
            if (kickerFactionPlayer == null || kickerFactionPlayer.getFaction() == null) {
                return new KickResult(KickResultType.NOT_IN_FACTION, null, null);
            }
            faction = kickerFactionPlayer.getFaction();

            // Check if the kicker has the required rank to kick (MODERATOR or higher)
            if (kickerFactionPlayer.getRank().isLowerThan(Rank.MODERATOR)) {
                return new KickResult(KickResultType.NOT_MODERATOR, faction, null);
            }
        }

        // Retrieve the player to kick
        FactionPlayer playerToKick = factionPlayerManager.getOrLoad(playerName);

        // Check if player to kick exists and has a faction
        if (playerToKick == null) {
            return new KickResult(KickResultType.PLAYER_NOT_FOUND, faction, null);
        }
        if (playerToKick.getFaction() == null) {
            return new KickResult(KickResultType.NO_FACTION, faction, playerToKick);
        }

        // Ensure the faction of kicker (if present) and player to kick are the same
        if (kicker != null && (faction == null || !faction.equals(playerToKick.getFaction()))) {
            return new KickResult(KickResultType.PLAYER_NOT_FOUND, faction, playerToKick);
        }

        faction = playerToKick.getFaction();

        // Check if the player to be kicked is the leader of the faction
        if (playerToKick.getRank().isEqualOrHigherThan(Rank.LEADER)) {
            return new KickResult(KickResultType.FACTION_OWNER, faction, playerToKick);
        }

        // Check if kicker is trying to kick themselves
        if (kickerFactionPlayer != null && kickerFactionPlayer.equals(playerToKick)) {
            return new KickResult(KickResultType.NOT_YOURSELF, faction, playerToKick);
        }

        // Check if the player to be kicked has a rank equal or higher than the kicker
        if (kickerFactionPlayer != null && playerToKick.getRank().isEqualOrHigherThan(kickerFactionPlayer.getRank())) {
            return new KickResult(KickResultType.SUPERIOR_RANK, faction, playerToKick);
        }

        String factionName = faction.getName();
        UUID playerToKickId = playerToKick.getPlayerId();
        // Remove player from faction
        factionManager.removePlayer(factionName, playerToKickId);
        factionManager.saveFactionToDatabase(faction);
        // Remove faction from player
        factionPlayerManager.updateFaction(playerToKickId, null);
        factionPlayerManager.save(playerToKick);
        mySQLProvider.getMemberDAO().removeMember(faction.getId(), playerToKickId);
        // Send update to redis
        redisProvider.removePlayer(faction.getId(), playerToKick.getPlayerId());
        redisProvider.updateFaction(playerToKick.getPlayerId(), null);

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
            e.printStackTrace();
            return new OpenChestResult(OpenChestResultType.ERROR, faction, factionPlayer);
        }
    }

    public FocusResult focus(Player player, String factionName) {
        FactionPlayer factionPlayer = factionPlayerManager.getOrLoad(player.getUniqueId());
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
        redisProvider.focus(faction.getId(), targetFaction.getId());

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
        boolean withdrawn = factionManager.withdraw(faction.getName(), amount);
        if (!withdrawn)
            return new WithdrawResult(WithdrawResultType.ERROR, 0);
        boolean deposited = economy.depositPlayer(player, amount).type == EconomyResponse.ResponseType.SUCCESS;
        if (!deposited) {
            factionManager.deposit(faction.getName(), amount);
            return new WithdrawResult(WithdrawResultType.ERROR, 0);
        }
        // Save to database
        factionManager.saveFactionToDatabase(faction);
        redisProvider.withdraw(faction.getId(), amount);
        return new WithdrawResult(WithdrawResultType.SUCCESS, amount); // Withdrawal successful
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
        boolean withdrawn = economy.withdrawPlayer(player, amount).type == EconomyResponse.ResponseType.SUCCESS;
        if (!withdrawn)
            return new DepositResult(DepositResultType.ERROR, 0);
        boolean deposited = factionManager.deposit(faction.getName(), amount);
        if (!deposited) {
            economy.depositPlayer(player, amount);
            return new DepositResult(DepositResultType.ERROR, 0);
        }
        // Save to database
        factionManager.saveFactionToDatabase(faction);
        redisProvider.deposit(faction.getId(), amount);
        return new DepositResult(DepositResultType.SUCCESS, amount); // Deposit successful
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

    public AddEventsWonResult addEvenstsWon(Player player) {
        FactionPlayer factionPlayer = getFactionPlayer(player);

        // Ensure player exist in the system
        if (factionPlayer == null) {
            return new AddEventsWonResult(AddEventsWonResultType.PLAYER_NOT_FOUND);
        }

        Faction faction = factionPlayer.getFaction();

        if (faction == null) {
            return new AddEventsWonResult(AddEventsWonResultType.NO_FACTION);
        }

        // Add event won
        faction.addEventsWon();
        return new AddEventsWonResult(AddEventsWonResultType.SUCCESS);
    }

    public int getEventsWon(Player player) {
        FactionPlayer factionPlayer = getFactionPlayer(player);

        if (factionPlayer == null) {
            return 0;
        }

        Faction faction = factionPlayer.getFaction();

        if (faction == null) {
            return 0;
        }

        return faction.getEventsWon();
    }

    public ClanEvent getCurrentEvent() {
        return MineClans.getInstance().getClanEventScheduler().getEvent();
    }

    public DiscordResult setDiscord(Player player, String discordLink) {
        FactionPlayer factionPlayer = factionPlayerManager.getOrLoad(player.getUniqueId());
        Faction playerFaction = factionPlayer.getFaction();

        if (playerFaction == null) {
            return new DiscordResult(DiscordResult.DiscordResultState.NO_FACTION);
        }

        if (factionPlayer.getRank().isLowerThan(Rank.LEADER)) {
            return new DiscordResult(DiscordResult.DiscordResultState.NO_PERMISSION);
        }

        // Updated Regex for a valid Discord invite link
        String discordLinkPattern = "^(https?://)?(www\\.)?(discord\\.gg|discord\\.com/invite)/[a-zA-Z0-9]{1,16}$";
        Pattern pattern = Pattern.compile(discordLinkPattern);

        // Check if the discordLink is null, empty, or doesn't match the pattern
        if (discordLink == null || discordLink.isEmpty() || !pattern.matcher(discordLink).matches()) {
            if (playerFaction.setDiscord(null)) {
                factionManager.saveFactionToDatabase(playerFaction);
            }
            return new DiscordResult(DiscordResult.DiscordResultState.INVALID_DISCORD_LINK);
        }

        try {
            if (playerFaction.setDiscord(discordLink)) {
                factionManager.saveFactionToDatabase(playerFaction);
            }
            return new DiscordResult(DiscordResult.DiscordResultState.SUCCESS, playerFaction);
        } catch (IllegalArgumentException ex) {
            return new DiscordResult(DiscordResult.DiscordResultState.ERROR, playerFaction);
        }
    }

    public AnnouncementResult setAnnouncement(Player player, String announcement) {
        FactionPlayer factionPlayer = factionPlayerManager.getOrLoad(player.getUniqueId());
        Faction playerFaction = factionPlayer.getFaction();

        if (playerFaction == null) {
            return new AnnouncementResult(AnnouncementResult.AnnouncementResultState.NO_FACTION);
        }

        if (factionPlayer.getRank().isLowerThan(Rank.LEADER)) {
            return new AnnouncementResult(AnnouncementResult.AnnouncementResultState.NO_PERMISSION);
        }

        // Check if the discordLink is null, empty, or doesn't match the pattern
        if (announcement == null || announcement.isEmpty()) {
            if (playerFaction.setAnnouncement(null)) {
                factionManager.saveFactionToDatabase(playerFaction);
                redisProvider.setAnnouncement(playerFaction.getId(), null);
            }
            return new AnnouncementResult(AnnouncementResult.AnnouncementResultState.NO_ANNOUNCEMENT);
        }

        try {
            if (playerFaction.setAnnouncement(announcement)) {
                factionManager.saveFactionToDatabase(playerFaction);
                redisProvider.setAnnouncement(playerFaction.getId(), announcement);
            }
            return new AnnouncementResult(AnnouncementResult.AnnouncementResultState.SUCCESS, playerFaction);
        } catch (IllegalArgumentException ex) {
            return new AnnouncementResult(AnnouncementResult.AnnouncementResultState.ERROR, playerFaction);
        }
    }

    public OpenResult toggleOpen(Player player) {
        // Retrieve the FactionPlayer instance for the given player
        FactionPlayer factionPlayer = factionPlayerManager.getOrLoad(player.getUniqueId());

        // Retrieve the Faction instance for the player's faction
        Faction faction = factionPlayer.getFaction();

        // Check if the player is part of a faction
        if (faction == null) {
            return new OpenResult(OpenResult.OpenResultState.NO_FACTION);
        }

        // Check if the player has sufficient rank to toggle the open state
        if (factionPlayer.getRank().isLowerThan(Rank.COLEADER)) {
            return new OpenResult(OpenResult.OpenResultState.NO_PERMISSION);
        }

        // Retrieve the current open state of the faction
        boolean currentlyOpen = faction.isOpen();

        // Toggle the open state
        boolean newOpenState = !currentlyOpen;
        faction.setOpen(newOpenState);

        // Save changes to the faction
        factionManager.saveFactionToDatabase(faction);

        // Return the result indicating the new state of the faction
        return new OpenResult(OpenResult.OpenResultState.SUCCESS, faction, newOpenState);
    }

    public AddDeathResult addDeath(Player player) {
        FactionPlayer factionPlayer = factionPlayerManager.getOrLoad(player.getUniqueId());

        if (factionPlayer == null) {
            return new AddDeathResult(AddDeathResult.AddDeathResultState.NO_PLAYER, factionPlayer);
        }

        try {
            factionPlayer.setDeaths(factionPlayer.getDeaths() + 1);
            factionPlayerManager.save(factionPlayer);
            return new AddDeathResult(AddDeathResult.AddDeathResultState.SUCCESS, factionPlayer);
        } catch (Exception e) {
            return new AddDeathResult(AddDeathResult.AddDeathResultState.ERROR, factionPlayer);
        }
    }

    public boolean startChestUpdate(Faction faction) {
        if (faction.isEditingChest())
            return false;
        faction.setEditingChest(true);
        faction.setReceivedSubDuringUpdate(false);
        redisProvider.startChestUpdate(faction);
        if (faction.isReceivedSubDuringUpdate())
            return false;
        return true;
    }

    public void endChestUpdate(Faction faction, boolean updateChestContent) {
        if (updateChestContent) {
            // Save chest data
            mySQLProvider.getChestDAO().saveFactionChest(faction.getId(), faction.getInventory());
        }
        redisProvider.endChestUpdate(faction, updateChestContent);
        faction.setEditingChest(false);
    }
}
