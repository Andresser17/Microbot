package net.runelite.client.plugins.microbot.pvmfighter.combat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.NPC;
import net.runelite.api.VarPlayer;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterPlugin;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.pvmfighter.enums.SlayerTask;
import net.runelite.client.plugins.microbot.util.Rs2InventorySetup;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

import java.util.List;

@Slf4j
public class SlayerScript extends Script {
    public void run(PvmFighterConfig config) {
        try {
            if (PvmFighterScript.playerState != PlayerState.SLAYER_MASTER) return;
            if (PvmFighterScript.currentLocation != PvmFighterScript.playerState.getPlayerLocation()) return;

            // locate slayer master
            NPC slayerMaster = Rs2Npc.getNpc(config.slayerMaster().getName());
            if (slayerMaster != null) {
                Rs2Npc.interact(slayerMaster, "Assignment");
                Rs2Random.wait(1000, 1200);
                Rs2Keyboard.keyPress(32);

                // check if player has a slayer task assigned
                int creatureVarbitValue = Microbot.getVarbitPlayerValue(VarPlayer.SLAYER_TASK_CREATURE);
                EnumComposition creatureEnum = Microbot.getEnum(EnumID.SLAYER_TASK_CREATURE);
                String creatureName = creatureEnum.getStringValue(creatureVarbitValue);
                if (creatureName != null) {
                    PvmFighterScript.slayerTask = SlayerTask.findTaskByName(creatureName);
                    if (PvmFighterScript.slayerTask != null) {
                        PvmFighterScript.npcTargets.clear();
                        PvmFighterScript.npcTargets.addAll(List.of(PvmFighterScript.slayerTask.getNpcName()));
                        PlayerLocation.COMBAT_FIELD.setWorldArea(PvmFighterScript.slayerTask.getWorldArea());
                        PlayerLocation.COMBAT_FIELD.setWorldPoint(PvmFighterScript.slayerTask.getWorldPoint());

                        switch (config.selectCombatStyle()) {
                            case MELEE:
                                PvmFighterScript.setup = PvmFighterScript.slayerTask.getMeleeSetup();
                                break;
                            case MAGIC:
                                PvmFighterScript.setup = PvmFighterScript.slayerTask.getMagicSetup();
                                break;
                            case RANGED:
                                PvmFighterScript.setup = PvmFighterScript.slayerTask.getRangedSetup();
                                break;
                        }

                        PvmFighterScript.inventorySetup = new Rs2InventorySetup(PvmFighterScript.setup.getInventorySetupName(), mainScheduledFuture);
                    } else {
                        Microbot.showMessage("Slayer task not found, shutting down!!!");
                        PvmFighterPlugin.shutdownFlag = true;
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
