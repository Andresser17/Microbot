package net.runelite.client.plugins.microbot.cooking.scripts;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.AnimationID;
import net.runelite.api.GameObject;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.cooking.AutoCookingConfig;
import net.runelite.client.plugins.microbot.cooking.enums.*;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntilTrue;

@Slf4j
public class AutoHeatingScript extends Script {
    private PlayerState playerState;
    private PlayerLocation currentLocation;

    public boolean run(AutoCookingConfig config) {
        Microbot.enableAutoRunOn = false;
        Rs2Antiban.resetAntibanSettings();
        Rs2AntibanSettings.simulateMistakes = true;
        Rs2Antiban.antibanSetupTemplates.applyCookingSetup();
        Rs2Antiban.setActivity(Activity.FILLING_WATER_CONTAINERS);

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.fulfillConditionsToRun()) return;

                getPlayerState(config);
                currentLocation = PlayerLocation.checkCurrentPlayerLocation();
                if (!checkIfInDesiredLocation()) walkToDesiredLocation();

                Microbot.log(String.format("PlayerState: %s, PlayerLocation: %s", playerState, currentLocation));
                switch (playerState) {
                    case FILLING:
                        if (currentLocation != PlayerLocation.COOKING_AREA) return;

                        Rs2Item emptyRecipient = Rs2Inventory.get(config.recipientToFill().getName());
                        if (emptyRecipient != null) {
                            // find water source
                            HeatingLocation heatingLocation = config.heatingLocation();
                            TileObject waterSource = Rs2GameObject.findObjectById(heatingLocation.getWaterSource());
                            if (waterSource != null) {
                                Rs2Inventory.use(emptyRecipient);
                                Rs2GameObject.interact(waterSource);

                                sleepUntilFulfillCondition(() -> !Rs2Inventory.hasItem(emptyRecipient.getId()), () -> Rs2Random.wait(800, 1200));
                            }
                        }
                        break;
                    case COOKING:
                        if (currentLocation != PlayerLocation.COOKING_AREA) return;

                        Rs2Item filledRecipient = Rs2Inventory.get(config.recipientToFill().getFilledName());
                        TileObject cookingTileObject = Rs2GameObject.findObjectById(PlayerLocation.COOKING_AREA.getCookingLocation().getCookingObjectID());
                        if (filledRecipient != null && cookingTileObject != null) {
                            GameObject cookingGameObject = Rs2GameObject.getGameObject(cookingTileObject.getWorldLocation());
                            if (!Rs2GameObject.isReachable(cookingGameObject)) return;
                            if (!Rs2Camera.isTileOnScreen(cookingTileObject.getLocalLocation())) {
                                Rs2Camera.turnTo(cookingTileObject.getLocalLocation());
                                return;
                            }
                            Rs2Inventory.use(filledRecipient);
                            Rs2GameObject.interact(cookingTileObject);
                            Rs2Random.wait(1200, 1600);

                            boolean howManyWidget = Rs2Widget.hasWidget("How many would you like to cook?");
                            if (howManyWidget) {
                                Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);

                                Rs2Antiban.actionCooldown();
                                Rs2Antiban.takeMicroBreakByChance();

                                sleepUntilFulfillCondition(() -> !hasFilledRecipientsInInventory(config.recipientToFill()), () -> Rs2Random.wait(800, 1200));
                            }
                            break;
                        }
                        break;
                    case BANKING:
                        if (currentLocation != PlayerLocation.BANK_LOCATION) return;

                        if (!Rs2Bank.isOpen()) Rs2Bank.openBank();

                        Rs2Bank.depositAll();
                        Rs2Random.wait(800, 1200);

                        if (hasEmptyRecipientsInBank(config.recipientToFill())) {
                            Rs2Bank.withdrawAll(config.recipientToFill().getName());
                            Rs2Random.wait(800, 1200);
                        } else {
                            Microbot.showMessage("No more recipients found in Bank");
                            shutdown();
                            return;
                        }
                        Rs2Bank.closeBank();
                        break;
                }
            } catch (Exception ex) {
                Microbot.log(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
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
        if (!Rs2Antiban.isIdle() || Rs2Player.isWalking()) return;

        switch (playerState) {
            case FILLING:
            case COOKING:
                walkToCookingArea();
                break;
            case BANKING:
                walkToSelectedBank();
                break;
        }
    }

    private void walkToCookingArea() {
        WorldPoint point = PlayerLocation.COOKING_AREA.getPoint();
        Rs2Walker.walkTo(point, 3);
        sleepUntil(() -> PlayerLocation.COOKING_AREA.getArea().contains(Rs2Player.getWorldLocation()));
    }

    private void walkToSelectedBank() {
        WorldPoint point = PlayerLocation.BANK_LOCATION.getPoint();
        Rs2Walker.walkTo(point, 8);
        sleepUntil(() -> point.equals(Rs2Player.getWorldLocation()));
    }

    private void getPlayerState(AutoCookingConfig config) {
        if (hasEmptyRecipientsInInventory(config.recipientToFill())) {
            playerState = PlayerState.FILLING;
            return;
        }

        if (hasFilledRecipientsInInventory(config.recipientToFill())) {
            playerState = PlayerState.COOKING;
            return;
        }

        playerState = PlayerState.BANKING;
    }

    private boolean hasEmptyRecipientsInInventory(Recipient recipient) {
        return Rs2Inventory.hasItem(recipient.getName());
    }

    private boolean hasFilledRecipientsInInventory(Recipient recipient) {
        return Rs2Inventory.hasItem(recipient.getFilledName());
    }

    private boolean hasEmptyRecipientsInBank(Recipient recipient) {
        if (!Rs2Bank.isOpen()) return false;

        return Rs2Bank.hasItem(recipient.getName());
    }
}
