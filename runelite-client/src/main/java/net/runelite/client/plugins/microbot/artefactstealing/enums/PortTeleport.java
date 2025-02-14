package net.runelite.client.plugins.microbot.artefactstealing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@Getter
@RequiredArgsConstructor
public enum PortTeleport {
    KHAREDST_MEMOIRS("Kharedst's memoirs", ItemID.KHAREDSTS_MEMOIRS),
    BOOK_OF_THE_DEAD("Book of the dead", ItemID.BOOK_OF_THE_DEAD);

    final String name;
    final int id;
}
