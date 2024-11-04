package com.arkflame.mineclans.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;

public class BungeeUtil implements PluginMessageListener {

    private final JavaPlugin plugin;
    private final Queue<Consumer<String>> serverNameCallbacks = new ArrayDeque<>();
    private BukkitTask cleanupTask;

    public BungeeUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);

        // Start a cleanup task that runs every 20 seconds
        startCleanupTask();
    }

    /**
     * Sends the player to the specified server.
     *
     * @param player     The player to send.
     * @param serverName The name of the target server.
     */
    public void sendPlayerToServer(Player player, String serverName) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(byteStream)) {

            out.writeUTF("Connect");
            out.writeUTF(serverName);

            player.sendPluginMessage(plugin, "BungeeCord", byteStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Requests the server the player is currently connected to.
     *
     * @param player   The player to get the server information for.
     * @param callback The callback function to handle the server name (or null if unavailable).
     */
    public void getCurrentServer(Player player, Consumer<String> callback) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(byteStream)) {

            out.writeUTF("GetServer");
            player.sendPluginMessage(plugin, "BungeeCord", byteStream.toByteArray());

            // Add the callback to the queue for processing when the response is received
            serverNameCallbacks.add(callback);
        } catch (IOException e) {
            e.printStackTrace();
            callback.accept(null);
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) return;

        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(message))) {
            String subChannel = in.readUTF();

            if (subChannel.equals("GetServer")) {
                String serverName = in.readUTF();

                // Retrieve and execute the callback from the queue if available
                Consumer<String> callback = serverNameCallbacks.poll();
                if (callback != null) {
                    callback.accept(serverName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the cleanup task to clear stale callbacks every 20 seconds.
     */
    private void startCleanupTask() {
        cleanupTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!serverNameCallbacks.isEmpty()) {
                serverNameCallbacks.poll(); // Remove stale callback if any
            }
        }, 0L, 20 * 20L); // 20 seconds in ticks
    }

    /**
     * Shuts down the BungeeUtil, cancelling the cleanup task.
     */
    public void shutdown() {
        if (cleanupTask != null && !cleanupTask.isCancelled()) {
            cleanupTask.cancel();
        }
    }
}
