package com.arkflame.mineclans.providers.daos;

import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.providers.processors.ResultSetProcessor;
import com.arkflame.mineclans.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ChestDAO {
    private MySQLProvider mySQLProvider;

    public ChestDAO(MySQLProvider mySQLProvider) {
        this.mySQLProvider = mySQLProvider;
    }

    public void createTable() {
        mySQLProvider.executeUpdateQuery("CREATE TABLE IF NOT EXISTS mineclans_chests ("
                + "faction_id CHAR(36) NOT NULL PRIMARY KEY, "
                + "chest_contents TEXT"
                + ")");
    }

    public void saveFactionChest(UUID id, Inventory chestInventory) {
        String data = InventoryUtil.itemStackArrayToBase64(chestInventory.getContents());
        mySQLProvider.executeUpdateQuery(
                "INSERT INTO mineclans_chests (faction_id, chest_contents) VALUES (?, ?) "
                        + "ON DUPLICATE KEY UPDATE chest_contents = VALUES(chest_contents)",
                id, data);
    }

    public Inventory loadFactionChest(Faction faction) {
        Inventory inventory = Bukkit.createInventory(faction, 27, "Faction Chest");
        mySQLProvider.executeSelectQuery("SELECT chest_contents FROM mineclans_chests WHERE faction_id = ?",
                new ResultSetProcessor() {
                    @Override
                    public void run(ResultSet resultSet) throws SQLException {
                        if (resultSet != null && resultSet.next()) {
                            String data = resultSet.getString("chest_contents");
                            if (data != null && !data.isEmpty()) {
                                try {
                                    ItemStack[] items = InventoryUtil.itemStackArrayFromBase64(data);
                                    inventory.setContents(items);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }, faction.getId());
        return inventory;
    }

    public void deleteFactionChest(UUID id) {
        mySQLProvider.executeUpdateQuery("DELETE FROM mineclans_chests WHERE faction_id = ?", id);
    }
}
