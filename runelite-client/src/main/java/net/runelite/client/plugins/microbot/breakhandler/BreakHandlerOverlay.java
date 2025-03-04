package net.runelite.client.plugins.microbot.breakhandler;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class BreakHandlerOverlay extends OverlayPanel {
    private final BreakHandlerConfig config;

    @Inject
    BreakHandlerOverlay(BreakHandlerPlugin plugin, BreakHandlerConfig config)
    {
        super(plugin);
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("BreakHandler V" + BreakHandlerScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Completed breaks: " + BreakHandlerScript.completedBreaks)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Completed sessions: " + BreakHandlerScript.completedSessions)
                    .build());

            if (BreakHandlerScript.scriptState == ScriptState.SESSION) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(BreakHandlerScript.formatDuration(BreakHandlerScript.sessionDuration, "Break in:"))
                        .build());
            } else {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(BreakHandlerScript.formatDuration(BreakHandlerScript.breakDuration, "Break duration:"))
                        .build());
            }

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
