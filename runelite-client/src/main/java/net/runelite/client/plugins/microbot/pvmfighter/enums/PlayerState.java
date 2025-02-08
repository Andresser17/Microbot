package net.runelite.client.plugins.microbot.pvmfighter.enums;

import lombok.Getter;

@Getter
public enum PlayerState {
    ATTACKING(PlayerLocation.COMBAT_FIELD),
    EATING(PlayerLocation.COMBAT_FIELD),
    POTION(PlayerLocation.COMBAT_FIELD),
    ATTACK_STYLE(PlayerLocation.COMBAT_FIELD),
    LOOTING(PlayerLocation.COMBAT_FIELD),
    SAFEKEEPING(PlayerLocation.SAFE_SPOT),
    BANKING(PlayerLocation.BANK_LOCATION),
    CANNON(PlayerLocation.COMBAT_FIELD),
    SLAYER_MASTER(PlayerLocation.SLAYER_MASTER),
    IDLE(PlayerLocation.OUTSIDE_POINT);

    private final PlayerLocation playerLocation;

    PlayerState(PlayerLocation playerLocation) {
        this.playerLocation = playerLocation;
    }
}
