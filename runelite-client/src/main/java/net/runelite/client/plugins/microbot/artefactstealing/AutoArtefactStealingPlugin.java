package net.runelite.client.plugins.microbot.artefactstealing;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.artefactstealing.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "Auto Artefact Stealing",
        description = "Stealing artefacts script",
        tags = {"microbot", "skilling", "thieving", "minigame"},
        enabledByDefault = false
)
@Slf4j
public class AutoArtefactStealingPlugin extends Plugin {
    @Inject
    public AutoArtefactStealingConfig config;
    @Provides
    AutoArtefactStealingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoArtefactStealingConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoArtefactStealingOverlay autoArtefactStealingOverlay;

    @Inject
    AutoArtefactStealingScript autoArtefactStealingScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(autoArtefactStealingOverlay);
        }

        autoArtefactStealingScript.run(config);
    }

    protected void shutDown() {
        autoArtefactStealingScript.shutdown();
        overlayManager.remove(autoArtefactStealingOverlay);
    }
}
