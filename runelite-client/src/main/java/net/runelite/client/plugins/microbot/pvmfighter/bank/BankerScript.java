package net.runelite.client.plugins.microbot.pvmfighter.bank;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.util.Rs2InventorySetup;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;
import net.runelite.client.plugins.microbot.pvmfighter.constants.Constants;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

enum ItemToKeep {
    TELEPORT(Constants.TELEPORT_IDS, PvmFighterConfig::ignoreTeleport, PvmFighterConfig::staminaValue),
    STAMINA(Constants.STAMINA_POTION_IDS, PvmFighterConfig::useStamina, PvmFighterConfig::staminaValue),
    PRAYER(Constants.PRAYER_RESTORE_POTION_IDS, PvmFighterConfig::usePrayer, PvmFighterConfig::prayerValue),
    FOOD(Constants.FOOD_ITEM_IDS, PvmFighterConfig::useFood, PvmFighterConfig::foodToWithdraw, PvmFighterConfig::amountOfFood),
    ANTIPOISON(Constants.ANTI_POISON_POTION_IDS, PvmFighterConfig::useAntipoison, PvmFighterConfig::antipoisonValue),
    ANTIFIRE(Constants.ANTI_FIRE_POTION_IDS, PvmFighterConfig::useAntifire, PvmFighterConfig::antifireValue),
    COMBAT(Constants.STRENGTH_POTION_IDS, PvmFighterConfig::useCombat, PvmFighterConfig::combatValue),
    RESTORE(Constants.RESTORE_POTION_IDS, PvmFighterConfig::useRestore, PvmFighterConfig::restoreValue);

    @Getter
    private final List<Integer> ids;
    private final Function<PvmFighterConfig, Boolean> withdrawItem;
    private final Function<PvmFighterConfig, String> itemsToWithdraw;
    private final Function<PvmFighterConfig, Integer> quantityToWithdraw;

    ItemToKeep(Set<Integer> ids, Function<PvmFighterConfig, Boolean> withdrawItem, Function<PvmFighterConfig, Integer> quantityToWithdraw) {
        this.ids = new ArrayList<>(ids);
        this.withdrawItem = withdrawItem;
        this.itemsToWithdraw = null;
        this.quantityToWithdraw = quantityToWithdraw;
    }

    ItemToKeep(Set<Integer> ids, Function<PvmFighterConfig, Boolean> withdrawItem, Function<PvmFighterConfig, String> itemsToWithdraw, Function<PvmFighterConfig, Integer> quantityToWithdraw) {
        this.ids = new ArrayList<>(ids);
        this.withdrawItem = withdrawItem;
        this.itemsToWithdraw = itemsToWithdraw;
        this.quantityToWithdraw = quantityToWithdraw;
    }

    public boolean isEnabled(PvmFighterConfig config) {
        return withdrawItem.apply(config);
    }

    public String[] getItemsToWithdraw(PvmFighterConfig config) {
        assert itemsToWithdraw != null;
        return Arrays.stream(itemsToWithdraw.apply(config).split(",")).map(String::trim).toArray(String[]::new);
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
            if (!config.itemsToKeep().isEmpty()) {
                depositAllExcept(config.itemsToKeep());
            } else depositAllExcept(config);

            if (config.withdrawNecessaryFoodToRestoreHealth()) {
                withdrawNecessaryFoodToRestoreHealth(config);
            }

            withdrawUpkeepItems(config);
            Rs2Random.wait(800, 1600);
            Rs2Bank.closeBank();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public boolean withdrawUpkeepItems(PvmFighterConfig config) {
        if (config.useInventorySetup()) {
            Rs2InventorySetup inventorySetup = new Rs2InventorySetup(config.inventorySetup(), mainScheduledFuture);
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
                    if (item.name().equals("FOOD")) {
                        String[] itemsToWithdraw = item.getItemsToWithdraw(config);
                        for (Rs2Food food : Arrays.stream(Rs2Food.values()).sorted(Comparator.comparingInt(Rs2Food::getHeal).reversed()).collect(Collectors.toList())) {
                            log.info("Checking bank for food: {}", food.getName());
                            if (itemsToWithdraw.length > 0) {
                                Optional<String> match = Arrays.stream(itemsToWithdraw).filter((foodName) -> food.getName().equals(foodName)).findFirst();
                                match.ifPresent(name -> Rs2Bank.withdrawX(true, name, item.getQuantityToWithdraw(config) - count));
                                break;
                            }

                            if (Rs2Bank.hasBankItem(food.getId(), item.getQuantityToWithdraw(config) - count)) {
                                Rs2Bank.withdrawX(true, food.getId(), item.getQuantityToWithdraw(config) - count);
                                break;
                            }
                        }
                    } else {
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
        }
        return !isUpkeepItemDepleted(config);
    }

    public boolean depositAllExcept(PvmFighterConfig config) {
        List<Integer> ids = Arrays.stream(ItemToKeep.values())
                .filter(item -> item.isEnabled(config))
                .flatMap(item -> item.getIds().stream())
                .collect(Collectors.toList());
        Rs2Bank.depositAllExcept(ids.toArray(new Integer[0]));
        return Rs2Bank.isOpen();
    }

    /**
     * @param itemsToKeep a string array of item's name
     * @return if action was completed successful
     */
    public boolean depositAllExcept(String itemsToKeep) {
        List<String> itemsToKeepList = Arrays.stream(itemsToKeep.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        return Rs2Bank.depositAllExcept(itemsToKeepList);
    }

    public boolean isUpkeepItemDepleted(PvmFighterConfig config) {
        return Arrays.stream(ItemToKeep.values())
                .filter(item -> item != ItemToKeep.TELEPORT && item.isEnabled(config))
                .anyMatch(item -> item.getIds().stream().mapToInt(Rs2Inventory::count).sum() == 0);
    }

    public boolean withdrawNecessaryFoodToRestoreHealth(PvmFighterConfig config) {
        int currentHealth = Rs2Player.getMaxHealth() - Rs2Player.getBoostedSkillLevel(Skill.HITPOINTS);
        List<String> foodToWithdraw = Arrays.stream(config.foodToWithdraw().split(","))
                .map(String::trim)
                .sorted(Comparator.comparingInt((name) -> currentHealth / Rs2Food.getFoodByName(name).getHeal()))
                .filter(Rs2Bank::hasBankItem).collect(Collectors.toList());

        if (!foodToWithdraw.isEmpty()) {
            int toWithdraw = currentHealth / Rs2Food.getFoodByName(foodToWithdraw.get(0)).getHeal();
            Rs2Bank.withdrawX(foodToWithdraw.get(0), toWithdraw);
            return true;
        }
        return false;
    }

    public void shutdown() {
        super.shutdown();
    }
}
