package net.runelite.client.plugins.microbot.artefactstealing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Artefact {
    STOLEN_PENDANT("Stolen pendant", ItemID.STOLEN_PENDANT),
    STOLEN_GARNET_RING("Stolen Garnet Ring", ItemID.STOLEN_GARNET_RING),
    STOLEN_CIRCLET("Stolen Circlet", ItemID.STOLEN_CIRCLET),
    STOLEN_FAMILY_HEIRLOOM("Stolen Family Heirloom", ItemID.STOLEN_FAMILY_HEIRLOOM),
    STOLEN_JEWELRY_BOX("Stolen Jewelry Box", ItemID.STOLEN_JEWELRY_BOX);


    private final String name;
    private final int id;

    @Override
    public String toString() {
        return name;
    }

    public static Stream<Artefact> stream() {
        return Stream.of(Artefact.values());
    }

    public static boolean hasArtefactInInventory() {
        return stream().anyMatch(artefact -> Rs2Inventory.hasItem(artefact.getId()));
    }
}
