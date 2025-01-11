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
            Set.of(new int[]{ItemID.IRON_ORE, 1}),
            Varbits.BLAST_FURNACE_IRON_BAR,
            Varbits.BLAST_FURNACE_IRON_ORE,
            false,
            false
    ),
    STEEL_BAR(
            ItemID.STEEL_BAR,
            Set.of(new int[]{ItemID.IRON_ORE, 1}, new int[]{ItemID.COAL, 1}),
            Varbits.BLAST_FURNACE_STEEL_BAR,
            Varbits.BLAST_FURNACE_IRON_ORE,
            true,
            false
    ),
    GOLD_BAR(
            ItemID.GOLD_BAR,
            Set.of(new int[]{ItemID.GOLD_ORE, 1}),
            Varbits.BLAST_FURNACE_GOLD_BAR,
            Varbits.BLAST_FURNACE_GOLD_ORE,
            false,
            true
    ),
    MITHRIL_BAR(
            ItemID.MITHRIL_BAR,
            Set.of(new int[]{ItemID.MITHRIL_ORE, 1}, new int[]{ItemID.COAL, 2}),
            Varbits.BLAST_FURNACE_MITHRIL_BAR,
            Varbits.BLAST_FURNACE_MITHRIL_ORE,
            true,
            false
    ),
    ADAMANTITE_BAR(
            ItemID.ADAMANTITE_BAR,
            Set.of(new int[]{ItemID.ADAMANTITE_ORE, 1}, new int[]{ItemID.COAL, 6}),
            Varbits.BLAST_FURNACE_ADAMANTITE_BAR,
            Varbits.BLAST_FURNACE_ADAMANTITE_ORE,
            true,
            false
    ),
    RUNITE_BAR(
            ItemID.RUNITE_BAR,
            Set.of(new int[]{ItemID.RUNITE_ORE, 1}, new int[]{ItemID.COAL, 8}),
            Varbits.BLAST_FURNACE_RUNITE_BAR,
            Varbits.BLAST_FURNACE_RUNITE_ORE,
            true,
            false
    );

    private final int id;
    private final Set<int[]> necessaryOres;
    private final int barVarbit;
    private final int oreVarbit;
    private final boolean requiresCoalBag;
    private final boolean requiresGoldsmithGloves;

    public static Stream<Bars> stream() {
        return Stream.of(Bars.values());
    }
}
