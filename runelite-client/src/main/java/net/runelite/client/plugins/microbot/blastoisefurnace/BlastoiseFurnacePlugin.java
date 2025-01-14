package net.runelite.client.plugins.microbot.blastoisefurnace;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;



@PluginDescriptor(
        name = "Blastoise Furnace",
        description = "Blast furnace automation plugin",
        tags = {"testing", "microbot", "smithing", "bar", "ore", "blast", "furnace"},
        enabledByDefault = false
)
@Slf4j
public class BlastoiseFurnacePlugin extends Plugin {
    @Inject
    private BlastoiseFurnaceConfig config;
    @Provides
    BlastoiseFurnaceConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BlastoiseFurnaceConfig.class);
    }
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BlastoiseFurnaceOverlay BlastoiseFurnaceOverlay;
    @Inject
    BlastoiseFurnaceScript BlastoiseFurnaceScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(BlastoiseFurnaceOverlay);
        }

        BlastoiseFurnaceScript.run(config);
    }

    @Subscribe
    public void onStatChanged(StatChanged event) {
        log.info("Stat changed: {}", event.getSkill());
        if (event.getSkill() == Skill.SMITHING) {
            net.runelite.client.plugins.microbot.blastoisefurnace.BlastoiseFurnaceScript.waitingXpDrop = false;
        }
//        if(event.getXp()> BlastoiseFurnaceScript.previousXP){
//            if(BlastoiseFurnaceScript.waitingXpDrop) {
//                //BlastoiseFurnaceScript.waitingXpDrop = false;
//            }
//        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarbitId() == Varbits.STAMINA_EFFECT) {
//            BlastoiseFurnaceScript.staminaTimer = event.getValue();
            if (event.getValue() > 30) Rs2Player.toggleRunEnergy(true);
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
//        if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE) {
//            if(chatMessage.getMessage().contains("The coal bag is now empty.")){
//                BlastoiseFurnaceScript.coalBagIsFull = true;
//            }
//
//            if(chatMessage.getMessage().contains("The coal bag contains")){
//                BlastoiseFurnaceScript.coalBagIsFull = false;
//            }
//        }
    }

    protected void shutDown() {
        BlastoiseFurnaceScript.shutdown();
        overlayManager.remove(BlastoiseFurnaceOverlay);
    }
}
