package com.arkflame.mineclans.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.modernlib.utils.Titles;

public class ClanEventScheduler {
    // Current event
    private ClanEvent event = null;

    // Next event
    private ClanEvent nextEvent = null;

    // Event start time
    private long time = 0;

    // Interval in minutes
    private final int interval;

    // Task reference for canceling if needed
    private BukkitTask eventTask;

    public ClanEventScheduler(int interval) {
        this.interval = interval;
        runTimer();
    }

    // Update time to be set to next event time
    public void updateTime() {
        time = System.currentTimeMillis() + (interval * 60 * 1000);
    }

    public void runTimer() {
        cancel();
        eventTask = Bukkit.getScheduler().runTaskTimerAsynchronously(MineClans.getInstance(), () -> {
            long currentTime = System.currentTimeMillis();

            if (event != null) {
                // Check if event is active, if not, finish the event
                if (!event.isActive()) {
                    finishEvent();
                }
            } else {
                // Check if next event is scheduled and it's time to start
                if (nextEvent != null && currentTime >= time) {
                    startEvent();

                    Bukkit.getScheduler().runTask(MineClans.getInstance(), () -> {
                        Titles.sendTitle(ChatColor.GREEN + "Start!", ChatColor.YELLOW + nextEvent.getConfig().getDescription(), 10, 40, 10);
                    });
                } else if (nextEvent == null) {
                    // Schedule the next event
                    scheduleNextEvent();
                } else {
                    // Countdown
                    long timeLeft = time - currentTime;
                    if ((timeLeft < 60000 && timeLeft > 59000) ||
                            (timeLeft < 30000 && timeLeft > 29000) ||
                            (timeLeft < 5000 && timeLeft > 0)) {
                        int secondsLeft = (int) Math.ceil((timeLeft + 1000) / 1000);
                        ChatColor color;

                        if (secondsLeft > 3) {
                            color = ChatColor.GREEN;
                        } else if (secondsLeft > 1) {
                            color = ChatColor.YELLOW;
                        } else {
                            color = ChatColor.RED;
                        }

                        String title = color + String.valueOf(secondsLeft);
                        Bukkit.getScheduler().runTask(MineClans.getInstance(), () -> {
                            Titles.sendTitle(title, ChatColor.YELLOW + nextEvent.getName(), 0, 20, 0);
                        });
                    }
                }
            }
        }, 20L, 20L); // Run every second (20 ticks)
    }

    private void scheduleNextEvent() {
        updateTime();
        nextEvent = RandomEventFactory
                .createRandomEvent(MineClans.getInstance().getClanEventManager().getEventConfigs());

        if (nextEvent != null) {
            Bukkit.getLogger().info("Next event scheduled: " + nextEvent.getName());
        } else {
            Bukkit.getLogger().warning("Failed to schedule the next event.");
        }
    }

    private void finishEvent() {
        Bukkit.getLogger().info("Event finished: " + event.getName());

        // Reset current event
        event = null;
        time = 0;

        // Schedule the next event
        scheduleNextEvent();
    }

    private void startEvent() {
        event = nextEvent;
        nextEvent = null;

        Bukkit.getLogger().info("Event started: " + event.getName());

        // Handle event starting logic here (e.g., announcements)
        event.startEvent();

        // Schedule the next event
        scheduleNextEvent();
    }

    public void cancel() {
        if (eventTask != null && !eventTask.isCancelled()) {
            eventTask.cancel();
        }
    }

    public ClanEvent getEvent() {
        return event;
    }

    public ClanEvent getNextEvent() {
        return nextEvent;
    }

    public long getTimeLeft() {
        return Math.max(0, time - System.currentTimeMillis());
    }

    public String getTimeLeftFormatted() {
        long timeLeftMillis = getTimeLeft();
        long hours = timeLeftMillis / 3600000;
        long minutes = (timeLeftMillis % 3600000) / 60000;
        long seconds = (timeLeftMillis % 60000) / 1000;

        StringBuilder formattedTime = new StringBuilder();

        if (hours > 0) {
            formattedTime.append(hours).append("H ");
        }
        if (minutes > 0) {
            formattedTime.append(minutes).append("M ");
        }
        if (seconds > 0 || formattedTime.length() == 0) { // Always show seconds if nothing else is shown
            formattedTime.append(seconds).append("S");
        }

        return formattedTime.toString().trim();
    }

    public void setEvent(ClanEvent event) {
        this.event = event;
    }
}