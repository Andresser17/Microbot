package net.runelite.client.plugins.microbot.pvmfighter.enums;

import lombok.Getter;

@Getter
public enum CombatStyle {
    MELEE("Melee"),
    RANGED("Ranged"),
    MAGIC("Magic");

    private final String name;


    CombatStyle(String name) {
        this.name = name;
    }

}