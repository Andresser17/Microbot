package net.runelite.client.plugins.microbot.plankmaker;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class PlankMakerOverlay extends OverlayPanel {
    private final PlankMakerConfig config;

    @Inject
    PlankMakerOverlay(PlankMakerPlugin plugin, PlankMakerConfig config) {
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
                    .text(String.format("Plank Maker v%s", PlankMakerPlugin.version))
                    .color(Color.GREEN)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(String.format("Play style: %s", Rs2Antiban.getPlayStyle()))
                    .right(String.format("Player state: %s, Location: %s", PlankMakerScript.playerState, PlankMakerScript.currentLocation))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
