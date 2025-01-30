package net.runelite.client.plugins.microbot.magic.aiomagic.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Herb {
    GUAM_LEAF("Guam leaf", ItemID.GUAM_LEAF, "Grimy Guam Leaf", ItemID.GRIMY_GUAM_LEAF, 3),
    MARRENTILL("Marrentill", ItemID.MARRENTILL, "Grimy Marrentill", ItemID.GRIMY_MARRENTILL, 5),
    TARROMIN("Tarromin", ItemID.TARROMIN, "Grimy Tarromin", ItemID.GRIMY_TARROMIN, 11),
    HARRALANDER("Harralander", ItemID.HARRALANDER, "Grimy Harralander", ItemID.GRIMY_HARRALANDER, 20),
    RANARR_WEED("Ranarr Weed", ItemID.RANARR_WEED, "Grimy Ranarr Weed", ItemID.GRIMY_RANARR_WEED, 25),
    TOADFLAX("Toadflax", ItemID.TOADFLAX, "Grimy Toadflax", ItemID.GRIMY_TOADFLAX, 30),
    IRIT_LEAF("Irit leaf", ItemID.IRIT_LEAF, "Grimy Irit Leaf", ItemID.GRIMY_IRIT_LEAF, 40),
    AVANTOE("Avantoe", ItemID.AVANTOE, "Grimy Avantoe", ItemID.GRIMY_AVANTOE, 48),
    KWUARM("Kwuarm", ItemID.KWUARM, "Grimy Kwuarm", ItemID.GRIMY_KWUARM, 54),
    HUASCA("Huasca", ItemID.HUASCA, "Grimy Huasca", ItemID.GRIMY_HUASCA, 58),
    SNAPDRAGON("Snapdragon", ItemID.SNAPDRAGON, "Grimy Snapdragon", ItemID.GRIMY_SNAPDRAGON, 59),
    CADANTINE("Cadantine", ItemID.CADANTINE, "Grimy Cadantine", ItemID.GRIMY_CADANTINE, 65),
    LANTADYME("Lantadyme", ItemID.LANTADYME, "Grimy Lantadyme", ItemID.GRIMY_LANTADYME, 67),
    DWARF_WEED("Dwarf Weed", ItemID.DWARF_WEED, "Grimy Dwarf Weed", ItemID.GRIMY_DWARF_WEED, 70),
    TORSTOL("Torstol", ItemID.TORSTOL, "Grimy Torstol", ItemID.GRIMY_TORSTOL, 75);

    private final String name;
    private final int cleanId;
    private final String grimyName;
    private final int grimyId;
    private final int requiredLevel;

    public static Stream<Herb> stream() {
        return Stream.of(Herb.values());
    }

    public static Herb[] getAllHerbsExcept(String[] herbNamesToExclude) {
        return stream().filter(herb -> {
            Optional<String> nameFound = Arrays.stream(herbNamesToExclude).filter(herbName -> herbName.equalsIgnoreCase(herb.getName())).findFirst();
            return nameFound.isEmpty();
        }).toArray(Herb[]::new);
    }

    public static Herb[] getGrimyHerbByCleanName(String[] herbNames) {
        return stream().filter(herb -> {
            Optional<String> nameFound = Arrays.stream(herbNames).filter(herbName -> herbName.equalsIgnoreCase(herb.getName())).findFirst();
            return nameFound.isPresent();
        }).toArray(Herb[]::new);
    }
}
