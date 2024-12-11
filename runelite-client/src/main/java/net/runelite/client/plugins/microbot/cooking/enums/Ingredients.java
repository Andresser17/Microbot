package net.runelite.client.plugins.microbot.cooking.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;


@Getter
@RequiredArgsConstructor
public enum Ingredients {
    PIZZA_BASE("Pizza base", ItemID.PIZZA_BASE, 9),
    TOMATO("Tomato", ItemID.TOMATO, 9),
    CHEESE("Cheese", ItemID.CHEESE, 9),
    INCOMPLETE_PIZZA("Incomplete pizza", ItemID.INCOMPLETE_PIZZA, 0),
    PLAIN_PIZZA("Plain pizza", ItemID.PLAIN_PIZZA, 14),
    ANCHOVIES("Anchovies", ItemID.ANCHOVIES, 14);

    private final String name;
    private final int id;
    private final int quantityToWithdraw;
}
