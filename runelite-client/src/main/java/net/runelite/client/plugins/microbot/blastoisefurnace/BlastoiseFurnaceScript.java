

package net.runelite.client.plugins.microbot.blastoisefurnace;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.plugins.microbot.breakhandler.BreakHandlerScript;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.blastoisefurnace.enums.Bars;
import net.runelite.client.plugins.microbot.blastoisefurnace.enums.PlayerState;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.ActivityIntensity;
import net.runelite.client.plugins.microbot.util.antiban.enums.PlayStyle;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

@Slf4j
public class BlastoiseFurnaceScript extends Script {
    private static final int BAR_DISPENSER_ID = 9092;
    private static final int MAX_ORE_PER_TRIP = 28;
    private static final int MAX_ORE_PER_TRIP_WITH_CB = 27;
    private static final int BLAST_FURNACE_HOUR_RATE = 72000;
    private static final int BLAST_FURNACE_FOREMAN_FEE = 2500;
    private static final int BLAST_FURNACE_COAL_VARBIT = Varbits.BLAST_FURNACE_COAL;
    private static final int BLAST_FURNACE_COFFER_VARBIT = Varbits.BLAST_FURNACE_COFFER;
    private static final int BLAST_FURNACE_FOREMAN_ID = 2923;
    private static final WorldArea BLAST_FURNACE_OUTSIDE_AREA = new WorldArea(0, 0, 0, 0, 0);
    public static double version = 1.0;
    public static boolean waitingXpDrop = false;
    private BlastoiseFurnaceConfig config;
    public static PlayerState playerState;
    public static boolean coalBagIsFull = false;
    public static boolean hasBarsToCollect = false;
    public static boolean needsToPayFee = false;
    public static int oreInInventoryCurrentTrip = -1;

    public boolean run(BlastoiseFurnaceConfig config) {
        this.config = config;

        Microbot.enableAutoRunOn = false;
        Rs2Camera.resetZoom();
//        Rs2Camera.turnTo(new LocalPoint(1943, 4967, 0)); // turn camera to conveyor belt
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applySmithingSetup();
        Rs2AntibanSettings.simulateMistakes = true;
        Rs2AntibanSettings.moveMouseOffScreen = true;
        Rs2Antiban.setActivity(Activity.SMELTING_STEEL_BARS_AT_BLAST_FURNACE);
        Rs2Antiban.setActivityIntensity(ActivityIntensity.MODERATE);
        Rs2Antiban.setPlayStyle(PlayStyle.MODERATE);

        this.mainScheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!fulfillConditionsToRun() || Rs2AntibanSettings.actionCooldownActive) return;
                getPlayerState(config);

