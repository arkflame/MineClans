package com.arkflame.mineclans.providers.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.providers.MySQLProvider;

public class MemberDAO {
    private MySQLProvider mySQLProvider;

    public MemberDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS members (" +
                "faction_id VARCHAR(36) PRIMARY KEY," +
                "member_id VARCHAR(36) NOT NULL)");
    }

    public void addMember(UUID factionId, UUID memberId) {
        mySQLProvider.executeUpdateQuery("INSERT INTO members (faction_id, member_id) VALUES (?, ?)", factionId.toString(),
                memberId.toString());
    }

    public void removeMember(UUID factionId, UUID memberId) {
        mySQLProvider.executeUpdateQuery("DELETE FROM members WHERE faction_id = ? AND member_id = ?", factionId.toString(),
                memberId.toString());
    }

    public void removeMembers(UUID factionId) {
        String query = "DELETE FROM members WHERE faction_id = ?";
        mySQLProvider.executeUpdateQuery(query, factionId.toString());
    }

    public Collection<UUID> getMembers(UUID factionId) {
        Collection<UUID> members = ConcurrentHashMap.newKeySet();
        String query = "SELECT member_id FROM members WHERE faction_id = ?";
        try (ResultSet resultSet = mySQLProvider.executeSelectQuery(query, factionId.toString())) {
            if (resultSet != null) {
                while (resultSet.next()) {
                    UUID memberId = UUID.fromString(resultSet.getString("member_id"));
                    members.add(memberId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public Faction getFactionByMemberName(String memberName) {
        Faction faction = null;
        // Step 1: Query the members table to find the faction ID associated with the
        // member name
        String memberIdQuery = "SELECT faction_id FROM members WHERE member_name = ?";
        try (ResultSet memberIdResultSet = mySQLProvider.executeSelectQuery(memberIdQuery, memberName)) {
            if (memberIdResultSet.next()) {
                UUID factionId = UUID.fromString(memberIdResultSet.getString("faction_id"));
                // Step 2: Use the obtained faction ID to query the factions table and retrieve
                // the faction details
                faction = mySQLProvider.getFactionDAO().getFactionById(factionId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return faction;
    }
}
