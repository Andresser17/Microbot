package net.runelite.client.plugins.microbot.util.grounditem;

import lombok.Getter;
import net.runelite.api.coords.WorldArea;

@Getter
public class LootingParameters {

    private int minValue, maxValue, range, minItems, minQuantity, minInvSlots;
    private WorldArea area;
    private boolean delayedLooting, antiLureProtection;
    private String[] names;
    private int[] ids;

    /**
     * This constructor is used to create a new LootingParameters object.
     * It sets the minimum value, maximum value, range, minimum items, delayed looting, and anti-lure protection.
     *
     * @param minValue           The minimum value of the items to be looted.
     * @param maxValue           The maximum value of the items to be looted.
     * @param range              The range within which the items to be looted are located.
     * @param minItems           The minimum number of items to be looted.
     * @param minInvSlots        The minimum number of inventory slots to have open.
     * @param delayedLooting     A boolean indicating whether looting should be delayed.
     * @param antiLureProtection A boolean indicating whether anti-lure protection should be enabled.
     */
    public LootingParameters(int minValue, int maxValue, int range, int minItems, int minInvSlots, boolean delayedLooting, boolean antiLureProtection) {
        setValues(minValue, maxValue, range, minItems, 1, minInvSlots, delayedLooting, antiLureProtection, null);
    }

    /**
     * This constructor is used to create a new LootingParameters object.
     * It sets the range, minimum items, minimum quantity, delayed looting, anti-lure protection, and names of the items to be looted.
     *
     * @param range              The range within which the items to be looted are located.
     * @param minItems           The minimum number of items to be looted.
     * @param minQuantity        The minimum quantity of items to be looted.
     * @param minInvSlots        The minimum number of inventory slots to have open.
     * @param delayedLooting     A boolean indicating whether looting should be delayed.
     * @param antiLureProtection A boolean indicating whether anti-lure protection should be enabled.
     * @param names              The names of the items to be looted.
     */
    public LootingParameters(int range, int minItems, int minQuantity, int minInvSlots, boolean delayedLooting, boolean antiLureProtection, String... names) {
        setValues(0, 0, range, minItems, minQuantity, minInvSlots, delayedLooting, antiLureProtection, names);
    }

    /**
     * This constructor is used to create a new LootingParameters object.
     * It sets the minimum value, maximum value, range, minimum items, delayed looting, and anti-lure protection.
     *
     * @param minValue           The minimum value of the items to be looted.
     * @param maxValue           The maximum value of the items to be looted.
     * @param area              The range within which the items to be looted are located.
     * @param minItems           The minimum number of items to be looted.
     * @param minInvSlots        The minimum number of inventory slots to have open.
     * @param delayedLooting     A boolean indicating whether looting should be delayed.
     * @param antiLureProtection A boolean indicating whether anti-lure protection should be enabled.
     */
    public LootingParameters(int minValue, int maxValue, WorldArea area, int minItems, int minInvSlots, boolean delayedLooting, boolean antiLureProtection) {
        setValues(minValue, maxValue, 0, minItems, 1, minInvSlots, delayedLooting, antiLureProtection, null);
        this.area = area;
    }

    /**
     * This constructor is used to create a new LootingParameters object.
     * It sets the range, minimum items, minimum quantity, delayed looting, anti-lure protection, and names of the items to be looted.
     *
     * @param area              The range within which the items to be looted are located.
     * @param minItems           The minimum number of items to be looted.
     * @param minQuantity        The minimum quantity of items to be looted.
     * @param minInvSlots        The minimum number of inventory slots to have open.
     * @param delayedLooting     A boolean indicating whether looting should be delayed.
     * @param antiLureProtection A boolean indicating whether anti-lure protection should be enabled.
     * @param names              The names of the items to be looted.
     */
    public LootingParameters(WorldArea area, int minItems, int minQuantity, int minInvSlots, boolean delayedLooting, boolean antiLureProtection, String... names) {
        setValues(0, 0, 0, minItems, minQuantity, minInvSlots, delayedLooting, antiLureProtection, names);
        this.area = area;
    }

    /**
     * This constructor is used to create a new LootingParameters object.
     * It sets the range, minimum items, minimum quantity, delayed looting, anti-lure protection, and names of the items to be looted.
     *
     * @param area              The range within which the items to be looted are located.
     * @param minItems           The minimum number of items to be looted.
     * @param minQuantity        The minimum quantity of items to be looted.
     * @param minInvSlots        The minimum number of inventory slots to have open.
     * @param delayedLooting     A boolean indicating whether looting should be delayed.
     * @param antiLureProtection A boolean indicating whether anti-lure protection should be enabled.
     * @param ids              The ids of the items to be looted.
     */
    public LootingParameters(WorldArea area, int minItems, int minQuantity, int minInvSlots, boolean delayedLooting, boolean antiLureProtection, int... ids) {
        setValues(0, 0, 0, minItems, minQuantity, minInvSlots, delayedLooting, antiLureProtection, null);
        this.area = area;
        this.ids = ids;
    }

    private void setValues(int minValue, int maxValue, int range, int minItems, int minQuantity, int minInvSlots, boolean delayedLooting, boolean antiLureProtection, String[] names) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.range = range;
        this.minItems = minItems;
        this.minQuantity = minQuantity;
        this.minInvSlots = minInvSlots;
        this.delayedLooting = delayedLooting;
        this.antiLureProtection = antiLureProtection;
        this.names = names;
    }
}
