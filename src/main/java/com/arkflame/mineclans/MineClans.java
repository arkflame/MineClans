package com.arkflame.mineclans;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.arkflame.mineclans.commands.FactionsCommand;
import com.arkflame.mineclans.listeners.PlayerJoinListener;
import com.arkflame.mineclans.listeners.PlayerQuitListener;
import com.arkflame.mineclans.managers.FactionManager;
import com.arkflame.mineclans.managers.FactionPlayerManager;
import com.arkflame.mineclans.providers.MySQLProvider;
import com.arkflame.modernlib.config.ConfigWrapper;
import com.arkflame.modernlib.menus.listeners.MenuListener;

public class MineClans extends JavaPlugin {
    private ConfigWrapper config;
    private ConfigWrapper messages;

    private FactionManager factionManager;
    private FactionPlayerManager factionPlayerManager;

    private MySQLProvider mySQLProvider;

    private FactionsCommand factionsCommand;

    public ConfigWrapper getCfg() {
        return config;
    }

    public ConfigWrapper getMsg() {
        return messages;
    }

    public FactionManager getFactionManager() {
        return factionManager;
    }

    public FactionPlayerManager getFactionPlayerManager() {
        return factionPlayerManager;
    }

    public MySQLProvider getMySQLProvider() {
        return mySQLProvider;
    }

    @Override
    public void onEnable() {
        // Set static instance
        setInstance(this);

        // Save default config
        config = new ConfigWrapper(this, "config.yml").saveDefault().load();
        messages = new ConfigWrapper(this, "messages.yml").saveDefault().load();

        factionManager = new FactionManager();
        factionPlayerManager = new FactionPlayerManager();

        mySQLProvider = new MySQLProvider(
                config.getBoolean("mysql.enabled"),
                config.getString("mysql.url"),
                config.getString("mysql.username"),
                config.getString("mysql.password"));

        // Register Listeners
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(factionPlayerManager), this);
        pluginManager.registerEvents(new PlayerQuitListener(factionPlayerManager), this);
        pluginManager.registerEvents(new MenuListener(), this);

        // Register Commands
        factionsCommand = new FactionsCommand();
        factionsCommand.register(this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        factionsCommand.unregisterBukkitCommand();

        mySQLProvider.close();
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