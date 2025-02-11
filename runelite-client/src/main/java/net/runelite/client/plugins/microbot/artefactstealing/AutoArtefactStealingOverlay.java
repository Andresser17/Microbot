package net.runelite.client.plugins.microbot.artefactstealing;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class AutoArtefactStealingOverlay extends OverlayPanel {
    private static final Color WHITE_TRANSLUCENT = new Color(0, 255, 255, 127);
    private final AutoArtefactStealingPlugin plugin;

    @Inject
    AutoArtefactStealingOverlay(AutoArtefactStealingPlugin plugin)
    {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 500));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Auto Artefact Stealing V" + AutoArtefactStealingScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(String.format("PlayerState: %s", plugin.autoArtefactStealingScript.playerState))
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(String.format("PlayerLocation: %s", plugin.autoArtefactStealingScript.currentLocation))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(String.format("Task: %s", plugin.autoArtefactStealingScript.task))
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());

            // render area
//            LocalPoint lp =  LocalPoint.fromWorld(Microbot.getClient(), Rs2Player.getWorldLocation());
//            if (lp != null) {
//                Polygon poly = Perspective.getCanvasTileAreaPoly(Microbot.getClient(), lp, plugin.config.area());
//
//                if (poly != null)
//                {
//                    renderPolygon(graphics, poly, WHITE_TRANSLUCENT);
//                }
//            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
