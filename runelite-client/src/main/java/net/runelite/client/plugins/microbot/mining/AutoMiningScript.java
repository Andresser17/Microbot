package net.runelite.client.plugins.microbot.mining;

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
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class AutoMiningScript extends Script {
    public static final String version = "1.4.3";
    private static final int GEM_MINE_UNDERGROUND = 11410;
    private static final int BASALT_MINE = 11425;
    private Rocks[] oresToMine;
    PlayerState playerState;
    PlayerLocation currentLocation;

    public boolean run(AutoMiningConfig config) {
        initialPlayerLocation = null;
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyMiningSetup();
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.simulateMistakes = true;
        Rs2AntibanSettings.moveMouseOffScreen = true;
        Rs2AntibanSettings.actionCooldownChance = 0.1;

        oresToMine = Arrays.stream(config.oresToMine().split(",")).map((name) -> Rocks.findTaskByName(name.trim().toLowerCase())).toArray(Rocks[]::new);

        // check if player has level to mine provided ores
        Arrays.stream(oresToMine).forEach(ore -> {
            if (!ore.hasRequiredLevel()) {
                Microbot.showMessage("You do not have the required mining level to mine this ore.");
                shutdown();
            }
        });
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!fulfillConditionsToRun() || Rs2AntibanSettings.actionCooldownActive) return;
                if (initialPlayerLocation == null) {
                    initialPlayerLocation = Rs2Player.getWorldLocation();
                }

//                if (Rs2Equipment.isWearing("Dragon pickaxe"))
//                    Rs2Combat.setSpecState(true, 1000);

                // check if player is in desired location
                getPlayerState(config);
                currentLocation = PlayerLocation.checkCurrentPlayerLocation();
                boolean desiredLocation = checkIfInDesiredLocation();
                if (!desiredLocation && Rs2Antiban.isIdle()) walkToDesiredLocation();

                switch (playerState) {
                    case MINING:
                        if (playerState.getPlayerLocation() != currentLocation) return;

                        GameObject rock = null;
                        for (Rocks ore : oresToMine) {
                            rock = Rs2GameObject.findReachableObject(ore.getName(), true, config.area(), initialPlayerLocation);
                            if (rock != null) break;
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

                        Rs2Bank.depositAllExcept(itemToKeepNames);
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
        if (!Rs2Antiban.isIdle() || Rs2Player.isWalking()) return;

        switch (playerState) {
            case MINING:
                walkToMiningField();
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

    private void walkToMiningField() {
        WorldPoint point = PlayerLocation.MINING_FIELD.getPoint();
        Rs2Walker.walkTo(point, 3);
        sleepUntil(() -> PlayerLocation.MINING_FIELD.getArea().contains(Rs2Player.getWorldLocation()));
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
