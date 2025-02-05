package net.runelite.client.plugins.microbot.pvmfighter.bank;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterPlugin;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.util.Rs2InventorySetup;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;

@Slf4j
public class BankerScript extends Script {

    public void run(PvmFighterConfig config) {
        try {
            if (PvmFighterScript.playerState != PlayerState.BANKING) return;
            if (PvmFighterScript.currentLocation != PvmFighterScript.playerState.getPlayerLocation()) return;
            if (!Rs2Bank.isOpen()) Rs2Bank.openBank();

            depositAllExcept(config);
            withdrawUpkeepItems(config);
            Rs2Random.wait(800, 1600);
            Rs2Bank.closeBank();
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
    }

    public void withdrawUpkeepItems(PvmFighterConfig config) {
        if (config.useInventorySetup() || config.toggleSlayer()) {
            if (PvmFighterScript.inventorySetup.isMainSchedulerCancelled())
                PvmFighterScript.inventorySetup = new Rs2InventorySetup(PvmFighterScript.setup.getInventorySetupName(), mainScheduledFuture);
            if (!PvmFighterScript.inventorySetup.doesEquipmentMatch()) {
                boolean equipmentMatch = PvmFighterScript.inventorySetup.loadEquipment();
                if (!equipmentMatch) PvmFighterPlugin.shutdownFlag = true;
            }
            boolean inventoryMatch = PvmFighterScript.inventorySetup.loadInventory();
            if (!inventoryMatch) PvmFighterPlugin.shutdownFlag = true;
        }
    }

    public void depositAllExcept(PvmFighterConfig config) {
        if (config.toggleSlayer() || config.useInventorySetup()) {
            PvmFighterScript.inventorySetup.loadInventory();
        } else Rs2Bank.depositAll();
    }
}
