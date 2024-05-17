package com.arkflame.mineclans.api;

public class ToggleChatResult {
    public enum ToggleChatState {
        ENABLED,
        DISABLED,
        NOT_IN_FACTION
    }

    private final ToggleChatState state;

    public ToggleChatResult(ToggleChatState state) {
        this.state = state;
    }

    public ToggleChatState getState() {
        return state;
    }
}
