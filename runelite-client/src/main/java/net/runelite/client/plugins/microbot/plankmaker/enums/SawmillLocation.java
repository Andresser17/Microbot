package net.runelite.client.plugins.microbot.plankmaker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum SawmillLocation {
    LUMBER_YARD(new String[]{"Ring of the elements", "Earth Altar"}, new String[]{"Amulet of glory", "Edgeville"}, Location.LUMBER_YARD, Location.EDGEVILLE_BANK);

    final String[] teleport;
    final String[] bankTeleport;
    final Location location;
    final Location bankLocation;
}
