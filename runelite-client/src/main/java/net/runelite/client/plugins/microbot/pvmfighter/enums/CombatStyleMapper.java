package net.runelite.client.plugins.microbot.pvmfighter.enums;

/**
 * This class provides a utility method for mapping a string representation of an attack style to an AttackStyle enum.
 */
public class CombatStyleMapper {
    /**
     * Maps a string representation of an attack style to an AttackStyle enum.
     * The mapping is case-insensitive and can handle multiple attack styles separated by a comma.
     * @param style The string representation of the attack style.
     * @return The corresponding AttackStyle enum.
     */
    public static CombatStyle mapToCombatStyle(String style) {
        // Convert style to lowercase for case-insensitive matching
        String lowerCaseStyle = style.toLowerCase();

        // Check if the style contains multiple attack styles separated by a comma
        if (lowerCaseStyle.contains(",")) {
            return CombatStyle.MIXED;
        }

        // Check for presence of melee sub-styles
        boolean hasMelee = lowerCaseStyle.contains("melee") ||
                lowerCaseStyle.contains("crush") ||
                lowerCaseStyle.contains("slash") ||
                lowerCaseStyle.contains("stab");

        // Check for presence of magic-related styles
        boolean hasMagic = lowerCaseStyle.contains("magic");

        // Check for presence of ranged-related styles
        boolean hasRanged = lowerCaseStyle.contains("ranged");

        // Determine the appropriate AttackStyle based on the presence of different styles
        if (hasMelee && hasMagic && hasRanged) {
            return CombatStyle.MIXED;
        } else if ((hasMelee && hasMagic) || (hasMelee && hasRanged) || (hasMagic && hasRanged)) {
            return CombatStyle.MIXED;
        } else if (hasMelee) {
            return CombatStyle.MELEE;
        } else if (hasMagic) {
            return CombatStyle.MAGIC;
        } else if (hasRanged) {
            return CombatStyle.RANGED;
        } else {
            return CombatStyle.MIXED;  // Default case if style does not match any category.
        }
    }
}