                log.info("PlayerState: {}", playerState);
                switch (playerState) {
                    case BANKING:
                        Microbot.status = "Banking";

                        if (!Rs2Bank.isOpen()) {
                            Rs2Bank.openBank();
                            sleepUntilFulfillCondition(Rs2Bank::isOpen, () -> Rs2Random.wait(1000, 1200)); // wait until player reached bank chest
                        }

                        if (Rs2Inventory.hasItem("bar")) {
                            Rs2Bank.depositAll("bar");
                            Rs2Random.wait(800, 1200);
                        }

                        if (!Rs2Inventory.hasItem("Coal bag")) Rs2Bank.withdrawOne("Coal bag");

                        if (!hasRequiredOresInBank(config)) {
                            Microbot.showMessage("Not required ores for smithing");
                            this.shutdown();
                        }

                        if (config.useStamina()) useRunEnergyPotions();

                        if (!isReadyToCollectBars()) withdrawNecessaryOres(config);
                        Rs2Random.wait(800, 1000);
                        Rs2Bank.closeBank();
                        break;
                    case PAY_FEE:
                        if (Rs2Inventory.hasItemAmount(ItemID.COINS_995, BLAST_FURNACE_FOREMAN_FEE)) {
                            Rs2Npc.interact(BLAST_FURNACE_FOREMAN_ID, "Pay");
                            Rs2Random.wait(1000, 1600);
                            boolean payFeeWidget = Rs2Widget.hasWidget("Pay 2,500 coins to use the Blast Furnace?");
                            if (payFeeWidget) {
                                Rs2Keyboard.typeString("1");
                                Rs2Random.wait(800, 1200);
                                Rs2Keyboard.keyPress(32);
                                needsToPayFee = false;
                            }
                        } else {
                            if (!Rs2Bank.isOpen()) Rs2Bank.openBank();
                            Rs2Bank.depositAllExcept(ItemID.COAL_BAG_12019);
                            Rs2Bank.withdrawX(ItemID.COINS_995, BLAST_FURNACE_HOUR_RATE);
                            Rs2Bank.closeBank();
                        }
                        break;
                    case REFILL_COFFER:
                        // check if player has gold in inventory
                        if (Rs2Inventory.hasItemAmount(ItemID.COINS_995, BLAST_FURNACE_HOUR_RATE)) {
                            GameObject coffer = Rs2GameObject.getGameObject(new WorldPoint(1946, 4957, 0));
                            if (coffer != null) {
                                Rs2GameObject.interact(coffer, "Use");
                                Rs2Random.wait(1000, 1600);
                                boolean fillCofferWidget = Rs2Widget.hasWidget("Select an option");
                                if (fillCofferWidget) {
                                    Rs2Keyboard.typeString("1");
                                    Rs2Random.wait(800, 1200);
                                    Rs2Keyboard.typeString(String.format("%d", BLAST_FURNACE_HOUR_RATE));
                                    Rs2Keyboard.enter();
                                }
                            }
                        } else {
                            if (!Rs2Bank.isOpen()) Rs2Bank.openBank();
                            Rs2Bank.depositAllExcept(ItemID.COAL_BAG_12019);
                            Rs2Bank.withdrawX(ItemID.COINS_995, BLAST_FURNACE_HOUR_RATE);
                            Rs2Bank.closeBank();
                        }
                        break;
                    case DEPOSIT_ORES:
                        Rs2GameObject.interact(ObjectID.CONVEYOR_BELT, "Put-ore-on");
                        Rs2Random.wait(800, 1200);
                        sleepUntilFulfillCondition(() -> {
                            if (needsToRefillCoffer() || needsToPayFee()) return true;

                            return !Rs2Inventory.hasItem(oreInInventoryCurrentTrip);
                        }, () -> Rs2Random.wait(800, 1200));

                        if (this.config.getBars().isRequiresCoalBag() && (!needsToRefillCoffer() || !needsToPayFee())) {
                            Rs2Inventory.interact(ItemID.COAL_BAG_12019, "Empty");
                            coalBagIsFull = false;
                            Rs2Random.wait(1200, 1500);

                            Rs2GameObject.interact(ObjectID.CONVEYOR_BELT, "Put-ore-on");
                        }
                        break;
                    case COLLECT_BARS:
                        // walk next to dispenser
//                        Rs2Walker.walkTo(new WorldPoint(1939, 4964, 0), 1);
//                        Rs2Random.wait(800, 1200);
//                        sleepUntil(() -> !Rs2Player.isMoving());
                        Rs2GameObject.interact(BAR_DISPENSER_ID, "Take");
                        sleepUntil(() -> !Rs2Player.isWalking(true));
                        Rs2Random.wait(800, 1200);
                        boolean howManyWidget = Rs2Widget.hasWidget("How many would you like");
                        boolean whatTakeWidget = Rs2Widget.hasWidget("What would you like to take");
                        if (howManyWidget || whatTakeWidget) {
                            Rs2Keyboard.keyPress(32);
                            Rs2Random.wait(1400, 1600);
                        }
                        break;
                    case BREAK:
                        if (!BLAST_FURNACE_OUTSIDE_AREA.contains(Rs2Player.getWorldLocation())) {
                            Rs2Walker.walkTo(BLAST_FURNACE_OUTSIDE_AREA.toWorldPoint());
                            sleepUntilFulfillCondition(() -> Rs2Player.isWalking(true), () -> Rs2Random.wait(800, 1200));
                        }
                        break;
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }

        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private void getPlayerState(BlastoiseFurnaceConfig config) {
        if (needsToTakeBreak()) {
            playerState = PlayerState.BREAK;
            return;
        }

        if (config.hasToPayFee() && (needsToPayFee() || needsToPayFee)) {
            playerState = PlayerState.PAY_FEE;
            return;
        }

        if (needsToRefillCoffer()) {
            playerState = PlayerState.REFILL_COFFER;
            return;
        }

        if (isReadyToCollectBars()) {
            playerState = PlayerState.COLLECT_BARS;
            return;
        }

        if (isReadyToDepositOres(config)) {
            playerState = PlayerState.DEPOSIT_ORES;
            waitingXpDrop = true;
            return;
        }

        if (isReadyToBank(config)) {
            playerState = PlayerState.BANKING;
            return;
        }

        playerState = PlayerState.IDLE;
    }

    private boolean needsToTakeBreak() {
        if (!BreakHandlerScript.scriptIsTurnedOn) return false;
        return BreakHandlerScript.isBreakActive() || BreakHandlerScript.sessionTime < 60;
    }

