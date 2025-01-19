package net.runelite.client.plugins.microbot.blackjack.enums;

import lombok.Getter;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;

import java.util.stream.Stream;

@Getter
public enum Location {
    NARDAH_BANK(new WorldArea(3287, 3465, 23, 27, 0), new WorldPoint(3302, 3489, 0)),
    OUTSIDE_POINT(new WorldArea(0, 0, 0, 0, 0), new WorldPoint(0, 0, 0));

    private WorldArea area;
    private WorldPoint point;

    Location(WorldArea area, WorldPoint point) {
        this.area = area;
        this.point = point;
    }

    public static Stream<Location> stream() {
        return Stream.of(Location.values());
    }

}
