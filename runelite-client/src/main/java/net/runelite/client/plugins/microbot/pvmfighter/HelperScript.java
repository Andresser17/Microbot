package net.runelite.client.plugins.microbot.pvmfighter;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.bank.BankerScript;
import net.runelite.client.plugins.microbot.pvmfighter.combat.AttackNpcScript;
import net.runelite.client.plugins.microbot.pvmfighter.combat.FoodScript;
import net.runelite.client.plugins.microbot.pvmfighter.combat.SafeSpot;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.pvmfighter.loot.LootScript;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * In this class should be call all the scripts that exist has helpers to the primary scripts in PvmFighterScript
 * ex: Food and prayer scripts are helper scripts to combat script
 * this script should run in a separate thread in parallel to the primary script thread
 */
@Slf4j
public class HelperScript extends Script {
    public static PlayerState helperState;
    private final FoodScript foodScript = new FoodScript();

    public boolean run(PvmFighterConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!fulfillConditionsToRun() || Rs2AntibanSettings.actionCooldownActive) return;
                // check if player is in desired location
                getPlayerState(config);

                Microbot.log(String.format("HelperState: %s", helperState));
                switch (helperState) {
                    case EATING:
                        foodScript.run(config);
                        break;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        return true;
    }

    @Override
    public void shutdown() {
        foodScript.shutdown();
        super.shutdown();
    }

    private void getPlayerState(PvmFighterConfig config) {
        if (needToEat(config)) {
            helperState = PlayerState.EATING;
            return;
        }

        helperState = PlayerState.IDLE;
    }

    private boolean needToEat(PvmFighterConfig config) {
        if (!config.toggleFood()) return false;

        // get food that heal value is less than lost health
        Optional<Rs2Item> foodToEat = Rs2Inventory.getInventoryFood().stream().filter(food -> {
            Rs2Food foodValue = Rs2Food.getFoodById(food.id);
            if (foodValue == null) return false;

            return foodValue.getHeal() <= (Rs2Player.getMaxHealth() - Rs2Player.checkCurrentHealth());
        }).findFirst();

        return foodToEat.isPresent() && !Rs2Player.isFullHealth();
    }
}
