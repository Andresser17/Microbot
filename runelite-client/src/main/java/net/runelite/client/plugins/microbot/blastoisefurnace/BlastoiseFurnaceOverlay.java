package net.runelite.client.plugins.microbot.blastoisefurnace;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class BlastoiseFurnaceOverlay extends OverlayPanel {
    @Inject
    BlastoiseFurnaceOverlay(BlastoiseFurnacePlugin plugin) {
        super(plugin);
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.setNaughty();
    }

    public Dimension render(Graphics2D graphics) {
        try {
            this.panelComponent.setPreferredSize(new Dimension(200, 300));
            this.panelComponent.getChildren().add(TitleComponent.builder().text(String.format("Blast Furnace V%s", BlastoiseFurnaceScript.version)).color(Color.GREEN).build());
            panelComponent.getChildren()
                    .add(LineComponent.builder()
                    .left(String.format("Player state: %s", BlastoiseFurnaceScript.playerState))
                    .build());
            this.panelComponent.getChildren().add(LineComponent.builder().left(Microbot.status).build());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return super.render(graphics);
    }
}