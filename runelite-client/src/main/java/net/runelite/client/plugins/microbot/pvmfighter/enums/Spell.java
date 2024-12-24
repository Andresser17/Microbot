package net.runelite.client.plugins.microbot.pvmfighter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * Staff id and the rune is equivalent to.
 */
@Getter
@RequiredArgsConstructor
enum Staff {
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

@Getter
@RequiredArgsConstructor
public enum Spell {
    // Strikes
    WIND_STRIKE("Wind Strike", new int[][]{new int[]{ItemID.MIND_RUNE, 1}, new int[]{ItemID.AIR_RUNE, 1}}, new Staff[]{Staff.AIR}, 1),
    WATER_STRIKE("Water Strike", new int[][]{new int[]{ItemID.MIND_RUNE, 1}, new int[]{ItemID.WATER_RUNE, 1}, new int[]{ItemID.AIR_RUNE, 1}}, new Staff[]{Staff.AIR, Staff.WATER}, 5),
    EARTH_STRIKE("Earth Strike", new int[][]{new int[]{ItemID.MIND_RUNE, 1}, new int[]{ItemID.EARTH_RUNE, 2}, new int[]{ItemID.AIR_RUNE, 1}}, new Staff[]{Staff.AIR, Staff.EARTH}, 9),
    FIRE_STRIKE("Fire Strike", new int[][]{new int[]{ItemID.MIND_RUNE, 1}, new int[]{ItemID.FIRE_RUNE, 3}, new int[]{ItemID.AIR_RUNE, 2}}, new Staff[]{Staff.AIR, Staff.FIRE}, 13),

    // Bolts
    WIND_BOLT("Wind Bolt", new int[][]{new int[]{ItemID.CHAOS_RUNE, 1}, new int[]{ItemID.AIR_RUNE, 2}}, new Staff[]{Staff.AIR},17),
    WATER_BOLT("Water Bolt", new int[][]{new int[]{ItemID.CHAOS_RUNE, 1}, new int[]{ItemID.WATER_RUNE, 2}, new int[]{ItemID.AIR_RUNE, 2}}, new Staff[]{Staff.AIR, Staff.WATER}, 23),
    EARTH_BOLT("Earth Bolt", new int[][]{new int[]{ItemID.CHAOS_RUNE, 1}, new int[]{ItemID.EARTH_RUNE, 3}, new int[]{ItemID.AIR_RUNE, 2}}, new Staff[]{Staff.AIR, Staff.EARTH}, 29),
    FIRE_BOLT("Fire Bolt", new int[][]{new int[]{ItemID.CHAOS_RUNE, 1}, new int[]{ItemID.FIRE_RUNE, 4}, new int[]{ItemID.AIR_RUNE, 3}}, new Staff[]{Staff.AIR, Staff.FIRE}, 35);

    private final String name;
    private final int[][] runes;
    private final Staff[] staffs;
    private final int requiredLevel;

    public static Stream<Spell> stream() {
        return Stream.of(Spell.values());
    }

    public static boolean checkIfPlayerHasNecessaryItems(Spell spell) {
        return Arrays.stream(spell.getRunes()).allMatch(rune -> {
            if (Rs2Inventory.hasItem(rune[0])) {
                Rs2Item runesItem = Rs2Inventory.get(rune[0]);
                return runesItem.quantity >= rune[1];
            }

            // if rune is not present check if equivalent staff is equipped
            Staff staff = Staff.getStaffByRuneId(rune[0]);
            if (staff != null) {
                return Rs2Equipment.hasEquipped(staff.getId());
            }

            return false;
        });
    }
}
