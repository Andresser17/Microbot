package net.runelite.client.plugins.microbot.pvmfighter;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.combat.FoodScript;
import net.runelite.client.plugins.microbot.pvmfighter.combat.PotionScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.pvmfighter.skill.AttackStyleScript;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

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

//                Microbot.log(String.format("HelperState: %s", helperState));
                switch (helperState) {
                    case EATING:
                        foodScript.run(config);
                        break;
                    case POTION:
                        potionScript.run(config);
                        break;
                    case MELEE_STYLE:
                        attackStyleScript.run(config);
                        break;
                }
            } catch (Exception ex) {
                log.info(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        return true;
    }

    @Override
    public void shutdown() {
        foodScript.shutdown();
        potionScript.shutdown();
        super.shutdown();
    }

    private void getPlayerState(PvmFighterConfig config) {
        if (needsToEat(config)) {
            helperState = PlayerState.EATING;
            return;
        }

        if (needsToChangeAttackStyle(config)) {
            helperState = PlayerState.MELEE_STYLE;
            return;
        }

        helperState = PlayerState.IDLE;
    }

    private boolean needsToEat(PvmFighterConfig config) {
        if (!config.toggleFood()) return false;

        // get food that heal value is less than lost health
        Optional<Rs2Item> foodToEat = Rs2Inventory.getInventoryFood().stream().filter(food -> {
            Rs2Food foodValue = Rs2Food.getFoodById(food.id);
            if (foodValue == null) return false;

            return foodValue.getHeal() <= (Rs2Player.getMaxHealth() - Rs2Player.checkCurrentHealth());
        }).findFirst();

        return foodToEat.isPresent() && !Rs2Player.isFullHealth();
    }

    private boolean needsToChangeAttackStyle(PvmFighterConfig config) {
        return config.attackSkillTarget() == Rs2Player.getRealSkillLevel(Skill.ATTACK)
                || config.attackSkillTarget() == Rs2Player.getRealSkillLevel(Skill.STRENGTH)
                || config.attackSkillTarget() == Rs2Player.getRealSkillLevel(Skill.DEFENCE);
    }
}
