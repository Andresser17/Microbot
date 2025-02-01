package net.runelite.client.plugins.microbot.mining.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PlayerState {
    MINING(PlayerLocation.MINING_FIELD),
    DROPPING(PlayerLocation.MINING_FIELD),
    BANKING(PlayerLocation.BANK_LOCATION);

    private final  PlayerLocation playerLocation;
}
