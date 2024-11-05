package com.arkflame.mineclans.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.modernlib.utils.Sounds;
import com.arkflame.mineclans.modernlib.utils.Titles;

public class ClanEventScheduler {
    // Current event
    private ClanEvent event = null;

    // Next event
    private ClanEvent nextEvent = null;

    // Current time updated by the timer each second
    private long currentTime = 0;

    // Event start time
    private long nextEventTime = 0;

    // Event finish time
    private long eventFinishTime = 0;

    // Interval in minutes
    private final int interval;

    // Time limit in minutes
    private final int timeLimit;

    // Task reference for canceling if needed
    private BukkitTask eventTask;

    public ClanEventScheduler(int interval, int timeLimit) {
        this.interval = interval;
        this.timeLimit = timeLimit;
        runTimer();
    }

    public void updateNextEventTime() {
        nextEventTime = currentTime + (interval * 60 * 1000) + 1000;
    }

    public void updateEventFinishTime() {
        eventFinishTime = currentTime + (timeLimit * 60 * 1000) + 1000;
    }

    public void runTimer() {
        FileConfiguration cfg = MineClans.getInstance().getConfig();
        if (!cfg.isBoolean("events.enabled")) {
            return;
        }
        cancel();
        eventTask = Bukkit.getScheduler().runTaskTimerAsynchronously(MineClans.getInstance(), () -> {
            currentTime = System.currentTimeMillis();

            if (event != null) {
                // Check if event is active, if not, finish the event
                if (!event.isActive()) {
                    finishEvent();
                } else {
                    // Check if time limit passed
                    if (currentTime > eventFinishTime) {
                        event.finish(null);
                    }
                }
            } else {
                // Check if next event is scheduled and it's time to start
                if (nextEvent != null && currentTime >= nextEventTime) {
                    startEvent();
                } else if (nextEvent == null) {
                    // Schedule the next event
                    scheduleNextEvent();
                } else {
                    // Countdown
                    long timeLeft = nextEventTime - currentTime;
                    handleCountdown(timeLeft);
                }
            }
        }, 20L, 20L); // Run every second (20 ticks)
    }

    private void handleCountdown(long timeLeft) {
        if (nextEvent == null) {
            return;
        }
        int secondsLeft = (int) Math.ceil(timeLeft / 1000.0);
        ChatColor color;

        // Determine the color based on the time left
        if (secondsLeft > 10) {
            color = ChatColor.GREEN;
        } else if (secondsLeft > 3) {
            color = ChatColor.YELLOW;
        } else {
            color = ChatColor.RED;
        }

        // Only show countdown messages for specific intervals
        if (secondsLeft == 60 || secondsLeft == 30 ||
                secondsLeft == 10 || secondsLeft <= 5) {

            String title = color + String.valueOf(secondsLeft);
            Bukkit.getScheduler().runTask(MineClans.getInstance(), () -> {
                Titles.sendTitle(title, ChatColor.YELLOW + nextEvent.getName(), 0, 20, 0);
            });
            for (Player player : Bukkit.getOnlinePlayers()) {
                Sounds.play(player, 1.0f, 1.0f, "CLICK");
            }
        }
    }

    private void scheduleNextEvent() {
        updateNextEventTime();
        nextEvent = RandomEventFactory.createRandomEvent(MineClans.getInstance().getClanEventManager().getEventConfigs());
    }

    public void finishEvent() {
        // Reset current event
        event = null;

        // Schedule the next event
        scheduleNextEvent();
    }

    public void startEvent() {
        event = nextEvent;
        nextEvent = null;
        updateEventFinishTime();

        // Handle event starting logic here (e.g., announcements)
        event.startEvent();

        // Schedule the next event
        //scheduleNextEvent();
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
        long timeLeft;
        if (event != null) {
            // Time left for current event
            timeLeft = eventFinishTime - currentTime;
        } else {
            // Time left for next event
            timeLeft = nextEventTime - currentTime;
        }
        return Math.max(0L, timeLeft);
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
