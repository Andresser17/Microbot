package net.runelite.client.plugins.microbot.pvmfighter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum SlayerMaster {
    VANNAKA("Vannaka", new WorldArea(3144, 9912, 6, 4, 0), new WorldPoint(3145, 9913, 0), 40);

    final String name;
    final WorldArea worldArea;
    final WorldPoint worldPoint;
    final int requiredCombatLevel;
}
