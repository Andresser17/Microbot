package net.runelite.client.plugins.microbot.cooking.scripts;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.AnimationID;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.cooking.AutoCookingConfig;
import net.runelite.client.plugins.microbot.cooking.enums.CookingItem;
import net.runelite.client.plugins.microbot.cooking.enums.CookingLocation;
import net.runelite.client.plugins.microbot.cooking.enums.PlayerState;
import net.runelite.client.plugins.microbot.cooking.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntilTrue;

@Slf4j
public class AutoCookingScript extends Script {

    private PlayerState playerState;
    private PlayerLocation currentLocation;

    public boolean run(AutoCookingConfig config) {
        Microbot.enableAutoRunOn = false;
        CookingItem cookingItem = config.cookingItem();
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyCookingSetup();
        Rs2Antiban.setActivity(Activity.GENERAL_COOKING);

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.fulfillConditionsToRun()) return;

//                if (init) {
//                    if (config.useNearestCookingLocation()) {
//                        currentLocation = CookingLocation.findNearestCookingLocation(cookingItem);
//                    } else {
//                        currentLocation = config.cookingLocation();
//                        if (cookingItem.getCookingAreaType() != CookingAreaType.BOTH) {
//                            if (currentLocation.getCookingAreaType() != cookingItem.getCookingAreaType()) {
//                                Microbot.showMessage("Cooking Area does not match item's cooking area");
//                                shutdown();
//                                return;
//                            }
//                        }
//                    }
//
//                    getPlayerState(config, currentLocation);
//                }

                getPlayerState(config);
                currentLocation = PlayerLocation.checkCurrentPlayerLocation();
                if (!checkIfInDesiredLocation()) walkToDesiredLocation();

                Microbot.status = String.format("PlayerState: %s - %s", playerState, currentLocation);
                switch (playerState) {
                    case COOKING:
                        if (currentLocation != PlayerLocation.COOKING_AREA) return;
                        if (!cookingItem.hasRequirements()) {
                            Microbot.showMessage("You do not meet the requirements to cook this item");
                            shutdown();
                            return;
                        }

                        TileObject cookingObject = Rs2GameObject.findObjectById(currentLocation.getCookingLocation().getCookingObjectID());

                        if (cookingObject != null) {
                            if (!Rs2Camera.isTileOnScreen(cookingObject.getLocalLocation())) {
                                Rs2Camera.turnTo(cookingObject.getLocalLocation());
                                return;
                            }
                            Rs2Inventory.useItemOnObject(cookingItem.getRawItemID(), cookingObject.getId());
                            sleepUntil(() -> !Rs2Player.isMoving() && Rs2Widget.findWidget("How many would you like to cook?", null, false) != null);

                            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                            
                            Rs2Antiban.actionCooldown();
                            Rs2Antiban.takeMicroBreakByChance();

                            sleepUntil(() -> (Rs2Player.getAnimation() != AnimationID.IDLE));
                            sleepUntilTrue(() -> (!hasRawFoodInInventory(cookingItem) && !Rs2Player.isAnimating(3500))
                                    || Rs2Dialogue.isInDialogue() || Rs2Player.isWalking(), 500, 150000);
                            if (hasRawFoodInInventory(cookingItem)) {
                                break;
                            }
                            if (hasBurntItem(cookingItem) && !cookingItem.getBurntItemName().isEmpty()) {
                                return;
                            }
                            break;
                        }
                    case DROPPING:
//                        Microbot.status = "Dropping " + cookingItem.getBurntItemName();
                        Rs2Inventory.dropAll(item -> item.name.equalsIgnoreCase(cookingItem.getBurntItemName()), config.getDropOrder());
                        sleepUntilTrue(() -> !hasBurntItem(cookingItem), 500, 150000);
                        break;
                    case BANKING:
                        if (currentLocation != PlayerLocation.BANK_LOCATION) return;

                        if (PlayerLocation.COOKING_AREA.getCookingLocation()  == CookingLocation.ROUGES_DEN) {
                            NPC npc = Rs2Npc.getBankerNPC();
                            boolean isNPCBankOpen = Rs2Bank.openBank(npc);
                            if (!isNPCBankOpen) return;
                        } else {
                            boolean isBankOpen = Rs2Bank.walkToBankAndUseBank();
                            if (!isBankOpen || !Rs2Bank.isOpen()) return;
                        }

                        Rs2Bank.depositAll(cookingItem.getCookedItemName(), true);
                        Rs2Random.wait(800, 1600);

                        // deposit burnt food
                        if (config.bankBurntFood() && hasBurntItem(config.cookingItem())) {
                            Rs2Bank.depositAll(config.cookingItem().getBurntItemID());
                        }
                        
                        if (!hasRawFoodInBank(cookingItem)) {
                            Microbot.showMessage("No Raw Food Item found in Bank");
                            shutdown();
                            return;
                        }
                        Rs2Bank.withdrawAll(cookingItem.getRawItemName(), true);
                        Rs2Random.wait(800, 1600);
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
    public void shutdown(){
        super.shutdown();
        Rs2Antiban.resetAntibanSettings();
    }

    private boolean checkIfInDesiredLocation() {
        return playerState.getPlayerLocation()  == currentLocation;
    }

    private void walkToDesiredLocation() {
        if (!Rs2Antiban.isIdle() || Rs2Player.isWalking()) return;

        switch (playerState) {
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
        if (hasRawFoodInInventory(config.cookingItem())) {
//            if (hasBurntItem(config.cookingItem())) {
//                playerState = CookingState.DROPPING;
//                return;
//            }

            playerState = PlayerState.COOKING;
            return;
        }

        if (hasBurntItem(config.cookingItem()) && !config.bankBurntFood()) {
            playerState = PlayerState.DROPPING;
            return;
        }

        if (needsBanking(config.cookingItem())) {
            playerState = PlayerState.BANKING;
            return;
        }

//        if (!isNearCookingLocation(location, 4)) {
//            playerState = CookingState.WALKING;
//            return;
//        }

        playerState = PlayerState.IDLE;
    }

    private boolean needsBanking(CookingItem cookingItem) {
//        if (Rs2Bank.isOpen()) {
//            return Rs2Bank.hasBankItem(cookingItem.getRawItemName(), true);
//        }
        // check if has cooked food to deposit or not raw food
        return Rs2Inventory.hasItem(cookingItem.getCookedItemName(), true)
                || !hasRawFoodInInventory(cookingItem);
    }

    private boolean isNearCookingLocation(CookingLocation location, int distance) {
        return Rs2Player.getWorldLocation().distanceTo(location.getCookingObjectWorldPoint()) <= distance && !Rs2Player.isMoving();
    }

    private boolean hasRawFoodInBank(CookingItem cookingItem) {
        return Rs2Bank.hasItem(cookingItem.getRawItemID());
    }

    private boolean hasRawFoodInInventory(CookingItem cookingItem) {
        return Rs2Inventory.hasItem(cookingItem.getRawItemID());
    }

    private boolean hasBurntItem(CookingItem cookingItem) {
        return Rs2Inventory.hasItem(cookingItem.getBurntItemName(), true);
    }
}
