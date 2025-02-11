package net.runelite.client.plugins.microbot.artefactstealing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Task {
    NO_TASK("No task", 0, PlayerLocation.OUTSIDE_POINT, null, null),
    NORTH("North", 1, PlayerLocation.NORTH_HOUSE_BASE_FLOOR, null, new Drawers[]{Drawers.NORTH_27771}),
    SOUTH_EAST("South East", 2, PlayerLocation.SOUTH_EAST_HOUSE_BASE_FLOOR, PlayerLocation.SOUTH_EAST_HOUSE_FIRST_FLOOR, new Drawers[]{Drawers.SOUTH_EAST_27772, Drawers.SOUTH_EAST_27675}),
    SOUTH("South", 3, PlayerLocation.SOUTH_HOUSE_BASE_FLOOR, PlayerLocation.SOUTH_HOUSE_FIRST_FLOOR, new Drawers[]{Drawers.SOUTH_27773, Drawers.SOUTH_27675, Drawers.SOUTH_27674}),
    SOUTH_WEST("South West", 4, PlayerLocation.SOUTH_WEST_HOUSE_BASE_FLOOR, PlayerLocation.SOUTH_WEST_HOUSE_FIRST_FLOOR, new Drawers[]{Drawers.SOUTH_WEST_27774}),
    WEST("West", 5, PlayerLocation.WEST_HOUSE_BASE_FLOOR, PlayerLocation.WEST_HOUSE_FIRST_FLOOR, new Drawers[]{Drawers.WEST_27675, Drawers.WEST_27775}),
    NORTH_WEST("North West", 6, PlayerLocation.NORTH_WEST_HOUSE_BASE_FLOOR, PlayerLocation.NORTH_WEST_HOUSE_FIRST_FLOOR, new Drawers[]{Drawers.NORTH_WEST_27776}),
    CAUGHT("Caught", 7, PlayerLocation.OUTSIDE_POINT, null, null),
    RETRIEVED("Retrieved", 8, PlayerLocation.CAPTAIN_LOCATION, null, null);


    final String name;
    final int varbitValue;
    final PlayerLocation baseFloor;
    final PlayerLocation firstFloor;
    final Drawers[] drawers;

    public static Stream<Task> stream() {
        return Stream.of(Task.values());
    }

    public static Task findTaskByVarbitValue(int value) {
        return stream().filter(task -> task.getVarbitValue() == value).findFirst().orElse(null);
    }
}
