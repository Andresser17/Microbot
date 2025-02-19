package net.runelite.client.plugins.microbot.magic.aiomagic;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.MagicActivity;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.StunSpell;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.SuperHeatItem;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.TeleportSpell;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.*;
import net.runelite.client.plugins.microbot.util.magic.Rs2CombatSpells;
import net.runelite.client.plugins.microbot.util.magic.Rs2Staff;

@ConfigGroup(AIOMagicConfig.configGroup)
public interface AIOMagicConfig extends Config {
	String configGroup = "aio-magic";
	String activity = "magicActivity";
	String combatSpell = "magicCombatSpell";
	String alchItems = "alchItems";
	String superHeatItem = "superHeatItem";
	String npcName = "npcName";
	String staff = "staff";
	String teleportSpell = "teleportSpell";
	String castAmount = "castAmount";
	String enchantSpell = "enchantSpell";
	String jewelleryToEnchant = "jewelleryToEnchant";
	String stunSpell = "stunSpell";
	String stunNpcName = "stunNpcName";
	String degrimeStaff = "degrimeStaff";
	String herbsToClean = "herbsToClean";
	String toggleCleanAllHerbs = "toggleCleanAllHerbs";
	String excludedHerbsToClean = "excludedHerbsToClean";
	String leatherStaff = "leatherStaff";
	String leatherToTan = "leatherToTan";

	@ConfigSection(
			name = "General Settings",
			description = "Configure general plugin configuration & preferences",
			position = 0
	)
	String generalSection = "general";

	@ConfigSection(
			name = "Splashing Settings",
			description = "Configure splashing settings",
			position = 1
	)
	String splashSection = "splash";

	@ConfigSection(
			name = "Stun Settings",
			description = "Configure splashing settings",
			position = 1
	)
	String stunSection = "stun";

	@ConfigSection(
			name = "Alch Settings",
			description = "Configure Alching settings",
			position = 2
	)
	String alchSection = "alch";

	@ConfigSection(
			name = "SuperHeat Settings",
			description = "Configure SuperHeat settings",
			position = 2
	)
	String superHeatSection = "superHeat";

	@ConfigSection(
			name = "Teleport Settings",
			description = "Configure teleport settings",
			position = 3
	)
	String teleportSection = "teleport";

	@ConfigSection(
			name = "Enchant Settings",
			description = "Configure enchant settings",
			position = 4
	)
	String enchantSection = "enchant";

	@ConfigSection(
			name = "Degrime Settings",
			description = "Configure degrime settings",
			position = 5
	)
	String degrimeSection = "degrime";

	@ConfigSection(
			name = "Tan Leather Settings",
			description = "Configure tan leather settings",
			position = 6
	)
	String tanLeatherSection = "tanLeatherSection";

	@ConfigItem(
			keyName = activity,
			name = "Activity",
			description = "Select the activity you would like to perform",
			position = 0,
			section = generalSection
	)
	default MagicActivity magicActivity() {
		return MagicActivity.SPLASHING;
	}

	@ConfigItem(
			keyName = stunNpcName,
			name = "Stun npc name",
			description = "Name of the npc to stun",
			position = 0,
			section = stunSection
	)
	default String stunNpcName() {
		return "";
	}
	@ConfigItem(
			keyName = stunSpell,
			name = "Stun spell",
			description = "Name of the stun spell",
			position = 1,
			section = stunSection
	)
	default StunSpell stunSpell() {
		return StunSpell.STUN;
	}

	@ConfigItem(
			keyName = npcName,
			name = "NPC Name",
			description = "Name of the NPC you would like to splash",
			position = 0,
			section = splashSection
	)
	default String npcName() {
		return "";
	}

	@ConfigItem(
			keyName = combatSpell,
			name = "Combat Spell",
			description = "Select the spell you would like to splash with",
			position = 1,
			section = splashSection
	)
	default Rs2CombatSpells combatSpell() {
		return Rs2CombatSpells.WIND_STRIKE;
	}

	@ConfigItem(
			keyName = alchItems,
			name = "Alch Items",
			description = "List of items you would like to alch",
			position = 0,
			section = alchSection
	)
	default String alchItems() {
		return "";
	}

	@ConfigItem(
			keyName = superHeatItem,
			name = "SuperHeat Items",
			description = "List of items you would like to superheat",
			position = 0,
			section = superHeatSection
	)
	default SuperHeatItem superHeatItem() {
		return SuperHeatItem.IRON;
	}

	@ConfigItem(
			keyName = teleportSpell,
			name = "Teleport Spell",
			description = "Select the teleport spell you would like to use",
			position = 0,
			section = teleportSection
	)
	default TeleportSpell teleportSpell() {
		return TeleportSpell.VARROCK_TELEPORT;
	}

	@ConfigItem(
			keyName = staff,
			name = "Staff",
			description = "Select the staff you would like to use",
			position = 1,
			section = teleportSection
	)
	default Rs2Staff staff() {
		return Rs2Staff.STAFF_OF_AIR;
	}

	@ConfigItem(
			keyName = castAmount,
			name = "Total amount of casts",
			description = "Define the amount of teleport casts",
			position = 2,
			section = teleportSection
	)
	default int castAmount() {
		return 1000;
	}

	@ConfigItem(
			keyName = enchantSpell,
			name = "Enchant Spell",
			description = "Select enchanting to cast",
			position = 0,
			section = enchantSection
	)
	default EnchantSpell enchantSpell() {
		return EnchantSpell.SAPPHIRE_OPAL_JEWELLERY;
	}

	@ConfigItem(
			keyName = jewelleryToEnchant,
			name = "Jewellery to enchant",
			description = "Select jewellery to enchant",
			position = 1,
			section = enchantSection
	)
	default Jewellery jewelleryToEnchant() {
		return Jewellery.SAPPHIRE_RING;
	}

	@ConfigItem(
			keyName = degrimeStaff,
			name = "Staff",
			description = "Staff to use, Earth staff or a variant",
			position = 0,
			section = degrimeSection
	)
	default EarthStaff degrimeStaff() {
		return EarthStaff.STAFF_OF_EARTH;
	}

	@ConfigItem(
			keyName = herbsToClean,
			name = "Herbs to clean",
			description = "Herbs to clean by name",
			position = 1,
			section = degrimeSection
	)
	default String herbsToClean() {
		return "";
	}

	@ConfigItem(
			keyName = toggleCleanAllHerbs,
			name = "Clean all grimy hers",
			description = "Clean all available herbs in bank",
			position = 2,
			section = degrimeSection
	)
	default boolean toggleCleanAllHerbs() {
		return false;
	}

	@ConfigItem(
			keyName = excludedHerbsToClean,
			name = "Excluded by name",
			description = "Exclude the following herbs by name",
			position = 3,
			section = degrimeSection
	)
	default String excludedHerbsToClean() {
		return "";
	}

	@ConfigItem(
			keyName = leatherStaff,
			name = "Staff",
			description = "Staff to use",
			position = 0,
			section = tanLeatherSection
	)
	default Staff leatherStaff() {
		return Staff.FIRE;
	}

	@ConfigItem(
			keyName = leatherToTan,
			name = "Leather to Tan",
			description = "Type of leather to tan",
			position = 1,
			section = tanLeatherSection
	)
	default Leather leatherToTan() {
		return Leather.GREEN_DRAGONHIDE;
	}
}
