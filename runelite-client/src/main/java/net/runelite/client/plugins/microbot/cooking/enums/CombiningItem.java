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

    PLAIN_PIZZA("Uncooked pizza", ItemID.UNCOOKED_PIZZA, 35, new Ingredients[]{
            Ingredients.PIZZA_BASE,
            Ingredients.TOMATO,
            Ingredients.CHEESE,
    }, new Ingredients[][] {
            new Ingredients[]{Ingredients.PIZZA_BASE, Ingredients.TOMATO},
            new Ingredients[]{Ingredients.INCOMPLETE_PIZZA, Ingredients.CHEESE},
    }),
    ANCHOVY_PIZZA("Anchovy pizza", ItemID.ANCHOVY_PIZZA, 55, new Ingredients[]{
        Ingredients.PLAIN_PIZZA, Ingredients.ANCHOVIES,
    }, new Ingredients[][] {
        new Ingredients[]{Ingredients.PLAIN_PIZZA, Ingredients.ANCHOVIES},
    });

    private final String combinedItemName;
    private final int combinedItemId;
    private final int levelRequired;
    private final Ingredients[] ingredients;
    private final Ingredients[][] combiningOrder;

    private boolean hasLevelRequired() {
        return Rs2Player.getSkillRequirement(Skill.COOKING, this.getLevelRequired());
    }
}
