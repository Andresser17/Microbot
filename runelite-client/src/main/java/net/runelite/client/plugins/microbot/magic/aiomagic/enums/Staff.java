package net.runelite.client.plugins.microbot.magic.aiomagic.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

import java.util.Optional;
import java.util.stream.Stream; /**
 * Staff id and the rune is equivalent to.
 */
@Getter
@RequiredArgsConstructor
public enum Staff {
    AIR(ItemID.STAFF_OF_AIR, ItemID.AIR_RUNE),
    WATER(ItemID.STAFF_OF_WATER, ItemID.WATER_RUNE),
    EARTH(ItemID.STAFF_OF_EARTH, ItemID.EARTH_RUNE),
    FIRE(ItemID.STAFF_OF_FIRE, ItemID.FIRE_RUNE);

    private final int id;
    private final int runeId;

    public static Stream<Staff> stream() {
        return Stream.of(Staff.values());
    }


    public static Staff getStaffByRuneId(int runeId) {
        Optional<Staff> optional = stream().filter(staff -> staff.runeId == runeId).findFirst();
        return optional.orElse(null);
    }
}
