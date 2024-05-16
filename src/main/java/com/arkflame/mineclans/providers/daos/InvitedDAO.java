package com.arkflame.mineclans.providers.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.arkflame.mineclans.providers.MySQLProvider;

public class InvitedDAO {
    private MySQLProvider mySQLProvider;

    public InvitedDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS invited (" +
                "faction_id VARCHAR(36) PRIMARY KEY," +
                "member_id VARCHAR(36) NOT NULL)");
    }

    public void addInvitedMember(UUID factionId, UUID memberId) {
        mySQLProvider.executeUpdateQuery("INSERT INTO invited (faction_id, member_id) VALUES (?, ?)", factionId,
                memberId);
    }

    public void removeInvitedMember(UUID factionId, UUID memberId) {
        mySQLProvider.executeUpdateQuery("DELETE FROM invited WHERE faction_id = ? AND member_id = ?", factionId,
                memberId);
    }

    public Collection<UUID> getInvitedMembers(UUID factionId) {
        Collection<UUID> invitedMembers = ConcurrentHashMap.newKeySet();
        String query = "SELECT member_id FROM invited WHERE faction_id = ?";
        try (ResultSet resultSet = mySQLProvider.executeSelectQuery(query, factionId)) {
            if (resultSet != null) {
                while (resultSet.next()) {
                    invitedMembers.add(UUID.fromString(resultSet.getString("member_id")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invitedMembers;
    }

    public void removeInvitedMembers(UUID factionId) {
        String query = "DELETE FROM invited WHERE faction_id = ?";
        mySQLProvider.executeUpdateQuery(query, factionId.toString());
    }
}
