package net.runelite.client.plugins.microbot.pvmfighter.enums;

import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
public enum RangedAmmo {
    // Bolts
    BRONZE_BOLTS("Bronze Bolts", ItemID.BRONZE_BOLTS, "Crossbow"),
    BONE_BOLTS("Bone Bolts", ItemID.BONE_BOLTS, "Crossbow"),
    BLURITE_BOLTS("Blurite Bolts", ItemID.BLURITE_BOLTS, "Crossbow"),
    IRON_BOLTS("Iron Bolts", ItemID.IRON_BOLTS, "Crossbow"),
    STEEL_BOLTS("Steel Bolts", ItemID.STEEL_BOLTS, "Crossbow"),
    MITHRIL_BOLTS("Mithril Bolts", ItemID.MITHRIL_BOLTS, "Crossbow"),
    ADAMANT_BOLTS("Adamant Bolts", ItemID.ADAMANT_BOLTS, "Crossbow"),
    KEBBIT_BOLTS("Kebbit Bolts", ItemID.KEBBIT_BOLTS, "Crossbow"),
    RUNITE_BOLTS("Runite Bolts", ItemID.RUNITE_BOLTS, "Crossbow"),

    // Arrows
    BRONZE_ARROWS("Bronze Arrows", ItemID.BRONZE_ARROW, "Bow"),
    IRON_ARROWS("Iron Arrows", ItemID.IRON_ARROW, "Bow"),
    STEEL_ARROWS("Steel Arrows", ItemID.STEEL_ARROW, "Bow"),
    MITHRIL_ARROWS("Mithril Arrows", ItemID.MITHRIL_ARROW, "Bow"),
    ADAMANT_ARROWS("Adamant Arrows", ItemID.ADAMANT_ARROW, "Bow"),
    RUNE_ARROWS("Rune Arrows", ItemID.RUNE_ARROW, "Bow");

    private final String name;
    private final int id;
    private final String weapon_type;

    RangedAmmo(String name, int id, String weapon_type) {
        this.name = name;
        this.id = id;
        this.weapon_type = weapon_type;
    }

}
