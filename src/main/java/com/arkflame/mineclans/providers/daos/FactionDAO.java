package com.arkflame.mineclans.providers.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Location;

import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.ResultSetProcessor;
import com.arkflame.mineclans.utils.LocationUtil;

public class FactionDAO {
    private MySQLProvider mySQLProvider;

    public FactionDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS mineclans_factions (" +
                "faction_id UUID PRIMARY KEY," +
                "owner_id UUID NOT NULL," +
                "display_name VARCHAR(64) NOT NULL," +
                "home VARCHAR(255)," +
                "name VARCHAR(16) UNIQUE," +
                "balance DOUBLE," +
                "kills INT," +
                "friendly_fire BOOLEAN)");
    }

    public void removeFactionByName(String name) {
        String query = "DELETE FROM mineclans_factions WHERE name = ?";
        mySQLProvider.executeUpdateQuery(query, name);
    }

    public void insertOrUpdateFaction(Faction faction) {
        String query = "INSERT INTO mineclans_factions (faction_id, owner_id, display_name, home, name, balance, kills, friendly_fire) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "owner_id = VALUES(owner_id), " +
                "display_name = VALUES(display_name), " +
                "home = VALUES(home), " +
                "name = VALUES(name), " +
                "balance = VALUES(balance), " +
                "kills = VALUES(kills), " +
                "friendly_fire = VALUES(friendly_fire)";
        mySQLProvider.executeUpdateQuery(query,
                faction.getId(),
                faction.getOwner(),
                faction.getDisplayName(),
                faction.getHomeString(),
                faction.getName(),
                faction.getBalance(),
                faction.getKills(),
                faction.isFriendlyFire());
    }

    public void removeFaction(UUID factionId) {
        String query = "DELETE FROM mineclans_factions WHERE faction_id = ?";
        mySQLProvider.executeUpdateQuery(query, factionId);
    }

    public void disbandFaction(Faction faction) {
        mySQLProvider.getMemberDAO().removeMembers(faction.getId());
        mySQLProvider.getInvitedDAO().removeInvitedMembers(faction.getId());
        mySQLProvider.getRelationsDAO().removeRelationsById(faction.getId());
        removeFaction(faction.getId());
    }

    private Faction extractFactionFromResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            UUID id = UUID.fromString(resultSet.getString("faction_id"));
            UUID ownerId = UUID.fromString(resultSet.getString("owner_id"));
            String displayName = resultSet.getString("display_name");
            // Fetch additional faction properties
            Location home = LocationUtil.parseLocation(resultSet.getString("home"));
            double balance = resultSet.getDouble("balance");
            int kills = resultSet.getInt("kills");
            boolean friendlyFire = resultSet.getBoolean("friendly_fire");
            // Name
            String name = resultSet.getString("name");

            // Create a Faction object and set additional properties
            Faction faction = new Faction(id, ownerId, name, displayName);
            faction.setHome(home);
            faction.setBalance(balance);
            faction.setFriendlyFire(friendlyFire);

            // Load other faction stuff
            faction.setMembers(mySQLProvider.getMemberDAO().getMembers(id));
            faction.setInvited(mySQLProvider.getInvitedDAO().getInvitedMembers(id));
            faction.setRelations(mySQLProvider.getRelationsDAO().getRelationsByFactionId(id));
            faction.setRanks(mySQLProvider.getRanksDAO().getAllRanks());

            // Load Chest
            faction.setChest(mySQLProvider.getChestDAO().loadFactionChest(faction));

            // Load Kills
            faction.setKills(kills);

            return faction;
        }
        return null;
    }

    public Faction getFactionById(UUID factionId) {
        AtomicReference<Faction> faction = new AtomicReference<>(null);
        String factionQuery = "SELECT faction_id, name, owner_id, display_name, home, balance, kills, friendly_fire FROM mineclans_factions WHERE faction_id = ?";
        mySQLProvider.executeSelectQuery(factionQuery, new ResultSetProcessor() {
            public void run(ResultSet resultSet) throws SQLException {
                faction.set(extractFactionFromResultSet(resultSet));
            };
        }, factionId.toString());
        return faction.get();
    }

    public Faction getFactionByName(String name) {
        AtomicReference<Faction> faction = new AtomicReference<>(null);
        String query = "SELECT faction_id, name, owner_id, display_name, home, balance, kills, friendly_fire FROM mineclans_factions WHERE name = ?";
        mySQLProvider.executeSelectQuery(query, new ResultSetProcessor() {
            public void run(ResultSet resultSet) throws SQLException {
                faction.set(extractFactionFromResultSet(resultSet));
            };
        }, name);
        return faction.get();
    }
}
