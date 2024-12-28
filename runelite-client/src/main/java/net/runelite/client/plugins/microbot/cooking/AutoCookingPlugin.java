package net.runelite.client.plugins.microbot.cooking;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.AnimationID;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.cooking.enums.CookingLocation;
import net.runelite.client.plugins.microbot.cooking.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.cooking.scripts.AutoCombiningScript;
import net.runelite.client.plugins.microbot.cooking.scripts.AutoCookingScript;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.GMason + "Auto Cooking",
        description = "Microbot cooking plugin",
        tags = {"cooking", "microbot", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class AutoCookingPlugin extends Plugin {
    public static double version = 1.0;
    @Inject
    AutoCookingScript autoCookingScript;
    @Inject
    AutoCombiningScript autoCombiningScript;
    @Inject
    private AutoCookingConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoCookingOverlay overlay;
    private boolean isRunning;

    @Provides
    AutoCookingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoCookingConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        if (isRunning) {
            Microbot.showMessage("Plugin is already running");
            return;
        }

        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setMouse(new VirtualMouse());
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }

        if (config.useNearestBankLocation()) {
            BankLocation location = Rs2Bank.getNearestBank();
            PlayerLocation.BANK_LOCATION.setWorldPoint(location.getWorldPoint(), 10);
        } else {
            BankLocation location = config.bankLocation();
            PlayerLocation.COOKING_AREA.setWorldPoint(location.getWorldPoint(), 10);
        }

        switch (config.cookingActivity()) {
            case COOKING:
                if (config.useNearestCookingLocation()) {
                    CookingLocation location = CookingLocation.findNearestCookingLocation(config.cookingItem());
                    PlayerLocation.COOKING_AREA.setWorldPoint(location.getCookingObjectWorldPoint(), 10);
                    PlayerLocation.COOKING_AREA.setCookingLocation(location);
                } else {
                    CookingLocation location = config.cookingLocation();
                    PlayerLocation.COOKING_AREA.setWorldPoint(location.getCookingObjectWorldPoint(), 10);
                    PlayerLocation.COOKING_AREA.setCookingLocation(location);
                }
                autoCookingScript.run(config);
                break;
            case COMBINING:
                autoCombiningScript.run(config);
                break;
            case COMBINE_COOK:
            default:
                Microbot.log("Invalid Cooking Activity");
        }

        isRunning = true;
    }

    protected void shutDown() {
        autoCookingScript.shutdown();
        autoCombiningScript.shutdown();
        overlayManager.remove(overlay);
        isRunning = false;
    }
}
