package net.runelite.client.plugins.microbot.cooking.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Recipient {
    CUP("Empty cup", "Cup of water", "Cup of hot water"),
    BOWL("Bowl", "Bowl of water", "Bowl of hot water");

    final String name;
    final String filledName;
    final String heated;
}
