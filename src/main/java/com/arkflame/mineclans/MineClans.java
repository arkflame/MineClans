package com.arkflame.mineclans;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.arkflame.mineclans.api.MineClansAPI;
import com.arkflame.mineclans.commands.FactionsCommand;
import com.arkflame.mineclans.events.ClanEventManager;
import com.arkflame.mineclans.events.ClanEventScheduler;
import com.arkflame.mineclans.listeners.ChatListener;
import com.arkflame.mineclans.listeners.ClanEventListener;
import com.arkflame.mineclans.listeners.FactionFriendlyFireListener;
import com.arkflame.mineclans.listeners.InventoryClickListener;
import com.arkflame.mineclans.listeners.PlayerJoinListener;
import com.arkflame.mineclans.listeners.PlayerKillListener;
import com.arkflame.mineclans.listeners.PlayerQuitListener;
import com.arkflame.mineclans.managers.FactionManager;
import com.arkflame.mineclans.managers.FactionPlayerManager;
import com.arkflame.mineclans.managers.LeaderboardManager;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.config.ConfigWrapper;
import com.arkflame.mineclans.modernlib.menus.listeners.MenuListener;
import com.arkflame.mineclans.placeholders.FactionsPlaceholder;
import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.mineclans.tasks.InventorySaveTask;

import net.milkbowl.vault.economy.Economy;

public class MineClans extends JavaPlugin {
    private ConfigWrapper config;
    private ConfigWrapper messages;

    // Providers
    private MySQLProvider mySQLProvider;

    // Managers
    private FactionManager factionManager;
    private FactionPlayerManager factionPlayerManager;

    private InventorySaveTask inventorySaveTask;

    // API
    private MineClansAPI api;

    private FactionsCommand factionsCommand;

    // Vault Economy
    private Economy economy;

    // Events
    private ClanEventManager clanEventManager;
    private ClanEventScheduler clanEventScheduler;

    // Leaderboard Manager
    private LeaderboardManager leaderboardManager;

    public ConfigWrapper getCfg() {
        return config;
    }

    public ConfigWrapper getMessages() {
        return messages;
    }

    public MySQLProvider getMySQLProvider() {
        return mySQLProvider;
    }

    public FactionManager getFactionManager() {
        return factionManager;
    }

    public FactionPlayerManager getFactionPlayerManager() {
        return factionPlayerManager;
    }

    public MineClansAPI getAPI() {
        return api;
    }

    public InventorySaveTask getInventorySaveTask() {
        return inventorySaveTask;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    public boolean isVaultHooked() {
        return economy != null;
    }

    public Economy getVaultEconomy() {
        return economy;
    }

    public ClanEventManager getClanEventManager() {
        return clanEventManager;
    }

    public ClanEventScheduler getClanEventScheduler() {
        return clanEventScheduler;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    @Override
    public void onEnable() {
        // Set static instance
        setInstance(this);

        // Save default config
        config = new ConfigWrapper(this, "config.yml").saveDefault().load();
        messages = new ConfigWrapper(this, "messages.yml").saveDefault().load();

        mySQLProvider = new MySQLProvider(
                config.getBoolean("mysql.enabled"),
                config.getString("mysql.url"),
                config.getString("mysql.username"),
                config.getString("mysql.password"));

        // Managers
        factionManager = new FactionManager();
        factionPlayerManager = new FactionPlayerManager();
        clanEventManager = new ClanEventManager(this);
        clanEventScheduler = new ClanEventScheduler(config.getInt("events.interval"));
        leaderboardManager = new LeaderboardManager(mySQLProvider.getPowerDAO());

        // Initialize API
        api = new MineClansAPI(factionManager, factionPlayerManager);

        // Register Listeners
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ChatListener(), this);
        pluginManager.registerEvents(new ClanEventListener(), this);
        pluginManager.registerEvents(new FactionFriendlyFireListener(), this);
        pluginManager.registerEvents(new InventoryClickListener(), this);
        pluginManager.registerEvents(new PlayerJoinListener(factionPlayerManager), this);
        pluginManager.registerEvents(new PlayerKillListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(factionPlayerManager), this);
        pluginManager.registerEvents(new MenuListener(), this);

        // Register Commands
        factionsCommand = new FactionsCommand();
        factionsCommand.register(this);

        // Register the placeholder
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            new FactionsPlaceholder(this).register();
        }

        // Register tasks
        inventorySaveTask = new InventorySaveTask();
        inventorySaveTask.register();

        // Attempt to hook Vault
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            if (!setupEconomy()) {
                getLogger().severe("Vault economy setup failed, using fallback.");
            }
        } else {
            getLogger().info("Vault not found, using fallback economy.");
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        factionsCommand.unregisterBukkitCommand();

        mySQLProvider.close();

        for (Player player : Bukkit.getOnlinePlayers()) {
            InventoryView view = player.getOpenInventory();

            if (view != null) {
                Inventory inventory = view.getTopInventory();

                if (inventory != null) {
                    InventoryHolder inventoryHolder = inventory.getHolder();

                    if (inventoryHolder instanceof Faction) {
                        player.closeInventory();
                    }
                }
            }
        }

        getServer().getScheduler().cancelTasks(this);
    }

    private static MineClans instance;

    public static void setInstance(MineClans instance) {
        MineClans.instance = instance;
    }

    public static MineClans getInstance() {
        return MineClans.instance;
    }

    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(MineClans.getInstance(), runnable);
    }

    public static void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(MineClans.getInstance(), runnable);
    }
}