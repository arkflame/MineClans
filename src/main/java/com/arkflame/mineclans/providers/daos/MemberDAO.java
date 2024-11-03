package com.arkflame.mineclans.providers.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.processors.ResultSetProcessor;

public class MemberDAO {
    private MySQLProvider mySQLProvider;

    public MemberDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS mineclans_members (" +
                "faction_id CHAR(36) NOT NULL," +
                "member_id CHAR(36) NOT NULL," +
                "PRIMARY KEY (faction_id, member_id))");
    }

    public void addMember(UUID factionId, UUID memberId) {
        String query = "INSERT INTO mineclans_members (faction_id, member_id) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE member_id = VALUES(member_id)";
        mySQLProvider.executeUpdateQuery(query, factionId.toString(), memberId.toString());
    }

    public void removeMember(UUID factionId, UUID memberId) {
        mySQLProvider.executeUpdateQuery(
                "DELETE FROM mineclans_members WHERE faction_id = ? AND member_id = ?",
                factionId.toString(),
                memberId.toString());
    }

    public void removeMembers(UUID factionId) {
        String query = "DELETE FROM mineclans_members WHERE faction_id = ?";
        mySQLProvider.executeUpdateQuery(query, factionId.toString());
    }

    public Collection<UUID> getMembers(UUID factionId) {
        Collection<UUID> members = ConcurrentHashMap.newKeySet();
        String query = "SELECT member_id FROM mineclans_members WHERE faction_id = ?";
        mySQLProvider.executeSelectQuery(query, new ResultSetProcessor() {
            @Override
            public void run(ResultSet resultSet) throws SQLException {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        UUID memberId = UUID.fromString(resultSet.getString("member_id"));
                        members.add(memberId);
                    }
                }
            }
        }, factionId.toString());
        return members;
    }

    public Faction getFactionByMemberName(String memberName) {
        AtomicReference<Faction> faction = new AtomicReference<>(null);
        // Step 1: Query the members table to find the faction ID associated with the
        // member name
        String memberIdQuery = "SELECT faction_id FROM mineclans_members WHERE member_name = ?";
        mySQLProvider.executeSelectQuery(memberIdQuery, new ResultSetProcessor() {
            @Override
            public void run(ResultSet resultSet) throws SQLException {
                if (resultSet.next()) {
                    // Get the faction ID
                    UUID factionId = UUID.fromString(resultSet.getString("faction_id"));

                    // Set the faction
                    faction.set(mySQLProvider.getFactionDAO().getFactionById(factionId));
                }
            }
        }, memberName);
        return faction.get();
    }
}
