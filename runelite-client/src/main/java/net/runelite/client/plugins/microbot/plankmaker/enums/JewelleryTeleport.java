package net.runelite.client.plugins.microbot.plankmaker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;

import java.util.Optional;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum JewelleryTeleport {
    RING_OF_ELEMENTS(new int[]{ItemID.RING_OF_THE_ELEMENTS_26818}, ItemID.RING_OF_THE_ELEMENTS),
    AMULET_OF_GLORY(new int[]{
            ItemID.AMULET_OF_GLORY6,
            ItemID.AMULET_OF_GLORY5,
            ItemID.AMULET_OF_GLORY4,
            ItemID.AMULET_OF_GLORY3,
            ItemID.AMULET_OF_GLORY2,
            ItemID.AMULET_OF_GLORY1
    }, ItemID.AMULET_OF_GLORY);

    final int[] chargedIds;
    final int unchargedId;

    public static Stream<JewelleryTeleport> stream() {
        return Stream.of(JewelleryTeleport.values());
    }

    public boolean hasUnchargedJewelleryEquipped() {
        return Rs2Equipment.hasEquipped(getUnchargedId());
    }

    public static JewelleryTeleport findByUnchargedId(int unchargedId) {
        Optional<JewelleryTeleport> optional = stream().filter(jewelleryTeleport -> jewelleryTeleport.getUnchargedId() == unchargedId).findFirst();
        return optional.orElse(null);
    }
}
