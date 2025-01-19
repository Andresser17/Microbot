package net.runelite.client.plugins.microbot.blackjack.enums;

import lombok.Getter;

@Getter
public enum PlayerState {
    BANKING(Location.NARDAH_BANK),
    UN_NOTING(null),
    WALK_TO_THUGS(null),
    LURE_AWAY(null),
    RUN_AWAY(null),
    TRAP_NPC(null),
    BLACKJACKING(null);

    private Location location;

    PlayerState(Location location) {
        this.location = location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
