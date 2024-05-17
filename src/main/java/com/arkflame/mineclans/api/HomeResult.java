package com.arkflame.mineclans.api;

import org.bukkit.Location;

public class HomeResult {
    private final HomeResultState state;
    private final Location homeLocation;

    public HomeResult(HomeResultState state) {
        this.state = state;
        this.homeLocation = null;
    }

    public HomeResult(HomeResultState state, Location homeLocation) {
        this.state = state;
        this.homeLocation = homeLocation;
    }

    public HomeResultState getState() {
        return state;
    }

    public Location getHomeLocation() {
        return homeLocation;
    }
    
    public enum HomeResultState {
        NOT_IN_FACTION, NO_HOME_SET, SUCCESS, ERROR
    }
}
