package net.runelite.client.plugins.microbot.pvmfighter.enums;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum Spell {
    // Strikes
    WIND_STRIKE("Wind Strike", new int[][]{new int[]{ItemID.MIND_RUNE, 1}, new int[]{ItemID.AIR_RUNE, 1}}, 1),
    WATER_STRIKE("Water Strike", new int[][]{new int[]{ItemID.MIND_RUNE, 1}, new int[]{ItemID.WATER_RUNE, 1}, new int[]{ItemID.AIR_RUNE, 1}}, 5),
    EARTH_STRIKE("Earth Strike", new int[][]{new int[]{ItemID.MIND_RUNE, 1}, new int[]{ItemID.EARTH_RUNE, 2}, new int[]{ItemID.AIR_RUNE, 1}}, 9),
    FIRE_STRIKE("Fire Strike", new int[][]{new int[]{ItemID.MIND_RUNE, 1}, new int[]{ItemID.FIRE_RUNE, 3}, new int[]{ItemID.AIR_RUNE, 2}}, 13),

    // Bolts
    WIND_BOLT("Wind Bolt", new int[][]{new int[]{ItemID.CHAOS_RUNE, 1}, new int[]{ItemID.AIR_RUNE, 2}}, 17),
    WATER_BOLT("Water Bolt", new int[][]{new int[]{ItemID.CHAOS_RUNE, 1}, new int[]{ItemID.WATER_RUNE, 2}, new int[]{ItemID.AIR_RUNE, 2}}, 23),
    EARTH_BOLT("Earth Bolt", new int[][]{new int[]{ItemID.CHAOS_RUNE, 1}, new int[]{ItemID.EARTH_RUNE, 3}, new int[]{ItemID.AIR_RUNE, 2}}, 29),
    FIRE_BOLT("Fire Bolt", new int[][]{new int[]{ItemID.CHAOS_RUNE, 1}, new int[]{ItemID.FIRE_RUNE, 4}, new int[]{ItemID.AIR_RUNE, 3}}, 35);

    private final String name;
    private final int[][] runes;
    private final int requiredLevel;

    Spell(String name, int[][] runes, int requiredLevel) {
        this.name = name;
        this.runes = runes;
        this.requiredLevel = requiredLevel;
    }

    public static Stream<Spell> stream() {
        return Stream.of(Spell.values());
    }

    public static boolean checkIfPlayerHasNecessaryRunes(Spell spell) {
        return Arrays.stream(spell.getRunes()).allMatch(runes -> {
            if (Rs2Inventory.hasItem(runes[0])) {
                Rs2Item runesItem = Rs2Inventory.get(runes[0]);
                return runesItem.quantity >= runes[1];
            }
            return false;
        });
    }
}
