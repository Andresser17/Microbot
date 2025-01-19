package net.runelite.client.plugins.microbot.magic.aiomagic.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum EnchantSpell {
    // Bolts
//    OPAL_BOLT(MagicAction.ENCHANT_OPAL_BOLT, new int[][]{
//            new int[]{ItemID.AIR_RUNE, 2},
//            new int[]{ItemID.COSMIC_RUNE, 1},
//    }, new Staff[]{Staff.AIR}, 4,),
//    SAPPHIRE_BOLT(MagicAction.ENCHANT_SAPPHIRE_BOLT, new int[][]{
//            new int[]{ItemID.WATER_RUNE, 1},
//            new int[]{ItemID.COSMIC_RUNE, 1},
//            new int[]{ItemID.MIND_RUNE, 1},
//    }, new Staff[]{Staff.WATER}, 7),
//    JADE_BOLT(MagicAction.ENCHANT_JADE_BOLT, new int[][]{
//            new int[]{ItemID.EARTH_RUNE, 2},
//            new int[]{ItemID.COSMIC_RUNE, 1},
//    }, new Staff[]{Staff.EARTH}, 14),
//    PEARL_BOLT(MagicAction.ENCHANT_PEARL_BOLT, new int[][]{
//            new int[]{ItemID.WATER_RUNE, 2},
//            new int[]{ItemID.COSMIC_RUNE, 1},
//    }, new Staff[]{Staff.WATER}, 24),
//    EMERALD_BOLT(MagicAction.ENCHANT_EMERALD_BOLT, new int[][]{
//            new int[]{ItemID.AIR_RUNE, 3},
//            new int[]{ItemID.COSMIC_RUNE, 1},
//            new int[]{ItemID.NATURE_RUNE, 1},
//    }, new Staff[]{Staff.AIR}, 27),
//    TOPAZ_BOLT(MagicAction.ENCHANT_TOPAZ_BOLT, new int[][]{
//            new int[]{ItemID.FIRE_RUNE, 2},
//            new int[]{ItemID.COSMIC_RUNE, 1},
//    }, new Staff[]{Staff.FIRE}, 29),
//    RUBY_BOLT(MagicAction.ENCHANT_RUBY_BOLT, new int[][]{
//            new int[]{ItemID.FIRE_RUNE, 5},
//            new int[]{ItemID.COSMIC_RUNE, 1},
//            new int[]{ItemID.BLOOD_RUNE, 1},
//    }, new Staff[]{Staff.FIRE}, 49),
//    DIAMOND_BOLT(MagicAction.ENCHANT_DIAMOND_BOLT, new int[][]{
//            new int[]{ItemID.EARTH_RUNE, 10},
//            new int[]{ItemID.COSMIC_RUNE, 1},
//            new int[]{ItemID.LAW_RUNE, 1},
//    }, new Staff[]{Staff.EARTH}, 57),
//    DRAGONSTONE_BOLT(MagicAction.ENCHANT_DRAGONSTONE_BOLT, new int[][]{
//            new int[]{ItemID.EARTH_RUNE, 15},
//            new int[]{ItemID.COSMIC_RUNE, 1},
//            new int[]{ItemID.SOUL_RUNE, 1},
//    }, new Staff[]{Staff.EARTH}, 68),
//    ONYX_BOLT(MagicAction.ENCHANT_ONYX_BOLT, new int[][]{
//            new int[]{ItemID.FIRE_RUNE, 20},
//            new int[]{ItemID.COSMIC_RUNE, 1},
//            new int[]{ItemID.DEATH_RUNE, 1},
//    }, new Staff[]{Staff.FIRE}, 87),
    // Jewellery
    SAPPHIRE_OPAL_JEWELLERY(MagicAction.ENCHANT_SAPPHIRE_JEWELLERY, Set.of(
            new int[]{ItemID.WATER_RUNE, 1},
            new int[]{ItemID.COSMIC_RUNE, 1}
    ), new Staff[]{Staff.WATER}, 7, Set.of(
            Jewellery.SAPPHIRE_RING,
            Jewellery.SAPPHIRE_NECKLACE,
            Jewellery.SAPPHIRE_BRACELET,
            Jewellery.SAPPHIRE_AMULET,

            Jewellery.OPAL_RING,
            Jewellery.OPAL_NECKLACE,
            Jewellery.OPAL_BRACELET,
            Jewellery.OPAL_AMULET

    )),
    EMERALD_JADE_JEWELLERY(MagicAction.ENCHANT_EMERALD_JEWELLERY, Set.of(
            new int[]{ItemID.AIR_RUNE, 3},
            new int[]{ItemID.COSMIC_RUNE, 1}
    ), new Staff[]{Staff.AIR}, 27, Set.of(
            Jewellery.EMERALD_RING,
            Jewellery.EMERALD_NECKLACE,
            Jewellery.EMERALD_BRACELET,
            Jewellery.EMERALD_AMULET,

            Jewellery.JADE_RING,
            Jewellery.JADE_NECKLACE,
            Jewellery.JADE_BRACELET,
            Jewellery.JADE_AMULET
    )),
    RUBY_TOPAZ_JEWELLERY(MagicAction.ENCHANT_RUBY_JEWELLERY, Set.of(
            new int[]{ItemID.FIRE_RUNE, 5},
            new int[]{ItemID.COSMIC_RUNE, 1}
    ), new Staff[]{Staff.FIRE}, 49, Set.of(
            Jewellery.RUBY_RING,
            Jewellery.RUBY_NECKLACE,
            Jewellery.RUBY_BRACELET,
            Jewellery.RUBY_AMULET,

            Jewellery.TOPAZ_RING,
            Jewellery.TOPAZ_NECKLACE,
            Jewellery.TOPAZ_BRACELET,
            Jewellery.TOPAZ_AMULET
    )),
    DIAMOND_JEWELLERY(MagicAction.ENCHANT_DIAMOND_JEWELLERY, Set.of(
            new int[]{ItemID.EARTH_RUNE, 10},
            new int[]{ItemID.COSMIC_RUNE, 1}
    ), new Staff[]{Staff.EARTH}, 57, Set.of(
            Jewellery.DIAMOND_RING,
            Jewellery.DIAMOND_NECKLACE,
            Jewellery.DIAMOND_BRACELET,
            Jewellery.DIAMOND_AMULET
    )),
    DRAGONSTONE_JEWELLERY(MagicAction.ENCHANT_DRAGONSTONE_JEWELLERY, Set.of(
            new int[]{ItemID.EARTH_RUNE, 15},
            new int[]{ItemID.WATER_RUNE, 15},
            new int[]{ItemID.COSMIC_RUNE, 1}
    ), new Staff[]{Staff.EARTH, Staff.WATER}, 68, Set.of(
            Jewellery.DRAGONSTONE_RING,
            Jewellery.DRAGONSTONE_NECKLACE,
            Jewellery.DRAGONSTONE_BRACELET,
            Jewellery.DRAGONSTONE_AMULET
    )),
    ONYX_JEWELLERY(MagicAction.ENCHANT_ONYX_JEWELLERY, Set.of(
            new int[]{ItemID.EARTH_RUNE, 20},
            new int[]{ItemID.FIRE_RUNE, 20},
            new int[]{ItemID.COSMIC_RUNE, 1}
    ), new Staff[]{Staff.EARTH, Staff.FIRE}, 87, Set.of(
            Jewellery.ONYX_RING,
            Jewellery.ONYX_NECKLACE,
            Jewellery.ONYX_BRACELET,
            Jewellery.ONYX_AMULET
    )),
    ZENYTE_JEWELLERY(MagicAction.ENCHANT_ZENYTE_JEWELLERY, Set.of(
            new int[]{ItemID.BLOOD_RUNE, 20},
            new int[]{ItemID.COSMIC_RUNE, 1},
            new int[]{ItemID.SOUL_RUNE, 20}
    ), new Staff[]{}, 93, Set.of(
            Jewellery.ZENYTE_RING,
            Jewellery.ZENYTE_NECKLACE,
            Jewellery.ZENYTE_BRACELET,
            Jewellery.ZENYTE_AMULET
    ));
    
    private final MagicAction magicAction;
    private final Set<int[]> runes;
    private final Staff[] staffs;
    private final int requiredLevel;
    private final Set<Jewellery> jewelleries; // [input, result]

    public static boolean checkIfPlayerHasNecessaryRunes(EnchantSpell spell) {
        return spell.getRunes().stream().allMatch(rune -> {
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
