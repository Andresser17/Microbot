package net.runelite.client.plugins.microbot.pvmfighter.combat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class AttackNpcScript extends Script {

    public static Actor currentNpc = null;
    public static List<NPC> attackableNpcs = new ArrayList<>();
    private boolean messageShown = false;

    public static void skipNpc() {
        currentNpc = null;
    }

    public void run(PvmFighterConfig config) {
        try {
            Rs2NpcManager.loadJson();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.fulfillConditionsToRun() || Rs2AntibanSettings.actionCooldownActive || PvmFighterScript.playerState != PlayerState.ATTACKING) return;
                if (PvmFighterScript.currentLocation != PvmFighterScript.playerState.getPlayerLocation()) return;
//                if (PvmFighterPlugin.getCooldown() > 0 || Rs2Combat.inCombat())
//                    return;

                List<String> npcsToAttack = Arrays.stream(config.attackableNpcs().split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());

                double healthPercentage = (double) Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) * 100
                        / Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS);
                if (Rs2Inventory.getInventoryFood().isEmpty() && healthPercentage < 10)
                    return;

                if (config.toggleCenterTile() && config.centerLocation().getX() == 0
                        && config.centerLocation().getY() == 0) {
                    if (!messageShown) {
                        Microbot.showMessage("Please set a center location");
                        messageShown = true;
                    }
                    return;
                }
                messageShown = false;

                attackableNpcs = Rs2Npc.getNpcs().filter(npc -> !npc.isDead()
                                && npc.getWorldLocation().distanceTo(config.centerLocation()) <= config.attackRadius()
                                && (npc.getInteracting() == null
                                || npc.getInteracting() == Microbot.getClient().getLocalPlayer())
                                && npcsToAttack.contains(npc.getName())
                                && Rs2Npc.hasLineOfSight(npc))
                        .sorted(Comparator
                                .comparing((NPC npc) -> npc.getInteracting() == Microbot.getClient().getLocalPlayer() ? 0 : 1)
                                .thenComparingInt(npc -> npc.getLocalLocation()
                                        .distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                        .collect(Collectors.toList());

                if (attackableNpcs.isEmpty()) return;
                NPC npc = attackableNpcs.get(0);

                if (!Rs2Camera.isTileOnScreen(npc.getLocalLocation()))
                    Rs2Camera.turnTo(npc);

                Rs2Npc.interact(npc, "attack");
                Microbot.status = "Attacking " + npc.getName();
                // Wait until player initialized combat
                sleepUntil(Rs2Combat::inCombat, () -> {}, 10000, 1000);
                // Wait until player finished combat
                sleepUntil(() -> !Rs2Combat.inCombat(), () -> {}, 10000, 1000);
                log.info("Combat finished");
//                    PvmFighterPlugin.setCooldown(config.playStyle().getRandomTickInterval());

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        super.shutdown();
    }
}
