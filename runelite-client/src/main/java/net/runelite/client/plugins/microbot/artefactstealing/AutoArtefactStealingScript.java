package net.runelite.client.plugins.microbot.artefactstealing;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.artefactstealing.enums.*;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcModel;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AutoArtefactStealingScript extends Script {
    public static final String version = "0.2";
    private final int[] STAMINA_POTION_IDS = new int[]{ItemID.STAMINA_POTION1, ItemID.STAMINA_POTION2, ItemID.STAMINA_POTION3, ItemID.STAMINA_POTION4};
    private final int CAPTAIN_KHALED_ID = 6972;
    private final int STEALING_ARTEFACTS_TASK_VARBIT = 4903;
    private final int BASE_FLOOR_LADDER = 27634;
    private final int FIRST_FLOOR_LADDER = 27635;
    private final WorldArea NORTH_CORRIDOR_PATROL_AVOID_AREA = new WorldArea(1763, 3754, 14, 4, 0);
    private final WorldPoint WEST_BRIDGE_PATROL_EAST_POINT = new WorldPoint(1762, 3755, 0);
    private final WorldPoint WEST_BRIDGE_SAFE_SPOT = new WorldPoint(1758, 3752, 0);
    private final WorldPoint WEST_BRIDGE_INTERPOLATION_SAFE_SPOT = new WorldPoint(1762, 3754, 0);
    private final WorldArea WEST_BRIDGE_INTERPOLATION_AREA = new WorldArea(1761, 3754, 5, 3, 0);
    private final WorldPoint WEST_BRIDGE_SAFE_SPOT_EAST = new WorldPoint(1763, 3752, 0);
    private final WorldArea WEST_BRIDGE_SAFE_SPOT_EAST_AREA = new WorldArea(1762, 3750, 4, 4, 0);
    private final WorldPoint NORTH_SOUTH_BRIDGE_SAFE_SPOT = new WorldPoint(1772, 3745, 0);
    private final WorldPoint NORTH_EAST_BRIDGE_SAFE_SPOT = new WorldPoint(1776, 3749, 0);
    private final WorldPoint NORTH_EAST_OUTSIDE_POINT = new WorldPoint(1790, 3749, 0);
    private final WorldPoint SOUTH_EAST_SAFE_SPOT = new WorldPoint(1778, 3729, 0);
    private final WorldPoint SOUTH_EAST_OUTSIDE_POINT = new WorldPoint(1797, 3730, 0);
    public Task task;
    public PlayerState playerState;
    public PlayerLocation currentLocation;

    public boolean run(AutoArtefactStealingConfig config) {
        Rs2Antiban.resetAntibanSettings();
        Rs2Camera.resetZoom();
        Rs2Camera.getAngleTo(Orientation.NORTH.getAngle());
        Rs2Antiban.antibanSetupTemplates.applyThievingSetup();
        Rs2AntibanSettings.naturalMouse = true;
//        Rs2AntibanSettings.simulateMistakes = true;
//        Rs2AntibanSettings.moveMouseOffScreen = true;
        Rs2AntibanSettings.actionCooldownChance = 0.1;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!fulfillConditionsToRun() || Rs2AntibanSettings.actionCooldownActive) return;

                getTask();
                getPlayerState(config);
                currentLocation = PlayerLocation.checkCurrentPlayerLocation();
                boolean desiredLocation = checkIfInDesiredLocation();
                if (!desiredLocation) walkToDesiredLocation();
                useStaminaPotions();

                log.info("PlayerState: {}, CurrentLocation: {}", playerState, currentLocation);
                switch (playerState) {
                    case GET_TASK:
                        if (playerState.getPlayerLocation() != PlayerLocation.CAPTAIN_LOCATION) return;

                        Rs2NpcModel captain = Rs2Npc.getNpc(CAPTAIN_KHALED_ID);
                        Rs2Npc.interact(captain, "Task");

                        // find if player already has a task
                        if (task == Task.NO_TASK) {
                            Rs2Keyboard.keyPress(32); // press enter
                            Rs2Random.wait(1000, 1200);

                        } else if (task == Task.CAUGHT) {
                            Rs2Keyboard.keyPress(32); // press enter
                            Rs2Random.wait(1000, 1200);

                            // select first option
                            Rs2Keyboard.typeString("1");
                            Rs2Random.wait(1000, 1200);

                            Rs2Keyboard.keyPress(32); // press enter
                            Rs2Random.wait(1000, 1200);

                            Rs2Keyboard.keyPress(32); // press enter
                            Rs2Random.wait(1000, 1200);

                            Rs2Keyboard.keyPress(32); // press enter
                            Rs2Random.wait(1000, 1200);
                        }

                        break;
                    case STEAL_ARTEFACT:
                        if (!task.getBaseFloor().getArea().contains(Rs2Player.getWorldLocation())) return;

                        if (task.getFirstFloor() != null) {
                            // find ladder
                            Rs2GameObject.interact(BASE_FLOOR_LADDER, "Climb");
                            Rs2Random.wait(3000, 4000);
                        }

                        for (Drawers drawers : task.getDrawers()) {
                            if (!isRunning()) break;

                           GameObject object = Rs2GameObject.findObject(drawers.getId(), drawers.getPoint());
                           Rs2GameObject.interact(object);
                           Rs2Random.wait(4000, 4200);
                           if (Artefact.hasArtefactInInventory()) {
                               log.info("Artefact in inventory");
                               break;
                           }
                        }

                        // find ladder
                        Rs2GameObject.interact(FIRST_FLOOR_LADDER, "Climb");
                        Rs2Random.wait(2000, 3000);
                        break;
                    case LURE_GUARDS:
                        break;
                    case SNEAK_GUARDS:
                        if (currentLocation != PlayerLocation.HOUSE_ZONE) return;

                        WorldPoint playerLocation =  Rs2Player.getWorldLocation();

                        // North house to North-east safe spot, to North-east outside point
                        if (Task.NORTH.getBaseFloor().getArea().contains(playerLocation)) {
                            while (!NORTH_EAST_OUTSIDE_POINT.equals(Rs2Player.getWorldLocation())) {
                                if (!isRunning()) break;

                                Rs2NpcModel northCorridorPatrol = Rs2Npc.getNpc(Patrolman.NORTH_CORRIDOR.getId());
                                Rs2NpcModel farNorthPatrol = Rs2Npc.getNpc(Patrolman.FAR_NORTH_BRIDGE.getId());
                                boolean northCorridorCondition = Orientation.SOUTH.getAngle() == northCorridorPatrol.getOrientation();
                                boolean farNorthCondition = farNorthPatrol == null || Orientation.SOUTH.getAngle() != farNorthPatrol.getOrientation();
                                if (northCorridorCondition && farNorthCondition) {
//                                    Rs2Walker.walkFastCanvas(NORTH_EAST_BRIDGE_SAFE_SPOT);
                                    Rs2Walker.walkFastCanvas(NORTH_EAST_OUTSIDE_POINT);
//                                    Rs2Random.wait(500, 600); // wait for player to start walking
                                    sleepUntilFulfillCondition(() -> !Rs2Player.isWalking(true), () -> Rs2Random.wait(300, 500));
                                }

                                Rs2Random.wait(300, 500);
                            }
                        }

                        // North-west house to West bridge safe spot
                        if (Task.NORTH_WEST.getBaseFloor().getArea().contains(playerLocation)) {
                            while (!WEST_BRIDGE_SAFE_SPOT.equals(Rs2Player.getWorldLocation())) {
                                if (!isRunning()) break;

                                Rs2NpcModel westBridgePatrol = Rs2Npc.getNpc(Patrolman.WEST_BRIDGE.getId());
                                if (westBridgePatrol.getWorldLocation().equals(WEST_BRIDGE_PATROL_EAST_POINT)
                                        || Orientation.EAST.getAngle() == westBridgePatrol.getOrientation() && westBridgePatrol.getPoseAnimation() != -1) {
                                    Rs2Walker.walkFastCanvas(WEST_BRIDGE_SAFE_SPOT);
                                    Rs2Random.wait(800, 1000);
                                    sleepUntilFulfillCondition(() -> !Rs2Player.isWalking(true), () -> Rs2Random.wait(800, 1000));
                                }

                                Rs2Random.wait(50, 60);
                            }
                        }

                        // West house to West Bridge SafeSpot
                        if (Task.WEST.getBaseFloor().getArea().contains(playerLocation)) {
                            while (!WEST_BRIDGE_SAFE_SPOT.equals(Rs2Player.getWorldLocation())) {
                                if (!isRunning()) break;

                                Rs2Walker.walkFastCanvas(WEST_BRIDGE_SAFE_SPOT);
                                sleepUntilFulfillCondition(() -> !Rs2Player.isWalking(true), () -> Rs2Random.wait(50, 60));

                                Rs2Random.wait(50, 60);
                            }
                        }

                        // South-west house to West House
                        if (Task.SOUTH_WEST.getBaseFloor().getArea().contains(playerLocation)) {
                            sleepUntilFulfillCondition(() -> {
                                Rs2NpcModel southWestPatrol = Rs2Npc.getNpc(Patrolman.SOUTH_WEST_CORRIDOR.getId());
                                if (Orientation.WEST.getAngle() == southWestPatrol.getCurrentOrientation()) {
                                    Rs2Walker.walkFastCanvas(PlayerLocation.WEST_HOUSE_BASE_FLOOR.getPoint());
                                }
                                return PlayerLocation.WEST_HOUSE_BASE_FLOOR.getArea().contains(Rs2Player.getWorldLocation());
                            }, () -> Rs2Random.wait(300, 500));
                        }

                        // West Bridge SafeSpot to SafeSpot east
                        if (WEST_BRIDGE_SAFE_SPOT.equals(playerLocation)) {
                            while (!WEST_BRIDGE_SAFE_SPOT_EAST.equals(Rs2Player.getWorldLocation())) {
                                if (!isRunning()) break;

                                Rs2NpcModel westBridgePatrol = Rs2Npc.getNpc(Patrolman.WEST_BRIDGE.getId());
                                Rs2NpcModel northCorridorPatrol = Rs2Npc.getNpc(Patrolman.NORTH_CORRIDOR.getId());

                                if (WEST_BRIDGE_SAFE_SPOT_EAST_AREA.contains(Rs2Player.getWorldLocation())) {
                                    Rs2Walker.walkFastCanvas(WEST_BRIDGE_SAFE_SPOT_EAST);
                                    sleepUntilFulfillCondition(() -> !Rs2Player.isWalking(true), () -> Rs2Random.wait(100, 110));
                                }
                                else if (WEST_BRIDGE_INTERPOLATION_AREA.contains(Rs2Player.getWorldLocation())
                                        && !WEST_BRIDGE_PATROL_EAST_POINT.equals(westBridgePatrol.getWorldLocation())) {
                                    Rs2Walker.walkFastCanvas(WEST_BRIDGE_SAFE_SPOT_EAST);
                                    sleepUntilFulfillCondition(() -> !Rs2Player.isWalking(true), () -> Rs2Random.wait(100, 110));
                                } else if (westBridgePatrol.getWorldLocation().equals(new WorldPoint(1759, 3755, 0))
                                        && (northCorridorPatrol == null || !NORTH_CORRIDOR_PATROL_AVOID_AREA.contains(northCorridorPatrol.getWorldLocation()))) {
                                    sleepUntilTick(4);
                                    Rs2Walker.walkCanvas(WEST_BRIDGE_INTERPOLATION_SAFE_SPOT);
                                    sleepUntilFulfillCondition(() -> !Rs2Player.isWalking(true), () -> Rs2Random.wait(100, 110));
                                }

                                Rs2Random.wait(50, 60);
                            }
                        }

                        // West Bridge Safe Spot east to North South SafeSpot
//                        if (WEST_BRIDGE_SAFE_SPOT_EAST.equals(playerLocation)) {
//                            while (!NORTH_SOUTH_BRIDGE_SAFE_SPOT.equals(Rs2Player.getWorldLocation())) {
//                                if (!isRunning()) break;
//
//                                Rs2NpcModel northCorridorPatrol = Rs2Npc.getNpc(Patrolman.NORTH_CORRIDOR.getId());
//                                int orientation = northCorridorPatrol.getCurrentOrientation();
//                                if (Orientation.SOUTH.getAngle() != orientation || Orientation.EAST.getAngle() != orientation) {
//                                    Rs2Walker.walkFastCanvas(NORTH_SOUTH_BRIDGE_SAFE_SPOT);
//                                    sleepUntilFulfillCondition(() -> NORTH_SOUTH_BRIDGE_SAFE_SPOT.equals(Rs2Player.getWorldLocation()), () -> Rs2Random.wait(800, 1200));
//                                }
//
//                                Rs2Random.wait(300, 500);
//                            }
//                        }

                        if (WEST_BRIDGE_SAFE_SPOT_EAST.equals(playerLocation)) {
                            while (!NORTH_EAST_BRIDGE_SAFE_SPOT.equals(Rs2Player.getWorldLocation())) {
                                if (!isRunning()) break;

                                Rs2NpcModel northCorridorPatrol = Rs2Npc.getNpc(Patrolman.NORTH_CORRIDOR.getId());

                                if (Orientation.SOUTH.getAngle() != northCorridorPatrol.getOrientation() || Orientation.EAST.getAngle() != northCorridorPatrol.getOrientation()) {
                                    Rs2Walker.walkFastCanvas(NORTH_EAST_BRIDGE_SAFE_SPOT);
                                    sleepUntilFulfillCondition(() -> !Rs2Player.isWalking(true), () -> Rs2Random.wait(800, 1200));
                                }

                                Rs2Random.wait(50, 60);
                            }

                            Rs2Walker.walkFastCanvas(NORTH_EAST_OUTSIDE_POINT);
                        }


                        // North South Bridge SafeSpot to North East SafeSpot
                        if (NORTH_SOUTH_BRIDGE_SAFE_SPOT.equals(playerLocation)) {
                            Rs2NpcModel northCorridorPatrol = Rs2Npc.getNpc(Patrolman.NORTH_CORRIDOR.getId());
                            Rs2NpcModel northEastPatrol = Rs2Npc.getNpc(Patrolman.NORTH_EAST_BRIDGE.getId());
                            int northCorridorOrientation = northCorridorPatrol.getCurrentOrientation();
                            int northEastOrientation = northEastPatrol.getOrientation();
                            if ((Orientation.WEST.getAngle() == northCorridorOrientation || Orientation.SOUTH.getAngle() == northCorridorOrientation)
                                    && (Orientation.EAST.getAngle() == northEastOrientation || Orientation.SOUTH.getAngle() == northEastOrientation)) {
                                Rs2Walker.walkFastCanvas(NORTH_EAST_BRIDGE_SAFE_SPOT);
                                sleepUntilFulfillCondition(() -> NORTH_EAST_BRIDGE_SAFE_SPOT.equals(Rs2Player.getWorldLocation()), () -> Rs2Random.wait(800, 1200));
                                Rs2Walker.walkFastCanvas(NORTH_EAST_OUTSIDE_POINT);
                            }
                        }

                        // South house to North-south safe spot
                        if (Task.SOUTH.getBaseFloor().getArea().contains(playerLocation)) {
                            Rs2NpcModel southCorridorPatrol = Rs2Npc.getNpc(Patrolman.SOUTH_EAST_CORRIDOR.getId());
                            int orientation = southCorridorPatrol.getCurrentOrientation();
                            if (Orientation.SOUTH.getAngle() == orientation || Orientation.EAST.getAngle() == orientation) {
                                Rs2Walker.walkFastCanvas(NORTH_SOUTH_BRIDGE_SAFE_SPOT);
                                sleepUntilFulfillCondition(() -> NORTH_SOUTH_BRIDGE_SAFE_SPOT.equals(Rs2Player.getWorldLocation()), () -> Rs2Random.wait(800, 1200));
                            }
                        }

                        // South East to safe spot, then outside point
                        if (Task.SOUTH_EAST.getBaseFloor().getArea().contains(playerLocation)) {
                            Rs2NpcModel southCorridorPatrol = Rs2Npc.getNpc(Patrolman.SOUTH_EAST_CORRIDOR.getId());
                            Rs2NpcModel southBridgePatrol = Rs2Npc.getNpc(Patrolman.SOUTH_EAST_BRIDGE.getId());
                            int southCorridorOrientation = southCorridorPatrol.getCurrentOrientation();
                            boolean southCorridorConditional = Orientation.NORTH.getAngle() == southCorridorOrientation
                                    || Orientation.WEST.getAngle() == southCorridorOrientation;

                            int southBridgeOrientation = southBridgePatrol.getCurrentOrientation();
                            boolean southBridgeConditional = Orientation.NORTH.getAngle() == southBridgeOrientation
                                    || Orientation.EAST.getAngle() ==  southBridgeOrientation || Orientation.WEST.getAngle() == southBridgeOrientation;

                            if (southCorridorConditional && southBridgeConditional) {
                                Rs2Walker.walkFastCanvas(SOUTH_EAST_SAFE_SPOT);
                                sleepUntilFulfillCondition(() -> SOUTH_EAST_SAFE_SPOT.equals(Rs2Player.getWorldLocation()), () -> Rs2Random.wait(800, 1200));
                                Rs2Walker.walkFastCanvas(SOUTH_EAST_OUTSIDE_POINT);
                            }
                        }
                        break;
                    case DELIVER_ARTEFACT:
                        if (currentLocation != PlayerLocation.CAPTAIN_LOCATION) return;

                        Rs2Npc.interact(CAPTAIN_KHALED_ID, "Talk");
                        Rs2Random.wait(2000, 3000);
                        Rs2Keyboard.keyPress(32); // press enter
                        break;
                    case BANKING:
                        if (playerState.getPlayerLocation() != PlayerLocation.BANK_LOCATION) return;
                        if (!Rs2Bank.isOpen()) Rs2Bank.openBank();
                        Rs2Bank.depositAll();

                        if (!Rs2Inventory.hasItem(ItemID.LOCKPICK)) {
                            if (Rs2Bank.hasItem(ItemID.LOCKPICK)) {
                                Rs2Bank.withdrawOne(ItemID.LOCKPICK);
                            } else {
                                Microbot.showMessage("Lock pick not available in bank, shutting down...");
                                shutdown();
                            }
                        }

                        int teleportId = config.getTeleport().getId();
                        if (!Rs2Equipment.isWearing(teleportId)) {
                            if (!Rs2Inventory.hasItem(teleportId)) {
                                if (Rs2Bank.hasItem(teleportId)) {
                                    Rs2Bank.withdrawOne(teleportId);
                                    Rs2Random.wait(1000, 1200);
                                    Rs2Inventory.wear(teleportId);
                                } else {
                                    Microbot.showMessage(config.getTeleport().getName() + " not available in bank, shutting down...");
                                    shutdown();
                                }
                            }
                        }

                        Rs2Bank.withdrawX(ItemID.STAMINA_POTION4, 25);
                        Rs2Bank.closeBank();
                        break;
                }
            } catch (Exception ex) {
                log.info(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private void useStaminaPotions() {
        if (!Rs2Player.hasStaminaBuffActive() && Rs2Player.getRunEnergy() < Rs2Random.between(63, 72)) {
            Rs2Inventory.interact("Stamina Potion", "Drink");
            if (Rs2Inventory.hasItem(ItemID.VIAL)) Rs2Inventory.dropAll(ItemID.VIAL);
        }
    }

    private boolean checkIfInDesiredLocation() {
        return playerState.getPlayerLocation() == currentLocation;
    }

    private void walkToDesiredLocation() {
        if (!Rs2Antiban.isIdle() || Rs2Player.isWalking(true)) return;
        useStaminaPotions();

        if (playerState == PlayerState.DELIVER_ARTEFACT && currentLocation == PlayerLocation.OUTSIDE_POINT) {
            walkToCaptainLocation();
            return;
        }
        if (task == Task.CAUGHT) Rs2Random.wait(4000, 4200);

        switch (playerState) {
            case GET_TASK:
                walkToCaptainLocation();
                break;
            case STEAL_ARTEFACT:
                walkToRequiredHouse();
                break;
            case BANKING:
                walkToSelectedBank();
                break;
        }
    }

    private void walkToCaptainLocation() {
        WorldPoint point = PlayerLocation.CAPTAIN_LOCATION.getPoint();
        Rs2Walker.walkTo(point, 3);
        sleepUntilFulfillCondition(() -> PlayerLocation.CAPTAIN_LOCATION.getArea().contains(Rs2Player.getWorldLocation()), () -> Rs2Random.wait(800, 1200));
    }

    private void walkToRequiredHouse() {
        WorldPoint point = task.getBaseFloor().getPoint();
        Rs2Walker.walkTo(point, 2);
        sleepUntilFulfillCondition(() -> task.getBaseFloor().getArea().contains(Rs2Player.getWorldLocation()), () -> Rs2Random.wait(800, 1200));
    }

    private void walkToSelectedBank() {
        WorldPoint point = PlayerLocation.BANK_LOCATION.getPoint();
        Rs2Walker.walkTo(point, 3);
        sleepUntilFulfillCondition(() -> PlayerLocation.BANK_LOCATION.getArea().contains(Rs2Player.getWorldLocation()), () -> Rs2Random.wait(800, 1200));
    }

    private void getPlayerState(AutoArtefactStealingConfig config) {
        if (isReadyToBank(config)) {
            playerState = PlayerState.BANKING;
            return;
        }

        if (needsToGetTask()) {
            playerState = PlayerState.GET_TASK;
            return;
        }

        if (isReadyToStealArtefact()) {
            playerState = PlayerState.STEAL_ARTEFACT;
            return;
        }

        if (hasToLureGuards()) {
            playerState = PlayerState.LURE_GUARDS;
            return;
        }

        if (hasToSneakGuards()) {
            playerState = PlayerState.SNEAK_GUARDS;
            return;
        }

        if (isReadyToDeliverArtefact()) {
            playerState = PlayerState.DELIVER_ARTEFACT;
            return;
        }

        playerState = PlayerState.IDLE;
    }

    private void getTask() {
        Task currentTask = Task.findTaskByVarbitValue(Microbot.getVarbitValue(STEALING_ARTEFACTS_TASK_VARBIT));
        if (currentTask.equals(Task.RETRIEVED) && !Artefact.hasArtefactInInventory()) task = Task.CAUGHT;
        else task = currentTask;
    }

    private boolean needsToGetTask() {
        return task == Task.NO_TASK || task == Task.CAUGHT;
    }

    private boolean isReadyToStealArtefact() {
        if (task == null) return false;
        return task.getDrawers() != null;
    }

    private boolean hasToLureGuards() {
        if (!task.equals(Task.RETRIEVED)) return false;

        return false;
    }

    private boolean hasToSneakGuards() {
        return task.equals(Task.RETRIEVED) && currentLocation.equals(PlayerLocation.HOUSE_ZONE);
    }

    private boolean isReadyToDeliverArtefact() {
        return task.equals(Task.RETRIEVED) && !currentLocation.equals(PlayerLocation.HOUSE_ZONE);
    }

    private boolean isReadyToBank(AutoArtefactStealingConfig config) {
        if (config.useTeleport()) {
            int teleportId = config.getTeleport().getId();
            if (!Rs2Equipment.isWearing(teleportId)) return !Rs2Inventory.hasItem(teleportId);
        }
        if (!Rs2Inventory.hasItem(ItemID.LOCKPICK)) return true;

        return !Rs2Inventory.hasItem("Stamina Potion", false);
    }

    @Override
    public void shutdown(){
        super.shutdown();
        Rs2Antiban.resetAntibanSettings();
    }
}
