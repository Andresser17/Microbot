package net.runelite.client.plugins.microbot.doughmaker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@Getter
@RequiredArgsConstructor
public enum DoughItem {

    BREAD_DOUGH("Bread dough", ItemID.BREAD_DOUGH, "1"),
    PASTRY_DOUGH("Pastry dough", ItemID.PASTRY_DOUGH, "2"),
    PIZZA_BASE("Pizza base", ItemID.PIZZA_BASE,  "3");

    private final String itemName;
    private final int itemId;
    private final String key;
}
