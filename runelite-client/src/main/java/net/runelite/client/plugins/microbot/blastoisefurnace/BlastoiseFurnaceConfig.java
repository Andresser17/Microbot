package net.runelite.client.plugins.microbot.blastoisefurnace;

import net.runelite.client.config.*;
import net.runelite.client.plugins.microbot.blastoisefurnace.enums.Bars;

@ConfigGroup("blastoisefurnace")
@ConfigInformation("must have Ice Gloves or smiths gloves (i) equiped<br />+<br />coalbag, stamina potions and energy potions in bank<br /><br />| if doing gold bars; must have Goldsmiths Gauntlet and bank your coalbag |<br /><br />(makes 1.5m an hour minimum with steel and should cover expenses)<br /><br />current version does not support foremen or coffer refill<br /><br />im working on that aswell as full native use of antiban<br />Enjoy! :)")
public interface BlastoiseFurnaceConfig extends Config {
    // Keys
    String stamina = "stamina";
    String bars = "bars";
    String payFee = "payFee";

    @ConfigSection(
            name = "Settings",
            description = "Blast Furnace Settings",
            position = 0,
            closedByDefault = false
    )
    String settings = "settings";

    @ConfigItem(
            keyName = bars,
            name = "Bars",
            description = "Select Bars to smith",
            position = 0,
            section = settings
    )
    default Bars getBars() {
        return Bars.STEEL_BAR;
    }

    @ConfigItem(
            keyName = stamina,
            name = "Use stamina potions",
            description = "Use drink and stamina potions",
            position = 1,
            section = settings
    )
    default boolean useStamina() {
        return true;
    }

    @ConfigItem(
            keyName = payFee,
            name = "Has to pay fee?",
            description = "Player not have smithing level 60, so has to pay 2500 coins every 10 minutes to used Blast Furnace.",
            position = 2,
            section = settings
    )
    default boolean hasToPayFee() {
        return false;
    }
}