package net.runelite.client.plugins.microbot.pvmfighter;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.bank.BankerScript;
import net.runelite.client.plugins.microbot.pvmfighter.combat.*;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.pvmfighter.enums.Setup;
import net.runelite.client.plugins.microbot.pvmfighter.enums.SlayerTask;
import net.runelite.client.plugins.microbot.pvmfighter.loot.LootScript;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.ActivityIntensity;
import net.runelite.client.plugins.microbot.util.antiban.enums.PlayStyle;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class PvmFighterScript extends Script {
    public static PlayerState playerState;
    public static PlayerLocation currentLocation;
    public static List<String> npcTargets = new ArrayList<>();
    public static SlayerTask slayerTask;
    public static Setup setup;
    public static boolean pickUpCannonFlag = false;
    public static boolean cannonIsAssembledFlag = false;
    private final AttackNpcScript attackNpcScript = new AttackNpcScript();
    private final FoodScript foodScript = new FoodScript();
    private final LootScript lootScript = new LootScript();
    private final SafeSpot safeSpotScript = new SafeSpot();
    private final BankerScript bankerScript = new BankerScript();
    private final CannonScript cannonScript = new CannonScript();
    private final SlayerScript slayerScript = new SlayerScript();

    public boolean run(PvmFighterConfig config) {
        Microbot.enableAutoRunOn = false;
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyCombatSetup();
        Rs2AntibanSettings.simulateMistakes = true;
        Rs2AntibanSettings.moveMouseOffScreen = true;
        Rs2Antiban.setActivity(Activity.GENERAL_COMBAT);
        Rs2Antiban.setActivityIntensity(ActivityIntensity.MODERATE);
        Rs2Antiban.setPlayStyle(PlayStyle.MODERATE);

        if (config.toggleSlayer()) {
            // check if player has a slayer task assigned
            int creatureVarbitValue = Microbot.getVarbitPlayerValue(VarPlayer.SLAYER_TASK_CREATURE);
            EnumComposition creatureEnum = Microbot.getEnum(EnumID.SLAYER_TASK_CREATURE);
            String creatureName = creatureEnum.getStringValue(creatureVarbitValue);
            if (creatureName != null) {
                slayerTask = SlayerTask.findTaskByName(creatureName);
                if (slayerTask != null) {
                    npcTargets.addAll(List.of(slayerTask.getNpcName()));
                    PlayerLocation.COMBAT_FIELD.setWorldArea(slayerTask.getWorldArea());
                    PlayerLocation.COMBAT_FIELD.setWorldPoint(slayerTask.getWorldPoint());

                    switch (config.selectCombatStyle()) {
                        case MELEE:
                            setup = slayerTask.getMeleeSetup();
                            break;
                        case MAGIC:
                            setup = slayerTask.getMagicSetup();
                            break;
                        case RANGED:
                            setup = slayerTask.getRangedSetup();
                            break;
                    }
                } else {
                    Microbot.showMessage("Slayer task not found, shutting down!!!");
                    PvmFighterPlugin.shutdownFlag = true;
                }
            }
        } else {
            npcTargets = Arrays.stream(config.npcTargets().split(","))
                    .map((name -> name.trim().toLowerCase()))
                    .collect(Collectors.toList());
            setup = config.inventorySetup();
        }

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!fulfillConditionsToRun() || Rs2AntibanSettings.actionCooldownActive) return;
                // check if player is in desired location
                getPlayerState(config);
                currentLocation = PlayerLocation.checkCurrentPlayerLocation();
                boolean desiredLocation = checkIfInDesiredLocation();
                if (!desiredLocation && Rs2Antiban.isIdle()) walkToDesiredLocation();

//                Microbot.log(String.format("PlayerState: %s, PlayerLocation: %s", playerState, currentLocation));
                switch (playerState) {
                    case ATTACKING:
                        if (!AttackNpcScript.isRunning) attackNpcScript.run(config);
                        break;
                    case LOOTING:
                        lootScript.run(config);
                        break;
                    case BANKING:
                        bankerScript.run(config);
                        break;
                    case CANNON:
                        cannonScript.run(config);
                        break;
                    case SLAYER_MASTER:
                        slayerScript.run(config);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        return true;
    }

    @Override
    public void shutdown() {
        attackNpcScript.shutdown();
        safeSpotScript.shutdown();
        lootScript.shutdown();
        bankerScript.shutdown();
        foodScript.shutdown();
        cannonScript.shutdown();
        slayerScript.shutdown();

        super.shutdown();
        Rs2Antiban.resetAntibanSettings();
        npcTargets = new ArrayList<>();
    }

    private boolean checkIfInDesiredLocation() {
        return playerState.getPlayerLocation() == currentLocation;
    }

    private void walkToDesiredLocation() {
        if (!Rs2Antiban.isIdle() || Rs2Player.isWalking()) return;

        switch (playerState) {
            case ATTACKING:
            case LOOTING:
                walkToCombatZone();
                break;
            case SLAYER_MASTER:
                walkToSlayerMaster();
                break;
            case SAFEKEEPING:
                walkToSafeSpot();
                break;
            case BANKING:
                walkToSelectedBank();
                break;
        }
    }

    private void walkToSelectedBank() {
        WorldPoint point = PlayerLocation.BANK_LOCATION.getPoint();
        Rs2Walker.walkTo(point, 8);
        sleepUntil(() -> point.equals(Rs2Player.getWorldLocation()));
    }

    private void walkToCombatZone() {
        WorldPoint point = PlayerLocation.COMBAT_FIELD.getPoint();
        Rs2Walker.walkTo(point, 3);
        sleepUntil(() -> PlayerLocation.COMBAT_FIELD.getArea().contains(Rs2Player.getWorldLocation()));
    }

    private void walkToSlayerMaster() {
        WorldPoint point = PlayerLocation.SLAYER_MASTER.getPoint();
        Rs2Walker.walkTo(point, 3);
        sleepUntil(() -> PlayerLocation.SLAYER_MASTER.getArea().contains(Rs2Player.getWorldLocation()));
    }

    private void walkToSafeSpot() {
        WorldPoint point = PlayerLocation.SAFE_SPOT.getPoint();
        Rs2Walker.walkTo(point, 0);
        sleepUntil(() -> point.equals(Rs2Player.getWorldLocation()));
    }

    private void getPlayerState(PvmFighterConfig config) {
        if (isReadyToBank(config)) {
            playerState = PlayerState.BANKING;
            return;
        }

        if (needsToAttendCannon(config)) {
            playerState = PlayerState.CANNON;
            return;
        }

        if (needsToGetSlayerTask(config)) {
            playerState = PlayerState.SLAYER_MASTER;
            return;
        }

//        if (checkIfPlayerIsBeingAttack()) {
//            Microbot.log("Player is being attacked");
//            // if auto attack is true set attacking state, else safekeeping
//            if (config.toggleCombat()) {
//                playerState = PlayerState.ATTACKING;
//                return;
//            }
//        }

        if (areGroundItemsToLoot(config)) {
            playerState = PlayerState.LOOTING;
            return;
        }

        if (needsToReturnToSafeSpot(config)) {
            playerState = PlayerState.SAFEKEEPING;
            return;
        }

        if (config.toggleCombat()) {
            playerState = PlayerState.ATTACKING;
            return;
        }

        playerState = PlayerState.IDLE;
    }

    private boolean checkIfPlayerIsBeingAttack() {
        return Rs2Player.isInCombat() && Rs2Antiban.isIdle() && AttackNpcScript.currentNPC == null;
    }

    private boolean needsToGetSlayerTask(PvmFighterConfig config) {
        if (!config.toggleSlayer()) return false;
        // check if player has a remaining slayer task
        return Microbot.getVarbitPlayerValue(VarPlayer.SLAYER_TASK_SIZE) == 0;
    }

    private boolean needsToAttendCannon(PvmFighterConfig config) {
        if (!config.useCannon() || (slayerTask != null && !slayerTask.isCanUseCannon())) return false;
        if (needsToGetSlayerTask(config) && cannonIsAssembledFlag) {
            pickUpCannonFlag = true;
            return true;
        }
        TileObject brokenCannon = Rs2GameObject.findObjectById(14916);
        return (Microbot.getVarbitPlayerValue(VarPlayer.CANNON_AMMO) == 0 || brokenCannon != null) && currentLocation.equals(PlayerLocation.COMBAT_FIELD);
    }

    private boolean needsToReturnToSafeSpot(PvmFighterConfig config) {
        if (!config.useSafeSpot()) return false;

        if (PlayerLocation.SAFE_SPOT.getPoint() != null) {
            return !Rs2Player.getWorldLocation().equals(PlayerLocation.SAFE_SPOT.getPoint());
        }

        return false;
    }

    private boolean areGroundItemsToLoot(PvmFighterConfig config) {
        if (!config.toggleLootItems()) return false;
        if (Rs2Inventory.getEmptySlots() <= config.minFreeInventorySlots()) return false;

        boolean result = false;
        if (config.lootItemsByName()) {
            result = LootScript.hasItemsToLootByName(config);
        }

        if (config.lootItemsByPriceRange() && !result) {
            result = LootScript.hasItemsToLootByValue(config);
        }

        if (config.lootCoins() && !result) {
            result = LootScript.hasCoinsToLoot(config);
        }

        if (config.lootRunes() && !result) {
            result = LootScript.hasRunesToLoot(config);
        }
        if (config.lootBones() && !result) {
            result = LootScript.hasBonesToLoot(config);
        }

        if (config.lootArrows() && !result) {
            result = LootScript.hasArrowsToLoot(config);
        }

        return result && Rs2Antiban.isIdle();
    }

    private boolean isReadyToBank(PvmFighterConfig config) {
        if (!config.toggleBanking()) return false;
        // while still have food don't bank
        if (config.withdrawFood() && !Rs2Inventory.getInventoryFood().isEmpty()) return false;

        log.info("minimum health: {}", config.minimumHealthToRetrieve());
        log.info("player needs to retrieve: {}", Rs2Player.getHealthPercentageInt() <= config.minimumHealthToRetrieve());
        return Rs2Inventory.getEmptySlots() <= config.minFreeInventorySlots() || needsToRetreat(config);
    }

    public static boolean needsToRetreat(PvmFighterConfig config) {
        boolean healthIsLessThanMinimum = Rs2Player.getHealthPercentageInt() <= config.minimumHealthToRetrieve();
        if (config.withdrawFood() && Rs2Inventory.getInventoryFood().isEmpty() && healthIsLessThanMinimum) return true;

        return healthIsLessThanMinimum;
    }
}
