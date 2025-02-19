package net.runelite.client.plugins.microbot.magic.aiomagic;

import com.google.inject.Provides;
import lombok.Getter;

import net.runelite.api.Skill;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.*;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.StunSpell;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.SuperHeatItem;
import net.runelite.client.plugins.microbot.magic.aiomagic.enums.TeleportSpell;
import net.runelite.client.plugins.microbot.magic.aiomagic.scripts.*;
import net.runelite.client.plugins.microbot.util.magic.Rs2CombatSpells;
import net.runelite.client.plugins.microbot.util.magic.Rs2Staff;
import net.runelite.client.plugins.microbot.util.magic.Rs2Spells;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@PluginDescriptor(
		name = PluginDescriptor.GMason + "AIO Magic",
		description = "Microbot Magic plugin",
		tags = {"magic", "microbot", "skilling", "training"},
		enabledByDefault = false
)
public class AIOMagicPlugin extends Plugin {
	
	@Inject
	private AIOMagicConfig config;
	@Provides
	AIOMagicConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(AIOMagicConfig.class);
	}

	@Inject
	private OverlayManager overlayManager;
	@Inject
	private AIOMagicOverlay aioMagicOverlay;
	
	@Inject
	private SplashScript splashScript;
	@Inject
	private AlchScript alchScript;
	@Inject
	private SuperHeatScript superHeatScript;
	@Inject
	private TeleportScript teleportScript;
	@Inject
	private TeleAlchScript teleAlchScript;
	@Inject
	private StunAlchScript stunAlchScript;
	@Inject
	private EnchantScript enchantScript;
	@Inject
	private DegrimeScript degrimeScript;
	@Inject
	private TanLeatherScript tanLeatherScript;

	public static String version = "1.1.0";
	
	@Getter
	private Rs2CombatSpells combatSpell;
	@Getter
	private List<String> alchItemNames = Collections.emptyList();
	@Getter
	private SuperHeatItem superHeatItem;
	@Getter
	private String npcName;
	@Getter
	private TeleportSpell teleportSpell;
	@Getter
	private StunSpell stunSpell;
	@Getter
	private int totalCasts;
	@Getter
	private EnchantSpell enchantSpell;
	@Getter
	private Jewellery jewelleryToEnchant;
	@Getter
	private Rs2Staff staff;
	@Getter
	private String stunNpcName;
	@Getter
	private EarthStaff degrimeStaff;
	@Getter
	private Staff leatherStaff;
	@Getter
	private Leather leatherToTan;
	@Getter
	private String herbsToClean;
	@Getter
	private boolean toggleCleanAllHerbs;
	@Getter
	private String excludedHerbsToClean;



	@Override
	protected void startUp() throws AWTException {
		combatSpell = config.combatSpell();
		alchItemNames = updateItemList(config.alchItems());
		superHeatItem = config.superHeatItem();
		npcName = config.npcName();
		teleportSpell = config.teleportSpell();
		staff = config.staff();
		totalCasts = config.castAmount();
		enchantSpell = config.enchantSpell();
		jewelleryToEnchant = config.jewelleryToEnchant();
		stunSpell = config.stunSpell();
		stunNpcName = config.stunNpcName();
		degrimeStaff = config.degrimeStaff();
		herbsToClean = config.herbsToClean();
		toggleCleanAllHerbs = config.toggleCleanAllHerbs();
		excludedHerbsToClean = config.excludedHerbsToClean();
		leatherStaff = config.leatherStaff();
		leatherToTan = config.leatherToTan();

		if (overlayManager != null) {
			overlayManager.add(aioMagicOverlay);
		}
		
		switch (config.magicActivity()) {
			case SPLASHING:
				splashScript.run();
				break;
			case ALCHING:
				alchScript.run();
				break;
			case SUPERHEAT:
				superHeatScript.run();
				break;
			case TELEPORT:
				teleportScript.run();
				break;
			case TELEALCH:
				teleAlchScript.run();
				break;
			case ENCHANT:
				enchantScript.run();
				break;
			case STUNALCH:
				stunAlchScript.run();
				break;
			case DEGRIME:
				degrimeScript.run();
				break;
			case TAN_LEATHER:
				tanLeatherScript.run();
				break;
		}
	}

	protected void shutDown() {
		splashScript.shutdown();
		alchScript.shutdown();
		superHeatScript.shutdown();
		teleportScript.shutdown();
		teleAlchScript.shutdown();
		enchantScript.shutdown();
		stunAlchScript.shutdown();
		degrimeScript.shutdown();
		tanLeatherScript.shutdown();
		overlayManager.remove(aioMagicOverlay);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (!event.getGroup().equals(AIOMagicConfig.configGroup)) return;
		
		if (event.getKey().equals(AIOMagicConfig.combatSpell)) {
			combatSpell = config.combatSpell();
		}
		
		if (event.getKey().equals(AIOMagicConfig.alchItems)) {
			alchItemNames = updateItemList(config.alchItems());
		}

		if (event.getKey().equals(AIOMagicConfig.superHeatItem)) {
			superHeatItem = config.superHeatItem();
		}
		
		if (event.getKey().equals(AIOMagicConfig.npcName)) {
			npcName = config.npcName();
		}

		if (event.getKey().equals(AIOMagicConfig.teleportSpell)) {
			teleportSpell = config.teleportSpell();
		}

		if (event.getKey().equals(AIOMagicConfig.stunSpell)) {
			stunSpell = config.stunSpell();
		}

		if (event.getKey().equals(AIOMagicConfig.stunNpcName)) {
			stunNpcName = config.stunNpcName();
		}
	}

	private List<String> updateItemList(String items) {
		if (items == null || items.isBlank()) {
			return Collections.emptyList();
		}

		return Arrays.stream(items.split(","))
				.map(String::trim)
				.filter(item -> !item.isEmpty())
				.map(String::toLowerCase)
				.collect(Collectors.toList());
	}

	public Rs2Spells getAlchSpell() {
		return Rs2Player.getSkillRequirement(Skill.MAGIC, 55) ? Rs2Spells.HIGH_LEVEL_ALCHEMY : Rs2Spells.LOW_LEVEL_ALCHEMY;
	}
}
