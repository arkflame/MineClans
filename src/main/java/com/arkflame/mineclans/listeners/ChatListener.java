package com.arkflame.mineclans.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.api.results.FactionChatResult;
import com.arkflame.mineclans.models.FactionPlayer;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        FactionPlayer factionPlayer = MineClans.getInstance().getAPI().getFactionPlayer(player.getUniqueId());

        if (factionPlayer != null && factionPlayer.isChatEnabled()) {
            String message = event.getMessage();
            if (MineClans.getInstance().getAPI().sendFactionMessage(player, message).getState() == FactionChatResult.FactionChatState.SUCCESS) {
                event.setCancelled(true);  // Cancel the default chat event
            }
        }
    }
}
