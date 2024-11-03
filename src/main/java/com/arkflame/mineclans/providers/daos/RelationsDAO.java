package com.arkflame.mineclans.providers.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.arkflame.mineclans.models.Relation;
import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.processors.ResultSetProcessor;

public class RelationsDAO {
    private MySQLProvider mySQLProvider;

    public RelationsDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS mineclans_relations (" +
                "faction_id CHAR(36) NOT NULL," +
                "target_faction_id CHAR(36) NOT NULL," +
                "relation_type VARCHAR(64) NOT NULL," +
                "PRIMARY KEY (faction_id, target_faction_id))");
    }

    public void insertRelation(UUID factionId, UUID targetFactionId, String relationType) {
        mySQLProvider.executeUpdateQuery("INSERT INTO mineclans_relations (faction_id, target_faction_id, relation_type) VALUES (?, ?, ?)",
                factionId, targetFactionId, relationType);
    }
    
    public void insertOrUpdateRelation(UUID factionId, UUID targetFactionId, String relationType) {
        String query = "INSERT INTO mineclans_relations (faction_id, target_faction_id, relation_type) " +
                       "VALUES (?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE " +
                       "relation_type = VALUES(relation_type)";
        mySQLProvider.executeUpdateQuery(query, factionId, targetFactionId, relationType);
    }

    public void removeRelationById(UUID factionId, UUID targetFactionId) {
        mySQLProvider.executeUpdateQuery("DELETE FROM mineclans_relations WHERE faction_id = ? AND target_faction_id = ?",
                factionId, targetFactionId);
    }

    public Collection<Relation> getRelationsByFactionId(UUID factionId) {
        Collection<Relation> relations = ConcurrentHashMap.newKeySet();
        String query = "SELECT target_faction_id, relation_type FROM mineclans_relations WHERE faction_id = ?";
        mySQLProvider.executeSelectQuery(query, new ResultSetProcessor() {
            public void run(ResultSet resultSet) throws SQLException {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        UUID targetFactionId = UUID.fromString(resultSet.getString("target_faction_id"));
                        String relationType = resultSet.getString("relation_type");
                        Relation relation = new Relation(factionId, targetFactionId, relationType);
                        relations.add(relation);
                    }
                }
            };
        }, factionId);
        return relations;
    }

    public void removeRelationsById(UUID id) {
        // Remove relations where the faction_id matches the given ID
        mySQLProvider.executeUpdateQuery("DELETE FROM mineclans_relations WHERE faction_id = ?", id);
    
        // Remove relations where the target_faction_id matches the given ID
        mySQLProvider.executeUpdateQuery("DELETE FROM mineclans_relations WHERE target_faction_id = ?", id);
    }
    
}
