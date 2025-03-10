package net.runelite.client.plugins.microbot.cooking;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.cooking.enums.*;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.inventory.InteractOrder;

@ConfigGroup("autocooking")
public interface AutoCookingConfig extends Config {
    String recipientToFill = "recipientToFill";
    String heatingLocation = "heatingLocation";

    @ConfigSection(
            name = "General",
            description = "General Cooking Settings",
            position = 0
    )
    String generalSection = "general";

    @ConfigSection(
            name = "Combining",
            description = "Combine items to make cooking item",
            position = 1
    )
    String combiningSection = "combining";

    @ConfigSection(
            name = "Cooking",
            description = "Cooking",
            position = 2
    )
    String cookingSection = "cooking";

    @ConfigSection(
            name = "Heating",
            description = "Fill recipients with water then heat with a stove",
            position = 3
    )
    String heatingSection = "heating";

    @ConfigItem(
            name = "Guide",
            keyName = "guide",
            position = 0,
            description = "",
            section = generalSection
    )
    default String guide() {
        return "This plugin allows for semi-AFK cooking, start the script with an empty inventory\n" +
                "1. Ensure to prepare your bank with ingredients\n" +
                "2. Use nearest cooking location will override the configured cooking location & choose the nearest from the configured locations\n\n" +
                "At the moment, only cooked fish are supported. Other cooking activities will be added in the future";
    }

    @ConfigItem(
            name = "Cooking Activity",
            keyName = "cookingActivity",
            position = 1,
            description = "Choose AutoCooking Activity",
            section = generalSection
    )
    default CookingActivity cookingActivity() {
        return CookingActivity.COOKING;
    }

    @ConfigItem(
            name = "Item to Combine",
            keyName = "itemToCombine",
            position = 0,
            description = "Item to Combine",
            section = combiningSection
    )
    default CombiningItem itemToCombine() {
        return CombiningItem.PLAIN_PIZZA;
    }

    @ConfigItem(
            keyName = "itemToCook",
            name = "Item to Cook",
            position = 0,
            description = "Item to cook",
            section = cookingSection
    )
    default CookingItem cookingItem() {
        return CookingItem.RAW_SHRIMP;
    }

    @ConfigItem(
            name = "Humidify Item",
            keyName = "humidifyItem",
            position = 1,
            description = "The item you wish to use to make to humidify the dough",
            section = cookingSection
    )
    default HumidifyItem humidifyItem() {
        return HumidifyItem.JUG;
    }

    @ConfigItem(
            name = "Location",
            keyName = "cookingLocation",
            position = 2,
            description = "Location to cook",
            section = cookingSection
    )
    default CookingLocation cookingLocation() {
        return CookingLocation.COOKS_KITCHEN;
    }

    @ConfigItem(
            name = "Use Nearest Cooking Location",
            keyName = "useNearestCookingLocation",
            position = 3,
            description = "Use Nearest Cooking location (this overrides the specified cooking location)",
            section = cookingSection
    )
    default boolean useNearestCookingLocation() {
        return false;
    }

    @ConfigItem(
            keyName = "BankLocation",
            name = "Bank location",
            position = 4,
            description = "Selected bank location",
            section = cookingSection
    )
    default BankLocation bankLocation() {
        return BankLocation.AL_KHARID;
    }

    @ConfigItem(
            keyName = "useNearestBankLocation",
            name = "Use Nearest Bank Location",
            position = 5,
            description = "Use nearest bank location (this overrides the specified bank location)",
            section = cookingSection
    )
    default boolean useNearestBankLocation() {
        return false;
    }

    @ConfigItem(
            keyName = "DropOrder",
            name = "Drop order",
            position = 6,
            description = "The order in which to drop items",
            section = cookingSection
    )
    default InteractOrder getDropOrder() {
        return InteractOrder.STANDARD;
    }

    @ConfigItem(
            name = "Bank burnt food?",
            keyName = "BankBurntFood",
            position = 7,
            description = "Bank burnt food in inventory",
            section = cookingSection
    )
    default boolean bankBurntFood() {
        return false;
    }

    @ConfigItem(
            keyName = recipientToFill,
            name = "Recipient",
            position = 0,
            description = "Select recipient to fill",
            section = heatingSection
    )
    default Recipient recipientToFill() {
        return Recipient.CUP;
    }

    @ConfigItem(
            keyName = heatingLocation,
            name = "Location",
            position = 1,
            description = "Location with a water source, a stove, and a near bank",
            section = heatingSection
    )
    default HeatingLocation heatingLocation() {
        return HeatingLocation.EDGEVILLE;
    }
}
