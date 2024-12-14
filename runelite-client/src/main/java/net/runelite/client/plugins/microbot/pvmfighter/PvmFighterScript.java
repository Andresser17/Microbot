package net.runelite.client.plugins.microbot.pvmfighter;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.grounditem.LootingParameters;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PvmFighterScript extends Script {
    public static PlayerState playerState;
    public static PlayerLocation currentLocation;

    public boolean run(PvmFighterConfig config) {
        Microbot.enableAutoRunOn = false;
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyCombatSetup();
        Rs2Antiban.setActivity(Activity.GENERAL_COMBAT);

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!fulfillConditionsToRun() || Rs2AntibanSettings.actionCooldownActive) return;

                // check if player is in desired location
                getPlayerState(config);
                currentLocation = PlayerLocation.checkCurrentPlayerLocation();
                if (!checkIfInDesiredLocation()) walkToDesiredLocation();

                Microbot.log(String.format("PlayerState: %s, PlayerLocation: %s", playerState, currentLocation));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 500, TimeUnit.MILLISECONDS);

        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        Rs2Antiban.resetAntibanSettings();
    }

    private boolean checkIfInDesiredLocation() {
        return playerState.getPlayerLocation() == currentLocation;
    }

    private void walkToDesiredLocation() {
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
        Rs2Walker.walkTo(point, 8);
        sleepUntil(() -> point.equals(Rs2Player.getWorldLocation()));
    }

    private void walkToSafeSpot() {
        WorldPoint point = PlayerLocation.SAFE_SPOT.getPoint();
        Rs2Walker.walkTo(point, 8);
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
        return !Rs2Inventory.getInventoryFood().isEmpty();
    }

    private boolean needToSafeKeep(PvmFighterConfig config) {
        if (!config.toggleSafeSpot()) return false;

        if (config.safeSpot() != null) {
            return Rs2Player.getHealthPercentage() <= config.minimumHealthSafeSpot();
        }

        return false;
    }

    private boolean areGroundItemsToLoot(PvmFighterConfig config) {
        if (!config.toggleLootItems()) return false;
        if (Rs2Inventory.getEmptySlots() <= config.minFreeSlots()) return false;

        if (config.toggleLootItemsByName()) {
            String[] itemsToLoot = config.lootItemsByName().split(",");
            Optional<String> optional = Arrays.stream(itemsToLoot).filter((itemToLoot) -> Rs2GroundItem.hasLootableItems((groundItem) -> Objects.equals(groundItem.getName(), itemToLoot) && playerState.getPlayerLocation().getArea().contains(groundItem.getLocation()))).findFirst();
            return optional.isPresent() && !Rs2Player.isInCombat();
        }

        return false;
    }

    private boolean isReadyToBank(PvmFighterConfig config) {
        if (!config.toggleBanking()) return false;
        return Rs2Inventory.getEmptySlots() <= config.minFreeSlots();
    }
}
