package net.runelite.client.plugins.microbot.magic.aiomagic.scripts;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.magic.aiomagic.AIOMagicPlugin;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.EnchantSpell;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.Leather;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.MagicState;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class TanLeatherScript extends Script {

    private MagicState playerState;
    private final AIOMagicPlugin plugin;
    private Leather leatherToTan;
    private BankLocation bankLocation;


    @Inject
    public TanLeatherScript(AIOMagicPlugin plugin) {
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
        Rs2Antiban.setActivity(Activity.CASTING_TAN_LEATHER);

        if (bankLocation == null) {
            bankLocation = Rs2Bank.getNearestBank();
        }

        if (!Rs2Magic.isLunar()) {
            Microbot.showMessage("Player is not lunar spell book available.");
            shutdown();
            return false;
        }

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!fulfillConditionsToRun()) return;
                leatherToTan = plugin.getLeatherToTan();

                long startTime = System.currentTimeMillis();

                getPlayerState();

                switch (playerState) {
                    case CASTING:
                        while (Rs2Inventory.hasItem(leatherToTan.getHideId())) {
                            if (!isRunning()) break;
                            Rs2Magic.cast(MagicAction.TAN_LEATHER);
                            Microbot.log(String.format("Enchanting %s", leatherToTan.name()));
                            Rs2Random.wait(1000, 1200);
                        }
                        break;
                    case BANKING:
                        Rs2Bank.openBank();
                        if (!Rs2Bank.isOpen()) return;

                        if (Rs2Inventory.hasItem(leatherToTan.getLeatherId())) {
                            Rs2Bank.depositAll(leatherToTan.getLeatherId());
                            Rs2Random.wait(800, 1600);
                        }

                        if (Rs2Bank.hasItem(leatherToTan.getHideId())) {
                            Rs2Bank.withdrawX(leatherToTan.getHideId(), 25);
                            Rs2Random.wait(800, 1600);
                        } else {
                            Microbot.showMessage(String.format("No more %s available in bank", leatherToTan.name()));
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

    private void getPlayerState() {
        if (hasNecessaryItemsInInventory()) {
            playerState = MagicState.CASTING;
            return;
        }

        playerState = MagicState.BANKING;
    }

    private boolean hasNecessaryItemsInInventory() {
        if (!hasNecessaryRunes()) return false;

        return Rs2Inventory.hasItemAmount(leatherToTan.getHideId(), 5, false);
    }

    private boolean hasNecessaryRunes() {
        return Rs2Inventory.hasItem(ItemID.NATURE_RUNE) && Rs2Inventory.hasItemAmount(ItemID.ASTRAL_RUNE, 2, false);
    }
}
