package net.runelite.client.plugins.microbot.pvmfighter.loot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.grounditems.GroundItem;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.util.grounditem.LootingParameters;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class LootScript extends Script {

    public void run(PvmFighterConfig config) {
        try {
            if (PvmFighterScript.playerState != PlayerState.LOOTING) return;
            if (PvmFighterScript.currentLocation != PvmFighterScript.playerState.getPlayerLocation()) return;

            if (config.lootItemsByPriceRange()) {
                lootItemsByValue(config);
            }

            if (config.lootBones()) lootBones(config);
            if (config.lootRunes()) lootRunes(config);
            if (config.lootCoins()) lootCoins(config);
            if (config.lootArrows()) lootArrows(config);

            if (config.lootItemsByName()) {
                lootItemsByName(config);
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void lootArrows(PvmFighterConfig config) {
        LootingParameters params = new LootingParameters(
                PlayerLocation.COMBAT_FIELD.getArea(),
                1,
                1,
                config.minFreeInventorySlots(),
                config.delayedLooting(),
                config.onlyLootMyItems(),
                " arrow"
        );
        List<GroundItem> groundItems = Rs2GroundItem.getItemsToLootByName(params);
        Rs2GroundItem.lootItem(params, groundItems);
    }

    public static boolean hasArrowsToLoot(PvmFighterConfig config) {
        LootingParameters params = new LootingParameters(
                PlayerLocation.COMBAT_FIELD.getArea(),
                1,
                1,
                config.minFreeInventorySlots(),
                config.delayedLooting(),
                config.onlyLootMyItems(),
                " arrow"
        );
        List<GroundItem> groundItems = Rs2GroundItem.getItemsToLootByName(params);
        return groundItems.size() >= config.minimumQuantityToLoot();
    }

    private void lootBones(PvmFighterConfig config) {
        LootingParameters params = new LootingParameters(
                PlayerLocation.COMBAT_FIELD.getArea(),
                1,
                1,
                config.minFreeInventorySlots(),
                config.delayedLooting(),
                config.onlyLootMyItems(),
                " bones"
        );
        List<GroundItem> groundItems = Rs2GroundItem.getItemsToLootByName(params);
        Rs2GroundItem.lootItem(params, groundItems);
    }

    public static boolean hasBonesToLoot(PvmFighterConfig config) {
        LootingParameters params = new LootingParameters(
                PlayerLocation.COMBAT_FIELD.getArea(),
                1,
                1,
                config.minFreeInventorySlots(),
                config.delayedLooting(),
                config.onlyLootMyItems(),
                " bones"
        );
        List<GroundItem> groundItems = Rs2GroundItem.getItemsToLootByName(params);
        return groundItems.size() >= config.minimumQuantityToLoot();
    }

    private void lootRunes(PvmFighterConfig config) {
        LootingParameters params = new LootingParameters(
                PlayerLocation.COMBAT_FIELD.getArea(),
                1,
                1,
                config.minFreeInventorySlots(),
                config.delayedLooting(),
                config.onlyLootMyItems(),
                " rune"
        );
        List<GroundItem> groundItems = Rs2GroundItem.getItemsToLootByName(params);
        Rs2GroundItem.lootItem(params, groundItems);
    }

    public static boolean hasRunesToLoot(PvmFighterConfig config) {
        LootingParameters params = new LootingParameters(
                PlayerLocation.COMBAT_FIELD.getArea(),
                1,
                1,
                config.minFreeInventorySlots(),
                config.delayedLooting(),
                config.onlyLootMyItems(),
                " rune"
        );
        List<GroundItem> groundItems = Rs2GroundItem.getItemsToLootByName(params);
        return groundItems.size() >= config.minimumQuantityToLoot();
    }

    private void lootCoins(PvmFighterConfig config) {
        LootingParameters params = new LootingParameters(
                PlayerLocation.COMBAT_FIELD.getArea(),
                1,
                1,
                config.minFreeInventorySlots(),
                config.delayedLooting(),
                config.onlyLootMyItems(),
                ItemID.COINS_995
        );
        List<GroundItem> groundItems = Rs2GroundItem.getItemsToLootById(params);
        Rs2GroundItem.lootItem(params, groundItems);
    }

    public static boolean hasCoinsToLoot(PvmFighterConfig config) {
        LootingParameters params = new LootingParameters(
                PlayerLocation.COMBAT_FIELD.getArea(),
                1,
                1,
                config.minFreeInventorySlots(),
                config.delayedLooting(),
                config.onlyLootMyItems(),
                ItemID.COINS_995
        );
        List<GroundItem> groundItems = Rs2GroundItem.getItemsToLootById(params);
        return groundItems.size() >= config.minimumQuantityToLoot();
    }

    private void lootItemsByValue(PvmFighterConfig config) {
        LootingParameters params = new LootingParameters(
                config.minPriceOfItemsToLoot(),
                config.maxPriceOfItemsToLoot(),
                PlayerLocation.COMBAT_FIELD.getArea(),
                1,
                config.minFreeInventorySlots(),
                config.delayedLooting(),
                config.onlyLootMyItems()
        );
        List<GroundItem> groundItems = Rs2GroundItem.getItemsToLootByValue(params);
        Rs2GroundItem.lootItem(params, groundItems);
    }

    public static boolean hasItemsToLootByValue(PvmFighterConfig config) {
        LootingParameters params = new LootingParameters(
                config.minPriceOfItemsToLoot(),
                config.maxPriceOfItemsToLoot(),
                PlayerLocation.COMBAT_FIELD.getArea(),
                1,
                config.minFreeInventorySlots(),
                config.delayedLooting(),
                config.onlyLootMyItems()
        );
        List<GroundItem> groundItems = Rs2GroundItem.getItemsToLootByValue(params);
        return groundItems.size() >= config.minimumQuantityToLoot();
    }

    private void lootItemsByName(PvmFighterConfig config) {
        LootingParameters params = new LootingParameters(
                PlayerLocation.COMBAT_FIELD.getArea(),
                1,
                1,
                config.minFreeInventorySlots(),
                config.delayedLooting(),
                config.onlyLootMyItems(),
                Arrays.stream(config.nameOfItemsToLoot().split(","))
                        .map(String::trim).toArray(String[]::new)
        );
        List<GroundItem> groundItems = Rs2GroundItem.getItemsToLootByName(params);
        Rs2GroundItem.lootItem(params, groundItems);
    }

    public static boolean hasItemsToLootByName(PvmFighterConfig config) {
        LootingParameters params = new LootingParameters(
                PlayerLocation.COMBAT_FIELD.getArea(),
                1,
                1,
                config.minFreeInventorySlots(),
                config.delayedLooting(),
                config.onlyLootMyItems(),
                Arrays.stream(config.nameOfItemsToLoot().split(","))
                        .map(String::trim).toArray(String[]::new)
        );
        List<GroundItem> groundItems = Rs2GroundItem.getItemsToLootByName(params);
        return groundItems.size() >= config.minimumQuantityToLoot();
    }

    public void shutdown() {
        super.shutdown();
    }
}
