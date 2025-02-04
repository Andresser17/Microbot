package net.runelite.client.plugins.microbot.cooking.enums;

import lombok.Getter;

@Getter
public enum PlayerState {
    COMBINING(PlayerLocation.BANK_LOCATION),
    COOKING(PlayerLocation.COOKING_AREA),
    BANKING(PlayerLocation.BANK_LOCATION),
    DROPPING(PlayerLocation.OUTSIDE_POINT),
    FILLING(PlayerLocation.COOKING_AREA),
    IDLE(PlayerLocation.OUTSIDE_POINT);

    private final PlayerLocation playerLocation;

    PlayerState(PlayerLocation playerLocation) {
        this.playerLocation = playerLocation;
    }
}
