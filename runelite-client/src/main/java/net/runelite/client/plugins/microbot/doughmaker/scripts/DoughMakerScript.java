package net.runelite.client.plugins.microbot.doughmaker.scripts;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.doughmaker.DoughMakerConfig;
import net.runelite.client.plugins.microbot.doughmaker.enums.DoughItem;
import net.runelite.client.plugins.microbot.doughmaker.enums.Location;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Getter
enum PlayerState {
    RECOLLECTING(Location.WHEAT_FIELD),
    PROCESSING(Location.COOKING_GUILD_THIRD_FLOOR),
    COMBINING(Location.COOKING_GUILD_FIRST_FLOOR),
    BANKING(Location.NEAREST_BANK);

    private final Location location;

    PlayerState(Location location) {
        this.location = location;
    }
}

@Slf4j
public class DoughMakerScript extends Script {

    private PlayerState playerState;
    private Location currentLocation;
    private final int RADIUS = 3;
    private int recollectedGrain = 0;
    private int processedGrain = 0;
    private boolean init = true;

    public boolean run(DoughMakerConfig config) {
        Microbot.enableAutoRunOn = false;
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyCookingSetup();
        Rs2Antiban.setActivity(Activity.MAKING_DOUGH_AT_COOKS_GUILD);
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.fulfillConditionsToRun() || Rs2AntibanSettings.actionCooldownActive) return;

                if (init) {
                    checkPlayerInventory();
                    init = false;
                }

                // check if player is in desired location
                checkCurrentPlayerLocation();
                getPlayerState(config);
                if (!checkIfInDesiredLocation()) walkToDesiredLocation();

