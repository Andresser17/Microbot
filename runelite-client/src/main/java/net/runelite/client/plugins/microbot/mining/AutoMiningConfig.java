package net.runelite.client.plugins.microbot.mining;

import net.runelite.client.config.*;

@ConfigGroup("Mining")
@ConfigInformation("<h2>Auto Mining</h2>" +
        "<h3>Version: "+ AutoMiningScript.version + "</h3>" +
        "<p>1. <strong>Ore Selection:</strong> Choose the type of ore you wish to mine. The default ore is <em>TIN</em>.</p>" +
        "<p></p>"+
        "<p>2. <strong>Distance to Stray:</strong> Set the maximum distance in tiles that the bot can travel from its initial position. The default distance is <em>20 tiles</em>.</p>" +
        "<p></p>"+
        "<p>3. <strong>Banking Option:</strong> Enable or disable the use of a bank. If enabled, the bot will walk back to the original location after banking. The default setting is <em>disabled</em>.</p>" +
        "<p></p>"+
        "<p>4. <strong>Items to Bank:</strong> Specify the items to be banked, separated by commas. The default value is <em>'ore'</em>.</p>"+
        "<p></p>"+
        "<p>5. <strong>Basalt:</strong> If mining basalt, ensure UseBank is checked and it will automatically note at Snowflake</em>.</p>")

public interface AutoMiningConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Ore",
            name = "Ore",
            description = "Choose the ore",
            position = 0,
            section = generalSection
    )
    default String oresToMine()
    {
        return "Tin Ore";
    }

    @ConfigItem(
            keyName = "Area",
            name = "Area",
            description = "Set how far you can travel from your initial position in tiles",
            position = 2,
            section = generalSection
    )
    default int area()
    {
        return 20;
    }

    @ConfigItem(
            keyName = "UseBank",
            name = "UseBank",
            description = "Use bank and walk back to original location",
            position = 3,
            section = generalSection
    )
    default boolean useBank()
    {
        return false;
    }

    @ConfigItem(
            keyName = "ItemsToKeep",
            name = "Items to Keep",
            description = "Items to keep in inventory, separate with coma",
            position = 4
    )
    default String itemsToKeep() {
        return "Rune Pickaxe";
    }

}