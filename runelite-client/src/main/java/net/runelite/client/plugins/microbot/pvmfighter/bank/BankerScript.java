package net.runelite.client.plugins.microbot.pvmfighter.bank;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.util.Rs2InventorySetup;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;
import net.runelite.client.plugins.microbot.pvmfighter.constants.Constants;

import java.util.*;
import java.util.function.Function;

enum ItemToKeep {
    TELEPORT(Constants.TELEPORT_IDS, PvmFighterConfig::ignoreTeleportItems, PvmFighterConfig::staminaPotionsAmount),
    STAMINA(Constants.STAMINA_POTION_IDS, PvmFighterConfig::withdrawStaminaPotions, PvmFighterConfig::staminaPotionsAmount),
    PRAYER(Constants.PRAYER_RESTORE_POTION_IDS, PvmFighterConfig::withdrawPrayerPotions, PvmFighterConfig::prayerPotionsAmount),
    FOOD(Constants.FOOD_ITEM_IDS, PvmFighterConfig::withdrawFood, PvmFighterConfig::amountOfFoodToWithdraw),
    ANTIPOISON(Constants.ANTI_POISON_POTION_IDS, PvmFighterConfig::withdrawAntiPoison, PvmFighterConfig::antiPoisonAmount),
    ANTIFIRE(Constants.ANTI_FIRE_POTION_IDS, PvmFighterConfig::withdrawAntiFirePotion, PvmFighterConfig::antiFireAmount),
    COMBAT(Constants.STRENGTH_POTION_IDS, PvmFighterConfig::withdrawCombatPotions, PvmFighterConfig::combatPotionsAmount),
    RESTORE(Constants.RESTORE_POTION_IDS, PvmFighterConfig::withdrawRestorePotions, PvmFighterConfig::restorePotionsAmount);

    @Getter
    private final List<Integer> ids;
    private final Function<PvmFighterConfig, Boolean> withdrawItem;
    private final Function<PvmFighterConfig, Integer> quantityToWithdraw;

    ItemToKeep(Set<Integer> ids, Function<PvmFighterConfig, Boolean> withdrawItem, Function<PvmFighterConfig, Integer> quantityToWithdraw) {
        this.ids = new ArrayList<>(ids);
        this.withdrawItem = withdrawItem;
        this.quantityToWithdraw = quantityToWithdraw;
    }

    public boolean isEnabled(PvmFighterConfig config) {
        return withdrawItem.apply(config);
    }


    public int getQuantityToWithdraw(PvmFighterConfig config) {
        return quantityToWithdraw.apply(config);
    }
}

@Slf4j
public class BankerScript extends Script {
    public void run(PvmFighterConfig config) {
        try {
            if (PvmFighterScript.playerState != PlayerState.BANKING) return;
            if (PvmFighterScript.currentLocation != PvmFighterScript.playerState.getPlayerLocation()) return;

            Rs2Bank.openBank();
            depositAllExcept(config);
            withdrawUpkeepItems(config);
            Rs2Random.wait(800, 1600);
            Rs2Bank.closeBank();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public boolean withdrawUpkeepItems(PvmFighterConfig config) {
        if (config.useInventorySetup()) {
            Rs2InventorySetup inventorySetup = new Rs2InventorySetup(config.inventorySetup().getInventorySetupName(), mainScheduledFuture);
            if (!inventorySetup.doesEquipmentMatch()) {
                inventorySetup.loadEquipment();
            }
            inventorySetup.loadInventory();
            return true;
        }

        for (ItemToKeep item : ItemToKeep.values()) {
            if (item.isEnabled(config)) {
                int count = item.getIds().stream().mapToInt(Rs2Inventory::count).sum();
                log.info("Item: {} Count: {}", item.name(), count);
                if (count < item.getQuantityToWithdraw(config)) {
                    log.info("Withdrawing {} {}(s)", item.getQuantityToWithdraw(config) - count, item.name());
                    ArrayList<Integer> ids = new ArrayList<>(item.getIds());
                    Collections.reverse(ids);
                    for (int id : ids) {
                        log.info("Checking bank for item: {}", id);
                        if (Rs2Bank.hasBankItem(id, item.getQuantityToWithdraw(config) - count)) {
                            Rs2Bank.withdrawX(true, id, item.getQuantityToWithdraw(config) - count);
                            break;
                        }
                    }
                }
            }
        }
        return !isUpkeepItemDepleted(config);
    }

    public boolean depositAllExcept(PvmFighterConfig config) {
        Rs2Bank.depositAllExcept(Arrays.stream(ItemToKeep.values())
                .filter(item -> item.isEnabled(config))
                .flatMap(item -> item.getIds().stream()).toArray(Integer[]::new));
        return Rs2Bank.isOpen();
    }

    public boolean isUpkeepItemDepleted(PvmFighterConfig config) {
        return Arrays.stream(ItemToKeep.values())
                .filter(item -> item != ItemToKeep.TELEPORT && item.isEnabled(config))
                .anyMatch(item -> item.getIds().stream().mapToInt(Rs2Inventory::count).sum() == 0);
    }
}
