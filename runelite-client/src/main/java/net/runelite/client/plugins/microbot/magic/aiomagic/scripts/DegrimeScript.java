package net.runelite.client.plugins.microbot.magic.aiomagic.scripts;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.magic.aiomagic.AIOMagicPlugin;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.*;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DegrimeScript extends Script {

    private MagicState playerState;
    private final AIOMagicPlugin plugin;
    private EarthStaff staff;
    private Herb[] herbsToClean;
    private BankLocation nearestBank;
    private boolean init = true;

    @Inject
    public DegrimeScript(AIOMagicPlugin plugin) {
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
        Rs2Antiban.setActivity(Activity.CASTING_PLANK_MAKE);

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!fulfillConditionsToRun()) return;
                if (init) {
                    nearestBank = Rs2Bank.getNearestBank();

                    if (plugin.isToggleCleanAllHerbs()) {
                        String[] herbsToExclude = Arrays.stream(plugin.getExcludedHerbsToClean().split(",")).map(String::trim).toArray(String[]::new);
                        herbsToClean = Herb.getAllHerbsExcept(herbsToExclude);
                    } else {
                        String[] herbsToCleanName = Arrays.stream(plugin.getHerbsToClean().split(",")).map(String::trim).toArray(String[]::new);
                        herbsToClean = Herb.getGrimyHerbByCleanName(herbsToCleanName);
                    }

                    staff = plugin.getDegrimeStaff();

                    init = false;
                }

                long startTime = System.currentTimeMillis();

                getPlayerState(herbsToClean);

                switch (playerState) {
                    case CASTING:
                        if (!Rs2Magic.isArceeus()) {
                            Microbot.showMessage("No Arceeus Spellbook available");
                        }
                        Rs2Magic.cast(MagicAction.DEGRIME);
                        sleepUntilTick(7);
                        break;
                    case BANKING:
                        if (!Rs2Bank.isNearBank(nearestBank, 5)) {
                            Rs2Walker.walkTo(nearestBank.getWorldPoint(), 3);
                        }
                        if (!Rs2Bank.isOpen()) Rs2Bank.openBank();

                        if (!Rs2Equipment.hasEquipped(staff.getId())) {
                            if (Rs2Inventory.hasItem(staff.getId())) Rs2Inventory.equip(staff.getId());
                            if (Rs2Bank.hasItem(staff.getId())) Rs2Bank.getBankItem(staff.getId());
                            else {
                                Microbot.showMessage("No staff available");
                                shutdown();
                            }
                        }

                        if (!Rs2Inventory.hasItem(ItemID.NATURE_RUNE)) {
                            if (Rs2Bank.hasItem(ItemID.NATURE_RUNE)) {
                                Rs2Bank.withdrawAll(ItemID.NATURE_RUNE);
                            } else {
                                Microbot.showMessage("No more Nature Runes available");
                                shutdown();
                            }
                        }

                        Integer[] cleanHerbsIds = Arrays.stream(herbsToClean).map(Herb::getCleanId).toArray(Integer[]::new);
                        if (Rs2Inventory.hasItem(cleanHerbsIds)) {
                            Rs2Bank.depositAllExcept(ItemID.NATURE_RUNE);
                            Rs2Random.wait(800, 1600);
                        }

                        int[] grimyIds = Arrays.stream(herbsToClean).mapToInt(Herb::getGrimyId).toArray();
                        if (Rs2Bank.hasAllItems(grimyIds)) {
                            Arrays.stream(grimyIds).sequential().forEach(id -> {
                                Rs2Bank.withdrawAll(id);
                                Rs2Random.wait(1000, 1200);
                            });
                        } else {
                            Microbot.showMessage("No grimy herbs found in Bank");
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
        init = true;
        super.shutdown();
    }

    private void getPlayerState(Herb[] herbsToClean) {
        if (hasNecessaryItemsInInventory(herbsToClean)) {
            playerState = MagicState.CASTING;
            return;
        }

        playerState = MagicState.BANKING;
    }

    private boolean hasNecessaryItemsInInventory(Herb[] herbsToClean) {
        boolean hasNatureRunes = Rs2Inventory.hasItem(ItemID.NATURE_RUNE);
        boolean hasHerbsToClean = Rs2Inventory.hasItem(Arrays.stream(herbsToClean).map(Herb::getGrimyName).toArray(String[]::new));
        return hasNatureRunes && hasHerbsToClean;
    }
}
