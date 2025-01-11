

package net.runelite.client.plugins.microbot.blastoisefurnace;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
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
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import static net.runelite.api.ItemID.COAL;
import static net.runelite.api.ItemID.GOLD_ORE;

@Slf4j
public class BlastoiseFurnaceScript extends Script {
    private static final int BAR_DISPENSER_ID = 9092;
    private static final int MAX_ORE_PER_INTERACTION = 27;
    private static final int BLAST_FURNACE_HOUR_RATE = 72000;
    private static final int BLAST_FURNACE_FOREMAN_FEE = 2500;
    private static final int BLAST_FURNACE_COAL_VARBIT = Varbits.BLAST_FURNACE_COAL;
    private static final int BLAST_FURNACE_COFFER_VARBIT = Varbits.BLAST_FURNACE_COFFER;
    private static final int BLAST_FURNACE_FOREMAN_ID = 2923;
    public static double version = 1.0;
    public static int staminaTimer;
    public static boolean waitingXpDrop;
    private BlastoiseFurnaceConfig config;
    public static PlayerState playerState;
    public static boolean coalBagIsFull;
    public static boolean hasBarsToCollect;
    public static boolean needsToPayFee = false;

    public boolean run(BlastoiseFurnaceConfig config) {
        staminaTimer = 0;
        this.config = config;
        waitingXpDrop = true;

        Microbot.enableAutoRunOn = false;
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applySmithingSetup();
        Rs2AntibanSettings.simulateMistakes = true;
        Rs2AntibanSettings.moveMouseOffScreen = true;
        Rs2Antiban.setActivity(Activity.SMELTING_STEEL_BARS_AT_BLAST_FURNACE);
        Rs2Antiban.setActivityIntensity(ActivityIntensity.MODERATE);
        Rs2Antiban.setPlayStyle(PlayStyle.MODERATE);

//                                if (config.getBars().isRequiresCoalBag() && !Rs2Inventory.contains(ItemID.COAL_BAG_12019)) {
//                            if (!Rs2Bank.hasItem(ItemID.COAL_BAG_12019)) {
//                                Microbot.showMessage("get a coal bag");
//                                this.shutdown();
//                                return;
//                            }
//
//                            Rs2Bank.withdrawItem(ItemID.COAL_BAG_12019);
//                        }
//
//                        if (config.getBars().isRequiresGoldsmithGloves()) {
//                            hasGauntlets = Rs2Inventory.contains(ItemID.GOLDSMITH_GAUNTLETS) || Rs2Equipment.isWearing(ItemID.GOLDSMITH_GAUNTLETS);
//                            if (!hasGauntlets) {
//                                if (!Rs2Bank.hasItem(ItemID.GOLDSMITH_GAUNTLETS)) {
//                                    Microbot.showMessage("Need goldsmith gauntlets");
//                                    this.shutdown();
//                                    return;
//                                }
//
//                                Rs2Bank.withdrawItem(ItemID.GOLDSMITH_GAUNTLETS);
//                            }
//                        }

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
                            Rs2Random.wait(2800, 3200); // wait until player reached bank chest
                        }

                        if (Rs2Inventory.hasItem("bar")) {
                            Rs2Bank.depositAll("bar");
                            Rs2Random.wait(800, 1200);
                        }

                        if (!hasRequiredOresInBank(config)) {
                            Microbot.showMessage("Not required ores for smithing");
                            this.shutdown();
                        }

                        if (config.useStamina()) useRunEnergyPotions();

                        if (!hasBarsToCollect) withdrawNecessaryOres(config);
                        Rs2Random.wait(800, 1000);
                        Rs2Bank.closeBank();
                        Rs2Random.wait(300, 500);
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
                            if (Rs2Inventory.hasItem(ItemID.COINS_995)) {
                                Rs2Bank.depositAll(ItemID.COINS_995);
                            }
                            config.getBars().getNecessaryOres().forEach(ore -> {
                                if (Rs2Inventory.hasItem(ore[0])) Rs2Bank.depositAll(ore[0]);
                            });

                            Rs2Bank.withdrawX(ItemID.COINS_995, BLAST_FURNACE_FOREMAN_FEE);
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
                            if (Rs2Inventory.hasItem(ItemID.COINS_995)) {
                                Rs2Bank.depositAll(ItemID.COINS_995);
                            }
                            config.getBars().getNecessaryOres().forEach(ore -> {
                               if (Rs2Inventory.hasItem(ore[0])) Rs2Bank.depositAll(ore[0]);
                            });

