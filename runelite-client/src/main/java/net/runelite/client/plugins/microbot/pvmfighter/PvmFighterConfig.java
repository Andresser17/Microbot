package net.runelite.client.plugins.microbot.pvmfighter;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.*;
import net.runelite.client.plugins.microbot.inventorysetups.InventorySetup;
import net.runelite.client.plugins.microbot.pvmfighter.enums.CombatStyle;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PrayerStyle;
import net.runelite.client.plugins.microbot.pvmfighter.enums.RangedAmmo;
import net.runelite.client.plugins.microbot.pvmfighter.enums.Spell;
import net.runelite.client.plugins.microbot.util.antiban.enums.PlayStyle;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;

@ConfigGroup(PvmFighterConfig.GROUP)
@ConfigInformation("1. Make sure to place the cannon first before starting the plugin. <br />" +
        "2. Use food also supports Guthan's healing, the shield weapon is default set to Dragon Defender. <br />" +
        "3. Prayer, Combat, Ranging & AntiPoison potions are supported. <br />" +
        "4. Items to loot based your requirements. <br />" +
        "5. You can turn auto attack NPC off if you have a cannon. <br />" +
        "6. PrayFlick in different styles. <br />" +
        "7. SafeSpot you can Shift Right-click the ground to select the tile. <br />" +
        "8. Right-click NPCs to add them to the attack list. <br />")
public interface PvmFighterConfig extends Config {
    String GROUP = "PvmFighter";

    @ConfigSection(
            name = "Combat",
            description = "Combat",
            position = 1,
            closedByDefault = false
    )
    String combatSection = "Combat";
    @ConfigSection(
            name = "Banking",
            description = "Banking settings",
            position = 992,
            closedByDefault = false
    )
    String banking = "Banking";
    //Gear section
    @ConfigSection(
            name = "Gear",
            description = "Gear",
            position = 55,
            closedByDefault = true
    )
    String gearSection = "Gear";
    @ConfigSection(
            name = "Food & Potions",
            description = "Food & Potions",
            position = 2,
            closedByDefault = false
    )
    String foodAndPotionsSection = "Food & Potions";
    @ConfigSection(
            name = "Loot",
            description = "Loot",
            position = 3,
            closedByDefault = false
    )
    String lootSection = "Loot";
    //Prayer section
    @ConfigSection(
            name = "Prayer",
            description = "Prayer",
            position = 4,
            closedByDefault = false
    )
    String prayerSection = "Prayer";

    @ConfigSection(
            name = "Skilling",
            description = "Skilling",
            position = 5,
            closedByDefault = false
    )
    String skillingSection = "Combat Skilling";

    @ConfigItem(
            keyName = "Combat",
            name = "Auto attack npc",
            description = "Attacks npc",
            position = 0,
            section = combatSection
    )
    default boolean toggleCombat() {
        return false;
    }

    @ConfigItem(
            keyName = "monster",
            name = "Attackable npcs",
            description = "List of attackable npcs",
            position = 1,
            section = combatSection
    )
    default String attackableNpcs() {
        return "";
    }

    @ConfigItem(
            keyName = "Attack Radius",
            name = "Attack Radius",
            description = "The max radius to attack npcs",
            position = 2,
            section = combatSection
    )
    default int attackRadius() {
        return 10;
    }

    @ConfigItem(
            keyName = "Use special attack",
            name = "Use special attack",
            description = "Use special attack",
            position = 3,
            section = combatSection
    )
    default boolean useSpecialAttack() {
        return false;
    }

    @ConfigItem(
            keyName = "Cannon",
            name = "Auto reload cannon",
            description = "Automatically reloads cannon",
            position = 4,
            section = combatSection
    )
    default boolean toggleCannon() {
        return false;
    }

    //safe spot
    @ConfigItem(
            keyName = "SafeSpot",
            name = "Safe Spot",
            description = "Shift Right-click the ground to select the safe spot tile",
            position = 5,
            section = combatSection
    )
    default boolean toggleSafeSpot() {
        return false;
    }

    // Player minimum HP if is bellow run to safe spot
    @ConfigItem(
            keyName = "SafeSpotMinimumHealth",
            name = "Minimum player health (%)",
            description = "if player health is less than the provided percentage, run to safe spot",
            position = 6,
            section = combatSection
    )
    default int minimumHealthSafeSpot() {
        return 10;
    }

