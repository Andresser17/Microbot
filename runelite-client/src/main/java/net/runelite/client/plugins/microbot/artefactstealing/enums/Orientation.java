package net.runelite.client.plugins.microbot.artefactstealing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Orientation {
    SOUTH(0),
    WEST(512),
    NORTH(1024),
    EAST(1536);

    private final int angle;

    public static Stream<Orientation> stream() {
        return Stream.of(Orientation.values());
    }
}
