package com.arkflame.mineclans.api.results;

public class ToggleChatResult {
    public enum ToggleChatState {
        FACTION,
        ALLIANCE,
        DISABLED,
        NOT_IN_FACTION;

        public ToggleChatState getNext() {
            switch (this) {
                case FACTION:
                    return ALLIANCE;
                case ALLIANCE:
                    return DISABLED;
                case DISABLED:
                    return FACTION;
                case NOT_IN_FACTION:
                default:
                    return NOT_IN_FACTION;
            }
        }
    }

    private final ToggleChatState state;

    public ToggleChatResult(ToggleChatState state) {
        this.state = state;
    }

    public ToggleChatState getState() {
        return state;
    }
}