    //PlayStyle
    @ConfigItem(
            keyName = "PlayStyle",
            name = "Play Style",
            description = "Play Style",
            position = 7,
            section = combatSection
    )
    default PlayStyle playStyle() {
        return PlayStyle.AGGRESSIVE;
    }

    @ConfigItem(
            keyName = "CombatStyle",
            name = "Set Combat Style",
            description = "Select between Melee, Ranged, and Magic.",
            position = 8,
            section = combatSection
    )
    default CombatStyle combatStyle() {
        return CombatStyle.MELEE;
    }

    @ConfigItem(
            keyName = "AmmoToUse",
            name = "Ammo to use",
            description = "Equip ammunition for ranged attack",
            position = 9,
            section = combatSection
    )
    default RangedAmmo ammoToUse() {
        return RangedAmmo.BRONZE_BOLTS;
    }

    @ConfigItem(
            keyName = "SpellToUse",
            name = "Spell to use",
            description = "Select attack spell",
            position = 10,
            section = combatSection
    )
    default Spell spellToUse() {
        return Spell.WIND_STRIKE;
    }

    @ConfigItem(
            keyName = "ReachableNpcs",
            name = "Only attack reachable npcs",
            description = "Only attack npcs that we can reach with melee",
            position = 11,
            section = combatSection
    )
    default boolean attackReachableNpcs() {
        return true;
    }

    // Food Section

    @ConfigItem(
            keyName = "Food",
            name = "Auto eat food",
            description = "Automatically eats food",
            position = 0,
            section = foodAndPotionsSection
    )
    default boolean toggleFood() {
        return false;
    }

    @ConfigItem(
            keyName = "Auto Prayer Potion",
            name = "Auto drink prayer potion",
            description = "Automatically drinks prayer potions",
            position = 1,
            section = foodAndPotionsSection
    )
    default boolean togglePrayerPotions() {
        return false;
    }

    @ConfigItem(
            keyName = "Combat potion",
            name = "Auto drink super combat potion",
            description = "Automatically drinks combat potions",
            position = 2,
            section = foodAndPotionsSection
    )
    default boolean toggleCombatPotion() {
        return false;
    }

    @ConfigItem(
            keyName = "Ranging/Bastion potion",
            name = "Auto drink Ranging/Bastion potion",
            description = "Automatically drinks Ranging/Bastion potions",
            position = 3,
            section = foodAndPotionsSection
    )
    default boolean toggleRangingPotion() {
        return false;
    }

    @ConfigItem(
            keyName = "Use AntiPoison",
            name = "Use AntiPoison",
            description = "Use AntiPoison",
            position = 4,
            section = foodAndPotionsSection
    )
    default boolean useAntiPoison() {
        return false;
    }

    @ConfigItem(
            keyName = "EatUntilFullHealth",
            name = "Eat food until 100% health",
            description = "Eat inventory food until player health is full",
            position = 5,
            section = foodAndPotionsSection
    )
    default boolean eatUntilFullHealth() {
        return false;
    }


    // Loot section

    @ConfigItem(
            keyName = "Loot items",
            name = "Auto loot items",
            description = "Enable/disable loot items",
            position = 0,
            section = lootSection
    )
    default boolean toggleLootItems() {
        return true;
    }

    @ConfigItem(
            keyName = "LootItemsByPriceRange",
            name = "Loot by price range",
            description = "Enable/Disabled loot items between provided min and max price range",
            position = 0,
            section = lootSection
    )
    default boolean toggleLootItemsByPriceRange() {
        return false;
    }

    @ConfigItem(
            keyName = "Min Price of items to loot",
            name = "Min. Price of items to loot",
            description = "Min. Price of items to loot",
            position = 1,
            section = lootSection
    )
    default int minPriceOfItemsToLoot() {
        return 5000;
    }

    @ConfigItem(
            keyName = "Max Price of items to loot",
            name = "Max. Price of items to loot",
            description = "Max. Price of items to loot default is set to 10M",
            position = 1,
            section = lootSection
    )
    default int maxPriceOfItemsToLoot() {
        return 10000000;
    }

    @ConfigItem(
            keyName = "Loot arrows",
            name = "Auto loot arrows",
            description = "Enable/disable loot arrows",
            position = 2,
            section = lootSection
    )
    default boolean toggleLootArrows() {
        return false;
    }

    @ConfigItem(
            keyName = "Loot runes",
            name = "Loot runes",
            description = "Enable/disable loot runes",
            position = 3,
            section = lootSection
    )
    default boolean toggleLootRunes() {
        return false;
    }

