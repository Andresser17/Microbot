package net.runelite.client.plugins.microbot.pvmfighter;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.bank.BankerScript;
import net.runelite.client.plugins.microbot.pvmfighter.combat.*;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.pvmfighter.loot.LootScript;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.ActivityIntensity;
import net.runelite.client.plugins.microbot.util.antiban.enums.PlayStyle;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PvmFighterScript extends Script {
    public static PlayerState playerState;
    public static PlayerLocation currentLocation;
    private final AttackNpcScript attackNpcScript = new AttackNpcScript();
    private final FoodScript foodScript = new FoodScript();
    private final LootScript lootScript = new LootScript();
    private final SafeSpot safeSpotScript = new SafeSpot();
    private final BankerScript bankerScript = new BankerScript();

    public boolean run(PvmFighterConfig config) {
        Microbot.enableAutoRunOn = false;
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyCombatSetup();
        Rs2Antiban.setActivity(Activity.GENERAL_COMBAT);
        Rs2Antiban.setActivityIntensity(ActivityIntensity.MODERATE);
        Rs2Antiban.setPlayStyle(PlayStyle.MODERATE);

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!fulfillConditionsToRun() || Rs2AntibanSettings.actionCooldownActive) return;
                // check if player is in desired location
                getPlayerState(config);
                currentLocation = PlayerLocation.checkCurrentPlayerLocation();
                boolean desiredLocation = checkIfInDesiredLocation();
                if (!desiredLocation && Rs2Antiban.isIdle()) walkToDesiredLocation();

                Microbot.log(String.format("PlayerState: %s, PlayerLocation: %s", playerState, currentLocation));
                switch (playerState) {
                    case EATING:
                        foodScript.run(config);
                        break;
                    case ATTACKING:
                        attackNpcScript.run(config);
                        break;
                    case LOOTING:
                        lootScript.run(config);
                        break;
                    case BANKING:
                        bankerScript.run(config);
                        break;
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

        super.shutdown();
        Rs2Antiban.resetAntibanSettings();
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
            case SAFEKEEPING:
                walkToSafeSpot();
                break;
            case BANKING:
                walkToNearestBank();
                break;
        }
    }

    private void walkToNearestBank() {
        WorldPoint point = PlayerLocation.NEAREST_BANK.getPoint();
        Rs2Walker.walkTo(point, 8);
        sleepUntil(() -> point.equals(Rs2Player.getWorldLocation()));
    }

    private void walkToCombatZone() {
        WorldPoint point = PlayerLocation.COMBAT_FIELD.getPoint();
        Rs2Walker.walkTo(point, 3);
        sleepUntil(() -> PlayerLocation.COMBAT_FIELD.getArea().contains(Rs2Player.getWorldLocation()));
    }

    private void walkToSafeSpot() {
        WorldPoint point = PlayerLocation.SAFE_SPOT.getPoint();
        Rs2Walker.walkTo(point, 2);
        sleepUntil(() -> point.equals(Rs2Player.getWorldLocation()));
    }

    private void getPlayerState(PvmFighterConfig config) {
        if (needToEat(config)) {
            playerState = PlayerState.EATING;
            return;
        }

        if (needToSafeKeep(config)) {
            playerState = PlayerState.SAFEKEEPING;
            return;
        }

        if (areGroundItemsToLoot(config)) {
            playerState = PlayerState.LOOTING;
            return;
        }

        if (isReadyToBank(config)) {
            playerState = PlayerState.BANKING;
            return;
        }

        if (config.toggleCombat()) {
            playerState = PlayerState.ATTACKING;
            return;
        }

        playerState = PlayerState.STALE;
    }

    private boolean needToEat(PvmFighterConfig config) {
        if (!config.toggleFood()) return false;

        // get food that heal value is less than lost health
        Optional<Rs2Item> foodToEat = Rs2Inventory.getInventoryFood().stream().filter(food -> {
            Rs2Food foodValue = Rs2Food.getFoodById(food.id);
            if (foodValue == null) return false;

            return foodValue.getHeal() <= (Rs2Player.getMaxHealth() - Rs2Player.checkCurrentHealth());
        }).findFirst();

        return foodToEat.isPresent() && !Rs2Player.isFullHealth();
    }

    private boolean needToSafeKeep(PvmFighterConfig config) {
        if (!config.toggleSafeSpot()) return false;

        if (PlayerLocation.SAFE_SPOT.getPoint() != null) {
            return Rs2Player.getHealthPercentage() <= config.minimumHealthSafeSpot();
        }

        return false;
    }

    private boolean areGroundItemsToLoot(PvmFighterConfig config) {
        if (!config.toggleLootItems()) return false;
        if (Rs2Inventory.getEmptySlots() <= config.minFreeSlots()) return false;
        if (!Rs2Antiban.isIdle()) return false;

        boolean result = false;
        if (config.toggleLootItemsByName()) {
            result = LootScript.hasItemsToLootByName(config);
        } else if (config.toggleLootItemsByValue()) {
            result = LootScript.hasItemsToLootByValue(config);
        } else if (config.toggleLootCoins()) {
            result = LootScript.hasCoinsToLoot(config);
        } else if (config.toggleLootRunes()) {
            result = LootScript.hasRunesToLoot(config);
        } else if (config.toggleLootBones()) {
            result = LootScript.hasBonesToLoot(config);
        } else if (config.toggleLootArrows()) {
            result = LootScript.hasArrowsToLoot(config);
        }

        return result && Rs2Antiban.isIdle();
    }

    private boolean isReadyToBank(PvmFighterConfig config) {
        if (!config.toggleBanking()) return false;
        return Rs2Inventory.getEmptySlots() <= config.minFreeSlots();
    }
}