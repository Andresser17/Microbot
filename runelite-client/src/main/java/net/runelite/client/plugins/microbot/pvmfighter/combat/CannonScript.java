package net.runelite.client.plugins.microbot.pvmfighter.combat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterScript;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2Cannon;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

@Slf4j
public class CannonScript extends Script {
    public void run(PvmFighterConfig config) {
        try {
            if (!fulfillConditionsToRun() && !config.useCannon()) return;
            if (PvmFighterScript.pickUpCannonFlag) {
                pickupCannon();
                PvmFighterScript.pickUpCannonFlag = false;
                PvmFighterScript.cannonIsAssembledFlag = false;
                return;
            }

            if (Rs2Inventory.hasItem(new int[]{ItemID.CANNON_BASE, ItemID.CANNON_STAND, ItemID.CANNON_FURNACE, ItemID.CANNON_BARRELS})) {
                setCannon(PvmFighterScript.slayerTask.getWorldPoint());
                PvmFighterScript.cannonIsAssembledFlag = true;
            }
            if (Rs2Cannon.repair())
               return;
            if (Rs2Inventory.hasItem(ItemID.CANNONBALL)) Rs2Cannon.refill();
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
    }

    private void setCannon(WorldPoint worldPoint) {
        if (Rs2Inventory.hasItem(new int[]{ItemID.CANNON_BARRELS, ItemID.CANNON_BASE, ItemID.CANNON_STAND, ItemID.CANNON_FURNACE})) {
            Rs2Inventory.interact(ItemID.CANNON_BASE, "Set-up");
        } else {
            Microbot.showMessage("Player has not cannon set in inventory");
            shutdown();
        }
    }

    private void pickupCannon() {
        GameObject cannon = Rs2GameObject.findObjectById(6, 3102);
        if (cannon != null) {
            Rs2GameObject.interact(cannon, "Pick-up");
        }
    }
}
