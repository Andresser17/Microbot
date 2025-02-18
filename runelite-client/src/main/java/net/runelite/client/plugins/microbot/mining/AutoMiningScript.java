package net.runelite.client.plugins.microbot.mining;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.mining.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.mining.enums.PlayerState;
import net.runelite.client.plugins.microbot.mining.enums.Rocks;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AutoMiningScript extends Script {
    public static final String version = "1.5";
    private Rocks[] oresToMine;
    PlayerState playerState;
    PlayerLocation currentLocation;

    public boolean run(AutoMiningConfig config) {
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyMiningSetup();
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.simulateMistakes = true;
        Rs2AntibanSettings.moveMouseOffScreen = true;
        Rs2AntibanSettings.actionCooldownChance = 0.1;

        oresToMine = Arrays.stream(config.oresToMine().split(",")).map((name) -> Rocks.findRockByName(name.trim().toLowerCase())).toArray(Rocks[]::new);

        // check if player has level to mine provided ores
        Arrays.stream(oresToMine).forEach(ore -> {
            if (ore != null) {
                if (!ore.hasRequiredLevel()) {
                    Microbot.showMessage("You do not have the required mining level to mine this ore.");
                    shutdown();
                }
            } else {
                Microbot.showMessage("Ore not found. Enter full name, ex: iron ore");
                shutdown();
            }
        });
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!fulfillConditionsToRun() || Rs2AntibanSettings.actionCooldownActive) return;

//                if (Rs2Equipment.isWearing("Dragon pickaxe"))
//                    Rs2Combat.setSpecState(true, 1000);

                // check if player is in desired location
                getPlayerState(config);
                currentLocation = PlayerLocation.checkCurrentPlayerLocation();
                boolean desiredLocation = checkIfInDesiredLocation();
                if (!desiredLocation) walkToDesiredLocation();

//                log.info("PlayerState: {}, CurrentLocation: {}", playerState, currentLocation);
//                log.info("Mining field: {}, {}", PlayerLocation.MINING_FIELD.getArea().getX(), PlayerLocation.MINING_FIELD.getArea().getY());
                switch (playerState) {
                    case MINING:
                        if (playerState.getPlayerLocation() != currentLocation) return;

                        GameObject rock = null;
                        for (Rocks ore : oresToMine) {
                            GameObject localRock = Rs2GameObject.findReachableObject(ore.getName(), true, config.area(), PlayerLocation.MINING_FIELD.getPoint());
                            if (localRock != null) {
                                if (PlayerLocation.MINING_FIELD.getArea().contains(localRock.getWorldLocation())) {
                                    rock = localRock;
                                    break;
                                }
                            }
                        }

                        if (rock != null) {
                            if (Rs2GameObject.interact(rock)) {
                                Rs2Player.waitForXpDrop(Skill.MINING, true);
                                Rs2Antiban.actionCooldown();
                                Rs2Antiban.takeMicroBreakByChance();
                            }
                        }
                        break;
                    case DROPPING:
                        if (Rs2Inventory.isFull()) {
                            String[] itemToKeepNames = Arrays.stream(config.itemsToKeep().split(","))
                                    .map((name) -> name.trim().toLowerCase()).toArray(String[]::new);

                            Rs2Inventory.dropAllExcept(itemToKeepNames);
                        }
                        break;
                    case BANKING:
                        if (playerState.getPlayerLocation() != currentLocation) return;
                        if (!Rs2Bank.isOpen()) Rs2Bank.openBank();

                        String[] itemToKeepNames = Arrays.stream(config.itemsToKeep().split(","))
                                .map((name) -> name.trim().toLowerCase()).toArray(String[]::new);

                        Rs2Bank.depositAllExcept(false, itemToKeepNames);
                        break;
                }
            } catch (Exception ex) {
                Microbot.log(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean checkIfInDesiredLocation() {
        return playerState.getPlayerLocation() == currentLocation;
    }

    private void walkToDesiredLocation() {
        if (!Rs2Antiban.isIdle() || Rs2Player.isWalking(true)) return;

        switch (playerState) {
            case MINING:
                walkToMiningField();
                break;
            case BANKING:
                walkToSelectedBank();
                break;
        }
    }

    private void walkToMiningField() {
        WorldPoint point = PlayerLocation.MINING_FIELD.getPoint();
        Rs2Walker.walkTo(point, 3);
        sleepUntilFulfillCondition(() -> PlayerLocation.MINING_FIELD.getArea().contains(Rs2Player.getWorldLocation()), () -> Rs2Random.wait(800, 1200));
    }

    private void walkToSelectedBank() {
        WorldPoint point = PlayerLocation.BANK_LOCATION.getPoint();
        Rs2Walker.walkTo(point, 3);
        sleepUntilFulfillCondition(() -> PlayerLocation.BANK_LOCATION.getArea().contains(Rs2Player.getWorldLocation()), () -> Rs2Random.wait(800, 1200));
    }

    private void getPlayerState(AutoMiningConfig config) {
        if (isReadyToBank()) {
            if (config.useBank()) playerState = PlayerState.BANKING;
            else playerState = PlayerState.DROPPING;
            return;
        }

        playerState = PlayerState.MINING;
    }

    private boolean isReadyToBank() {
        return Rs2Inventory.isFull();
    }

    @Override
    public void shutdown(){
        super.shutdown();
        Rs2Antiban.resetAntibanSettings();
    }
}
