package net.runelite.client.plugins.microbot.pvmfighter.combat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterPlugin;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterScript;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2Cannon;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

@Slf4j
public class CannonScript extends Script {
    public void run(PvmFighterConfig config) {
        try {
            if (!fulfillConditionsToRun() && !config.useCannon()) return;
            if (PvmFighterScript.currentLocation != PvmFighterScript.playerState.getPlayerLocation()) return;
            if (PvmFighterScript.pickUpCannonFlag) {
                pickupCannon();
                PvmFighterScript.pickUpCannonFlag = false;
                PvmFighterScript.cannonIsAssembledFlag = false;
                return;
            }

            if (config.toggleSlayer()) {
                if (setCannon(PvmFighterScript.slayerTask.getWorldPoint())) PvmFighterScript.cannonIsAssembledFlag = true;
            }
            if (Rs2Cannon.repair())
               return;
            if (Rs2Inventory.hasItem(ItemID.CANNONBALL)) Rs2Cannon.refill();
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
    }

    private boolean setCannon(WorldPoint worldPoint) {
        if (!Rs2Player.getWorldLocation().equals(worldPoint)) {
            Rs2Walker.walkTo(worldPoint);
            Rs2Random.wait(1200, 1500);
        }

        if (Rs2Inventory.hasItem(new int[]{ItemID.CANNON_BARRELS, ItemID.CANNON_BASE, ItemID.CANNON_STAND, ItemID.CANNON_FURNACE})) {
            Rs2Inventory.interact(ItemID.CANNON_BASE, "Set-up");
            Rs2Random.wait(3000, 4000);
            GameObject cannon = Rs2GameObject.findObjectById(6, 3102);
            if (cannon != null) {
                Rs2GameObject.interact(cannon, "Fire");
                return true;
            }
        } else {
            Microbot.showMessage("Player has not cannon set in inventory");
            PvmFighterPlugin.shutdownFlag = true;
        }

        return false;
    }

    private void pickupCannon() {
        GameObject cannon = Rs2GameObject.findObjectById(6, 3102);
        if (cannon != null) {
            Rs2GameObject.interact(cannon, "Pick-up");
        }
    }
}
