package net.runelite.client.plugins.microbot.pvmfighter;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.pvmfighter.helpers.FoodScript;
import net.runelite.client.plugins.microbot.pvmfighter.helpers.PotionScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.pvmfighter.helpers.skill.AttackStyleScript;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
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
    private final PotionScript potionScript = new PotionScript();
    private final AttackStyleScript attackStyleScript = new AttackStyleScript();

    public boolean run(PvmFighterConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!fulfillConditionsToRun() || Rs2AntibanSettings.actionCooldownActive) return;
                // check if player is in desired location
                getPlayerState(config);

//                log.info("currentStyle: {}", AttackStyleScript.currentAttackStyle);
//                int value = Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE);
//                log.info("value: {}", value);
//                Microbot.log(String.format("HelperState: %s", helperState));
                switch (helperState) {
                    case EATING:
                        foodScript.run(config);
                        break;
                    case POTION:
                        potionScript.run(config);
                        break;
                    case SAFEKEEPING:
                        walkToSafeSpot();
                        break;
                    case ATTACK_STYLE:
                        attackStyleScript.run(config);
                        break;
                }
            } catch (Exception ex) {
                log.info(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        return true;
    }

    private void walkToSafeSpot() {
        WorldPoint point = PlayerLocation.SAFE_SPOT.getPoint();
        Rs2Walker.walkTo(point, 0);
        sleepUntilFulfillCondition(() -> point.equals(Rs2Player.getWorldLocation()), () -> Rs2Random.wait(800, 1200));
    }

    @Override
    public void shutdown() {
        foodScript.shutdown();
        potionScript.shutdown();
        attackStyleScript.shutdown();
        super.shutdown();
    }

    private void getPlayerState(PvmFighterConfig config) {
        if (needsToEat(config)) {
            helperState = PlayerState.EATING;
            return;
        }

        if (potionScript.needsToDrinkPotion(config)) {
            helperState = PlayerState.POTION;
            return;
        }

        if (attackStyleScript.needsToChangeAttackStyle(config)) {
            helperState = PlayerState.ATTACK_STYLE;
            return;
        }

        if (needsToReturnToSafeSpot(config)) {
            helperState = PlayerState.SAFEKEEPING;
            return;
        }

        helperState = PlayerState.IDLE;
    }

    private boolean needsToEat(PvmFighterConfig config) {
        if (!config.useFood()) return false;

        // get food that heal value is less than lost health
        Optional<Rs2ItemModel> foodToEat = Rs2Inventory.getInventoryFood().stream().filter(food -> {
            Rs2Food foodValue = Rs2Food.getFoodById(food.id);
            if (foodValue == null) return false;

            return foodValue.getHeal() <= (Rs2Player.getMaxHealth() - Rs2Player.checkCurrentHealth() - Rs2Random.between(2, 8));
        }).findFirst();

        return foodToEat.isPresent() && !Rs2Player.isFullHealth();
    }

    private boolean needsToReturnToSafeSpot(PvmFighterConfig config) {
        if (!config.useSafeSpot() || PvmFighterScript.playerState != PlayerState.ATTACKING) return false;

        if (PlayerLocation.SAFE_SPOT.getPoint() != null) {
            return !Rs2Player.getWorldLocation().equals(PlayerLocation.SAFE_SPOT.getPoint());
        }

        return false;
    }
}
