package net.runelite.client.plugins.microbot.pvmfighter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum SlayerTask {
    ABERRANT_SPECTRE("Aberrant Spectres", new String[]{"Aberrant Spectre"}, true,
            new WorldArea(2437, 9770, 39, 28, 0), new WorldPoint(2456, 9792, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    ABYSSAL_DEMON("Abyssal Demons", new String[]{"Abyssal Demon"}, true,
            new WorldArea(1666, 10083, 14, 9, 0), new WorldPoint(1673, 10087, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    ANKOU("Ankou", new String[]{"Ankou"}, true, new WorldArea(2466, 9795, 22, 16, 0),
            new WorldPoint(2479, 9800, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    BASILISK("Basilisks", new String[]{"Basilisk"}, false, new WorldArea(2735, 10000, 15, 18, 0),
            new WorldPoint(2742, 10010, 0), Setup.SLAYER_MELEE_MIRROR_SHIELD, Setup.SLAYER_MAGIC_MIRROR_SHIELD, Setup.SLAYER_RANGED_MIRROR_SHIELD),
    BLOODVELD("Bloodvelds", new String[]{"Bloodveld"}, true,
            new WorldArea(2429, 9813, 69, 26, 0), new WorldPoint(2436, 9819, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    BLUE_DRAGON("Blue Dragons", new String[]{"Blue Dragon"}, true,
            new WorldArea(2890, 9794, 33, 20, 0), new WorldPoint(2907, 9802, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    BRINE_RAT("Brine Rats", new String[]{"Brine Rat"}, true,
            new WorldArea(2690, 10116, 51, 38, 0), new WorldPoint(2707, 10134, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    COCKATRICE("Cockatrice", new String[]{"Cockatrice"}, false, new WorldArea(2778, 10027, 31, 18, 0),
            new WorldPoint(2791, 10034, 0), Setup.SLAYER_MELEE_MIRROR_SHIELD, Setup.SLAYER_MAGIC_MIRROR_SHIELD, Setup.SLAYER_RANGED_MIRROR_SHIELD),
    CROCODILE("Crocodiles", new String[]{"Crocodile"}, true,
            new WorldArea(3330, 2918, 34, 20, 0), new WorldPoint(3346, 2935, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    DAGANNOTH("Dagannoth", new String[]{"Dagannoth"}, true,
            new WorldArea(1657, 9992, 23, 11, 0), new WorldPoint(1667, 9998, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    DUST_DEVIL("Dust Devils", new String[]{"Dust Devil"}, true,
            new WorldArea(1704, 10008, 17, 29, 0), new WorldPoint(1712, 10017, 0), Setup.SLAYER_MELEE_FACEMASK, Setup.SLAYER_MAGIC_FACEMASK, Setup.SLAYER_RANGED_FACEMASK),
    ELVES("Elves", new String[]{"Iorwerth Archer", "Elf Archer", "Iorwerth Warrior", "Elf Warrior", "Mourner", "Guard", "Reanimated Elf"}, true,
            new WorldArea(2314, 3144, 49, 52, 0), new WorldPoint(2332, 3166, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    FEVER_SPIDER("Fever Spiders", new String[]{"Fever Spider"}, true,
            new WorldArea(0, 0, 49, 52, 0), new WorldPoint(0, 0, 0), Setup.SLAYER_MELEE_SLAYER_GLOVES, Setup.SLAYER_MAGIC_SLAYER_GLOVES, Setup.SLAYER_RANGED_SLAYER_GLOVES),
    FIRE_GIANT("Fire Giants", new String[]{"Fire Giant"}, true, new WorldArea(2387, 9768, 32, 24, 0),
            new WorldPoint(2401, 9781, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    GARGOYLE("Gargoyles", new String[]{"Gargoyle"}, false, new WorldArea(3430, 3531, 23, 25, 2),
            new WorldPoint(3441, 3545, 2), Setup.SLAYER_MELEE_ROCK_HAMMER, Setup.SLAYER_MAGIC_ROCK_HAMMER, Setup.SLAYER_RANGED_ROCK_HAMMER),
    GHOUL("Ghouls", new String[]{"Ghoul"}, true, new WorldArea(3426, 3458, 17, 10, 0),
            new WorldPoint(3432, 3462, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    HARPIE_BUG_SWARM("Harpie Bug Swarms", new String[]{"Harpie Bug Swarm"}, true, new WorldArea(2866, 3104, 23, 12, 0),
            new WorldPoint(2876, 3109, 0), Setup.SLAYER_MELEE_LIT_BUG_LANTERN, Setup.SLAYER_MAGIC_LIT_BUG_LANTERN, Setup.SLAYER_RANGED_LIT_BUG_LANTERN),
    HELLHOUND("Hellhounds", new String[]{"Hellhound"}, true, new WorldArea(2847, 9831, 30, 24, 0),
            new WorldPoint(2859, 9842, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    HILL_GIANT("Hill Giants", new String[]{"Hill Giant"}, true, new WorldArea(3090, 9821, 40, 33, 0),
            new WorldPoint(3112, 9839, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    HOBGOBLIN("Hobgoblins", new String[]{"Hobgoblin"}, true, new WorldArea(3119, 9871, 12, 11, 0),
            new WorldPoint(3125, 9875, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    ICE_GIANT("Ice Giants", new String[]{"Ice Giant"}, true, new WorldArea(1527, 3864, 65, 46, 0),
            new WorldPoint(1575, 3899, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    ICE_WARRIOR("Ice Warriors", new String[]{"Ice Warrior"}, true, new WorldArea(3041, 9566, 25, 25, 0),
            new WorldPoint(3051, 9580, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    INFERNAL_MAGE("Infernal Mages", new String[]{"Infernal Mage"}, true, new WorldArea(3430, 3554, 20, 24, 1),
            new WorldPoint(3440, 3572, 1), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    JELLY("Jellies", new String[]{"Jelly"}, false, new WorldArea(2692, 10020, 23, 16, 0),
            new WorldPoint(2705, 10027, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    JUNGLE_HORROR("Jungle Horrors", new String[]{"Jungle Horror"}, true, new WorldArea(3668, 3008, 107, 34, 0),
            new WorldPoint(3694, 3022, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    KALPHITE("Kalphites", new String[]{"Kalphite Worker", "Kalphite Soldier", "Kalphite Guardian", "Kalphite Queen"}, true, new WorldArea(3263, 9473, 82, 79, 0),
            new WorldPoint(3298, 9501, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    KURASK("Kurask", new String[]{"Kurask"}, false, new WorldArea(2689, 9987, 21, 22, 0),
            new WorldPoint(2699, 9999, 0), Setup.SLAYER_MELEE_KURASK, Setup.SLAYER_MAGIC_KURASK, Setup.SLAYER_RANGED_KURASK),
    LESSER_DEMON("Lesser Demons", new String[]{"Lesser Demon"}, true, new WorldArea(2145, 9329, 24, 14, 0),
            new WorldPoint(2153, 9334, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    MOGRE("Mogres", new String[]{"Mogre"}, true, new WorldArea(2982, 3105, 26, 18, 0),
            new WorldPoint(2994, 3119, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    MOSS_GIANT("Moss Giants", new String[]{"Moss Giant"}, true, new WorldArea(3152, 9898, 15, 12, 0),
            new WorldPoint(3158, 9903, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    NECHRYAEL("Nechryael", new String[]{"Nechryael"}, false, new WorldArea(3430, 3554, 24, 27, 2),
            new WorldPoint(3440, 3568, 2), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    OGRE("Ogres", new String[]{"Ogre"}, true, new WorldArea(2490, 2948, 128, 60, 0),
            new WorldPoint(2574, 2996, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    OTHERWORLDLY_BEING("Otherworldly Beings", new String[]{"Otherworldly Being"}, true, new WorldArea(2375, 4415, 19, 20, 0),
            new WorldPoint(2385, 4420, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    PYREFIEND("Pyrefiends", new String[]{"Pyrefiend"}, true, new WorldArea(2239, 2953, 31, 19, 0),
            new WorldPoint(2255, 2968, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    SEA_SNAKE("Sea Snakes", new String[]{"Sea Snake Hatchling", "Sea Snake Young"}, true, new WorldArea(2580, 10265, 27, 38, 0),
            new WorldPoint(2593, 10286, 0), Setup.SLAYER_MELEE_ANTIPOISON, Setup.SLAYER_MAGIC_ANTIPOISON, Setup.SLAYER_RANGED_ANTIPOISON),
    SHADES("Shades", new String[]{"Loar Shade", "Phin Shade", "Riyl Shade", "Asyn Shade", "Fiyr Shade", "Urium Shade"}, true, new WorldArea(3463, 3260, 62, 52, 0),
            new WorldPoint(3477, 3302, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    SHADOW_WARRIOR("Shadow Warriors", new String[]{"Shadow Warrior"}, true, new WorldArea(2689, 9764, 23, 21, 0),
            new WorldPoint(2699, 9775, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    SPIRITUAL_CREATURE("", new String[]{""}, true, new WorldArea(2689, 9764, 23, 21, 0),
            new WorldPoint(2699, 9775, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    TERROR_DOG("", new String[]{""}, true, new WorldArea(2689, 9764, 23, 21, 0),
            new WorldPoint(2699, 9775, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    TROLL("Trolls", new String[]{"Mountain Troll", "Ice Troll"}, true, new WorldArea(1227, 3508, 27, 19, 0),
            new WorldPoint(1239, 3518, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    TUROTH("Turoth", new String[]{"Turoth", "Spiked Turoth"}, true, new WorldArea(2709, 9991, 25, 24, 0),
            new WorldPoint(2722, 10005, 0), Setup.SLAYER_MELEE_KURASK, Setup.SLAYER_MAGIC_KURASK, Setup.SLAYER_RANGED_KURASK),
    VAMPYRE("Vampyres", new String[]{"Feral Vampyre", "Vampyre Juvenile", "Vampyre Juvinate", "Vyrewatch", "Vyrewatch Sentinel"}, true,
            new WorldArea(3586, 3459, 51, 56, 0), new WorldPoint(3611, 3488, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED),
    WEREWOLF("Werewolves", new String[]{"Werewolf", "Alexis", "Boris", "Eduard", "Galina", "Georgy", "Imre",
            "Irina", "Joseph", "Ksenia", "Lev", "Liliya", "Milla", "Nikita",
            "Nikolai", "Sofiya", "Svetlana", "Vera", "Yadviga", "Yuri", "Zoja"}, true, new WorldArea(3470, 3463, 51, 45, 0),
            new WorldPoint(3493, 3490, 0), Setup.SLAYER_MELEE, Setup.SLAYER_MAGIC, Setup.SLAYER_RANGED);

    final String name;
    final String[] NpcName;
    final boolean canUseCannon;
    final WorldArea worldArea;
    final WorldPoint worldPoint;
    final Setup meleeSetup;
    final Setup magicSetup;
    final Setup rangedSetup;

    public static Stream<SlayerTask> stream() {
        return Stream.of(SlayerTask.values());
    }

    public static SlayerTask findTaskByName(String name) {
        return stream().filter(slayerTask -> slayerTask.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
