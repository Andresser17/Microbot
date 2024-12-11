package net.runelite.client.plugins.microbot.doughmaker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.doughmaker.enums.DoughItem;

@ConfigGroup("doughMaker")
public interface DoughMakerConfig extends Config {

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
            name = "Dough to generate",
            keyName = "doughItem",
            position = 0,
            description = "Select which dough paste you want to create",
            section = generalSection
    )
    default DoughItem doughItem() {
        return DoughItem.BREAD_DOUGH;
    }
}
