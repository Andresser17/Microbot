package net.runelite.client.plugins.microbot.util;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.inventorysetups.InventorySetup;
import net.runelite.client.plugins.microbot.inventorysetups.InventorySetupsItem;
import net.runelite.client.plugins.microbot.inventorysetups.InventorySetupsStackCompareID;
import net.runelite.client.plugins.microbot.inventorysetups.MInventorySetupsPlugin;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

/**
 * Utility class for managing inventory setups in the Microbot plugin.
 * Handles loading inventory and equipment setups, verifying matches, and ensuring
 * the correct items are equipped and in the inventory.
 */
@Slf4j
public class Rs2InventorySetup {

    InventorySetup inventorySetup;

    ScheduledFuture<?> _mainScheduler;

    final ArrayList<Integer> CANNON_SET = new ArrayList<>(Arrays.asList(ItemID.CANNON_BARRELS, ItemID.CANNON_BASE, ItemID.CANNON_STAND, ItemID.CANNON_FURNACE));

    final int CANNON_IS_PLACED_STATE = 4;

    /**
     * Constructor to initialize the Rs2InventorySetup with a specific setup name and scheduler.
     *
     * @param name          The name of the inventory setup to load.
     * @param mainScheduler The scheduler to monitor for cancellation.
     */
    public Rs2InventorySetup(String name, ScheduledFuture<?> mainScheduler) {
        inventorySetup = MInventorySetupsPlugin.getInventorySetups().stream().filter(Objects::nonNull).filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        _mainScheduler = mainScheduler;
        if (inventorySetup == null) {
            Microbot.showMessage("Inventory load with name " + name + " not found!");
            Microbot.pauseAllScripts = true;
        }
    }

    /**
     * Constructor to initialize the Rs2InventorySetup with a specific setup and scheduler.
     * The setup can now directly be fetched from the new config selector.
     *
     * @param setup          The inventory setup to load.
     * @param mainScheduler The scheduler to monitor for cancellation.
     */
    public Rs2InventorySetup(InventorySetup setup, ScheduledFuture<?> mainScheduler) {
        inventorySetup = setup;
        _mainScheduler = mainScheduler;
        if (inventorySetup == null) {
            Microbot.showMessage("Inventory load error!");
            Microbot.pauseAllScripts = true;
        }
    }

    /**
     * Checks if the main scheduler has been cancelled.
     *
     * @return true if the scheduler is cancelled, false otherwise.
     */
    public boolean isMainSchedulerCancelled() {
        return _mainScheduler != null && _mainScheduler.isCancelled();
    }

    /**
     * Loads the inventory setup from the bank.
     *
     * @return true if the inventory matches the setup after loading, false otherwise.
     */
    public boolean loadInventory() {
        if(!Rs2Bank.isOpen()) Rs2Bank.openBank();

        Rs2Bank.depositAllExcept(itemsToNotDeposit());
        Map<Integer, List<InventorySetupsItem>> groupedByItems = inventorySetup.getInventory().stream().collect(Collectors.groupingBy(InventorySetupsItem::getId));

        for (Map.Entry<Integer, List<InventorySetupsItem>> groupedItem : groupedByItems.entrySet()) {
            if (isMainSchedulerCancelled()) break;

            InventorySetupsItem item = groupedItem.getValue().get(0);
            int itemQuantity = groupedItem.getValue().size();
            int key = groupedItem.getKey();

            if (key == -1) continue;
            // check if dwarf cannon set is assembled
            if (CANNON_SET.contains(key)) {
                if (Microbot.getVarbitPlayerValue(VarPlayer.CANNON_STATE) == CANNON_IS_PLACED_STATE) continue;
            }

            int withdrawQuantity = calculateWithdrawQuantity(item, itemQuantity);
            if (withdrawQuantity == 0) continue;

            if (item.getStackCompare() == InventorySetupsStackCompareID.None
            || item.getStackCompare() == InventorySetupsStackCompareID.Standard
            || item.getStackCompare() == InventorySetupsStackCompareID.Greater_Than) {
                if (!Rs2Bank.hasBankItem(item.getId(), withdrawQuantity)) {
                    Microbot.showMessage("Bank is missing the following item or the necessary quantity" + item.getName());
                    break;
                }
            } else if (item.getStackCompare() == InventorySetupsStackCompareID.Less_Than) {
                if (!Rs2Bank.hasItem(key)) {
                    Microbot.showMessage("Bank is missing the following item " + item.getName());
                    break;
                }
            }

            withdrawItem(item, withdrawQuantity);
        }

        sleep(1000);

        return doesInventoryMatch();
    }

