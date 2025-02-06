package net.runelite.client.plugins.microbot.pvmfighter;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.*;
import net.runelite.client.plugins.microbot.inventorysetups.InventorySetup;
import net.runelite.client.plugins.microbot.pvmfighter.enums.*;
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

    String selectSettings = "selectSettings";
    // Combat
    String toggleCombat = "toggleCombat";
    String npcTargets = "npcTargets";
    String attackRadius = "attackRadius";
    String useSpecialAttack = "useSpecialAttack";
    String useCannon = "useCannon";
    String useSafeSpot = "useSafeSpot";
    String safeSpotLocation = "safeSpotLocation";
    String minimumHealthToRetrieve = "minimumHealthToRetrieve";
    String selectPlayStyle = "selectPlayStyle";
    String selectCombatStyle = "selectCombatStyle";
    String selectSpellToUse = "selectSpellToUse";
    String attackReachableNpc = "attackReachableNpc";
    // Consumables
    String toggleFood = "toggleFood";
    String usePrayerPotion = "usePrayerPotion";
    String useCombatPotion = "useCombatPotion";
    String useRangingPotion = "useRangingPotion";
    String useAntiPoison = "useAntiPoison";
    String eatUntilFulfillHealth = "eatUntilFulfillHealth";
    // Loot
    String toggleLootItems = "toggleLootItems";
    String lootItemsByPriceRange = "lootItemsByPriceRange";
    String minPriceOfItemsToLoot = "minPriceOfItemsToLoot";
    String maxPriceOfItemsToLoot = "maxPriceOfItemsToLoot";
    String lootArrows = "lootArrows";
    String lootRunes = "lootRunes";
    String lootCoins = "lootCoins";
    String lootItemsByName = "lootItemsByName";
    String nameOfItemsToLoot = "nameOfItemsToLoot";
    String lootBones = "lootBones";
    String lootAshes = "lootAshes";
    String minimumQuantityToLoot = "minimumQuantityToLoot";
    String delayedLooting = "delayedLooting";
    String onlyLootMyItems = "onlyLootMyItems";
    String forceLoot = "forceLoot";
    // Prayer
    String usePrayer = "usePrayer";
    String useQuickPrayer = "useQuickPrayer";
    String usePrayerStyle = "usePrayerStyle";
    String usePrayerStyleGuide = "usePrayerStyleGuide";
    // Skilling
    String toggleSkilling = "toggleSkilling";
    String balanceCombatSkills = "balanceCombatSkills";
    String avoidControlledTraining = "avoidControlledTraining";
    String attackStyleChangeDelay = "attackStyleChangeDelay";
    String disableOnMaxCombat = "disableOnMaxCombat";
    String attackSkillTarget = "attackSkillTarget";
    String strengthSkillTarget = "strengthSkillTarget";
    String defenceSkillTarget = "defenceSkillTarget";
    // Gear
    String useInventorySetup = "useInventorySetup";
    String inventorySetup = "inventorySetup";
    // Bank
    String toggleBank = "toggleBank";
    String bankLocation = "bankLocation";
    String nearestBankLocation = "nearestBankLocation";
    // Slayer
    String toggleSlayer = "toggleSlayer";
    String slayerMaster = "slayerMaster";

    @ConfigSection(
            name = "General Settings",
            description = "General Settings",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "General";

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
            position = 2,
            closedByDefault = false
    )
    String bankingSection = "Banking";

    @ConfigSection(
            name = "Gear",
            description = "Gear",
            position = 4,
            closedByDefault = true
    )
    String gearSection = "Gear";

    @ConfigSection(
            name = "Consumables",
            description = "Food & Potions",
            position = 5,
            closedByDefault = false
    )
    String consumablesSection = "Consumables";

    @ConfigSection(
            name = "Loot",
            description = "Loot",
            position = 6,
            closedByDefault = false
    )
    String lootSection = "Loot";

    @ConfigSection(
            name = "Prayer",
            description = "Prayer",
            position = 7,
            closedByDefault = false
    )
    String prayerSection = "Prayer";

    @ConfigSection(
            name = "Skilling",
            description = "Skilling",
            position = 8,
            closedByDefault = false
    )
    String skillingSection = "Combat Skilling";

    @ConfigItem(
            keyName = toggleSlayer,
            name = "Toggle slayer",
            description = "On/Off slayer training mode",
            position = 0,
            section = generalSection
    )
    default boolean toggleSlayer() {
        return false;
    }

    @ConfigItem(
            keyName = slayerMaster,
            name = "Select Slayer Master",
            description = "Select Slayer master to use",
            position = 1,
            section = generalSection
    )
    default SlayerMaster slayerMaster() {
        return SlayerMaster.VANNAKA;
    }

    @ConfigItem(
            keyName = selectCombatStyle,
            name = "Set Combat Style",
            description = "Select between Melee, Ranged, and Magic.",
            position = 2,
            section = generalSection
    )
    default CombatStyle selectCombatStyle() {
        return CombatStyle.MELEE;
    }

    @ConfigItem(
            keyName = selectSpellToUse,
            name = "Spell to use",
            description = "Select attack spell",
            position = 3,
            section = generalSection
    )
    default Spell selectSpellToUse() {
        return Spell.WIND_STRIKE;
    }

    @ConfigItem(
            keyName = toggleCombat,
            name = "Auto attack npc",
            description = "Attacks npc",
            position = 0,
            section = combatSection
    )
    default boolean toggleCombat() {
        return false;
    }

    @ConfigItem(
            keyName = npcTargets,
            name = "NPC Targets",
            description = "List of NPC to attack",
            position = 1,
            section = combatSection
    )
    default String npcTargets() {
        return "";
    }

    @ConfigItem(
            keyName = attackRadius,
            name = "Attack Radius",
            description = "The max radius to attack NPC",
            position = 2,
            section = combatSection
    )
    default int attackRadius() {
        return 10;
    }

    @ConfigItem(
            keyName = useSpecialAttack,
            name = "Use special attack",
            description = "Use special attack",
            position = 3,
            section = combatSection
    )
    default boolean useSpecialAttack() {
        return false;
    }

    @ConfigItem(
            keyName = useCannon,
            name = "Use cannon",
            description = "Set and auto reload dwarf cannon",
            position = 4,
            section = combatSection
    )
    default boolean useCannon() {
        return false;
    }

    //safe spot
    @ConfigItem(
            keyName = useSafeSpot,
            name = "Safe Spot",
            description = "Shift Right-click the ground to select the safe spot tile",
            position = 5,
            section = combatSection
    )
    default boolean useSafeSpot() {
        return false;
    }

    @ConfigItem(
            keyName = safeSpotLocation,
            name = "Location",
            description = "World point comma separate, ex: X, Y, Plane",
            position = 6,
            section = combatSection
    )
    default String safeSpotLocation() {
        return "X, Y, Z";
    }

    // Player minimum HP if is bellow walk to bank
    @ConfigItem(
            keyName = minimumHealthToRetrieve,
            name = "Minimum player health (%)",
            description = "if player health is less than the provided percentage, walk to bank",
            position = 7,
            section = combatSection
    )
    default int minimumHealthToRetrieve() {
        return 30;
    }

    @ConfigItem(
            keyName = selectPlayStyle,
            name = "Play Style",
            description = "Play Style",
            position = 8,
            section = combatSection
    )
    default PlayStyle selectPlayStyle() {
        return PlayStyle.AGGRESSIVE;
    }

    @ConfigItem(
            keyName = attackReachableNpc,
            name = "Only attack reachable NPC",
            description = "Only attack NPC that we can reach with melee",
            position = 9,
            section = combatSection
    )
    default boolean attackReachableNpc() {
        return true;
    }

    // set center tile manually
    @ConfigItem(
            keyName = "Center Tile",
            name = "Manual Center Tile",
            description = "Shift Right-click the ground to select the center tile",
            position = 10,
            section = combatSection
    )
    default boolean toggleCenterTile() {
        return false;
    }

    // Consumables Section

    @ConfigItem(
            keyName = toggleFood,
            name = "Auto eat food",
            description = "Automatically eats food",
            position = 0,
            section = consumablesSection
    )
    default boolean toggleFood() {
        return false;
    }

    @ConfigItem(
            keyName = usePrayerPotion,
            name = "Auto drink prayer potion",
            description = "Automatically drinks prayer potions",
            position = 1,
            section = consumablesSection
    )
    default boolean usePrayerPotion() {
        return false;
    }

    @ConfigItem(
            keyName = useCombatPotion,
            name = "Auto drink super combat potion",
            description = "Automatically drinks combat potions",
            position = 2,
            section = consumablesSection
    )
    default boolean useCombatPotion() {
        return false;
    }

    @ConfigItem(
            keyName = useRangingPotion,
            name = "Auto drink Ranging/Bastion potion",
            description = "Automatically drinks Ranging/Bastion potions",
            position = 3,
            section = consumablesSection
    )
    default boolean useRangingPotion() {
        return false;
    }

    @ConfigItem(
            keyName = useAntiPoison,
            name = "Use AntiPoison",
            description = "Use AntiPoison",
            position = 4,
            section = consumablesSection
    )
    default boolean useAntiPoison() {
        return false;
    }

    @ConfigItem(
            keyName = eatUntilFulfillHealth,
            name = "Eat food until 100% health",
            description = "Eat inventory food until player health is full",
            position = 5,
            section = consumablesSection
    )
    default boolean eatUntilFulfillHealth() {
        return false;
    }

    // Loot section
    @ConfigItem(
            keyName = toggleLootItems,
            name = "Auto loot items",
            description = "Enable/disable loot items",
            position = 0,
            section = lootSection
    )
    default boolean toggleLootItems() {
        return true;
    }

    @ConfigItem(
            keyName = lootItemsByPriceRange,
            name = "Loot by price range",
            description = "Enable/Disabled loot items between provided min and max price range",
            position = 0,
            section = lootSection
    )
    default boolean lootItemsByPriceRange() {
        return false;
    }

    @ConfigItem(
            keyName = minPriceOfItemsToLoot,
            name = "Min. Price of items to loot",
            description = "Min. Price of items to loot",
            position = 1,
            section = lootSection
    )
    default int minPriceOfItemsToLoot() {
        return 5000;
    }

    @ConfigItem(
            keyName = maxPriceOfItemsToLoot,
            name = "Max. Price of items to loot",
            description = "Max. Price of items to loot default is set to 10M",
            position = 1,
            section = lootSection
    )
    default int maxPriceOfItemsToLoot() {
        return 10000000;
    }

    @ConfigItem(
            keyName = lootArrows,
            name = "Auto loot arrows",
            description = "Enable/disable loot arrows",
            position = 2,
            section = lootSection
    )
    default boolean lootArrows() {
        return false;
    }

    @ConfigItem(
            keyName = lootRunes,
            name = "Loot runes",
            description = "Enable/disable loot runes",
            position = 3,
            section = lootSection
    )
    default boolean lootRunes() {
        return false;
    }

    @ConfigItem(
            keyName = lootCoins,
            name = "Loot coins",
            description = "Enable/disable loot coins",
            position = 4,
            section = lootSection
    )
    default boolean lootCoins() {
        return false;
    }

    @ConfigItem(
            keyName = lootItemsByName,
            name = "Loot items by their name",
            description = "Enable/Disable loot items by their name",
            position = 6,
            section = lootSection
    )
    default boolean lootItemsByName() {
        return false;
    }

    @ConfigItem(
            keyName = nameOfItemsToLoot,
            name = "Items to loot",
            description = "Loot items by name",
            position = 7,
            section = lootSection
    )
    default String nameOfItemsToLoot() {
        return "";
    }

    @ConfigItem(
            keyName = lootBones,
            name = "Loot bones",
            description = "Picks up and Bury Bones",
            position = 8,
            section = lootSection
    )
    default boolean lootBones() {
        return false;
    }

    @ConfigItem(
            keyName = lootAshes,
            name = "Loot Ashes",
            description = "Picks up and Scatter ashes",
            position = 9,
            section = lootSection
    )
    default boolean lootAshes() {
        return false;
    }

    // delayed looting
    @ConfigItem(
            keyName = minimumQuantityToLoot,
            name = "Minimum Quantity",
            description = "Minimum quantity of one item to loot.",
            position = 10,
            section = lootSection
    )
    default int minimumQuantityToLoot() {
        return 3;
    }

    // delayed looting
    @ConfigItem(
            keyName = delayedLooting,
            name = "Delayed Looting",
            description = "Lets the loot stay on the ground for a while before picking it up",
            position = 11,
            section = lootSection
    )
    default boolean delayedLooting() {
        return false;
    }

    // only loot my items
    @ConfigItem(
            keyName = onlyLootMyItems,
            name = "Only Loot My Items",
            description = "Only loot items that are dropped for/by you",
            position = 12,
            section = lootSection
    )
    default boolean onlyLootMyItems() {
        return false;
    }

    //Force loot regardless if we are in combat or not
    @ConfigItem(
            keyName = forceLoot,
            name = "Force Loot",
            description = "Force loot regardless if we are in combat or not",
            position = 13,
            section = lootSection
    )
    default boolean forceLoot() {
        return false;
    }

    //Use quick prayer
    @ConfigItem(
            keyName = usePrayer,
            name = "Use prayer",
            description = "Use prayer",
            position = 0,
            section = prayerSection
    )
    default boolean usePrayerPotions() {
        return false;
    }

    //Flick quick prayer
    @ConfigItem(
            keyName = useQuickPrayer,
            name = "Quick prayer",
            description = "Use quick prayer",
            position = 1,
            section = prayerSection
    )
    default boolean useQuickPrayer() {
        return false;
    }

    //Lazy flick
    @ConfigItem(
            keyName = usePrayerStyle,
            name = "Prayer Style",
            description = "Select type of prayer style to use",
            position = 2,
            section = prayerSection
    )
    default PrayerStyle usePrayerStyle() {
        return PrayerStyle.LAZY_FLICK;
    }

    @ConfigItem(
            keyName = usePrayerStyleGuide,
            name = "Prayer Style Guide",
            description = "Prayer Style Guide",
            position = 3,
            section = prayerSection
    )
    default String usePrayerStyleGuide() {
        return "Lazy Flick: Flicks tick before hit\n" +
                "Perfect Lazy Flick: Flicks on hit\n" +
                "Continuous: Quick prayer is on when in combat\n" +
                "Always On: Quick prayer is always on";
    }

    @ConfigItem(
            keyName = toggleSkilling,
            name = "Toggle Combat Training",
            description = "Train combat skills to an specific level",
            position = 0,
            section = skillingSection
    )
    default boolean toggleSkilling() {
        return false;
    }

    @ConfigItem(
            keyName = balanceCombatSkills,
            name = "Balance combat skills",
            description = "Balance combat skills",
            position = 1,
            section = skillingSection
    )
    default boolean balanceCombatSkills() {
        return false;
    }
    @ConfigItem(
            keyName = avoidControlledTraining,
            name = "No Controlled Attack",
            description = "Avoid Controlled attack style so you won't accidentally train unwanted combat skills",
            position = 2,
            section = skillingSection
    )
    default boolean avoidControlledTraining() {
        return true;
    }
    @ConfigItem(
            keyName = attackStyleChangeDelay,
            name = "Change Delay",
            description = "Attack Style Change Delay In Seconds",
            position = 3,
            section = skillingSection
    )
    default int attackStyleChangeDelay() {
        return 60 * 15;
    }
    @ConfigItem(
            keyName = disableOnMaxCombat,
            name = "Disable on Max Combat",
            description = "Disable on Max Combat",
            position = 4,
            section = skillingSection
    )
    default boolean disableOnMaxCombat() {
        return true;
    }
    @ConfigItem(
            keyName = attackSkillTarget,
            name = "Attack Level Target",
            description = "Attack level target",
            position = 5,
            section = skillingSection
    )
    default int attackSkillTarget() {
        return 99;
    }
    @ConfigItem(
            keyName = strengthSkillTarget,
            name = "Strength Level Target",
            description = "Strength level target",
            position = 6,
            section = skillingSection
    )
    default int strengthSkillTarget() {
        return 99;
    }
    @ConfigItem(
            keyName = defenceSkillTarget,
            name = "Defence Level Target",
            description = "Defence level target",
            position = 7,
            section = skillingSection
    )
    default int defenceSkillTarget() {
        return 99;
    }

    // Inventory Setup
    @ConfigItem(
            keyName = useInventorySetup,
            name = "Use Inventory Setup",
            description = "Use Inventory Setup, make sure to select consumables used in the bank section",
            position = 1,
            section = gearSection
    )
    default boolean useInventorySetup() {
        return false;
    }
    @ConfigItem(
            keyName = inventorySetup,
            name = "Inventory setup name",
            description = "Create an inventory setup in the inventory setup plugin and enter the name here",
            position = 2,
            section = gearSection
    )
    default Setup inventorySetup() {
        return Setup.SLAYER_MELEE;
    }

    // Banking section
    @ConfigItem(
            keyName = toggleBank,
            name = "Bank",
            description = "If enabled, will bank items when inventory is full. If disabled, will just stop looting",
            position = 0,
            section = bankingSection
    )
    default boolean toggleBanking() {
        return false;
    }

    @ConfigItem(
            keyName = bankLocation,
            name = "Bank Location",
            description = "Select the bank to use",
            position = 1,
            section = bankingSection
    )
    default BankLocation bankLocation() {
        return BankLocation.EDGEVILLE;
    }

    @ConfigItem(
            keyName = nearestBankLocation,
            name = "Go to nearest bank",
            description = "Use nearest bank location available",
            position = 2,
            section = bankingSection
    )
    default boolean useNearestBank() {
        return true;
    }

    // Hidden configurations

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
}


