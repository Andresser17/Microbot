package net.runelite.client.plugins.microbot.pvmfighter.combat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.HelperScript;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.pvmfighter.enums.Potion;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PotionType;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.List;

@Slf4j
public class PotionScript extends Script {
    public void run(PvmFighterConfig config) {
        try {
            if (HelperScript.helperState != PlayerState.POTION || PvmFighterScript.currentLocation != PlayerState.POTION.getPlayerLocation()) return;

            List<Rs2Item> potions = Rs2Inventory.getPotions();
            if (potions == null || potions.isEmpty()) {
                return;
            }

            for (Rs2Item potionItem: potions) {
                Potion potion = Potion.findPotionByName(potionItem.getName());

                if (potion != null) {
                    switch (potion) {
                        case PRAYER:
                            // check prayer remaining.
                            int prayerLevel = Rs2Player.getRealSkillLevel(Skill.PRAYER);
                            int currentPrayer = Rs2Player.getBoostedSkillLevel(Skill.PRAYER);
                            int prayerPercentage = (currentPrayer / prayerLevel) * 100;
                            if (prayerLevel >= potion.getRestorePoints() && prayerPercentage >= 30) {
                                usePotion(potionItem);
                            }
                            break;
                        case ANTIPOISON:
                            int antiPoisonTimer = potion.getTimer();

                            // player is poisoned
                            if (antiPoisonTimer > 0) {
                                usePotion(potionItem);
                            }
                    }
                }
            }
        } catch(Exception ex) {
            log.info(ex.getMessage());
        }
    }

    private void usePotion(Rs2Item potion) {
        Rs2Inventory.interact(potion, "Drink");
        sleep(1200, 1800);
        Rs2Inventory.dropAll("Vial");
    }
}
