package net.runelite.client.plugins.microbot.pvmfighter.helpers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.HelperScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.Optional;

import static net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment.get;

@Slf4j
public class FoodScript extends Script {

    String weaponName = "";
    String bodyName = "";
    String legsName = "";
    String helmName = "";

    String shieldName = "";

    public void run(PvmFighterConfig config) {
        weaponName = "";
        bodyName = "";
        legsName = "";
        helmName = "";
        shieldName = "";
        try {
            if (HelperScript.helperState != PlayerState.EATING) return;
            if (Rs2Bank.isOpen()) return;

            double healthPercentage = Rs2Player.getHealthPercentage();
            if (Rs2Inventory.hasItem("empty vial")) Rs2Inventory.drop("empty vial");
            if (Rs2Equipment.isWearingFullGuthan()) {
                if (healthPercentage > 80) // only unequipped Guthan's armour if we have more than 80% hp
                    unEquipGuthansArmour();
                return;
            }

            Rs2ItemModel idealFood = getIdealFood();
            if (idealFood != null) {
                Rs2Inventory.interact(idealFood, "Eat");
                Rs2Random.wait(1000, 1600);
            } else {
                if (!equipFullGuthansArmour()) {
                    Microbot.showMessage("No more food left & no Guthan's armour available. Please teleport");
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public Rs2ItemModel getIdealFood() {
        // get food that heal value is less than lost health
        Optional<Rs2ItemModel> foodToEat = Rs2Inventory.getInventoryFood().stream().filter(food -> {
            Rs2Food foodValue = Rs2Food.getFoodById(food.id);
            if (foodValue == null) return false;

            return foodValue.getHeal() <= (Rs2Player.getMaxHealth() - Rs2Player.checkCurrentHealth());
        }).findFirst();

        return foodToEat.orElse(null);
    }

    private void unEquipGuthansArmour() {
        if (Rs2Equipment.hasGuthanWeaponEquiped() && !weaponName.isEmpty()) {
            Rs2Inventory.equip(weaponName);
            if (shieldName != null)
                Rs2Inventory.equip(shieldName);
        }
        if (Rs2Equipment.hasGuthanBodyEquiped() && !bodyName.isEmpty()) {
            Rs2Inventory.equip(bodyName);
        }
        if (Rs2Equipment.hasGuthanLegsEquiped() && !legsName.isEmpty()) {
            Rs2Inventory.equip(legsName);
        }
        if (Rs2Equipment.hasGuthanHelmEquiped() && !helmName.isEmpty()) {
            Rs2Inventory.equip(helmName);
        }
    }

    private boolean equipFullGuthansArmour() {
        Rs2ItemModel shield = get(EquipmentInventorySlot.SHIELD);
        if (shield != null)
            shieldName = shield.name;

        if (!Rs2Equipment.hasGuthanWeaponEquiped()) {
            Rs2ItemModel spearWidget = Microbot.getClientThread().runOnClientThread(() -> Rs2Inventory.get("guthan's warspear"));
            if (spearWidget == null) return false;
            Rs2ItemModel weapon = Rs2Equipment.get(EquipmentInventorySlot.WEAPON);
            weaponName = weapon != null ? weapon.name : "";
            Rs2Inventory.equip(spearWidget.name);
        }
        if (!Rs2Equipment.hasGuthanBodyEquiped()) {
            Rs2ItemModel bodyWidget = Microbot.getClientThread().runOnClientThread(() -> Rs2Inventory.get("guthan's platebody"));
            if (bodyWidget == null) return false;
            Rs2ItemModel body = Rs2Equipment.get(EquipmentInventorySlot.BODY);
            bodyName = body != null ? body.name : "";
            Rs2Inventory.equip(bodyWidget.name);
        }
        if (!Rs2Equipment.hasGuthanLegsEquiped()) {
            Rs2ItemModel legsWidget = Microbot.getClientThread().runOnClientThread(() -> Rs2Inventory.get("guthan's chainskirt"));
            if (legsWidget == null) return false;
            Rs2ItemModel legs = Rs2Equipment.get(EquipmentInventorySlot.LEGS);
            legsName = legs != null ? legs.name : "";
            Rs2Inventory.equip(legsWidget.name);
        }
        if (!Rs2Equipment.hasGuthanHelmEquiped()) {
            Rs2ItemModel helmWidget = Microbot.getClientThread().runOnClientThread(() -> Rs2Inventory.get("guthan's helm"));
            if (helmWidget == null) return false;
            Rs2ItemModel helm = Rs2Equipment.get(EquipmentInventorySlot.HEAD);
            helmName = helm != null ? helm.name : "";
            Rs2Inventory.equip(helmWidget.name);
        }
        return true;
    }
}
