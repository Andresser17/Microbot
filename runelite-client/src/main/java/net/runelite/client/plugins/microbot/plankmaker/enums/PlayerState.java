package net.runelite.client.plugins.microbot.plankmaker.enums;

import lombok.Getter;

@Getter
public enum PlayerState {

    PROCESSING(null),
    BANKING(null),
    IDLE(Location.OUTSIDE_POINT);

    private Location location;

    PlayerState(Location location) {
        this.location = location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

