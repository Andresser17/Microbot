package net.runelite.client.plugins.microbot.mining;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.mining.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Auto Mining",
        description = "Mines and banks ores",
        tags = {"mining", "microbot", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class AutoMiningPlugin extends Plugin {
    @Inject
    public AutoMiningConfig config;
    @Provides
    AutoMiningConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoMiningConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoMiningOverlay autoMiningOverlay;

    @Inject
    AutoMiningScript autoMiningScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(autoMiningOverlay);
        }

        PlayerLocation.MINING_FIELD.setWorldPoint(Rs2Player.getWorldLocation(), config.area());

        if (config.useNearestBank()) {
            PlayerLocation.BANK_LOCATION.setWorldPoint(Rs2Bank.getNearestBank().getWorldPoint(), 10);
        } else {
            PlayerLocation.BANK_LOCATION.setWorldPoint(config.bankLocation().getWorldPoint(), 10);
        }

        autoMiningScript.run(config);
    }

    protected void shutDown() {
        autoMiningScript.shutdown();
        overlayManager.remove(autoMiningOverlay);
    }
}