    /**
     * Calculates the quantity of an item to withdraw based on the current inventory state.
     *
     * @param inventorySetupsItem The inventory setup item.
     * @param size                total quantity of items
     * @return The quantity to withdraw.
     */
    private int calculateWithdrawQuantity(InventorySetupsItem inventorySetupsItem, int size) {
        int withdrawQuantity;
        if (size == 1) {
            Rs2Item rs2Item = Rs2Inventory.get(inventorySetupsItem.getId());
            if (rs2Item != null && rs2Item.isStackable()) {
                withdrawQuantity = inventorySetupsItem.getQuantity() - rs2Item.quantity;
                if (Rs2Inventory.hasItemAmount(inventorySetupsItem.getName(), inventorySetupsItem.getQuantity())) {
                    return 0;
                }
            } else {
                withdrawQuantity = inventorySetupsItem.getQuantity();
                if (Rs2Inventory.hasItemAmount(inventorySetupsItem.getName(), withdrawQuantity)) {
                    return 0;
                }
            }
        } else {
            withdrawQuantity = size - (int) Rs2Inventory.items().stream().filter(x -> x.getId() == inventorySetupsItem.getId()).count();
            if (Rs2Inventory.hasItemAmount(inventorySetupsItem.getName(), size)) {
                return 0;
            }
        }
        return withdrawQuantity;
    }


    /**
     * Withdraws an item from the bank.
     *
     * @param item     The item to withdraw.
     * @param quantity The quantity to withdraw.
     */
    private void withdrawItem(InventorySetupsItem item, int quantity) {
        if (item.isFuzzy()) {
            Rs2Bank.withdrawX(item.getName(), quantity);
        } else {
            if (quantity > 1) {
                Rs2Bank.withdrawX(item.getId(), quantity);
            } else {
                Rs2Bank.withdrawItem(item.getId());
            }
            sleep(100, 250);
        }
    }

    /**
     * Loads the equipment setup from the bank.
     *
     * @return true if the equipment matches the setup after loading, false otherwise.
     */
    public boolean loadEquipment() {
        Rs2Bank.openBank();
        if (!Rs2Bank.isOpen()) {
            return false;
        }

        //Clear inventory if full
        if (Rs2Inventory.isFull()) {
            Rs2Bank.depositAll();
        } else {
            //only deposit the items we don't need
            Rs2Bank.depositAllExcept(itemsToNotDeposit());
        }

        for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
            if (isMainSchedulerCancelled()) break;
            if (InventorySetupsItem.itemIsDummy(inventorySetupsItem)) continue;

            if (inventorySetupsItem.isFuzzy()) {

                if (Rs2Inventory.hasItemAmount(inventorySetupsItem.getName(), (int) inventorySetup.getInventory().stream().filter(x -> x.getId() == inventorySetupsItem.getId()).count()))
                    continue;
                if (Rs2Equipment.isWearing(inventorySetupsItem.getName()))
                    continue;

                if (inventorySetupsItem.getQuantity() > 1) {
                    Rs2Bank.withdrawAllAndEquip(inventorySetupsItem.getName());
                    sleep(100, 250);
                } else {
                    Rs2Bank.withdrawAndEquip(inventorySetupsItem.getName());
                    sleep(100, 250);
                }

            } else {
                if (inventorySetupsItem.getId() == -1 || !Rs2Bank.hasItem(inventorySetupsItem.getName()))
                    continue;
                if (Rs2Inventory.hasItem(inventorySetupsItem.getName())) {
                    Rs2Bank.wearItem(inventorySetupsItem.getName());
                    continue;
                }
                if (inventorySetupsItem.getQuantity() > 1) {
                    Rs2Bank.withdrawAllAndEquip(inventorySetupsItem.getName());
                    sleep(100, 250);
                } else {
                    Rs2Bank.withdrawAndEquip(inventorySetupsItem.getName());
                }
            }
        }

        sleep(1000);

