package net.runelite.client.plugins.microbot.magic.aiomagic.scripts;

import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.magic.aiomagic.AIOMagicPlugin;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.EnchantSpell;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.Jewellery;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.MagicState;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class EnchantScript extends Script {

    private MagicState playerState;
    private final AIOMagicPlugin plugin;
    private EnchantSpell enchantSpell;
    private Jewellery jewelleryToEnchant;

    @Inject
    public EnchantScript(AIOMagicPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean run() {
        Microbot.enableAutoRunOn = false;
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyGeneralBasicSetup();
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.moveMouseOffScreen = true;
        Rs2Antiban.setActivity(Activity.ENCHANTING_JADE_AMULETS);

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!fulfillConditionsToRun()) return;
                enchantSpell = plugin.getEnchantSpell();
                jewelleryToEnchant = plugin.getJewelleryToEnchant();

                long startTime = System.currentTimeMillis();

                getPlayerState(enchantSpell);

                switch (playerState) {
                    case CASTING:
                        if (Rs2Inventory.hasItem(jewelleryToEnchant.getNormalId())) {
                            Rs2Item jewelleryItem = Rs2Inventory.get(jewelleryToEnchant.getNormalId());
                            Rs2Magic.enchant(enchantSpell, jewelleryItem);
//                            Rs2Player.waitForAnimation(1200);
                            while (Rs2Player.waitForXpDrop(Skill.MAGIC)) {
                                if (!isRunning()) break;
                                Microbot.log(String.format("Enchanting %s", jewelleryToEnchant.name()));
                                Rs2Random.wait(1200, 1500);
                            }
                        }
                        break;
                    case BANKING:
                        boolean isBankOpen = Rs2Bank.walkToBankAndUseBank();
                        if (!isBankOpen || !Rs2Bank.isOpen()) return;

                        if (Rs2Inventory.hasItem(jewelleryToEnchant.getEnchantedId())) {
                            Rs2Bank.depositAll(jewelleryToEnchant.getEnchantedId());
                            Rs2Random.wait(800, 1600);
                        }

                        if (Rs2Bank.hasItem(jewelleryToEnchant.getNormalId())) {
                            Rs2Bank.withdrawAll(jewelleryToEnchant.getNormalId());
                            Rs2Random.wait(800, 1600);
                        } else {
                            Microbot.showMessage("No jewellery found in Bank");
                            shutdown();
                            return;
                        }
                        Rs2Bank.closeBank();
                        break;
                }

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        Rs2Antiban.resetAntibanSettings();
        super.shutdown();
    }

    private void getPlayerState(EnchantSpell enchantSpell) {
        if (hasNecessaryItemsInInventory(enchantSpell)) {
            playerState = MagicState.CASTING;
            return;
        }

        playerState = MagicState.BANKING;
    }

//    private boolean needsToWithdrawRunes() {
//        return false;
//    }
//
//    private hasNecessaryRunesInBank() {
//
//    }

    private boolean hasNecessaryItemsInInventory(EnchantSpell enchantSpell) {
        return EnchantSpell.checkIfPlayerHasNecessaryRunes(enchantSpell) && Rs2Inventory.hasItem(jewelleryToEnchant.getNormalId());
    }
}
