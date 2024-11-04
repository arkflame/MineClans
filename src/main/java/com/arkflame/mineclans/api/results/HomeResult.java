package com.arkflame.mineclans.api.results;

import com.arkflame.mineclans.utils.LocationData;

public class HomeResult {
    private final HomeResultState state;
    private final LocationData homeLocation;

    public HomeResult(HomeResultState state) {
        this.state = state;
        this.homeLocation = null;
    }

    public HomeResult(HomeResultState state, LocationData homeLocation) {
        this.state = state;
        this.homeLocation = homeLocation;
    }

    public HomeResultState getState() {
        return state;
    }

    public LocationData getHomeLocation() {
        return homeLocation;
    }
    
    public enum HomeResultState {
        NOT_IN_FACTION, NO_HOME_SET, SUCCESS, ERROR
    }
}
