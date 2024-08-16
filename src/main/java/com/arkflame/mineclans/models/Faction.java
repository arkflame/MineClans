package com.arkflame.mineclans.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.buff.ActiveBuff;
import com.arkflame.mineclans.enums.Rank;
import com.arkflame.mineclans.enums.RelationType;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.utils.FactionNamingUtil;
import com.arkflame.mineclans.utils.LocationUtil;

import net.md_5.bungee.api.ChatColor;

import java.util.Map;
import java.util.HashSet;
import java.util.Iterator;

public class Faction implements InventoryHolder {
    // The ID
    private final UUID id;

    // Members
    private Collection<UUID> members = ConcurrentHashMap.newKeySet();

    // Owner of the faction
    private UUID owner;

    // Invited members
    private Collection<UUID> invited = ConcurrentHashMap.newKeySet();

    // Friendly fire
    private boolean friendlyFire = false;

    // Faction Home
    private Location home;

    // Display name
    private String displayName;

    // Faction name
    private String name;

    // Faction balance (economy integration)
    private double balance;

    // Faction relations
    private Map<UUID, Relation> relations = new ConcurrentHashMap<>(); // UUID -> Relation

    // Faction chest permissions
    private Map<String, Boolean> chestPermissions = new ConcurrentHashMap<>(); // Role -> permission

    // Ranks
    private Map<UUID, Rank> ranks = new ConcurrentHashMap<>(); // UUID -> rank

    // Chest Inventory
    private Inventory chestInventory;

    // Focused Faction
    private UUID focusedFaction = null;

    // Kills
    private int kills = 0;
    private Collection<UUID> killedPlayers = new HashSet<>();

    // Events Won
    private int eventsWon = 0;

    // Power
    private double power = 0;

    // Active Buffs
    private Collection<ActiveBuff> activeBuffs = ConcurrentHashMap.newKeySet();

    private boolean open = false;

    private Date creationDate = new Date();

    private String announcement = null;

    private String discord = null;

    // Constructor
    public Faction(UUID id, UUID owner, String name, String displayName) {
        this.id = id;
        this.owner = owner;
        setName(name);
        setDisplayName(displayName);
    }

