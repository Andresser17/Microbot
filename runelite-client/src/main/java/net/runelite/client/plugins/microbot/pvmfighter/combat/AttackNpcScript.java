package net.runelite.client.plugins.microbot.pvmfighter.combat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.pvmfighter.enums.Spell;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterPlugin;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

@Slf4j
public class AttackNpcScript {

    public static NPC currentNPC;
    public static List<NPC> attackableNpcs = new ArrayList<>();
    private boolean init = true;
    public static boolean isRunning = false;

    public void run(PvmFighterConfig config) {
        if (init) {
            try {
                Rs2NpcManager.loadJson();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            init = false;
            isRunning = true;
        }

        try {
            if (PvmFighterScript.playerState != PlayerState.ATTACKING) return;
            if (PvmFighterScript.currentLocation != PvmFighterScript.playerState.getPlayerLocation()) return;
            if (PvmFighterPlugin.getCooldown() > 0) return;
            if (config.toggleCenterTile() && config.centerLocation().getX() == 0 && config.centerLocation().getY() == 0) {
                Microbot.showMessage("Please set a center location");
                PvmFighterPlugin.shutdownFlag = true;
            }

            switch (config.combatStyle()) {
                case RANGED:
                    if (!Rs2Equipment.hasEquipped(config.ammoToUse().getId())) {
                        if (Rs2Inventory.hasItem(config.ammoToUse().getId())) {
                            Rs2Inventory.equip(config.ammoToUse().getId());
                        } else {
                            Microbot.showMessage("Player has not selected ammunition available");
                            PvmFighterPlugin.shutdownFlag = true;
                        }
                    }
                    break;
                case MAGIC:
                    if (!Spell.checkIfPlayerHasNecessaryItems(config.spellToUse())) {
                        Microbot.showMessage("Player has not necessary runes to make spell");
                        PvmFighterPlugin.shutdownFlag = true;
                    }
                    break;
            }

            List<String> npcsToAttack = Arrays.stream(config.attackableNpcs().split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());

            attackableNpcs = Rs2Npc.getNpcs().filter(npc -> {
                boolean isInArea = PlayerLocation.COMBAT_FIELD.getArea().contains(npc.getWorldLocation());
                boolean isNotInteracting = (npc.getInteracting() == null || npc.getInteracting() == Microbot.getClient().getLocalPlayer());
                boolean isInList = npcsToAttack.contains(npc.getName());
                return isInList && !npc.isDead() && isInArea && isNotInteracting && Rs2Npc.hasLineOfSight(npc);
            }).collect(Collectors.toList());
//                    .sorted(Comparator.comparing((NPC npc) -> npc.getInteracting() == Microbot.getClient().getLocalPlayer() ? 0 : 1)
//                            .thenComparingInt(npc -> npc.getLocalLocation()
//                            .distanceTo(Rs2Player.getLocalLocation())))
//                            .collect(Collectors.toList());

            if (attackableNpcs.isEmpty()) {
                Microbot.log("Not attackable npc found");
                return;
            };
            currentNPC = attackableNpcs.get(0);

            if (!Rs2Camera.isTileOnScreen(currentNPC.getLocalLocation()))
                Rs2Camera.turnTo(currentNPC);

            Rs2Npc.interact(currentNPC, "attack");
            PvmFighterPlugin.setCooldown(config.playStyle().getRandomTickInterval());
            Microbot.status = "Attacking " + currentNPC.getName();
            Rs2Random.wait(1500, 2000);
            sleepUntilFulfillCondition(() -> {
                log.info("Player is attacking");
                boolean isIdle = Rs2Antiban.isIdle();
                log.info("isIdle {}", isIdle);
                log.info("AttackNPC CurrentNPC: {}", currentNPC);
                log.info("AttackNPC CurrentNPC is Dead: {}", currentNPC.isDead());
                return isIdle && currentNPC.isDead();
            }, () -> Rs2Random.wait(1500, 1600));
            if (currentNPC.isDead()) currentNPC = null;
            Microbot.log("Combat finished");

            // wait until loot appears
            if (config.toggleLootItems()) {
                Microbot.log("Waiting for loot");
                Rs2Random.wait(1500, 2000);
            }
            isRunning = false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void shutdown() {
        init = true;
        isRunning = false;
    }

    public boolean sleepUntilFulfillCondition(BooleanSupplier awaitedCondition, Runnable iterationWait) {
        boolean done;
        do {
            done = awaitedCondition.getAsBoolean();
            iterationWait.run();
            if (!isRunning) break;
        } while (!done);
        return done;
    }
}
