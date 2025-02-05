package net.runelite.client.plugins.microbot.pvmfighter.combat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.CombatStyle;
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
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

@Slf4j
public class AttackNpcScript {

    public static NPC currentNPC;
    public static List<NPC> npcToAttack = new ArrayList<>();
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
        }

        try {
            if (PvmFighterScript.playerState != PlayerState.ATTACKING || PlayerLocation.COMBAT_FIELD.getArea() == null) return;
            if (PvmFighterScript.currentLocation != PvmFighterScript.playerState.getPlayerLocation()) return;
            if (PvmFighterPlugin.getCooldown() > 0) return;
            if (config.toggleCenterTile() && config.centerLocation().getX() == 0 && config.centerLocation().getY() == 0) {
                Microbot.showMessage("Please set a center location");
                PvmFighterPlugin.shutdownFlag = true;
            }

            isRunning = true;
            if (Objects.requireNonNull(config.selectCombatStyle()) == CombatStyle.MAGIC) {
                if (!Spell.checkIfPlayerHasNecessaryItems(config.selectSpellToUse())) {
                    Microbot.showMessage("Player has not necessary runes to make spell");
                    PvmFighterPlugin.shutdownFlag = true;
                }
            }

            log.info("npcs: {}", PvmFighterScript.npcTargets);
            npcToAttack = Rs2Npc.getNpcs().filter(npc -> {
                boolean isInArea = PlayerLocation.COMBAT_FIELD.getArea().contains(npc.getWorldLocation());
                boolean isNotInteracting = (npc.getInteracting() == null || npc.getInteracting() == Microbot.getClient().getLocalPlayer());
                boolean isInList = PvmFighterScript.npcTargets.stream().anyMatch(npcName -> npcName.equalsIgnoreCase(npc.getName()));
                return isInList && !npc.isDead() && isInArea && isNotInteracting && Rs2Npc.hasLineOfSight(npc);
            }).collect(Collectors.toList());

            if (npcToAttack.isEmpty()) {
                Microbot.log("Not target NPC found");
                isRunning = false;
                return;
            };
            currentNPC = npcToAttack.get(0);

            if (!Rs2Camera.isTileOnScreen(currentNPC.getLocalLocation()))
                Rs2Camera.turnTo(currentNPC);

            Rs2Npc.interact(currentNPC, "attack");
            PvmFighterPlugin.setCooldown(config.selectPlayStyle().getRandomTickInterval());
            Microbot.status = "Attacking " + currentNPC.getName();
            Rs2Random.wait(1500, 2000);
            sleepUntilFulfillCondition(() -> {
//                log.info("Player is attacking");
                boolean isIdle = Rs2Antiban.isIdle();
//                log.info("isIdle {}", isIdle);
//                log.info("AttackNPC CurrentNPC: {}", currentNPC);
//                log.info("AttackNPC CurrentNPC is Dead: {}", currentNPC.isDead());
                return isIdle && (currentNPC.isDead() || !Rs2Player.isInCombat());
            }, () -> Rs2Random.wait(1500, 1600));
            if (currentNPC.isDead()) currentNPC = null;
            Microbot.log("Combat finished");

//            // wait until loot appears
//            if (config.toggleLootItems()) {
//                Microbot.log("Waiting for loot");
//                Rs2Random.wait(1500, 2000);
//            }
            isRunning = false;
        } catch (Exception ex) {
            log.info(ex.getMessage());
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
