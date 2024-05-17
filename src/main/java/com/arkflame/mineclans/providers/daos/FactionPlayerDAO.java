package com.arkflame.mineclans.providers.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.FactionPlayer;
import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.ResultSetProcessor;

public class FactionPlayerDAO {
    private MySQLProvider mySQLProvider;

    public FactionPlayerDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS faction_players ("
                + "player_id UUID NOT NULL PRIMARY KEY,"
                + "faction_name VARCHAR(255),"
                + "join_date TIMESTAMP,"
                + "last_active TIMESTAMP,"
                + "kills INT DEFAULT 0,"
                + "deaths INT DEFAULT 0,"
                + "name VARCHAR(255),"
                + "FOREIGN KEY (faction_name) REFERENCES factions(name) ON DELETE SET NULL"
                + ")");
    }

    public void insertOrUpdatePlayer(FactionPlayer player) {
        mySQLProvider.executeUpdateQuery(
                "INSERT INTO faction_players (player_id, faction_name, join_date, last_active, kills, deaths, name) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "faction_name = VALUES(faction_name), join_date = VALUES(join_date), last_active = VALUES(last_active), "
                        + "kills = VALUES(kills), deaths = VALUES(deaths), name = VALUES(name)",
                player.getPlayerId(),
                player.getFactionName(),
                player.getJoinDate(),
                player.getLastActive(),
                player.getKills(),
                player.getDeaths(),
                player.getName());
    }

    public FactionPlayer getPlayerById(UUID playerId) {
        AtomicReference<FactionPlayer> player = new AtomicReference<>(null);
        mySQLProvider.executeSelectQuery("SELECT * FROM faction_players WHERE player_id = ?",
                new ResultSetProcessor() {
                    @Override
                    public void run(ResultSet resultSet) throws SQLException {
                        if (resultSet != null && resultSet.next()) {
                            player.set(extractPlayerFromResultSet(resultSet));
                        }
                    }
                }, playerId.toString());
        return player.get();
    }

    public FactionPlayer getPlayerByName(String name) {
        AtomicReference<FactionPlayer> player = new AtomicReference<>(null);
        mySQLProvider.executeSelectQuery("SELECT * FROM faction_players WHERE name = ?",
                new ResultSetProcessor() {
                    public void run(ResultSet resultSet) throws SQLException {
                        if (resultSet != null && resultSet.next()) {
                            player.set(extractPlayerFromResultSet(resultSet));
                        }
                    };
                },
                name);
        return player.get();
    }

    private FactionPlayer extractPlayerFromResultSet(ResultSet resultSet) throws SQLException {
        FactionPlayer player = new FactionPlayer(UUID.fromString(resultSet.getString("player_id")));
        player.setFaction(MineClans.getInstance().getFactionManager().getFaction(resultSet.getString("faction_name")));
        player.setJoinDate(resultSet.getTimestamp("join_date"));
        player.setLastActive(resultSet.getTimestamp("last_active"));
        player.setKills(resultSet.getInt("kills"));
        player.setDeaths(resultSet.getInt("deaths"));
        player.setName(resultSet.getString("name"));
        return player;
    }

    public void deletePlayer(UUID playerId) {
        mySQLProvider.executeUpdateQuery("DELETE FROM faction_players WHERE player_id = ?", playerId);
    }
}
