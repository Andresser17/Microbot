package net.runelite.client.plugins.microbot.blastoisefurnace.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.client.plugins.microbot.pvmfighter.enums.Spell;

import java.util.Set;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Bars {
    IRON_BAR(
            ItemID.IRON_BAR,
            ItemID.IRON_ORE,
            1,
            0,
            Varbits.BLAST_FURNACE_IRON_BAR,
            Varbits.BLAST_FURNACE_IRON_ORE,
            false,
            false
    ),
    STEEL_BAR(
            ItemID.STEEL_BAR,
            ItemID.IRON_ORE,
            1,
            1,
            Varbits.BLAST_FURNACE_STEEL_BAR,
            Varbits.BLAST_FURNACE_IRON_ORE,
            true,
            false
    ),
    GOLD_BAR(
            ItemID.GOLD_BAR,
            ItemID.GOLD_ORE,
            1,
            0,
            Varbits.BLAST_FURNACE_GOLD_BAR,
            Varbits.BLAST_FURNACE_GOLD_ORE,
            false,
            true
    ),
    MITHRIL_BAR(
            ItemID.MITHRIL_BAR,
            ItemID.MITHRIL_ORE,
            1,
            2,
            Varbits.BLAST_FURNACE_MITHRIL_BAR,
            Varbits.BLAST_FURNACE_MITHRIL_ORE,
            true,
            false
    ),
    ADAMANTITE_BAR(
            ItemID.ADAMANTITE_BAR,
            ItemID.ADAMANTITE_ORE,
            1,
            6,
            Varbits.BLAST_FURNACE_ADAMANTITE_BAR,
            Varbits.BLAST_FURNACE_ADAMANTITE_ORE,
            true,
            false
    ),
    RUNITE_BAR(
            ItemID.RUNITE_BAR,
            ItemID.RUNITE_ORE,
            1,
            8,
            Varbits.BLAST_FURNACE_RUNITE_BAR,
            Varbits.BLAST_FURNACE_RUNITE_ORE,
            true,
            false
    );

    private final int id;
    private final int oreId;
    private final int oreQuantity;
    private final int coalQuantity;
    private final int barVarbit;
    private final int oreVarbit;
    private final boolean requiresCoalBag;
    private final boolean requiresGoldsmithGloves;

    public static Stream<Bars> stream() {
        return Stream.of(Bars.values());
    }
}
