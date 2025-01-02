package net.runelite.client.plugins.microbot.cooking.scripts;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.cooking.AutoCookingConfig;
import net.runelite.client.plugins.microbot.cooking.enums.*;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AutoCombiningScript extends Script {

    private PlayerState playerState;

    public boolean run(AutoCookingConfig config) {
        Microbot.enableAutoRunOn = false;
        CombiningItem combiningItem = config.itemToCombine();
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyCookingSetup();
        Rs2Antiban.setActivity(Activity.MAKING_PINEAPPLE_PIZZAS);

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.fulfillConditionsToRun()) return;

                getPlayerState(config);

                Microbot.log(String.format("PlayerState: %s", playerState));
                switch (playerState) {
                    case COMBINING:
                        // combine items
                        int[][] combiningOrder = combiningItem.getCombiningOrder();
                        for (int[] ingredients : combiningOrder) {
                            Rs2Item ingredient1 = Rs2Inventory.get(ingredients[0]);
                            Rs2Item ingredient2 = Rs2Inventory.get(ingredients[1]);
                            if (ingredient1 != null && ingredient2 != null) {
                                Rs2Inventory.combine(ingredient1, ingredient2);
                                Rs2Random.wait(800, 1200);
                                boolean combineWidget = Rs2Widget.hasWidget("How many do you wish to make?");
                                if (combineWidget) {
                                    Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                                    log.info("Combining {} and {}", ingredient1.name, ingredient2.name);

                                }
                                Rs2Inventory.whileInventoryIsChanging(() -> {
                                    log.info("Combining ingredients");
                                });

                                log.info("Sleep finished");
                            }
                        }
                        break;
                    case BANKING:
                        boolean isBankOpen = Rs2Bank.walkToBankAndUseBank();
                        if (!isBankOpen || !Rs2Bank.isOpen()) return;

                        Rs2Bank.depositAll();
                        Rs2Random.wait(800, 1600);

                        if (hasNecessaryIngredientsInBank(combiningItem)) {
                            Arrays.stream(combiningItem.getIngredients()).forEach((ingredient) -> Rs2Bank.withdrawX(ingredient[0], ingredient[1]));
                            Rs2Random.wait(800, 1600);
                        } else {
                            Microbot.showMessage("No ingredients found in Bank");
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

    private void validateInventory() {
        List<Rs2Item> items = Rs2Inventory.all();

    }

    private void getPlayerState(AutoCookingConfig config) {
        if (hasNecessaryIngredientsInInventory(config.itemToCombine())) {
            playerState = PlayerState.COMBINING;
            return;
        }

        playerState = PlayerState.BANKING;
    }

    private boolean hasNecessaryIngredientsInInventory(CombiningItem combiningItem) {
        int[][] ingredients = combiningItem.getIngredients();
        return Arrays.stream(ingredients).allMatch((ingredient) -> Rs2Inventory.hasItem(ingredient[0]));
    }

    private boolean hasNecessaryIngredientsInBank(CombiningItem combiningItem) {
        if (!Rs2Bank.isOpen()) return false;

        int[][] ingredients = combiningItem.getIngredients();
        return Arrays.stream(ingredients).allMatch((ingredient) -> Rs2Bank.hasItem(ingredient[0]));
    }

    private boolean hasCombinedItem(CombiningItem combiningItem) {
        return Rs2Inventory.hasItem(combiningItem.getCombinedItemId());
    }
}
