package net.runelite.client.plugins.microbot.plankmaker;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.plankmaker.enums.Location;
import net.runelite.client.plugins.microbot.plankmaker.enums.Plank;
import net.runelite.client.plugins.microbot.plankmaker.enums.PlayerState;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.PlayStyle;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PlankMakerScript extends Script {

    public static PlayerState playerState;
    public static Location currentLocation;
    private final int MAX_PLANKS_PER_INVENTORY = 27;
    private final int RADIUS = 3;

    public boolean run(PlankMakerConfig config) {
        Microbot.enableAutoRunOn = false;
        Rs2Camera.resetZoom();
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyCookingSetup();
        Rs2AntibanSettings.moveMouseOffScreen = true;
        Rs2AntibanSettings.simulateMistakes = true;
        Rs2Antiban.setActivity(Activity.MAKING_MAHOGANY_PLANKS);
        Rs2Antiban.setPlayStyle(PlayStyle.BALANCED);

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.fulfillConditionsToRun() || Rs2AntibanSettings.actionCooldownActive) return;

                // check if player is in desired location
                checkCurrentPlayerLocation();
                getPlayerState(config);
                if (!checkIfInDesiredLocation()) walkToDesiredLocation(config);

                switch (playerState) {
                    case PROCESSING:
                        if (currentLocation != playerState.getLocation()) {
                            log.info("Player is not inside sawmill area.");
                            break;
                        }

                        NPC operator = Rs2Npc.getNpc("Sawmill operator");
                        if (operator != null) {
                            Rs2Npc.interact(operator, "Buy-plank");
                            Rs2Random.wait(1200, 1600);
                            boolean howMany = Rs2Widget.hasWidget("How many do you wish to make?");
                            if (howMany) {
                                Rs2Keyboard.typeString(config.plankToMake().getStringKey());
                                Rs2Random.wait(1200, 1600);
                            }
                        }
                        break;
                    case BANKING:
                        if (currentLocation != playerState.getLocation()) {
                            log.info("Player is not in nearest bank location");
                            break;
                        }

                        String bankTeleportItemName = config.sawmillLocation().getBankTeleport()[0];
                        // check if bank teleport is exhausted
                        Rs2Item bankTeleportItem = Rs2Equipment.get(bankTeleportItemName);
                        if (bankTeleportItem != null) {
                            // check if item has available the necessary teleport
                            if (!bankTeleportItem.getEquipmentActions().contains(config.sawmillLocation().getBankTeleport()[1])) {
                                Rs2Equipment.unEquip(bankTeleportItemName);

                                if (!Rs2Bank.isOpen()) Rs2Bank.openBank();
                                Rs2Bank.depositOne(bankTeleportItemName);
                                Rs2Random.wait(1200, 1600);
                                // check if is left another charged teleport item in bank
                                if (Rs2Bank.hasItem(String.format("%s(", bankTeleportItemName))) {
                                    Rs2Bank.withdrawOne(bankTeleportItemName);
                                    Rs2Bank.closeBank();
                                    Rs2Inventory.interact(bankTeleportItemName, "Wear");
                                } else {
                                    // No more necessary teleport amulets found, shutdown.
                                    Microbot.showMessage("No more bank teleport left");
                                    shutdown();
                                }
                            }
                        } else {
                            if (!Rs2Bank.isOpen()) Rs2Bank.openBank();
                            // check if is left another charged teleport item in bank
                            if (Rs2Bank.hasItem(String.format("%s(", bankTeleportItemName))) {
                                Rs2Bank.withdrawOne(bankTeleportItemName);
                                Rs2Bank.closeBank();
                                Rs2Inventory.interact(bankTeleportItemName, "Wear");
                            } else {
                                // No more necessary teleport amulets found, shutdown.
                                Microbot.showMessage("No more bank teleport left");
                                shutdown();
                            }
                        }

                        // deposit all planks and withdraw more logs
                        if (!Rs2Bank.isOpen()) Rs2Bank.openBank();

                        Plank plankToMake = config.plankToMake();
                        if (Rs2Inventory.hasItem(plankToMake.getName())) {
                            Rs2Bank.depositAll(plankToMake.getName());
                        }

                        // check if player has necessary coins in inventory
                        if (!Rs2Inventory.hasItem(ItemID.COINS_995)) {
                            // check if player has necessary money in banks to process a full inventory
                            int quantityToWithdraw = MAX_PLANKS_PER_INVENTORY * plankToMake.getNecessaryCoins();
                            if (Rs2Bank.hasBankItem(ItemID.COINS_995, quantityToWithdraw)) {
                                Rs2Bank.withdrawAll(ItemID.COINS_995);
                            } else {
                                Microbot.showMessage("No more money left to process a full inventory");
                                shutdown();
                            }
                        }

                        // withdraw logs
                        if (Rs2Bank.hasItem(plankToMake.getRequiredLogs())) {
                            Rs2Bank.withdrawAll(plankToMake.getRequiredLogs());
                        } else {
                            Microbot.showMessage("No more logs left");
                            shutdown();
                        }

                        Rs2Random.wait(800, 1200);
                        Rs2Bank.closeBank();
                        break;
                }
            } catch (Exception ex) {
                Microbot.log(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private void checkCurrentPlayerLocation() {
        Optional<Location> currentLoc = Location.stream().filter((location) -> {
            WorldArea area = location.getArea();
            return area.contains(Rs2Player.getWorldLocation());
        }).findFirst();
        currentLoc.ifPresentOrElse(location -> currentLocation = location, () -> currentLocation = Location.OUTSIDE_POINT);
    }

    @Override
    public void shutdown() {
        super.shutdown();
        Rs2Antiban.resetAntibanSettings();
    }

    private boolean checkIfInDesiredLocation() {
        return playerState.getLocation() == currentLocation;
    }

    private void walkToDesiredLocation(PlankMakerConfig config) {
        switch (playerState) {
            case PROCESSING:
                // Go to sawmill
                WorldPoint sawmillPoint = config.sawmillLocation().getLocation().getPoint();
                Rs2Walker.walkTo(sawmillPoint, 8);
                sleepUntilFulfillCondition(() -> playerState.getLocation().getArea().contains(Rs2Player.getWorldLocation()), () -> Rs2Random.wait(800, 1200));
                break;
            case BANKING:
                // Go to nearest bank
                WorldPoint nearestBank = config.sawmillLocation().getBankLocation().getPoint();
                Rs2Walker.walkTo(nearestBank, 8);
                sleepUntilFulfillCondition(() -> playerState.getLocation().getArea().contains(Rs2Player.getWorldLocation()), () -> Rs2Random.wait(800, 1200));
                break;
        }
    }

    private boolean playerIsInProvidedArea(WorldPoint point, int radius) {
        return Rs2Player.getWorldLocation().distanceTo(point) <= radius;
    }

    private boolean playerIsInProvidedArea(WorldPoint point) {
        return playerIsInProvidedArea(point, RADIUS);
    }

    private void getPlayerState(PlankMakerConfig config) {
        if (isReadyToProcess(config)) {
            playerState = PlayerState.PROCESSING;
            return;
        }

        if (isReadyToBank(config)) {
            playerState = PlayerState.BANKING;
            return;
        }

        playerState =  PlayerState.IDLE;
    }

    private boolean isReadyToProcess(PlankMakerConfig config) {
        String requiredLogs = config.plankToMake().getRequiredLogs();
        if (Rs2Inventory.hasItem(requiredLogs)) {
            Rs2Item logs = Rs2Inventory.get(requiredLogs);
            Rs2Item coins = Rs2Inventory.get(ItemID.COINS_995);
            return coins.quantity >= (config.plankToMake().getNecessaryCoins() * logs.quantity);
        }
        return false;
    }

    private boolean isReadyToBank(PlankMakerConfig config) {
        return Rs2Inventory.hasItem(config.plankToMake().getName()) || !Rs2Inventory.hasItem(config.plankToMake().getRequiredLogs());
    }
}
