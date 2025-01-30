package net.runelite.client.plugins.microbot.magic.aiomagic.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Staff id and the rune is equivalent to.
 */
@Getter
@RequiredArgsConstructor
public enum EarthStaff {
    STAFF_OF_EARTH(ItemID.STAFF_OF_EARTH, ItemID.EARTH_RUNE),
    EARTH_BATTLESTAFF(ItemID.EARTH_BATTLESTAFF, ItemID.EARTH_RUNE),
    MYSTIC_EARTH_STAFF(ItemID.MYSTIC_EARTH_STAFF, ItemID.EARTH_RUNE),
    LAVA_BATTLESTAFF(ItemID.LAVA_BATTLESTAFF, ItemID.EARTH_RUNE),
    MYSTIC_LAVA_STAFF(ItemID.MYSTIC_LAVA_STAFF, ItemID.EARTH_RUNE);

    private final int id;
    private final int runeId;
}
