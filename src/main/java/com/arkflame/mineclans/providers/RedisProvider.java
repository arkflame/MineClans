package com.arkflame.mineclans.providers;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.enums.Rank;
import com.arkflame.mineclans.enums.RelationType;
import com.arkflame.mineclans.managers.FactionManager;
import com.arkflame.mineclans.managers.FactionPlayerManager;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.models.FactionPlayer;

import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RedisProvider {
    private final Logger logger;
    private final FactionManager factionManager;
    private final FactionPlayerManager factionPlayerManager;
    private final Configuration config;
    private JedisPool jedisPool;
    private String channelName;
    private final String instanceId;
    private boolean shutdown = false;

    public RedisProvider(FactionManager factionManager, FactionPlayerManager factionPlayerManager, Configuration config,
            Logger logger) {
        validateInputs(factionManager, config);

        this.factionManager = factionManager;
        this.factionPlayerManager = factionPlayerManager;
        this.instanceId = UUID.randomUUID().toString();
        this.config = config;
        this.logger = logger;

        if (config.getBoolean("redis.enabled", false)) {
            new Thread(this::subscribeToFactionUpdates).start();
        } else {
            this.jedisPool = null;
            this.channelName = null;
            logger.info("Redis is disabled in configuration.");
        }
    }

    private void validateInputs(FactionManager factionManager, Configuration config) {
        if (factionManager == null || config == null) {
            throw new IllegalArgumentException("FactionManager and Configuration cannot be null");
        }
    }

    // Call this method once to initialize and open the pool
    public void initializeRedis() {
        if (config.getBoolean("redis.enabled", false)) {
            if (jedisPool == null || jedisPool.isClosed()) {
                JedisPoolConfig poolConfig = new JedisPoolConfig();
                poolConfig.setMaxTotal(128);
                poolConfig.setMaxIdle(64);
                poolConfig.setMinIdle(16);
                poolConfig.setTestOnBorrow(true);
                poolConfig.setTestOnReturn(true);

                jedisPool = new JedisPool(config.getString("redis.host"), config.getInt("redis.port"));
            }
        }
    }

    public Jedis getResource() {
        Jedis jedis = jedisPool.getResource();
        if (config.getBoolean("redis.auth.enabled")) {
            jedis.auth(config.getString("redis.auth.password", ""));
        }
        return jedis;
    }

    private void subscribeToFactionUpdates() {
        while (!shutdown) {
            try {
                try {
                    shutdown();
                    shutdown = false;
                    initializeRedis();

                    this.channelName = config.getString("redis.channel", "mineclansUpdates");

                    try (Jedis sub = getResource()) {
                        sub.subscribe(new FactionMessageSubscriber(), channelName);
                    }
                } catch (Exception e) {
                    if (!shutdown) {
                        logger.log(Level.SEVERE, "Cannot connect to redis. Please check your configuration files.", e);
                    }
                }
                Thread.sleep(1000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class FactionMessageSubscriber extends JedisPubSub {
        @Override
        public void onMessage(String channel, String message) {
            if (channel.equals(channelName) && !message.startsWith(instanceId)) {
                processMessage(message.substring(instanceId.length() + 1));
            }
        }
    }

    private void processMessage(String message) {
        String[] parts = message.split(":");
        String action = parts[0];
        if (parts.length < 2)
            return;
        parts = Arrays.copyOfRange(parts, 1, parts.length);

        switch (action) {
            case "faction":
                processFactionUpdate(parts);
                break;
            case "player":
                processPlayerUpdate(parts);
                break;
            default:
                logger.warning("Unknown message type: " + parts[0]);
        }
    }

    private void processFactionUpdate(String[] parts) {
        UUID factionId = parseUUID(parts[1]);
        if (parts[0].equalsIgnoreCase("createFaction")) {
            UUID playerId = parseUUID(parts[2]);
            String factionName = parts[3];
            factionManager.createFaction(playerId, factionName, factionId);
            return;
        }
        Faction faction = factionManager.getFaction(factionId);
        if (faction == null)
            return;
        String factionName = faction.getName();

        switch (parts[0]) {
            case "deposit":
            case "withdraw":
                double amount = parseDouble(parts[2]);
                updateFactionBalance(faction, amount, parts[0].equals("deposit"));
                break;
            case "updateHome":
                factionManager.updateHome(factionName, parseLocation(parts, 2));
                break;
            case "updateFriendlyFire":
                factionManager.updateFriendlyFire(factionName, Boolean.parseBoolean(parts[2]));
                break;
            case "invite":
                factionManager.invitePlayerToFaction(factionName, parseUUID(parts[2]));
                break;
            case "focus":
                faction.setFocusedFaction(parseUUID(parts[2]));
                break;
            case "announcement":
                faction.setAnnouncement(parts.length > 2 ? parts[2] : null);
                break;
            case "removePlayer":
                factionManager.removePlayer(factionName, parseUUID(parts[2]));
                break;
            case "addPlayer":
                factionManager.addPlayer(factionName, parseUUID(parts[2]));
                break;
            case "startChestUpdate":
                faction.setEditingChest(true);
                faction.setReceivedSubDuringUpdate(true);
                break;
            case "endChestUpdate":
                MineClans.runAsync(() -> {
                    try {
                        boolean updateChest = parts.length > 2 ? parts[2].equals("true") : false;
                        if (updateChest) {
                            faction.setChest(
                                    MineClans.getInstance().getMySQLProvider().getChestDAO().loadFactionChest(faction));
                        }
                    } finally {
                        faction.setEditingChest(false);
                    }
                });
                break;
            case "updateRelation":
                UUID otherFactionId = parseUUID(parts[2]);
                String relationName = parts[3];
                factionManager.updateFactionRelation(factionName, otherFactionId, relationName);
                break;
            case "updateDisplayName":
                String displayName = parts[2];
                factionManager.updateFactionDisplayName(factionName, displayName);
                break;
            case "updateName":
                String name = parts[2];
                factionManager.updateFactionDisplayName(factionName, name);
                break;
            case "updateFactionOwner":
                UUID newOwnerId = parseUUID(parts[2]);
                factionManager.updateFactionOwner(factionName, newOwnerId);
                break;
            case "removeFaction":
                factionManager.disbandFaction(faction.getName());
                factionManager.removeFactionFromDatabase(faction);
                MineClans.getInstance().getLeaderboardManager().removeFaction(faction.getId());
                break;
            case "sendFactionMessage":
                factionManager.sendFactionMessage(faction, parts[2]);
                break;
            case "sendAllianceMessage":
                factionManager.sendAllianceMessage(faction, parts[2]);
                break;
            default:
                logger.warning("Unsupported faction action: " + parts[0]);
        }
    }

    private void updateFactionBalance(Faction faction, double amount, boolean isDeposit) {
        factionManager.setFactionBalance(
                faction.getName(),
                faction.getBalance() + (isDeposit ? amount : -amount));
    }

    private void processPlayerUpdate(String[] parts) {
        UUID playerId = parseUUID(parts[1]);
        FactionPlayer player = factionPlayerManager.getOrLoad(playerId);
        if (player == null)
            return;

        switch (parts[0]) {
            case "updateFaction":
                factionPlayerManager.updateFaction(playerId, factionManager.getFaction(parts[2]));
                break;
            case "updateRank":
                factionPlayerManager.updateRank(playerId, Rank.valueOf(parts[2]));
                break;
            default:
                logger.warning("Unsupported player action: " + parts[0]);
                break;
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Failed to parse double: " + value, e);
            return 0;
        }
    }

    private UUID parseUUID(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Failed to parse UUID: " + value, e);
            return null;
        }
    }

    private Location parseLocation(String[] parts, int startIndex) {
        try {
            return new Location(null, Double.parseDouble(parts[startIndex]), Double.parseDouble(parts[startIndex + 1]),
                    Double.parseDouble(parts[startIndex + 2]));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to parse location", e);
            return null;
        }
    }

    public void publishUpdate(String actionType, UUID id, String action, String... params) {
        try (Jedis pub = getResource()) {
            if (id == null || action == null)
                return;
            StringBuilder message = new StringBuilder(instanceId).append(":").append(actionType).append(":")
                    .append(action).append(":").append(id);
            for (String param : params)
                message.append(":").append(param);
            pub.publish(channelName, message.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void shutdown() {
        this.shutdown = true;
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    public void setAnnouncement(UUID factionId, String announcement) {
        publishUpdate("faction", factionId, "announcement", announcement != null ? announcement : "");
    }

    public void focus(UUID factionId, UUID factionFocus) {
        publishUpdate("faction", factionId, "focus", factionFocus.toString());
    }

    public void deposit(UUID factionId, double amount) {
        publishUpdate("faction", factionId, "deposit", String.valueOf(amount));
    }

    public void withdraw(UUID factionId, double amount) {
        publishUpdate("faction", factionId, "withdraw", String.valueOf(amount));
    }

    public void updateHome(UUID factionId, Location home) {
        publishUpdate("faction", factionId, "updateHome",
                String.valueOf(home.getX()), String.valueOf(home.getY()), String.valueOf(home.getZ()));
    }

    public void updateFriendlyFire(UUID factionId, boolean friendlyFire) {
        publishUpdate("faction", factionId, "updateFriendlyFire", String.valueOf(friendlyFire));
    }

    public void invite(UUID factionId, UUID playerId) {
        publishUpdate("faction", factionId, "invite", playerId.toString());
    }

    public void uninvite(UUID factionId, UUID playerId) {
        publishUpdate("faction", factionId, "uninvite", playerId.toString());
    }

    public void removePlayer(UUID factionId, String playerName) {
        publishUpdate("faction", factionId, "removePlayer", playerName);
    }

    public void addPlayer(UUID factionId, String playerName) {
        publishUpdate("faction", factionId, "addPlayer", playerName);
    }

    public void startChestUpdate(Faction faction) {
        publishUpdate("faction", faction.getId(), "startChestUpdate");
    }

    public void endChestUpdate(Faction faction, boolean updateChestContent) {
        publishUpdate("faction", faction.getId(), "endChestUpdate", String.valueOf(updateChestContent));
    }

    public void updateRelation(UUID factionId, UUID otherFactionId, RelationType relationType) {
        publishUpdate("faction", factionId, "updateRelation", String.valueOf(otherFactionId), relationType.name());
    }

    public void updateDisplayName(UUID factionId, String displayName) {
        publishUpdate("faction", factionId, "updateDisplayName", displayName);
    }

    public void updateName(UUID factionId, String name) {
        publishUpdate("faction", factionId, "updateName", name);
    }

    public void updateFactionOwner(UUID factionId, UUID newOwnerId) {
        publishUpdate("faction", factionId, "updateFactionOwner", newOwnerId.toString());
    }

    public void removeFaction(UUID id) {
        publishUpdate("faction", id, "removeFaction");
    }

    public void createFaction(UUID id, UUID playerId, String factionName) {
        publishUpdate("faction", id, "createFaction", playerId.toString(), factionName);
    }

    public void sendFactionMessage(UUID id, String message) {
        publishUpdate("faction", id, "sendFactionMessage", message);
    }

    public void sendAllianceMessage(UUID id, String message) {
        publishUpdate("faction", id, "sendAllianceMessage", message);
    }

    public void updateFaction(UUID playerId, String factionName) {
        publishUpdate("player", playerId, "updateFaction", factionName);
    }

    public void updateRank(UUID playerId, Rank rank) {
        publishUpdate("player", playerId, "updateRank", rank.name());
    }
}
