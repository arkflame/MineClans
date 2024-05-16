package com.arkflame.example.tasks;

import org.bukkit.Bukkit;

import com.arkflame.example.ExamplePlugin;
import com.arkflame.modernlib.tasks.ModernTask;

public class ExampleTask extends ModernTask {
    public ExampleTask() {
        super(ExamplePlugin.getInstance(), ExamplePlugin.getInstance().getCfg().getInt("task-repeat-every") * 20L, false);
    }

    @Override
    public void run() {
        final String message = ExamplePlugin.getInstance().getMsg().getText("messages.from-task");
        Bukkit.getServer().broadcastMessage(message);
    }
}