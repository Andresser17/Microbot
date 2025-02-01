package net.runelite.client.plugins.microbot.pvmfighter;


import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class PvmFighterInfoOverlay extends OverlayPanel {
    private final PvmFighterConfig config;

    @Inject
    PvmFighterInfoOverlay(PvmFighterPlugin plugin, PvmFighterConfig config) {
        super(plugin);
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(250, 400));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text(String.format("PVM Fighter V%s", PvmFighterPlugin.version))
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(String.format("Play style: %s", Rs2Antiban.getPlayStyle()))
                    .right(String.format("Player state: %s, Location: %s", PvmFighterScript.playerState, PvmFighterScript.currentLocation))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(String.format("Helper state: %s", HelperScript.helperState))
                    .right(String.format("Safe Spot: %s", PlayerLocation.SAFE_SPOT.getPoint()))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(String.format("Slayer Task: %s", PvmFighterScript.slayerTask.getName()))
                    .right(String.format("Inventory Setup: %s", PvmFighterScript.setup.getInventorySetupName()))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .right(String.format("Health percentage: %s", Rs2Player.getHealthPercentageInt()))
                    .build());


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
