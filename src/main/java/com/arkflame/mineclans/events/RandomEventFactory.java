package com.arkflame.mineclans.events;

import java.util.Map;
import java.util.Random;

public class RandomEventFactory {
    public static ClanEvent createRandomEvent(Map<String, EventConfig> eventConfigs) {
        if (eventConfigs.isEmpty()) return null;
        Random random = new Random();
        EventConfig config = eventConfigs.values().toArray(new EventConfig[0])[random.nextInt(eventConfigs.size())];
        return createEventFromConfig(config);
    }

    public static ClanEvent createEventFromConfig(EventConfig config) {
        return new ClanEvent(config);
    }
}
