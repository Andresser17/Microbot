package net.runelite.client.plugins.microbot.pvmfighter;


import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.cooking.enums.PlayerLocation;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
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
                    .text("PVM Fighter")
                    .color(Color.GREEN)
                    .build());


            panelComponent.getChildren().add(LineComponent.builder()
                    .left(String.format("Play Style: %s", Rs2Antiban.getPlayStyle()))
                    .right("Attack cooldown: " + PvmFighterPlugin.getCooldown())
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(String.format("Health percentage: %s", Rs2Player.getHealthPercentage()))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .right("Version:" + PvmFighterPlugin.version)
                    .build());


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
