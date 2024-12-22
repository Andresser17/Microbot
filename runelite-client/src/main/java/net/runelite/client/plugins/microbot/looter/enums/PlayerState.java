package net.runelite.client.plugins.microbot.looter.enums;

import lombok.Getter;

@Getter
public enum PlayerState {
    LOOTING(PlayerLocation.LOOT_FIELD),
    BANKING(PlayerLocation.NEAREST_BANK),
    STALE(PlayerLocation.OUTSIDE_POINT),
    WALKING(PlayerLocation.OUTSIDE_POINT);

    private final PlayerLocation playerLocation;

    PlayerState(PlayerLocation playerLocation) {
        this.playerLocation = playerLocation;
    }
}
