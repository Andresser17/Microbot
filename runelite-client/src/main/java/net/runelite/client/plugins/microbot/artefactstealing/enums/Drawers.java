package net.runelite.client.plugins.microbot.artefactstealing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum Drawers {
    NORTH_27771(27771, new WorldPoint(1767, 3750, 0)),
    SOUTH_EAST_27772(27772, new WorldPoint(1773, 3730, 1)),
    SOUTH_EAST_27675(27675, new WorldPoint(1776, 3732, 1)),
    SOUTH_27674(27674, new WorldPoint(1761, 3733, 1)),
    SOUTH_27773(27773, new WorldPoint(1764, 3735, 1)),
    SOUTH_27675(27675, new WorldPoint(1764, 3737, 1)),
    SOUTH_WEST_27774(27774, new WorldPoint(1749, 3735, 1)),
    WEST_27775(27775, new WorldPoint(1747, 3749, 1)),
    WEST_27675(27675, new WorldPoint(1747, 3748, 1)),
    NORTH_WEST_27776(27776, new WorldPoint(1750, 3763, 1));

    final int id;
    final WorldPoint point;
}
