package net.runelite.client.plugins.microbot.artefactstealing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Patrolman {
    WEST_BRIDGE(6974, null),
    NORTH_WEST_CORRIDOR(6973, null),
    NORTH_CORRIDOR(6975, null),
    FAR_NORTH_BRIDGE(6976, null),
    SOUTH_WEST_CORRIDOR(6979, null),
    SOUTH_EAST_CORRIDOR(6978, null),
    NORTH_EAST_BRIDGE(6980, new WorldPoint(1777, 3746, 0)),
    SOUTH_EAST_BRIDGE(6980, new WorldPoint(1780, 3731, 0));

    private final int id;
    private final WorldPoint point;

    public static Stream<Patrolman> stream() {
        return Stream.of(Patrolman.values());
    }
}
