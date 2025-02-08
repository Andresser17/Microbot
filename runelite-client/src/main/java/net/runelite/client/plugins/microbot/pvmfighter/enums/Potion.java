package net.runelite.client.plugins.microbot.pvmfighter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.globval.enums.Skill;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Potion {
    COMBAT("Combat",
            new int[]{ItemID.COMBAT_POTION1, ItemID.COMBAT_POTION2, ItemID.COMBAT_POTION3, ItemID.COMBAT_POTION4},
            PotionType.BOOST, new Skill[]{Skill.ATTACK, Skill.STRENGTH}, -1, 0),
    STRENGTH("Strength",
            new int[]{ItemID.STRENGTH_POTION1, ItemID.STRENGTH_POTION2, ItemID.STRENGTH_POTION3, ItemID.STRENGTH_POTION4},
            PotionType.BOOST, new Skill[]{Skill.STRENGTH}, -1, 0),
    PRAYER("Prayer",
            new int[]{ItemID.PRAYER_POTION1, ItemID.PRAYER_POTION2, ItemID.PRAYER_POTION3, ItemID.PRAYER_POTION4},
            PotionType.RESTORATION, new Skill[]{Skill.PRAYER}, -1, 7),
    ANTIPOISON("Antipoison",
            new int[]{ItemID.ANTIPOISON1, ItemID.ANTIPOISON2, ItemID.ANTIPOISON3, ItemID.ANTIPOISON4},
            PotionType.RESTORATION, null, Rs2Player.antiPoisonTime, 0),
    ANTIVENOM("Antivenom",
            new int[]{ItemID.ANTIVENOM1, ItemID.ANTIVENOM2, ItemID.ANTIVENOM3, ItemID.ANTIVENOM4},
            PotionType.RESTORATION, null, Rs2Player.antiVenomTime, 0);

    final String name;
    final int[] ids;
    final PotionType type;
    final Skill[] boostedSkills;
    final int timer;
    final int restorePoints;

    public static Stream<Potion> stream() {
        return Stream.of(Potion.values());
    }

    public static Potion findPotionByName(String name) {
        Optional<Potion> optional = stream().filter(potion -> potion.getName().toLowerCase().contains(name)).findFirst();
        return optional.orElse(null);
    }

    public static Potion findPotionById(int id) {
        Optional<Potion> optional = stream().filter(potion -> Arrays.stream(potion.getIds()).anyMatch((potionId -> potionId == id))).findFirst();
        return optional.orElse(null);
    }
}
