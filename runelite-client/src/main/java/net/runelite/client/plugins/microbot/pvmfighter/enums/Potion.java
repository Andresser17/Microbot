package net.runelite.client.plugins.microbot.pvmfighter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.plugins.microbot.globval.enums.Skill;
import net.runelite.client.plugins.microbot.util.misc.Rs2Potion;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

@Getter
@RequiredArgsConstructor
public enum Potion {
    COMBAT("Combat potion", PotionType.BOOST, new Skill[]{Skill.ATTACK, Skill.STRENGTH}, -1, 0),
    PRAYER("Prayer potion", PotionType.RESTORATION, new Skill[]{Skill.PRAYER}, -1, 7),
    ANTIPOISON("Antipoison", PotionType.RESTORATION, null, Rs2Player.antiPoisonTime, 0),
    ANTIVENOM("Antivenom", PotionType.RESTORATION, null, Rs2Player.antiVenomTime, 0);

    final String name;
    final PotionType type;
    final Skill[] boostedSkills;
    final int timer;
    final int restorePoints;

    public static Potion findPotionByName(String name) {
        return null;
    }

    public void checkBoostedSkills() {

    }
}
