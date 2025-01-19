package net.runelite.client.plugins.microbot.plankmaker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SawmillLocation {
    LUMBER_YARD(Teleport.EARTH_ALTAR, Teleport.EDGEVILLE);

    final Teleport teleport;
    final Teleport bankTeleport;
}
