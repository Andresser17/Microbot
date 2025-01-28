package net.runelite.client.plugins.microbot.pvmfighter.combat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterPlugin;

import java.util.List;
import java.util.Objects;

@Slf4j
public class SafeSpot extends Script {

    public int minimumHealth = 10;
    public WorldPoint currentSafeSpot = null;

    public void run(PvmFighterConfig config) {
        try {
            if (!super.fulfillConditionsToRun() || PvmFighterScript.playerState != PlayerState.SAFEKEEPING) return;

            minimumHealth = config.minimumHealthToRetrieve();
            currentSafeSpot = config.safeSpot();
            if (isDefaultSafeSpot(currentSafeSpot)) {
                Microbot.showMessage("Please set a safe spot location");
                PvmFighterPlugin.shutdownFlag = true;
                return;
            }

            // Player health is less than minimum configured
            if (shouldRetreat() && !isPlayerAtSafeSpot(currentSafeSpot)) {
                PvmFighterScript.playerState = PlayerState.SAFEKEEPING;
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void walkToSafeSpot() {
        Rs2Walker.walkFastCanvas(currentSafeSpot);
        Microbot.pauseAllScripts = true;
        sleepUntil(() -> isPlayerAtSafeSpot(currentSafeSpot));
        Microbot.pauseAllScripts = false;
    }

    private void attackNpcFromSafeSpot(PvmFighterConfig config, List<String> npcsToAttack) {
        // check if there is an NPC targeting us
        List<NPC> npcList = Rs2Npc.getNpcsAttackingPlayer(Microbot.getClient().getLocalPlayer());

        // if there is an NPC interacting with us, and we are not Interacting with it, attack it again
        if (!npcList.isEmpty() && !Rs2Player.isInMulti()) {
            npcList.forEach(npc -> {
                if (Microbot.getClient().getLocalPlayer().getInteracting() == null) {
                    if (npcsToAttack.get(0).contains(Objects.requireNonNull(npc.getName()))) {
                        Rs2Npc.attack(npc);
                        PvmFighterPlugin.setCooldown(config.selectPlayStyle().getRandomTickInterval());
                    }
                }
            });
        }
    }

    private boolean isDefaultSafeSpot(WorldPoint safeSpot) {
        return safeSpot.getX() == 0 && safeSpot.getY() == 0;
    }

    private boolean isPlayerAtSafeSpot(WorldPoint safeSpot) {
        return safeSpot.equals(Microbot.getClient().getLocalPlayer().getWorldLocation());
    }

    private boolean shouldRetreat() {
        int currentHp = Rs2Player.checkCurrentHealth();
        return minimumHealth >= currentHp;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