    @ConfigItem(
            keyName = "Loot coins",
            name = "Loot coins",
            description = "Enable/disable loot coins",
            position = 4,
            section = lootSection
    )
    default boolean toggleLootCoins() {
        return false;
    }

    @ConfigItem(
            keyName = "Loot items by name",
            name = "Loot items by their name",
            description = "Enable/Disable loot items by their name",
            position = 6,
            section = lootSection
    )
    default boolean toggleLootItemsByName() {
        return false;
    }

    @ConfigItem(
            keyName = "Items to loot",
            name = "Items to loot",
            description = "Loot items by name",
            position = 7,
            section = lootSection
    )
    default String lootItemsByName() {
        return "";
    }

    @ConfigItem(
            keyName = "Bury Bones",
            name = "Bury Bones",
            description = "Picks up and Bury Bones",
            position = 96,
            section = lootSection
    )
    default boolean toggleLootBones() {
        return false;
    }

    @ConfigItem(
            keyName = "Scatter",
            name = "Scatter",
            description = "Picks up and Scatter ashes",
            position = 97,
            section = lootSection
    )
    default boolean toggleScatter() {
        return false;
    }

    // delayed looting
    @ConfigItem(
            keyName = "minimumQuantityToLoot",
            name = "Minimum Quantity",
            description = "Minimum quantity of one item to loot.",
            position = 98,
            section = lootSection
    )
    default int minimumQuantityToLoot() {
        return 3;
    }

    // delayed looting
    @ConfigItem(
            keyName = "delayedLooting",
            name = "Delayed Looting",
            description = "Lets the loot stay on the ground for a while before picking it up",
            position = 98,
            section = lootSection
    )
    default boolean toggleDelayedLooting() {
        return false;
    }

    // only loot my items
    @ConfigItem(
            keyName = "onlyLootMyItems",
            name = "Only Loot My Items",
            description = "Only loot items that are dropped for/by you",
            position = 99,
            section = lootSection
    )
    default boolean toggleOnlyLootMyItems() {
        return false;
    }

    //Force loot regardless if we are in combat or not
    @ConfigItem(
            keyName = "forceLoot",
            name = "Force Loot",
            description = "Force loot regardless if we are in combat or not",
            position = 100,
            section = lootSection
    )
    default boolean toggleForceLoot() {
        return false;
    }

    //set center tile manually
    @ConfigItem(
            keyName = "Center Tile",
            name = "Manual Center Tile",
            description = "Shift Right-click the ground to select the center tile",
            position = 6,
            section = combatSection
    )
    default boolean toggleCenterTile() {
        return false;
    }

    //Use quick prayer
    @ConfigItem(
            keyName = "Use prayer",
            name = "Use prayer",
            description = "Use prayer",
            position = 0,
            section = prayerSection
    )
    default boolean togglePrayer() {
        return false;
    }

    //Flick quick prayer
    @ConfigItem(
            keyName = "quickPrayer",
            name = "Quick prayer",
            description = "Use quick prayer",
            position = 1,
            section = prayerSection
    )
    default boolean toggleQuickPray() {
        return false;
    }

    //Lazy flick
    @ConfigItem(
            keyName = "prayerStyle",
            name = "Prayer Style",
            description = "Select type of prayer style to use",
            position = 2,
            section = prayerSection
    )
    default PrayerStyle prayerStyle() {
        return PrayerStyle.LAZY_FLICK;
    }

    //Prayer style guide
    @ConfigItem(
            keyName = "prayerStyleGuide",
            name = "Prayer Style Guide",
            description = "Prayer Style Guide",
            position = 3,
            section = prayerSection
    )
    default String prayerStyleGuide() {
        return "Lazy Flick: Flicks tick before hit\n" +
                "Perfect Lazy Flick: Flicks on hit\n" +
                "Continuous: Quick prayer is on when in combat\n" +
                "Always On: Quick prayer is always on";
    }

    // Enable skilling
    @ConfigItem(
            keyName = "enableSkilling",
            name = "Enable Skilling",
            description = "Enable Skilling",
            position = 0,
            section = skillingSection
    )
    default boolean toggleEnableSkilling() {
        return false;
    }
    //Balance combat skills
    @ConfigItem(
            keyName = "balanceCombatSkills",
            name = "Balance combat skills",
            description = "Balance combat skills",
            position = 0,
            section = skillingSection
    )
    default boolean toggleBalanceCombatSkills() {
        return false;
    }

