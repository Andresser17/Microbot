package net.runelite.client.plugins.microbot.artefactstealing;

import net.runelite.client.config.*;
import net.runelite.client.plugins.microbot.artefactstealing.enums.PortTeleport;

@ConfigGroup("AutoArtefactStealing")
@ConfigInformation("<h2>Auto Artefact Stealing</h2>" +
        "<h3>Version: "+ AutoArtefactStealingScript.version + "</h3>")

public interface AutoArtefactStealingConfig extends Config {
    String teleport  = "teleport";
    String useTeleport = "useTeleport";

    @ConfigSection(
            name = "General",
            description = "General",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = useTeleport,
            name = "Use teleport",
            description = "Use the selected teleport to Port Piscarilius",
            position = 0,
            section = generalSection
    )
    default boolean useTeleport()
    {
        return false;
    }

    @ConfigItem(
            keyName = teleport,
            name = "Teleport",
            description = "Teleport to Port Piscarilius",
            position = 1,
            section = generalSection
    )
    default PortTeleport getTeleport()
    {
        return PortTeleport.KHAREDST_MEMOIRS;
    }
}