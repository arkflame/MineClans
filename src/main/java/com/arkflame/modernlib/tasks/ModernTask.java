package com.arkflame.modernlib.tasks;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public abstract class ModernTask implements Runnable {
    private Plugin plugin;
    private long time;
    private boolean async;

    public ModernTask(Plugin plugin, long time, boolean async) {
        this.plugin = plugin;
        this.time = time;
        this.async = async;
    }

    public void register() {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();

        if (async) {
            scheduler.runTaskTimerAsynchronously(plugin, this, time, time);
        } else {
            scheduler.runTaskTimer(plugin, this, time, time);
        }
    }
}
