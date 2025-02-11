package net.runelite.client.plugins.microbot.artefactstealing.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.Optional;
import java.util.stream.Stream;

@Getter
@Slf4j
public enum PlayerLocation {
    CAPTAIN_LOCATION(new WorldArea(1838, 3746, 13, 13, 0), new WorldPoint(1845, 3752, 0)),
    HOUSE_ZONE(new WorldArea(1743, 3726, 42, 50, 0), new WorldPoint(1778, 3748, 0)),
    BANK_LOCATION(new WorldArea(1794, 3784, 18, 10, 0), new WorldPoint(1803, 3788, 0)),
    NORTH_WEST_HOUSE_BASE_FLOOR(new WorldArea(1746, 3755, 10, 10, 0), new WorldPoint(1749, 3757, 0)),
    NORTH_WEST_HOUSE_FIRST_FLOOR(new WorldArea(1746, 3755, 10, 10, 1), new WorldPoint(1749, 3757, 1)),
    WEST_HOUSE_BASE_FLOOR(new WorldArea(1746, 3747, 8, 6, 0), new WorldPoint(1751, 3749, 0)),
    WEST_HOUSE_FIRST_FLOOR(new WorldArea(1746, 3747, 8, 6, 1), new WorldPoint(1751, 3750, 1)),
    SOUTH_WEST_HOUSE_BASE_FLOOR(new WorldArea(1745, 3729, 8, 8, 0), new WorldPoint(1749, 3733, 0)),
    SOUTH_WEST_HOUSE_FIRST_FLOOR(new WorldArea(1745, 3729, 8, 8, 1), new WorldPoint(1749, 3731, 1)),
    NORTH_HOUSE_BASE_FLOOR(new WorldArea(1766, 3749, 7, 4, 0), new WorldPoint(1769, 3751, 0)),
    SOUTH_HOUSE_BASE_FLOOR(new WorldArea(1760, 3729, 10, 10, 0), new WorldPoint(1766, 3732, 0)),
    SOUTH_HOUSE_FIRST_FLOOR(new WorldArea(1760, 3729, 10, 10, 1), new WorldPoint(1767, 3733, 1)),
    SOUTH_EAST_HOUSE_BASE_FLOOR(new WorldArea(1772, 3729, 6, 8, 0), new WorldPoint(1776, 3732, 0)),
    SOUTH_EAST_HOUSE_FIRST_FLOOR(new WorldArea(1772, 3729, 6, 8, 1), new WorldPoint(1775, 3730, 1)),
    OUTSIDE_POINT(new WorldArea(0, 0, 0, 0, 0), new WorldPoint(0, 0, 0));

    private WorldArea area;
    private WorldPoint point;

    PlayerLocation(WorldArea area, WorldPoint point) {
        this.area = area;
        this.point = point;
    }

    public static Stream<PlayerLocation> stream() {
        return Stream.of(PlayerLocation.values());
    }

    public static PlayerLocation checkCurrentPlayerLocation() {
        Optional<PlayerLocation> currentLoc = stream().filter((location) -> {
            WorldArea area = location.getArea();
            if (area != null) return area.contains(Rs2Player.getWorldLocation());
            return false;
        }).findFirst();
        return currentLoc.orElse(PlayerLocation.OUTSIDE_POINT);
    }

    public void setWorldPoint(WorldPoint point) {
        this.point = point;
    }

    public void setWorldPoint(WorldPoint point, int size) {
        this.area = getWorldAreaFromCenter(point, size, size);
        this.point = point;
    }

    public void setWorldArea(WorldArea worldArea) {
        this.area = worldArea;
    }

    /**
     * Creates a WorldArea around a central WorldPoint.
     *
     * @param centerPoint The central WorldPoint.
     * @param width       The width of the area in tiles.
     * @param height      The height of the area in tiles.
     * @return The WorldArea object representing the rectangular area.
     */
    private static WorldArea getWorldAreaFromCenter(WorldPoint centerPoint, int width, int height) {
        int halfWidth = width / 2;
        int halfHeight = height / 2;

        // Calculate the northwest corner (upper-left)
        int nwX = centerPoint.getX() - halfWidth;
        int nwY = centerPoint.getY() - halfHeight;

        // Return a new WorldArea
        return new WorldArea(nwX, nwY, width, height, centerPoint.getPlane());
    }
}
