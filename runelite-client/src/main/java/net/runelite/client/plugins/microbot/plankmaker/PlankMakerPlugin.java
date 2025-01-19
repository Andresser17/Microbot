package net.runelite.client.plugins.microbot.plankmaker;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.plankmaker.enums.Location;
import net.runelite.client.plugins.microbot.plankmaker.enums.PlayerState;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "Plank Maker",
        description = "Plank making automation plugin",
        tags = {"processing", "plank", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class PlankMakerPlugin extends Plugin {
    public static double version = 1.0;
    @Inject
    PlankMakerScript plankMakerScript;
    @Inject
    private PlankMakerConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PlankMakerOverlay overlay;

    @Provides
    PlankMakerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PlankMakerConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
//        Microbot.setMouse(new VirtualMouse());
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }

        // set sawmill and bank locations to use
        Location sawmillLocation = config.sawmillLocation().getTeleport().getLocation();
        Location bankLocation = config.sawmillLocation().getBankTeleport().getLocation();

        PlayerState.PROCESSING.setLocation(sawmillLocation);
        PlayerState.BANKING.setLocation(bankLocation);

        plankMakerScript.run(config);
    }

    protected void shutDown() {
        plankMakerScript.shutdown();
        overlayManager.remove(overlay);
    }
}
