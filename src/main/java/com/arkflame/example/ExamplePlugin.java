package com.arkflame.example;

import org.bukkit.plugin.java.JavaPlugin;

import com.arkflame.example.commands.ExampleCommand;
import com.arkflame.example.listeners.PlayerJoinListener;
import com.arkflame.example.tasks.ExampleTask;
import com.arkflame.modernlib.config.ConfigWrapper;
import com.arkflame.modernlib.menus.listeners.MenuListener;

public class ExamplePlugin extends JavaPlugin {
    private ConfigWrapper config;
    private ConfigWrapper messages;

    public ConfigWrapper getCfg() {
        return config;
    }

    public ConfigWrapper getMsg() {
        return messages;
    }

    @Override
    public void onEnable() {
        // Set static instance
        setInstance(this);

        // Save default config
        config = new ConfigWrapper(this, "config.yml").saveDefault().load();
        messages = new ConfigWrapper(this, "messages.yml").saveDefault().load();

        // Register the example listener
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        this.getServer().getPluginManager().registerEvents(new MenuListener(), this);

        // Register the example task
        new ExampleTask().register();

        // Register example commands
        new ExampleCommand().register(this);
    }

    private static ExamplePlugin instance;

    public static void setInstance(ExamplePlugin instance) {
        ExamplePlugin.instance = instance;
    }

    public static ExamplePlugin getInstance() {
        return ExamplePlugin.instance;
    }
}