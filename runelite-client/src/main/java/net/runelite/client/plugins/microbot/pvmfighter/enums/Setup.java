package net.runelite.client.plugins.microbot.pvmfighter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Setup {
    MELEE(true, CombatStyle.MELEE,  true, false, false, 50, true, "Melee"),
    MAGIC(true, CombatStyle.MAGIC, true, false, true, 50, true, "Magic"),
    RANGED(true, CombatStyle.RANGED, true, true, true, 50, true, "Ranged"),

    SLAYER_MELEE(true, CombatStyle.MELEE,  true, false, false, 50, true, "Slayer Melee"),
    SLAYER_MAGIC(true, CombatStyle.MAGIC, true, false, true, 50, true, "Slayer Magic"),
    SLAYER_RANGED(true, CombatStyle.RANGED, true, true, true, 50, true, "Slayer Ranged"),

    SLAYER_MELEE_LIT_BUG_LANTERN(true, CombatStyle.MELEE,  true, false, false, 50, true, "Slayer Melee Lit Bug Lantern"),
    SLAYER_MAGIC_LIT_BUG_LANTERN(true, CombatStyle.MAGIC, true, false, true, 50, true, "Slayer Magic Lit Bug Lantern"),
    SLAYER_RANGED_LIT_BUG_LANTERN(true, CombatStyle.RANGED, true, true, true, 50, true, "Slayer Ranged Lit Bug Lantern"),

    SLAYER_MELEE_MIRROR_SHIELD(true, CombatStyle.MELEE,  true, false, false, 50, true, "Slayer Melee Mirror Shield"),
    SLAYER_MAGIC_MIRROR_SHIELD(true, CombatStyle.MAGIC, true, false, true, 50, true, "Slayer Magic Mirror Shield"),
    SLAYER_RANGED_MIRROR_SHIELD(true, CombatStyle.RANGED, true, true, true, 50, true, "Slayer Ranged Mirror Shield"),

    SLAYER_MELEE_FACEMASK(true, CombatStyle.MELEE,  true, false, false, 50, true, "Slayer Melee Facemask"),
    SLAYER_MAGIC_FACEMASK(true, CombatStyle.MAGIC, true, false, true, 50, true, "Slayer Magic Facemask"),
    SLAYER_RANGED_FACEMASK(true, CombatStyle.RANGED, true, true, true, 50, true, "Slayer Ranged Facemask"),

    SLAYER_MELEE_SLAYER_GLOVES(true, CombatStyle.MELEE,  true, false, false, 50, true, "Slayer Melee Slayer Gloves"),
    SLAYER_MAGIC_SLAYER_GLOVES(true, CombatStyle.MAGIC, true, false, true, 50, true, "Slayer Magic Slayer Gloves"),
    SLAYER_RANGED_SLAYER_GLOVES(true, CombatStyle.RANGED, true, true, true, 50, true, "Slayer Ranged Slayer Gloves"),

    SLAYER_MELEE_ROCK_HAMMER(true, CombatStyle.MELEE,  true, false, false, 50, true, "Slayer Melee Rock Hammer"),
    SLAYER_MAGIC_ROCK_HAMMER(true, CombatStyle.MAGIC, true, false, true, 50, true, "Slayer Magic Rock Hammer"),
    SLAYER_RANGED_ROCK_HAMMER(true, CombatStyle.RANGED, true, true, true, 50, true, "Slayer Ranged Rock Hammer"),

    SLAYER_MELEE_KURASK(true, CombatStyle.MELEE,  true, false, false, 50, true, "Slayer Melee Kurask"),
    SLAYER_MAGIC_KURASK(true, CombatStyle.MAGIC, true, false, true, 50, true, "Slayer Magic Kurask"),
    SLAYER_RANGED_KURASK(true, CombatStyle.RANGED, true, false, true, 50, true, "Slayer Ranged Kurask"),

    SLAYER_MELEE_ANTIPOISON(true, CombatStyle.MELEE,  true, false, false, 50, true, "Slayer Melee Anti-poison"),
    SLAYER_MAGIC_ANTIPOISON(true, CombatStyle.MAGIC, true, false, true, 50, true, "Slayer Magic Anti-poison"),
    SLAYER_RANGED_ANTIPOISON(true, CombatStyle.RANGED, true, false, true, 50, true, "Slayer Ranged Anti-poison"),

    SLAYER_MELEE_RESTORE(true, CombatStyle.MELEE,  true, false, false, 50, true, "Slayer Melee Restore"),
    SLAYER_MAGIC_RESTORE(true, CombatStyle.MAGIC, true, false, true, 50, true, "Slayer Magic Restore"),
    SLAYER_RANGED_RESTORE(true, CombatStyle.RANGED, true, false, true, 50, true, "Slayer Ranged Restore");

    final boolean toggleCombat;
    final CombatStyle combatStyle;
    final boolean useSpecialAttack;
    final boolean useCannon;
    final boolean useSafeSpot;
    final int minimumHealthToRetrieve;

    // Consumables

    // Gear
    final boolean useInventorySetup;
    final String inventorySetupName;
}
