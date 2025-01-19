package net.runelite.client.plugins.microbot.plankmaker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.Jewellery;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum Teleport {
    EARTH_ALTAR("Earth altar", Location.LUMBER_YARD, JewelleryTeleport.RING_OF_ELEMENTS),
    EDGEVILLE("Edgeville", Location.EDGEVILLE_BANK, JewelleryTeleport.AMULET_OF_GLORY);

    final String name;
    final Location location;
    final JewelleryTeleport jewelleryTeleport;
}