    //Avoid Controlled attack style
    @ConfigItem(
            keyName = "avoidControlled",
            name = "No Controlled Attack",
            description = "Avoid Controlled attack style so you won't accidentally train unwanted combat skills",
            position = 1,
            section = skillingSection
    )
    default boolean toggleAvoidControlled() {
        return true;
    }


    //Attack style change delay (Seconds)
    @ConfigItem(
            keyName = "attackStyleChangeDelay",
            name = "Change Delay",
            description = "Attack Style Change Delay In Seconds",
            position = 2,
            section = skillingSection
    )
    default int attackStyleChangeDelay() {
        return 60 * 15;
    }
    // Disable on Max combat
    @ConfigItem(
            keyName = "disableOnMaxCombat",
            name = "Disable on Max Combat",
            description = "Disable on Max Combat",
            position = 3,
            section = skillingSection
    )
    default boolean toggleDisableOnMaxCombat() {
        return true;
    }
    //Attack skill target
    @ConfigItem(
            keyName = "attackSkillTarget",
            name = "Attack Level Target",
            description = "Attack level target",
            position = 97,
            section = skillingSection
    )
    default int attackSkillTarget() {
        return 99;
    }

    //Strength skill target
    @ConfigItem(
            keyName = "strengthSkillTarget",
            name = "Strength Level Target",
            description = "Strength level target",
            position = 98,
            section = skillingSection
    )
    default int strengthSkillTarget() {
        return 99;
    }

    //Defence skill target
    @ConfigItem(
            keyName = "defenceSkillTarget",
            name = "Defence Level Target",
            description = "Defence level target",
            position = 99,
            section = skillingSection
    )
    default int defenceSkillTarget() {
        return 99;
    }


    // Use Inventory Setup
    @ConfigItem(
            keyName = "useInventorySetup",
            name = "Use Inventory Setup",
            description = "Use Inventory Setup, make sure to select consumables used in the bank section",
            position = 1,
            section = gearSection
    )
    default boolean useInventorySetup() {
        return false;
    }

    // Inventory setup selection TODO: Add inventory setup selection
    @ConfigItem(
            keyName = "InventorySetupName",
            name = "Inventory setup name",
            description = "Create an inventory setup in the inventory setup plugin and enter the name here",
            position = 99,
            section = gearSection
    )
    default String inventorySetup() {
        return "";
    }

    // Banking section

    @ConfigItem(
            keyName = "bank",
            name = "Bank",
            description = "If enabled, will bank items when inventory is full. If disabled, will just stop looting",
            position = 0,
            section = banking
    )
    default boolean toggleBanking() {
        return false;
    }

    @ConfigItem(
            keyName = "BankLocation",
            name = "Bank Location",
            description = "Select the bank to use",
            position = 1,
            section = banking
    )
    default BankLocation bankLocation() {
        return BankLocation.AL_KHARID;
    }

    @ConfigItem(
            keyName = "NearestBankLocation",
            name = "Go to nearest bank",
            description = "Use nearest bank location available",
            position = 2,
            section = banking
    )
    default boolean useNearestBank() {
        return true;
    }

    //Minimum free inventory slots to bank
    @Range(max = 28)
    @ConfigItem(
            keyName = "minFreeSlots",
            name = "Min. free slots",
            description = "Minimum free inventory slots to bank, if less than this, will bank items",
            position = 3,
            section = banking
    )
    default int minFreeSlots() {
        return 5;
    }

    // checkbox to use stamina potions when banking
    @ConfigItem(
            keyName = "useStamina",
            name = "Use stamina potions",
            description = "Use stamina potions when banking",
            position = 4,
            section = banking
    )
    default boolean useStamina() {
        return false;
    }

    @ConfigItem(
            keyName = "staminaValue",
            name = "Stamina Potions",
            description = "Amount of stamina potions to withdraw",
            position = 5,
            section = banking
    )
    default int staminaValue() {
        return 0;
    }

    // checkbox to use food when banking
    @ConfigItem(
            keyName = "useFood",
            name = "Use food",
            description = "Use food when banking",
            position = 6,
            section = banking
    )
    default boolean useFood() {
        return false;
    }

    @ConfigItem(
            keyName = "AmountOfFood",
            name = "Food",
            description = "Amount of food to withdraw",
            position = 7,
            section = banking
    )
    default int amountOfFood() {
        return 0;
    }

