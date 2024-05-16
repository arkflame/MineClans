package com.arkflame.example.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.arkflame.example.ExamplePlugin;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin (final PlayerJoinEvent event) {
        final String message = ExamplePlugin.getInstance().getMsg().getText("messages.from-listener");
        event.getPlayer().sendMessage(message);
    }
}
