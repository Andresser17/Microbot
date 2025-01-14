package net.runelite.client.plugins.microbot.plankmaker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.plankmaker.enums.Plank;
import net.runelite.client.plugins.microbot.plankmaker.enums.SawmillLocation;

@ConfigGroup("plankmaker")
public interface PlankMakerConfig extends Config {
    String sawmillLocation = "sawmillLocation";
    String plankToMake = "plankToMake";

    @ConfigSection(
            name = "General",
            description = "General Settings",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            name = "Guide",
            keyName = "guide",
            position = 0,
            description = "",
            section = generalSection
    )
    default String guide() {
        return "This plugin objective is allow for AFK Dough making in Cooking Guild.\n\n" +
                "1. Player needs to be near Cooking Guild and have a Chef's hat equipped.\n" +
                "2. Have a empty bucket (or bucket of water) and a pot (or pot of flour) in inventory.";
    }

    @ConfigItem(
            keyName = sawmillLocation,
            name = "Sawmill Location",
            position = 0,
            description = "Select which sawmill to use and the necessary teleports",
            section = generalSection
    )
    default SawmillLocation sawmillLocation() {
        return SawmillLocation.LUMBER_YARD;
    }

    @ConfigItem(
            keyName = plankToMake,
            name = "Plank to make",
            position = 0,
            description = "Select the plank to produce and necessary logs",
            section = generalSection
    )
    default Plank plankToMake() {
        return Plank.MAHOGANY;
    }
}