    private boolean needsToPayFee() {
        boolean permissionDenied = Rs2Widget.hasWidget("You must ask the foreman's permission before using the blast<br>furnace.");
        if (permissionDenied && (Rs2Player.getBoostedSkillLevel(Skill.SMITHING) < 60)) {
            needsToPayFee = true;
            return true;
        }

        return false;
    }

    private boolean needsToRefillCoffer() {
        return Microbot.getVarbitValue(BLAST_FURNACE_COFFER_VARBIT) == 0;
    }

    private boolean isReadyToBank(BlastoiseFurnaceConfig config) {
        return Rs2Inventory.contains(config.getBars().getId()) || !isReadyToDepositOres(config);
    }

    private boolean isReadyToDepositOres(BlastoiseFurnaceConfig config) {
        return Rs2Inventory.hasItemAmount("ore", MAX_ORE_PER_TRIP_WITH_CB) || Rs2Inventory.hasItemAmount("coal", MAX_ORE_PER_TRIP_WITH_CB);
    }

    private boolean isReadyToCollectBars() {
        return Bars.stream().anyMatch(bar -> Microbot.getVarbitValue(bar.getBarVarbit()) > 0) && !Rs2Inventory.isFull();
    }

    private boolean hasRequiredOresInBank(BlastoiseFurnaceConfig config) {
        Bars bars = config.getBars();
        if (bars.isRequiresCoalBag()) return Rs2Bank.hasItem(bars.getOreId()) && Rs2Bank.hasItem(ItemID.COAL);
        return Rs2Bank.hasItem(bars.getOreId());
    }

    private void withdrawNecessaryOres(BlastoiseFurnaceConfig config) {
        int coalInBlastFurnace = Microbot.getVarbitValue(BLAST_FURNACE_COAL_VARBIT);
        log.info("coal: {}, {}", coalInBlastFurnace, ItemID.COAL);

        Bars bars = config.getBars();
        if (bars.isRequiresCoalBag()) {
            if (!coalBagIsFull) {
                Rs2Inventory.interact(ItemID.COAL_BAG_12019, "Fill");
                coalBagIsFull = true;
                Rs2Random.wait(1000, 1200);
            }

            if (coalInBlastFurnace >= (MAX_ORE_PER_TRIP_WITH_CB * bars.getCoalQuantity())) {
                Rs2Bank.withdrawAll(bars.getOreId());
                oreInInventoryCurrentTrip = bars.getOreId();
            } else {
                Rs2Bank.withdrawAll(ItemID.COAL);
                oreInInventoryCurrentTrip = ItemID.COAL;
            }
        }
    }

    private void useRunEnergyPotions() {
        // Drink energy potions and stamina potions until energy is above 71%, else only stamina potion
        if (Rs2Player.getRunEnergy() < Rs2Random.between(62, 69)) {
            drinkEnergyPotion();
            Rs2Random.wait(1000, 1200);
            drinkStaminaPotion();
        } else if (Rs2Player.getRunEnergy() < Rs2Random.between(75, 81) && !Rs2Player.hasStaminaBuffActive()) {
            drinkStaminaPotion();
        }
    }

    private void drinkEnergyPotion() {
        String potionName = "Energy potion";
        if (Rs2Bank.hasItem(potionName)) {
            Rs2Bank.withdrawOne(potionName);
            sleepUntilFulfillCondition(() -> {
                if (Rs2Player.getRunEnergy() >= 69) return true;
                Rs2Inventory.interact(potionName, "Drink");
                return false;
            }, () -> Rs2Random.wait(1000, 1200));
            if (Rs2Inventory.hasItem(ItemID.VIAL)) Rs2Bank.depositAll(ItemID.VIAL);
            if (Rs2Inventory.hasItem(potionName)) Rs2Bank.depositAll(potionName);
        }
    }

    private void drinkStaminaPotion() {
        String potionName = "Stamina potion";
        if (Rs2Bank.hasItem(potionName)) {
            Rs2Bank.withdrawOne(potionName);
            sleepUntilFulfillCondition(() -> {
                if (Rs2Player.hasStaminaBuffActive()) return true;
                Rs2Inventory.interact(potionName, "Drink");
                return false;
            }, () -> Rs2Random.wait(1000, 1200));
            if (Rs2Inventory.hasItem(ItemID.VIAL)) Rs2Bank.depositAll(ItemID.VIAL);
            if (Rs2Inventory.hasItem(potionName)) Rs2Bank.depositAll(potionName);
        }
    }

    public void shutdown() {
        coalBagIsFull = false;
        hasBarsToCollect = false;
        oreInInventoryCurrentTrip = -1;
        super.shutdown();
    }
}