        return doesEquipmentMatch();
    }

    /**
     * Wears the equipment items defined in the inventory setup.
     * Iterates through the equipment setup and equips the items to the player.
     *
     * @return true if the equipment setup matches the current worn equipment, false otherwise.
     */
    public boolean wearEquipment() {
        for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
            Rs2Inventory.wield(inventorySetupsItem.getId());
        }
        return doesEquipmentMatch();
    }

    /**
     * Checks if the current inventory matches the setup defined in the inventory setup.
     * It compares the quantity and stackability of items in the current inventory
     * against the quantities required by the inventory setup.
     *
     * @return true if the inventory matches the setup, false otherwise.
     */
    public boolean doesInventoryMatch() {
        Map<Integer, List<InventorySetupsItem>> groupedByItems = inventorySetup.getInventory().stream().collect(Collectors.groupingBy(InventorySetupsItem::getId));
        boolean found = true;
        for (Integer key : groupedByItems.keySet()) {
            InventorySetupsItem inventorySetupsItem = groupedByItems.get(key).get(0);
            if (inventorySetupsItem.getId() == -1) continue;
            int withdrawQuantity = -1;
            boolean isStackable = false;
            if (groupedByItems.get(key).size() == 1) {
                withdrawQuantity = groupedByItems.get(key).get(0).getQuantity();
                isStackable = withdrawQuantity > 1;
            } else {
                withdrawQuantity = groupedByItems.get(key).size();
            }
            // check if dwarf cannon set is assembled
            if (CANNON_SET.contains(inventorySetupsItem.getId())) {
                if (Microbot.getVarbitPlayerValue(VarPlayer.CANNON_STATE) == CANNON_IS_PLACED_STATE) found = true;
            } else if (!Rs2Inventory.hasItemAmount(inventorySetupsItem.getName(), withdrawQuantity, isStackable)) {
                Microbot.log("Looking for " + inventorySetupsItem.getName() + " with amount " + withdrawQuantity);
                found = false;
            }
        }

        return found;
    }

    /**
     * Checks if the current equipment setup matches the desired setup.
     * Iterates through the equipment setup items and verifies if they are equipped properly.
     *
     * @return true if all equipment items match the setup, false otherwise.
     */
    public boolean doesEquipmentMatch() {
        for (InventorySetupsItem inventorySetupsItem : inventorySetup.getEquipment()) {
            if (inventorySetupsItem.getId() == -1) continue;
            if (inventorySetupsItem.isFuzzy()) {
                if (!Rs2Equipment.isWearing(inventorySetupsItem.getName(), false)) {
                    Microbot.log("Missing item " + inventorySetupsItem.getName());
                    return false;
                }
            } else {
                if (!Rs2Equipment.isWearing(inventorySetupsItem.getName(), true)) {
                    Microbot.log("Missing item " + inventorySetupsItem.getName());
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Retrieves the list of inventory items from the setup, excluding any dummy items (ID == -1).
     *
     * @return A list of valid inventory items.
     */
    public List<InventorySetupsItem> getInventoryItems() {
        return inventorySetup.getInventory().stream().filter(x -> x.getId() != -1).collect(Collectors.toList());
    }

    /**
     * Retrieves the list of equipment items from the setup, excluding any dummy items (ID == -1).
     *
     * @return A list of valid equipment items.
     */
    public List<InventorySetupsItem> getEquipmentItems() {
        return inventorySetup.getEquipment().stream().filter(x -> x.getId() != -1).collect(Collectors.toList());
    }

    /**
     * Creates a list of item names that should not be deposited into the bank.
     * Combines items from both the inventory setup and the equipment setup.
     *
     * @return A list of item names that should not be deposited.
     */
    public List<String> itemsToNotDeposit() {
        List<InventorySetupsItem> inventorySetupItems = getInventoryItems();
        List<InventorySetupsItem> equipmentSetupItems = getEquipmentItems();

        List<InventorySetupsItem> combined = new ArrayList<>();

        combined.addAll(inventorySetupItems);
        combined.addAll(equipmentSetupItems);

        return combined.stream().map(InventorySetupsItem::getName).collect(Collectors.toList());
    }

    /**
     * Checks if the current spellbook matches the one defined in the inventory setup.
     *
     * @return true if the current spellbook matches the setup, false otherwise.
     */
    public boolean hasSpellBook() {
        return inventorySetup.getSpellBook() == Microbot.getVarbitValue(Varbits.SPELLBOOK);
    }
}
