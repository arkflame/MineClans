package com.arkflame.mineclans.providers.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.ResultSetProcessor;

public class InvitedDAO {
    private MySQLProvider mySQLProvider;

    public InvitedDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS mineclans_invited (" +
                "faction_id UUID PRIMARY KEY," +
                "member_id UUID NOT NULL)");
    }

    public void addInvitedMember(UUID factionId, UUID memberId) {
        mySQLProvider.executeUpdateQuery("INSERT INTO mineclans_invited (faction_id, member_id) VALUES (?, ?)", factionId,
                memberId);
    }

    public void removeInvitedMember(UUID factionId, UUID memberId) {
        mySQLProvider.executeUpdateQuery("DELETE FROM mineclans_invited WHERE faction_id = ? AND member_id = ?", factionId,
                memberId);
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
}
