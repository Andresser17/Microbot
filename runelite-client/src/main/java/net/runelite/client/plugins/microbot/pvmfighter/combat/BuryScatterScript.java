package net.runelite.client.plugins.microbot.pvmfighter.combat;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2ItemModel;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;

import java.util.List;
import java.util.concurrent.TimeUnit;
public class BuryScatterScript extends Script {
public boolean run(PvmFighterConfig config) {
    mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
        try {
            if (!Microbot.isLoggedIn() || !super.run() || (!config.lootBones() && !config.lootAshes())) return;

            processItems(config.lootBones(), Rs2Inventory.getBones(), "bury");
            processItems(config.lootAshes(), Rs2Inventory.getAshes(), "scatter");

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }, 0, 600, TimeUnit.MILLISECONDS);
    return true;
}

private void processItems(boolean toggle, List<Rs2ItemModel> items, String action) {
    if (!toggle || items == null || items.isEmpty()) return;
    Rs2Inventory.interact(items.get(0), action);
    Rs2Player.waitForAnimation();
}

    public void shutdown() {
        super.shutdown();
    }
}
