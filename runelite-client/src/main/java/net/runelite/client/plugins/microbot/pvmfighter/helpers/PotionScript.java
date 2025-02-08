package net.runelite.client.plugins.microbot.pvmfighter.helpers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.HelperScript;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.pvmfighter.enums.Potion;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PotionScript extends Script {
    private final List<Rs2Item> potionsToDrink = new ArrayList<>();

    public void run(PvmFighterConfig config) {
        try {
            if (HelperScript.helperState != PlayerState.POTION || PvmFighterScript.currentLocation != PlayerState.POTION.getPlayerLocation()) return;

            sleepUntilFulfillCondition(() -> {
                usePotion();
                return potionsToDrink.isEmpty();
            }, () -> Rs2Random.wait(1000, 1200));
        } catch(Exception ex) {
            log.info(ex.getMessage());
        }
    }

    public boolean needsToDrinkPotion(PvmFighterConfig config) {
        if (!config.usePotion()) return false;

        List<Rs2Item> potions = Rs2Inventory.getPotions();
        if (potions == null || potions.isEmpty()) return false;

        for (Rs2Item potionItem: potions) {
            Potion potion = Potion.findPotionById(potionItem.getId());

            if (potion != null) {
                if (potion == Potion.PRAYER) {
                    // check prayer remaining.
                    int prayerLevel = Rs2Player.getRealSkillLevel(Skill.PRAYER);
                    int currentPrayer = Rs2Player.getBoostedSkillLevel(Skill.PRAYER);
                    int prayerPercentage = (currentPrayer / prayerLevel) * 100;
                    if (prayerLevel >= potion.getRestorePoints() && prayerPercentage >= 30) potionsToDrink.add(potionItem);
                }

                if (potion == Potion.ANTIPOISON) {
                    int antiPoisonTimer = potion.getTimer();

                    // player is poisoned
                    if (antiPoisonTimer > 0) potionsToDrink.add(potionItem);
                }

                if (potion == Potion.STRENGTH) {
                    // check if strength skill is boosted
                    int strengthLevel = Rs2Player.getRealSkillLevel(Skill.STRENGTH);
                    int boostedLevel = Rs2Player.getBoostedSkillLevel(Skill.STRENGTH);

                    if (boostedLevel == strengthLevel) potionsToDrink.add(potionItem);
                }
            }
        }

        return !potionsToDrink.isEmpty();
    }

    private void usePotion() {
        Rs2Item potionItem = potionsToDrink.get(0);
        potionsToDrink.remove(0);
        Rs2Inventory.interact(potionItem, "Drink");
        sleep(1000, 1200);
        if (Rs2Inventory.hasItem("Vial")) Rs2Inventory.dropAll("Vial");
    }

    @Override
    public void shutdown() {
        potionsToDrink.clear();
    }
}
