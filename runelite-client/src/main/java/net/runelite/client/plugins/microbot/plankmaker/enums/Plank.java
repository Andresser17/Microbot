package net.runelite.client.plugins.microbot.plankmaker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Plank {

    MAHOGANY("Mahogany plank", "Mahogany logs", 1500, "4");

    private final String name;
    private final String requiredLogs;
    private final int necessaryCoins;
    private final String stringKey;

    @Override
    public String toString() {
        return name;
    }
}
