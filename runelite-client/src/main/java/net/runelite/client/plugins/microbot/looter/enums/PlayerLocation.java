package net.runelite.client.plugins.microbot.looter.enums;

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
    LOOT_FIELD(null, null),
    SAFE_SPOT(null, null),
    NEAREST_BANK(null, null),
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
            return area.contains(Rs2Player.getWorldLocation());
        }).findFirst();
        return currentLoc.orElse(PlayerLocation.OUTSIDE_POINT);
    }

    public void setWorldPoint(WorldPoint point, int size) {
        this.area = getWorldAreaFromCenter(point, size, size);
        this.point = point;
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