                            Rs2Bank.withdrawX(ItemID.COINS_995, BLAST_FURNACE_HOUR_RATE);
                            Rs2Bank.closeBank();
                        }
                        break;
                    case DEPOSIT_ORES:
                        depositOresInBlastFurnace();
                        break;
                    case COLLECT_BARS:
                        Rs2GameObject.interact(BAR_DISPENSER_ID, "Take");
                        Rs2Random.wait(3500, 3600);
                        boolean howManyWidget = Rs2Widget.hasWidget("How many would you like");
                        boolean whatTakeWidget = Rs2Widget.hasWidget("What would you like to take");
                        if (howManyWidget || whatTakeWidget) {
                            Rs2Keyboard.keyPress(32);
                            Rs2Random.wait(1400, 1600);
                        }
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }

        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private void getPlayerState(BlastoiseFurnaceConfig config) {

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
            return;
        }

        if (isReadyToBank(config)) {
            playerState = PlayerState.BANKING;
            return;
        }

        playerState = PlayerState.IDLE;
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
        int coalInBlastFurnace = Microbot.getVarbitValue(BLAST_FURNACE_COAL_VARBIT);

        switch (config.getBars()) {
            case STEEL_BAR:
                return this.config.getBars().getNecessaryOres().stream().allMatch(ore -> {
                    if (ore[0] == ItemID.COAL) {
                        // check if player has necessary coal in Blast furnace or coal bag
                        return coalInBlastFurnace >= MAX_ORE_PER_INTERACTION || coalBagIsFull;
                    }

                    return Rs2Inventory.hasItemAmount(ore[0], MAX_ORE_PER_INTERACTION);
                });
            case MITHRIL_BAR:
                return false;
        }

        return false;
    }

    private boolean isReadyToCollectBars() {
        hasBarsToCollect = Bars.stream().anyMatch(bar -> Microbot.getVarbitValue(bar.getBarVarbit()) > 0) && !Rs2Inventory.isFull();
        return hasBarsToCollect;
    }

    private boolean hasRequiredOresInBank(BlastoiseFurnaceConfig config) {
        return config.getBars().getNecessaryOres().stream().allMatch(ore -> Rs2Bank.hasItem(ore[0]));
    }

    private void withdrawNecessaryOres(BlastoiseFurnaceConfig config) {
        config.getBars().getNecessaryOres().forEach(ore -> {
            if (ore[0] == ItemID.COAL) {
                Rs2Inventory.interact(ItemID.COAL_BAG_12019, "Fill");
                coalBagIsFull = true;
            } else Rs2Bank.withdrawX(ore[0], ore[1] * MAX_ORE_PER_INTERACTION);
        });
    }

    private void depositOresInBlastFurnace() {
        Rs2GameObject.interact(ObjectID.CONVEYOR_BELT, "Put-ore-on");
        sleepUntil(() -> !Rs2Inventory.isFull(), 7000); // Wait until the player stops moving

        if (this.config.getBars().isRequiresCoalBag()) {
            Rs2Inventory.interact(ItemID.COAL_BAG_12019, "Empty");
            sleepUntil(() -> Rs2Inventory.isFull(), 1000); // Wait for animation to finish

            Rs2GameObject.interact(ObjectID.CONVEYOR_BELT, "Put-ore-on");
            sleepUntil(() -> !Rs2Inventory.isFull(), 2000); // Wait until the player stops moving
        }

        Rs2Random.wait(1500, 2000);
    }

    private void useRunEnergyPotions() {
        // Drink energy potions and stamina potions until energy is above 71%, else only stamina potion
        if (Rs2Player.getRunEnergy() < 69) {
            drinkEnergyPotion();
            Rs2Random.wait(1000, 1200);
            drinkStaminaPotion();
        } else if (Rs2Player.getRunEnergy() < 81 && !Rs2Player.hasStaminaBuffActive()) {
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
            Rs2Inventory.interact(potionName, "Drink");
            if (Rs2Inventory.hasItem(ItemID.VIAL)) Rs2Bank.depositAll(ItemID.VIAL);
            if (Rs2Inventory.hasItem(potionName)) Rs2Bank.depositAll(potionName);
        }
    }

