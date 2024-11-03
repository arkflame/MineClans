package com.arkflame.mineclans.providers.daos;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.processors.ResultSetProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PowerDAO {
    private final MySQLProvider mySQLProvider;

    public PowerDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS mineclans_power ("
                + "faction_id VARCHAR(36) NOT NULL, "
                + "power DOUBLE, "
                + "PRIMARY KEY (faction_id), "
                + "INDEX idx_power (power))");
    }

    public Double getFactionPower(UUID factionId) {
        final Double[] power = {null};
        mySQLProvider.executeSelectQuery("SELECT power FROM mineclans_power WHERE faction_id = ?",
                new ResultSetProcessor() {
                    @Override
                    public void run(ResultSet resultSet) throws SQLException {
                        if (resultSet.next()) {
                            power[0] = resultSet.getDouble("power");
                        }
                    }
                }, factionId.toString());
        return power[0];
    }

    public void updateFactionPower(UUID factionId, double power) {
        mySQLProvider.executeUpdateQuery(
                "INSERT INTO mineclans_power (faction_id, power) VALUES (?, ?) "
                        + "ON DUPLICATE KEY UPDATE power = VALUES(power)",
                factionId.toString(), power);
    }

    public int getFactionPosition(UUID factionId) {
        final int[] position = {0};
        mySQLProvider.executeSelectQuery(
                "SELECT (SELECT COUNT(*) FROM mineclans_power AS mp WHERE mp.power > m.power " +
                        "OR (mp.power = m.power AND mp.faction_id < m.faction_id)) AS idx_power " +
                        "FROM mineclans_power AS m WHERE m.faction_id = ?",
                new ResultSetProcessor() {
                    @Override
                    public void run(ResultSet resultSet) throws SQLException {
                        if (resultSet.next()) {
                            position[0] = resultSet.getInt("idx_power");
                        }
                    }
                }, factionId.toString());
        return position[0] + 1; // Adjusting for 1-based index
    }    

    public UUID getFactionIdByPosition(int position) {
        final UUID[] factionId = {null};
        mySQLProvider.executeSelectQuery(
                "SELECT faction_id FROM (SELECT faction_id, RANK() OVER (ORDER BY power DESC, faction_id ASC) as rank FROM mineclans_power) ranked WHERE rank = ?",
                new ResultSetProcessor() {
                    @Override
                    public void run(ResultSet resultSet) throws SQLException {
                        if (resultSet.next()) {
                            factionId[0] = UUID.fromString(resultSet.getString("faction_id"));
                        }
                    }
                }, position);
        return factionId[0];
    }

    public void removeFaction(UUID factionId) {
        mySQLProvider.executeUpdateQuery(
                "DELETE FROM mineclans_power WHERE faction_id = ?",
                factionId.toString());
    }
}