    @ConfigItem(
            keyName = "FoodToWithdraw",
            name = "Food to withdraw",
            description = "Food to withdraw by name, will withdraw prioritizing position from first to last",
            position = 8,
            section = banking
    )
    default String foodToWithdraw() {
        return "";
    }

    @ConfigItem(
            keyName = "WithdrawNecessaryToRestoreHealth",
            name = "Withdraw food to restore health",
            description = "When banking withdraw and eat only the necessary food to restore full health.",
            position = 9,
            section = banking
    )
    default boolean withdrawNecessaryFoodToRestoreHealth() {
        return false;
    }

    // checkbox to use restore potions when banking
    @ConfigItem(
            keyName = "useRestore",
            name = "Use restore potions",
            description = "Use restore potions when banking",
            position = 10,
            section = banking
    )
    default boolean useRestore() {
        return false;
    }

    @ConfigItem(
            keyName = "restoreValue",
            name = "Restore Potions",
            description = "Amount of restore potions to withdraw",
            position = 11,
            section = banking
    )
    default int restoreValue() {
        return 0;
    }

    // checkbox to use prayer potions when banking
    @ConfigItem(
            keyName = "usePrayer",
            name = "Use prayer potions",
            description = "Use prayer potions when banking",
            position = 12,
            section = banking
    )
    default boolean usePrayer() {
        return false;
    }

    @ConfigItem(
            keyName = "prayerValue",
            name = "Prayer Potions",
            description = "Amount of prayer potions to withdraw",
            position = 13,
            section = banking
    )
    default int prayerValue() {
        return 0;
    }

    // checkbox to use antipoison potions when banking
    @ConfigItem(
            keyName = "useAntipoison",
            name = "Use antipoison potions",
            description = "Use antipoison potions when banking",
            position = 14,
            section = banking
    )
    default boolean useAntipoison() {
        return false;
    }

    @ConfigItem(
            keyName = "antipoisonValue",
            name = "Antipoison Potions",
            description = "Amount of antipoison potions to withdraw",
            position = 15,
            section = banking
    )
    default int antipoisonValue() {
        return 0;
    }

    // checkbox to use antifire potions when banking
    @ConfigItem(
            keyName = "useAntifire",
            name = "Use antifire potions",
            description = "Use antifire potions when banking",
            position = 16,
            section = banking
    )
    default boolean useAntifire() {
        return false;
    }

    @ConfigItem(
            keyName = "antifireValue",
            name = "Antifire Potions",
            description = "Amount of antifire potions to withdraw",
            position = 17,
            section = banking
    )
    default int antifireValue() {
        return 0;
    }

    // checkbox to use combat potions when banking
    @ConfigItem(
            keyName = "useCombat",
            name = "Use combat potions",
            description = "Use combat potions when banking",
            position = 18,
            section = banking
    )
    default boolean useCombat() {
        return false;
    }

    @ConfigItem(
            keyName = "combatValue",
            name = "Combat Potions",
            description = "Amount of combat potions to withdraw",
            position = 19,
            section = banking
    )
    default int combatValue() {
        return 0;
    }


    // checkbox to use teleportation items when banking
    @ConfigItem(
            keyName = "ignoreTeleport",
            name = "Ignore Teleport Items",
            description = "ignore teleport items when banking",
            position = 20,
            section = banking
    )
    default boolean ignoreTeleport() {
        return true;
    }

    @ConfigItem(
            keyName = "itemsToKeep",
            name = "Items to keep",
            description = "Items to keep by item name",
            position = 21,
            section = banking
    )
    default String itemsToKeep() {
        return "";
    }

    // Hidden configurations

    // Hidden config item for inventory setup
    @ConfigItem(
            keyName = "inventorySetupHidden",
            name = "inventorySetupHidden",
            description = "inventorySetupHidden",
            hidden = true
    )
    default InventorySetup inventorySetupHidden() {
        return null;
    }

    //hidden config item for center location
    @ConfigItem(
            keyName = "centerLocation",
            name = "Center Location",
            description = "Center Location",
            hidden = true
    )
    default WorldPoint centerLocation() {
        return new WorldPoint(0, 0, 0);
    }

    //hidden config item for safe spot location
    @ConfigItem(
            keyName = "SafeSpotLocation",
            name = "Safe Spot Location",
            description = "Safe Spot Location",
            hidden = true
    )
    default WorldPoint safeSpot() {
        return new WorldPoint(0, 0, 0);
    }

}