//    private void withdrawNecessaryOres() {
//        switch (config.getBars()) {
//            case GOLD_BAR:
//                handleGold();
//                break;
//            case STEEL_BAR:
//                handleSteel();
//                break;
//            case MITHRIL_BAR:
//                handleMithril();
//                break;
//            case ADAMANTITE_BAR:
//                handleAdamantite();
//                break;
//            case RUNITE_BAR:
//                handleRunite();
//        }
//
//    }

    private void handleGold() {
        int coalInFurnace = Microbot.getVarbitValue(Varbits.BLAST_FURNACE_GOLD_ORE);
        switch (coalInFurnace / MAX_ORE_PER_INTERACTION) {
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
            case 0:
                retrieveGold();
                break;
            default:
                assert false : "how did you get there";

        }
    }

    private void handleSteel() {
        depositOresInBlastFurnace();
//        int coalInFurnace = Microbot.getVarbitValue(Varbits.BLAST_FURNACE_COAL);
//
//        switch (coalInFurnace / MAX_ORE_PER_INTERACTION) {
//            case 8:
//                retrievePrimary();
//                break;
//            case 7:
//            case 6:
//
//            case 5:
//            case 4:
//            case 3:
//            case 2:
//
//            case 1:
//                withdrawNecessaryOres();
//                break;
//            case 0:
//                retrieveDoubleCoal();
//                break;
//            default:
//                assert false : "how did you get there";
//        }
    }

    private void retrievePrimary() {
        int primaryOre = config.getBars().getNecessaryOres().toArray(new int[][]{})[0][0];
        if (!Rs2Inventory.hasItem(primaryOre)) {
            Rs2Bank.withdrawAll(primaryOre);
            return;
        }
        depositOresInBlastFurnace();
        Rs2Walker.walkFastCanvas(new WorldPoint(1940, 4962, 0));
        sleep(3400);
        sleepUntil(() -> {
            return barsInDispenser(this.config.getBars()) > 0;
        }, 300000);
    }

    private void retrieveDoubleCoal() {
        if (!Rs2Inventory.hasItem(COAL)) {
            Rs2Bank.withdrawAll(COAL);
            return;
        }
        boolean fullCoalBag = Rs2Inventory.interact(ItemID.COAL_BAG_12019, "Fill");
        if (!fullCoalBag)
            return;
        depositOresInBlastFurnace();

    }

    private void retrieveGold() {
        if (!Rs2Inventory.hasItem(GOLD_ORE)) {
            Rs2Bank.withdrawAll(GOLD_ORE);
            return;
        }
        depositOresInBlastFurnace();

        Rs2Walker.walkFastCanvas(new WorldPoint(1940, 4962, 0));

        sleep(3400);
        sleepUntil(() -> {
            return barsInDispenser(config.getBars()) > 5;
        }, 300000);
        Rs2Inventory.interact(ItemID.ICE_GLOVES, "wear");
    }

//    private void handleMithril() {
//        int coalInFurnace = Microbot.getVarbitValue(Varbits.BLAST_FURNACE_COAL);
//        switch (coalInFurnace / MAX_ORE_PER_INTERACTION) {
//            case 8:
//                retrievePrimary();
//                break;
//            case 7:
//            case 6:
//
//            case 5:
//            case 4:
//            case 3:
//            case 2:
//                withdrawNecessaryOres();
//                break;
//            case 1:
//                withdrawNecessaryOres();
//                break;
//            case 0:
//                retrieveDoubleCoal();
//                break;
//            default:
//                assert false : "how did you get there";
//
//        }
//
//    }

//    private void handleAdamantite() {
//        int coalInFurnace = Microbot.getVarbitValue(Varbits.BLAST_FURNACE_COAL);
//        switch (coalInFurnace / MAX_ORE_PER_INTERACTION) {
//            case 8:
//                retrievePrimary();
//                break;
//            case 7:
//            case 6:
//
//            case 5:
//            case 4:
//            case 3:
//                withdrawNecessaryOres();
//            break;
//            case 2:
//                retrieveDoubleCoal();
//                break;
//            case 1:
//                retrieveDoubleCoal();
//                break;
//            case 0:
//                retrieveDoubleCoal();
//                break;
//            default:
//                assert false : "how did you get there";
//        }
//
//    }

//    private void handleRunite() {
//        int coalInFurnace = Microbot.getVarbitValue(Varbits.BLAST_FURNACE_COAL);
//        switch (coalInFurnace / MAX_ORE_PER_INTERACTION) {
//            case 8:
//                retrievePrimary();
//                break;
//            case 7:
//            case 6:
//
//            case 5:
//            case 4:
//                withdrawNecessaryOres();
//                break;
//            case 3:
//                withdrawNecessaryOres();
//                break;
//
//            case 2:
//                retrieveDoubleCoal();
//                break;
//            case 1:
//                retrieveDoubleCoal();
//                break;
//            case 0:
//                retrieveDoubleCoal();
//                break;
//                default:
//                assert false : "how did you get there";
//        }
//
//    }

    public int barsInDispenser(Bars bar) {
        switch (bar) {
            case GOLD_BAR:
                return Microbot.getVarbitValue(Varbits.BLAST_FURNACE_GOLD_BAR);
            case STEEL_BAR:
                return Microbot.getVarbitValue(Varbits.BLAST_FURNACE_STEEL_BAR);
            case MITHRIL_BAR:
                return Microbot.getVarbitValue(Varbits.BLAST_FURNACE_MITHRIL_BAR);
            case ADAMANTITE_BAR:
                return Microbot.getVarbitValue(Varbits.BLAST_FURNACE_ADAMANTITE_BAR);
            case RUNITE_BAR:
                return Microbot.getVarbitValue(Varbits.BLAST_FURNACE_RUNITE_BAR);
            default:
                return -1;
        }
    }

    public void shutdown() {
        if (mainScheduledFuture != null && !mainScheduledFuture.isDone()) {
            mainScheduledFuture.cancel(true);
            ShortestPathPlugin.exit();
            if (Microbot.getClientThread().scheduledFuture != null)
                Microbot.getClientThread().scheduledFuture.cancel(true);
            initialPlayerLocation = null;
            Microbot.pauseAllScripts = false;
            Microbot.getSpecialAttackConfigs().reset();
        }

        super.shutdown();
    }
}