    public Collection<UUID> getMembers() {
        return members;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Collection<UUID> getInvited() {
        return invited;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        String strippedName = ChatColor.stripColor(displayName).toLowerCase().trim();
        if (!strippedName.equals(name.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Invalid faction displayname: " + strippedName + " - " + name.toLowerCase());
        }
        if (displayName.length() < 3 || displayName.length() > 32) {
            throw new IllegalArgumentException("Invalid faction name");
        }
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        FactionNamingUtil.checkName(name);
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        updatePower();
    }

    public Map<UUID, Relation> getRelations() {
        return relations;
    }

    public void setRelation(UUID factionId, Relation relation) {
        this.relations.put(factionId, relation);
    }

    public void setRelations(Collection<Relation> relationsByFactionId) {
        for (Relation relation : relationsByFactionId) {
            this.relations.put(relation.getTargetFactionId(), relation);
        }
    }

    public Relation getRelation(UUID otherFactionId) {
        return relations.get(otherFactionId);
    }

    public RelationType getRelationType(UUID otherFactionId) {
        Relation relation = relations.get(otherFactionId);

        if (relation != null) {
            return relation.getRelationType();
        }

        return RelationType.NEUTRAL;
    }

    public Map<String, Boolean> getChestPermissions() {
        return chestPermissions;
    }

    public void setChestPermission(String role, boolean permission) {
        this.chestPermissions.put(role, permission);
    }

    public Map<UUID, Rank> getRanks() {
        return ranks;
    }

    public Rank getRank(UUID playerId) {
        return ranks.getOrDefault(playerId, Rank.MEMBER);
    }

    public void setRank(UUID member, Rank rank) {
        this.ranks.put(member, rank);
    }

    public void invitePlayer(UUID id) {
        this.invited.add(id);
    }

    public void uninvitePlayer(UUID id) {
        this.invited.remove(id);
    }

    public void addMember(UUID member) {
        this.members.add(member);
        updatePower();
    }

    public void removeMember(UUID member) {
        this.members.remove(member);
    }

    public void setMembers(Collection<UUID> members) {
        this.members = members;
    }

    public void setInvited(Collection<UUID> invited) {
        this.invited = invited;
    }

    public void disbandFaction() {
        this.members.clear();
        this.invited.clear();
        this.relations.clear();
        this.chestPermissions.clear();
        this.ranks.clear();
        this.balance = 0;
    }

    public UUID getId() {
        return id;
    }

    public String getHomeString() {
        return LocationUtil.locationToString(home);
    }

    public Collection<UUID> getOnlineMembers() {
        Collection<UUID> onlineMembers = new ArrayList<>();
        for (UUID memberId : members) {
            Player player = Bukkit.getPlayer(memberId);
            if (player != null && player.isOnline()) {
                onlineMembers.add(memberId);
            }
        }
        return onlineMembers;
    }

    public void setRanks(Map<UUID, Rank> ranks) {
        this.ranks = ranks;
    }

    public Inventory getChest() {
        if (chestInventory == null) {
            chestInventory = Bukkit.createInventory(this, 27, "Faction Chest NULL");
        }

        return chestInventory;
    }

    public void setChest(Inventory chestInventory) {
        this.chestInventory = chestInventory;
    }

    public UUID getFocusedFaction() {
        return focusedFaction;
    }

    public void setFocusedFaction(UUID focusedFaction) {
        this.focusedFaction = focusedFaction;
    }

    public int getKills() {
        return kills;
    }

    public boolean addKill(UUID killedPlayerId) {
        // If player is already killed by this player, return false
        if (killedPlayers.contains(killedPlayerId)) {
            return false;
        }

        killedPlayers.add(killedPlayerId);
        setKills(kills + 1);

        return true;
    }

    public void setKills(int kills) {
        this.kills = kills;
        updatePower();
    }

    public int getEventsWon() {
        return eventsWon;
    }

    public void setEventsWon(int eventsWon) {
        this.eventsWon = eventsWon;
        updatePower();
    }

    public void addEventsWon() {
        setEventsWon(eventsWon + 1);
    }

    private double calculatePower() {
        ConfigWrapper config = MineClans.getInstance().getCfg();
        double killsWeight = config.getDouble("weights.kill");
        double moneyWeight = config.getDouble("weights.money");
        double memberCountWeight = config.getDouble("weights.member_count");
        double eventsWonWeight = config.getDouble("weights.events_won");

        double killsPower = kills * killsWeight;
        double moneyPower = balance * moneyWeight;
        double memberCountPower = members.size() * memberCountWeight;
        double eventsWonPower = eventsWon * eventsWonWeight;

        double power = killsPower + moneyPower + memberCountPower + eventsWonPower;
        return power;
    }

    @Override
    public Inventory getInventory() {
        return chestInventory;
    }

    public double getPower() {
        return this.power;
    }

    public void updatePower() {
        double newPower = calculatePower();
        if (this.power != newPower) {
            this.power = newPower;
        }
    }

    public void giveEffects(Player player) {
        Iterator<ActiveBuff> iterator = activeBuffs.iterator();
        while (iterator.hasNext()) {
            ActiveBuff buff = iterator.next();
            if (buff.isActive()) {
                buff.giveEffectToPlayer(player);
            } else {
                iterator.remove();
            }
        }
    }

    public void removeEffects(Player player) {
        Iterator<ActiveBuff> iterator = activeBuffs.iterator();
        while (iterator.hasNext()) {
            ActiveBuff buff = iterator.next();
            if (buff.isActive()) {
                buff.removeEffectFromPlayer(player);
            } else {
                iterator.remove();
            }
        }
    }

    public ActiveBuff addBuff(ActiveBuff buff) {
        activeBuffs.add(buff);
        return buff;
    }

    public ActiveBuff removeBuff(ActiveBuff buff) {
        activeBuffs.remove(buff);
        return buff;
    }

    public Collection<ActiveBuff> getBuffs() {
        return activeBuffs;
    }

    public boolean isOpen() {
        return open;
    }

    public String getCreationDate() {
        return creationDate.toString();
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public void setDiscord(String discord) {
        this.discord = discord;
    }

    public String getOwnerName() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(getOwner());
        if (offlinePlayer == null) {
            return "null";
        }
        return offlinePlayer.getName();
    }

    public String getDiscord() {
        return discord;
    }
}
