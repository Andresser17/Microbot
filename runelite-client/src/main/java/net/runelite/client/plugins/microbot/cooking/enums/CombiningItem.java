package net.runelite.client.plugins.microbot.cooking.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum CombiningItem {

    PLAIN_PIZZA("Uncooked pizza", ItemID.UNCOOKED_PIZZA, 35, new int[][]{
            new int[]{
                    ItemID.PIZZA_BASE, // Item ID
                    9 // Item quantity
            },
            new int[]{ItemID.TOMATO, 9},
            new int[]{ItemID.CHEESE, 9},
    }, new int[][] {
            new int[]{ItemID.PIZZA_BASE, ItemID.TOMATO},
            new int[]{ItemID.INCOMPLETE_PIZZA, ItemID.CHEESE},
    }),
    ANCHOVY_PIZZA("Anchovy pizza", ItemID.ANCHOVY_PIZZA, 55, new int[][]{
            new int[]{ItemID.PLAIN_PIZZA, 14},
            new int[]{ItemID.ANCHOVIES, 14},
    }, new int[][] {
        new int[]{ItemID.PLAIN_PIZZA, ItemID.ANCHOVIES},
    }),
    RAW_ADMIRAL_PIE("Raw admiral pie", ItemID.RAW_ADMIRAL_PIE, 70, new int[][]{
            new int[]{ItemID.PIE_SHELL, 7},
            new int[]{ItemID.SALMON, 7},
            new int[]{ItemID.TUNA, 7},
            new int[]{ItemID.POTATO, 7},
    }, new int[][] {
            new int[]{ItemID.PIE_SHELL, ItemID.SALMON},
            new int[]{ItemID.PART_ADMIRAL_PIE, ItemID.TUNA},
            new int[]{ItemID.PART_ADMIRAL_PIE_7194, ItemID.POTATO},
    }),
    GUTHIX_REST("Guthix rest", ItemID.GUTHIX_REST3, 18, new int[][]{
            new int[]{ItemID.CUP_OF_HOT_WATER, 5},
            new int[]{ItemID.HARRALANDER, 5},
                new int[]{ItemID.MARRENTILL, 5},
                new int[]{ItemID.GUAM_LEAF, 10},
    }, new int[][] {
        new int[]{ItemID.CUP_OF_HOT_WATER, ItemID.HARRALANDER},
    }),
    HARRALANDER_POTION_UNF("Harralander potion (unf)", ItemID.HARRALANDER_POTION_UNF, 22, new int[][]{
            new int[]{ItemID.HARRALANDER, 14},
            new int[]{ItemID.VIAL_OF_WATER, 14}
    }, new int[][]{
            new int[]{ItemID.HARRALANDER, ItemID.VIAL_OF_WATER}
    }),
    COMBAT_POTION("Combat potion", ItemID.COMBAT_POTION3, 36, new int[][]{
            new int[]{ItemID.HARRALANDER_POTION_UNF, 14},
            new int[]{ItemID.GOAT_HORN_DUST, 14}
    }, new int[][]{
            new int[]{ItemID.HARRALANDER_POTION_UNF, ItemID.GOAT_HORN_DUST}
    });

    private final String combinedItemName;
    private final int combinedItemId;
    private final int levelRequired;
    private final int[][] ingredients;
    private final int[][] combiningOrder;

    private boolean hasLevelRequired() {
        return Rs2Player.getSkillRequirement(Skill.COOKING, this.getLevelRequired());
    }
}
