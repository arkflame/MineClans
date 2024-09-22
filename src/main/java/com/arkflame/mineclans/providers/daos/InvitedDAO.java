package com.arkflame.mineclans.providers.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.ResultSetProcessor;

public class InvitedDAO {
    private MySQLProvider mySQLProvider;

    public InvitedDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS mineclans_invited (" +
                "faction_id CHAR(36) PRIMARY KEY," +
                "member_id CHAR(36) NOT NULL)");
    }

    public void addInvitedMember(UUID factionId, UUID memberId) {
        mySQLProvider.executeUpdateQuery(
                "INSERT INTO mineclans_invited (faction_id, member_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE faction_id = VALUES(faction_id), member_id = VALUES(member_id)",
                factionId, memberId);
    }

    public void removeInvitedMember(UUID factionId, UUID memberId) {
        mySQLProvider.executeUpdateQuery("DELETE FROM mineclans_invited WHERE faction_id = ? AND member_id = ?",
                factionId, memberId);
    }

    public boolean isMemberInvited(UUID factionId, UUID memberId) {
        AtomicBoolean isMemberInvited = new AtomicBoolean(false);
        mySQLProvider.executeSelectQuery("SELECT 1 FROM mineclans_invited WHERE faction_id = ? AND member_id = ?",
                new ResultSetProcessor() {
                    @Override
                    public void run(ResultSet resultSet) throws SQLException {
                        isMemberInvited.set(resultSet.next());
                    }
                }, factionId, memberId);
        return isMemberInvited.get();
    }

    public Collection<UUID> getInvitedMembers(UUID factionId) {
        Collection<UUID> invitedMembers = ConcurrentHashMap.newKeySet();
        String query = "SELECT member_id FROM mineclans_invited WHERE faction_id = ?";
        mySQLProvider.executeSelectQuery(query, new ResultSetProcessor() {
            @Override
            public void run(ResultSet resultSet) throws SQLException {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        invitedMembers.add(UUID.fromString(resultSet.getString("member_id")));
                    }
                }
            }
        }, factionId);
        return invitedMembers;
    }

    public void removeInvitedMembers(UUID factionId) {
        String query = "DELETE FROM mineclans_invited WHERE faction_id = ?";
        mySQLProvider.executeUpdateQuery(query, factionId.toString());
    }

    public Collection<UUID> getInvitingFactions(UUID memberId) {
        Collection<UUID> invitingFactions = ConcurrentHashMap.newKeySet();
        String query = "SELECT faction_id FROM mineclans_invited WHERE member_id = ?";
        
        mySQLProvider.executeSelectQuery(query, new ResultSetProcessor() {
            @Override
            public void run(ResultSet resultSet) throws SQLException {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        invitingFactions.add(UUID.fromString(resultSet.getString("faction_id")));
                    }
                }
            }
        }, memberId);
        
        return invitingFactions;
    }    
}
