/*
    Members Table:
        Columns:
            Faction ID (UUID)
            Member ID (UUID)

    Factions Table:
        Columns:
            Faction ID (UUID)
            Owner ID (UUID)
            Display Name (String)
            Name (String)
            Home (Location)
            Balance (Double)
            Friendly Fire (Boolean)

    Invitations Table:
        Columns:
            Faction ID (UUID)
            Invited Member ID (UUID)

    Relations Table:
        Columns:
            faction_id (UUID)
            target_faction_id (UUID)
            relation_type (String)

    Chest Permissions Table:
        Columns:
            Faction ID (UUID)
            Role (String)
            Permission (Boolean)

    Ranks Table:
        Columns:
            Faction ID (UUID)
            Member ID (UUID)
            Rank (String)
 */
package com.arkflame.mineclans.providers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.providers.daos.ChestDAO;
import com.arkflame.mineclans.providers.daos.FactionDAO;
import com.arkflame.mineclans.providers.daos.FactionPlayerDAO;
import com.arkflame.mineclans.providers.daos.InvitedDAO;
import com.arkflame.mineclans.providers.daos.MemberDAO;
import com.arkflame.mineclans.providers.daos.PowerDAO;
import com.arkflame.mineclans.providers.daos.RanksDAO;
import com.arkflame.mineclans.providers.daos.RelationsDAO;
import com.arkflame.mineclans.providers.processors.ResultSetProcessor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySQLProvider {
    private HikariConfig config;
    private HikariDataSource dataSource = null;

    private ChestDAO chestDAO;
    private FactionDAO factionDAO;
    private FactionPlayerDAO factionPlayerDAO;
    private InvitedDAO invitedDAO;
    private MemberDAO memberDAO;
    private RanksDAO ranksDAO;
    private RelationsDAO relationsDAO;
    private PowerDAO powerDAO;

    private boolean connected = false;

    public MySQLProvider(boolean enabled, String url, String username, String password) {
        if (!enabled || url == null || username == null || password == null) {
            MineClans.getInstance().getLogger().severe("No database information provided.");
            return;
        } else {
            MineClans.getInstance().getLogger().info("Using MySQL database for factions.");
        }

        chestDAO = new ChestDAO(this);
        factionDAO = new FactionDAO(this);
        factionPlayerDAO = new FactionPlayerDAO(this);
        invitedDAO = new InvitedDAO(this);
        memberDAO = new MemberDAO(this);
        ranksDAO = new RanksDAO(this);
        relationsDAO = new RelationsDAO(this);
        powerDAO = new PowerDAO(this);

        // Generate hikari config
        generateHikariConfig(url, username, password);

        // Initialize
        initialize();
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    public void generateHikariConfig(String url, String username, String password) {
        config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTestQuery("SELECT 1"); // Example query for connection testing
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    }

    public ChestDAO getChestDAO() {
        return chestDAO;
    }

    public FactionDAO getFactionDAO() {
        return factionDAO;
    }

    public MemberDAO getMemberDAO() {
        return memberDAO;
    }

    public InvitedDAO getInvitedDAO() {
        return invitedDAO;
    }

    public RelationsDAO getRelationsDAO() {
        return relationsDAO;
    }

    public PowerDAO getPowerDAO() {
        return powerDAO;
    }

    public RanksDAO getRanksDAO() {
        return ranksDAO;
    }

    public FactionPlayerDAO getFactionPlayerDAO() {
        return factionPlayerDAO;
    }

    public boolean isConnected() {
        return connected;
    }

    public void createTables() {
        chestDAO.createTable();
        memberDAO.createTable();
        factionDAO.createTable();
        invitedDAO.createTable();
        relationsDAO.createTable();
        ranksDAO.createTable();
        factionPlayerDAO.createTable();
        powerDAO.createTable();
    }

    public void initialize() {
        try {
            this.dataSource = new HikariDataSource(config);
            createTables();
            this.connected = true;
        } catch (Exception e) {
            MineClans.getInstance().getLogger().info("Failed to initialize database connection: " + e.getMessage());
            this.dataSource = null; // Ensure dataSource is null to avoid any further usage attempts
        }
    }

    public void executeUpdateQuery(String query, Object... params) {
        if (dataSource == null) {
            return;
        }
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i] instanceof UUID ? params[i].toString() : params[i]);
            }
            // Execute query
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void executeSelectQuery(String query, ResultSetProcessor task, Object... params) {
        if (dataSource == null) {
            return;
        }
        try {
            try (Connection connection = dataSource.getConnection();
                    PreparedStatement statement = connection.prepareStatement(query);) {
                // Set parameters
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i] instanceof UUID ? params[i].toString() : params[i]);
                }
                // Execute query and return result set
                try (ResultSet result = statement.executeQuery()) {
                    task.run(result);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