                log.info("Current Location: {}", currentLocation);
                log.info("Player State: {}", playerState);
                Microbot.log(String.format("Recollected Grain: %d", recollectedGrain));
                Microbot.log(String.format("Processed Grain: %d", processedGrain));
                switch (playerState) {
                    case RECOLLECTING:
                        if (currentLocation != playerState.getLocation()) {
                            log.info("Player is not inside Wheat Field");
                            break;
                        }

                        while(!Rs2Inventory.isFull() && fulfillConditionsToRun()) {
                            // if wheat field door is closed open
                            openWheatFieldDoor();

                            // Recollect wheat
                            GameObject wheatItem = Rs2GameObject.findReachableObject("Wheat", true, 8, Rs2Player.getWorldLocation());
                            if (wheatItem != null) {
                                Rs2GameObject.interact(wheatItem, "Pick");
                            }
                            Rs2Random.wait(800, 1600);
                            checkPlayerInventory();
                        }
                        break;
                    case PROCESSING:
                        if (currentLocation != playerState.getLocation()) {
                            log.info("Player is not in Cooking Guild third floor");
                            break;
                        }

                        while(recollectedGrain > 0 && fulfillConditionsToRun()) {
                            log.info("Recollected grain {}", recollectedGrain);
                            GameObject hopper = Rs2GameObject.findObject(2586, new WorldPoint(3142, 3452, 2));
                            if (hopper != null) {
                                log.info("Putting grain in hopper");
                                Rs2GameObject.interact(hopper, "Fill");
                                Rs2Random.wait(800, 1600);
                            }

                            GameObject hopperControls = Rs2GameObject.findObject(2607, new WorldPoint(3141, 3453, 2));
                            if (hopperControls != null) {
                                log.info("Move hopper controls");
                                Rs2GameObject.interact(hopperControls, "Operate");
                                Rs2Random.wait(800, 1600);
                            }
                            Rs2Random.wait(800, 1200);
                            checkPlayerInventory();
                        }

                        break;
                    case COMBINING:
                        if (currentLocation != playerState.getLocation()) {
                            log.info("Player is not in Cooking Guild first floor");
                            break;
                        }

                        while (processedGrain > 0 && fulfillConditionsToRun()) {
                            GameObject basePlantMill = Rs2GameObject.findObject(14960, new WorldPoint(3140, 3449, 0));
                            Rs2ItemModel pot = Rs2Inventory.get(ItemID.POT);
                            if (basePlantMill != null && pot != null) {
                                log.info("Recollecting flour from mill");
                                Rs2GameObject.interact(basePlantMill, "Empty");
                                sleepUntil(() -> !Rs2Player.isMoving());
                            }
                            Rs2Random.wait(800, 1200);

                            GameObject sink = Rs2GameObject.findObject(ObjectID.SINK_1763, new WorldPoint(3138, 3449, 0));
                            Rs2ItemModel emptyBucket = Rs2Inventory.get(ItemID.BUCKET);
                            if (sink != null && emptyBucket != null) {
                                // use empty bucket from inventory and then click sink
                                log.info("Filling empty bucket with water");
                                Rs2Inventory.useItemOnObject(emptyBucket.getId(), sink.getId());
                                sleepUntil(() -> !Rs2Player.isMoving());
                            }
                            Rs2Random.wait(800, 1200);

                            // combine items
                            if (!makeDough(config)) break;

                            Rs2Random.wait(800, 1200);
                            checkPlayerInventory();
                        }
                        break;
                    case BANKING:
                        if (currentLocation != playerState.getLocation()) {
                            log.info("Player is not in nearest bank location");
                            break;
                        }

                        // deposit all dough
                        int doughItem = config.doughItem().getItemId();
                        if (Rs2Inventory.hasItem(doughItem)) {
                            Rs2Bank.openBank();
                            Rs2Bank.depositAll(doughItem);
                        }
                        Rs2Random.wait(800, 1200);
                        Rs2Bank.closeBank();

                        // check if player has bucket of water and pot of flour
                        if (playerHasMaterialsToCombine()) {
                            makeDough(config);
                            Rs2Bank.openBank();
                            Rs2Bank.depositAll(doughItem);
                            Rs2Random.wait(800, 1200);
                            Rs2Bank.closeBank();
                        }

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

    // check player inventory and update grainRecollected and processedGrain
    private void checkPlayerInventory() {
        List<Rs2ItemModel> grainItemList = Rs2Inventory.all((rs2Item) -> rs2Item.id == ItemID.GRAIN);
        int newSize = grainItemList.size();
        if (newSize < recollectedGrain) {
            recollectedGrain = newSize;
            processedGrain++;
        } else {
            recollectedGrain = newSize;
            if (processedGrain > 0) processedGrain--;
        }
    }

    private boolean makeDough(DoughMakerConfig config) {
        Rs2ItemModel bucketOfWater = Rs2Inventory.get(ItemID.BUCKET_OF_WATER);
        Rs2ItemModel potOfFlour = Rs2Inventory.get(ItemID.POT_OF_FLOUR);
        if (potOfFlour != null && bucketOfWater != null && !Rs2Inventory.isFull()) {
            log.info("Combining bucket of water and pot of flour");
            Rs2Inventory.combine(bucketOfWater, potOfFlour);
            Rs2Random.wait(800, 1200);
            boolean makeDoughWidget = Rs2Widget.hasWidget("What sort of dough do you wish to make?");
            if (makeDoughWidget) {
                Rs2Keyboard.typeString(config.doughItem().getKey());
                log.info("Make dough key has been pressed");
            }
            return true;
        }

        return false;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        recollectedGrain = 0;
        processedGrain = 0;
        init = true;
        Rs2Antiban.resetAntibanSettings();
    }

    private boolean checkIfInDesiredLocation() {
        return playerState.getLocation() == currentLocation;
    }

    private void walkToDesiredLocation() {
        switch (playerState) {
            case RECOLLECTING:
                if (currentLocation == Location.COOKING_GUILD_FIRST_FLOOR) {
                    exitCookingGuild();
                }
                walkToWheatField();
                openWheatFieldDoor();
                break;
            case PROCESSING:
                if (currentLocation == Location.WHEAT_FIELD) {
                    openWheatFieldDoor();
                    walkNearCookingGuildDoor();
                    enterCookingGuild();
                    walkToCGThirdFloor();
                } else if (currentLocation == Location.NEAR_COOKING_GUILD_DOOR) {
                    enterCookingGuild();
                    walkToCGFirstFloor();
                } else {
                    walkNearCookingGuildDoor();
                    enterCookingGuild();
                    walkToCGThirdFloor();
                }
                break;
            case COMBINING:
                if (currentLocation == Location.COOKING_GUILD_THIRD_FLOOR) {
                    walkToCGFirstFloor();
                } else if (currentLocation == Location.COOKING_GUILD_SECOND_FLOOR) {
                    walkToCGFirstFloor();
                } else {
                    walkNearCookingGuildDoor();
                    enterCookingGuild();
                }
                break;
            case BANKING:
                if (currentLocation == Location.COOKING_GUILD_FIRST_FLOOR) {
                    exitCookingGuild();
                    walkToBank();
                } else {
                    walkToBank();
                }
                break;
        }
    }

    private void walkToBank() {
        // Go to nearest bank
        WorldPoint nearestBank = Location.NEAREST_BANK.getPoint();
        Rs2Walker.walkTo(nearestBank, 8);
        Microbot.pauseAllScripts = true;
        sleepUntil(() -> nearestBank.equals(Rs2Player.getWorldLocation()));
        Microbot.pauseAllScripts = false;
    }

    private void walkToWheatField() {
        WorldPoint point = Location.WHEAT_FIELD.getPoint();
        WorldArea area = Location.WHEAT_FIELD.getArea();
        Rs2Walker.walkTo(point, 0);
        Microbot.pauseAllScripts = true;
        sleepUntil(() -> area.contains(Rs2Player.getWorldLocation()));
        Microbot.pauseAllScripts = false;
    }

    private void openWheatFieldDoor() {
        WallObject wheatFieldDoor = Rs2GameObject.findDoor(15512);
        if (wheatFieldDoor != null) {
            Microbot.pauseAllScripts = true;
            Rs2GameObject.interact(wheatFieldDoor, "Open");
            log.info("Player has open wheat field door");
            Microbot.pauseAllScripts = false;
        } else log.info("Wheat field door is not available");
    }

    private void walkNearCookingGuildDoor() {
        WorldPoint point = Location.NEAR_COOKING_GUILD_DOOR.getPoint();
        Rs2Walker.walkTo(point, 0);
        Microbot.pauseAllScripts = true;
        sleepUntil(() -> point.equals(Rs2Player.getWorldLocation()));
        Microbot.pauseAllScripts = false;
    }

    private void enterCookingGuild() {
        WallObject entranceDoor = Rs2GameObject.findDoor(24958);
        if (entranceDoor != null) {
            Rs2GameObject.interact(entranceDoor, "Open");
            log.info("Player has enter Cooking Guild");
        }
        Rs2Random.wait(2000, 3200);
    }

    private void exitCookingGuild() {
        WallObject entranceDoor = Rs2GameObject.findDoor(24958);
        if (entranceDoor != null) {
            Rs2GameObject.interact(entranceDoor, "Open");
            Rs2Random.wait(800, 1600);
            log.info("Player has left Cooking Guild");
        }
    }

    private void walkToCGFirstFloor() {
        GameObject thirdFloorStaircase = Rs2GameObject.findObject(2610, new WorldPoint(3144, 3447, 2));
        if (thirdFloorStaircase != null) {
            Rs2GameObject.interact(thirdFloorStaircase, "Climb-down");
        }

        GameObject secondFloorStaircase = Rs2GameObject.findObject(2609, new WorldPoint(3144, 3447, 1));
        if (secondFloorStaircase != null) {
            Rs2GameObject.interact(secondFloorStaircase, "Climb-down");
        }
    }

    private void walkToCGThirdFloor() {
        if (currentLocation != Location.COOKING_GUILD_FIRST_FLOOR) {
            log.info("Player is not on Cooking Guild first floor, walk the player to the designated area before start the script");
        }

        WorldPoint point = Location.COOKING_GUILD_THIRD_FLOOR.getPoint();
        sleepUntil(() -> {
            GameObject firstFloorStaircase = Rs2GameObject.findObject(2608, new WorldPoint(3144, 3447, 0));
            if (firstFloorStaircase != null) {
                Rs2GameObject.interact(firstFloorStaircase, "Climb-up");
            }
            Rs2Random.wait(800, 1200);

            GameObject secondFloorStaircase = Rs2GameObject.findObject(2609, new WorldPoint(3144, 3447, 1));
            if (secondFloorStaircase != null) {
                Rs2GameObject.interact(secondFloorStaircase, "Climb-up");
            }

            if (playerIsInProvidedArea(point)) {
                log.info("Player is now in Cooking Guild third floor");
                return true;
            }

            return false;
        }, Rs2Random.between(4000, 5000));
    }

    private boolean playerIsInProvidedArea(WorldPoint point, int radius) {
        return Rs2Player.getWorldLocation().distanceTo(point) <= radius;
    }

    private boolean playerIsInProvidedArea(WorldPoint point) {
        return playerIsInProvidedArea(point, RADIUS);
    }

    private void getPlayerState(DoughMakerConfig config) {
        if (hasGrainRemaining()) {
            playerState = PlayerState.PROCESSING;
            return;
        }

        if (isReadyToCombine()) {
            playerState = PlayerState.COMBINING;
            return;
        }

        if (isReadyToBank(config.doughItem())) {
            playerState = PlayerState.BANKING;
            return;
        }

        // Recollect grain until grainRecollected equals 26
        playerState =  PlayerState.RECOLLECTING;
    }

    private boolean hasGrainRemaining() {
        if (currentLocation == Location.COOKING_GUILD_THIRD_FLOOR) {
            return Rs2Inventory.hasItem("Grain");
        }
        return (Rs2Inventory.hasItem("Grain") && recollectedGrain >= 26);
    }

    private boolean playerHasMaterialsToCombine() {
        return !Rs2Inventory.hasItem(ItemID.BUCKET_OF_WATER) || !Rs2Inventory.hasItem(ItemID.POT_OF_FLOUR);
    }

    private boolean checkFlourBin() {
        // check if base plant mill has flour
        GameObject basePlantMill = Rs2GameObject.findObject(14960, new WorldPoint(3140, 3449, 0));
        if (basePlantMill != null) {
            ObjectComposition obj = Rs2GameObject.convertGameObjectToObjectComposition(basePlantMill);
            if (Rs2GameObject.hasAction(obj, "Empty") && (!Rs2Inventory.isFull() || !playerHasMaterialsToCombine())) {
                processedGrain++;
                return true;
            }
        }

        // check if bucket of water and pot flour is available in inventory
        Rs2ItemModel bucketOfWater = Rs2Inventory.get(ItemID.BUCKET_OF_WATER);
        Rs2ItemModel potOfFlour = Rs2Inventory.get(ItemID.POT_OF_FLOUR);
        if (bucketOfWater != null && potOfFlour != null && !Rs2Inventory.isFull()) {
            processedGrain++;
            return true;
        }

        return false;
    }

    private boolean isReadyToCombine() {
        if (processedGrain > 0) {
            return true;
        } else {
            return checkFlourBin();
        }
    }

    private boolean isReadyToBank(DoughItem doughItem) {
        return Rs2Inventory.hasItem(doughItem.getItemId()) && recollectedGrain == 0 && processedGrain == 0;
    }
}
