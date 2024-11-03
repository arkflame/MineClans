package com.arkflame.mineclans.providers.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import com.arkflame.mineclans.enums.Rank;
import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.processors.ResultSetProcessor;

public class RanksDAO {
    private MySQLProvider mySQLProvider;

    public RanksDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS mineclans_ranks (" +
                "player_id VARCHAR(36) PRIMARY KEY," +
                "rank VARCHAR(255) NOT NULL)");
    }

    public void setRank(UUID playerId, Rank rank) {
        mySQLProvider.executeUpdateQuery("INSERT INTO mineclans_ranks (player_id, rank) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE rank = VALUES(rank)", playerId.toString(), rank.toString());
    }

    public Rank getRank(UUID playerId) {
        AtomicReference<Rank> rank = new AtomicReference<>(null);
        String query = "SELECT rank FROM mineclans_ranks WHERE player_id = ?";
        mySQLProvider.executeSelectQuery(query, new ResultSetProcessor() {
            @Override
            public void run(ResultSet resultSet) throws SQLException {
                if (resultSet != null && resultSet.next()) {
                    String rankStr = resultSet.getString("rank");
                    rank.set(Rank.valueOf(rankStr));
                }
            }
        }, playerId.toString());
        return rank.get();
    }

    public Map<UUID, Rank> getAllRanks() {
        Map<UUID, Rank> ranks = new ConcurrentHashMap<>();
        String query = "SELECT player_id, rank FROM mineclans_ranks";
        mySQLProvider.executeSelectQuery(query, new ResultSetProcessor() {
            @Override
            public void run(ResultSet resultSet) throws SQLException {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        UUID playerId = UUID.fromString(resultSet.getString("player_id"));
                        String rankStr = resultSet.getString("rank");
                        try {
                            Rank rank = Rank.valueOf(rankStr);
                            ranks.put(playerId, rank);
                        } catch (IllegalArgumentException ex) {
                            // Skip invalid rank
                        }
                    }
                }
            }
        });
        return ranks;
    }
}
