package net.runelite.client.plugins.microbot.artefactstealing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PlayerState {
    GET_TASK(PlayerLocation.CAPTAIN_LOCATION),
    STEAL_ARTEFACT(PlayerLocation.HOUSE_ZONE),
    LURE_GUARDS(PlayerLocation.HOUSE_ZONE),
    SNEAK_GUARDS(PlayerLocation.HOUSE_ZONE),
    DELIVER_ARTEFACT(PlayerLocation.CAPTAIN_LOCATION),
    BANKING(PlayerLocation.BANK_LOCATION),
    IDLE(null);

    private final PlayerLocation playerLocation;
}
