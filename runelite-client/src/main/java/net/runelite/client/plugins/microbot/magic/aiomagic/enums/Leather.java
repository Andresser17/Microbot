package net.runelite.client.plugins.microbot.magic.aiomagic.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@Getter
@RequiredArgsConstructor
public enum Leather {
    GREEN_DRAGONHIDE(ItemID.GREEN_DRAGONHIDE, ItemID.GREEN_DRAGON_LEATHER),
    BLUE_DRAGONHIDE(ItemID.BLUE_DRAGONHIDE, ItemID.BLUE_DRAGON_LEATHER),
    RED_DRAGONHIDE(ItemID.RED_DRAGONHIDE, ItemID.RED_DRAGON_LEATHER),
    BLACK_DRAGONHIDE(ItemID.BLACK_DRAGONHIDE, ItemID.BLACK_DRAGON_LEATHER);

    private final int hideId;
    private final int leatherId;
}
