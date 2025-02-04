package net.runelite.client.plugins.microbot.cooking.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;

@Getter
@RequiredArgsConstructor
public enum HeatingLocation {
    EDGEVILLE(BankLocation.EDGEVILLE, ObjectID.SINK_12279, CookingLocation.EDGEVILLE);

    final BankLocation bankLocation;
    final int waterSource;
    final CookingLocation cookingLocation;
}
