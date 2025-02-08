package net.runelite.client.plugins.microbot.pvmfighter.helpers.skill;

import lombok.Getter;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.WidgetNode;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.plugins.microbot.pvmfighter.enums.Potion;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
public enum AttackStyle {
    ACCURATE("Accurate", 0, 38862853, Skill.ATTACK),
    AGGRESSIVE("Aggressive", 1, 38862857, Skill.STRENGTH),
    CONTROLLED("Controlled", 2, 38862861, Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE),
    DEFENSIVE("Defensive", 3, 38862865, Skill.DEFENCE),
    CASTING("Casting", 4, 38862875, Skill.MAGIC),
    DEFENSIVE_CASTING("Defensive Casting", 5, 38862870, Skill.MAGIC, Skill.DEFENCE),
    ACCURATE_RANGED("Accurate", 6, 38862853, Skill.RANGED),
    RAPID("Rapid", 6, 38862857, Skill.RANGED),
    LONG_RANGE("Longrange", 7, 38862865, Skill.RANGED, Skill.DEFENCE),
    OTHER("Other", -1, -1);

    private final String name;
    private final int varbit;
    private final int widgetId;
    private final Skill[] skills;

    AttackStyle(String name, int varbit, int widgetId, Skill... skills) {
        this.name = name;
        this.varbit = varbit;
        this.widgetId = widgetId;
        this.skills = skills;
    }

    public static Stream<AttackStyle> stream() {
        return Stream.of(AttackStyle.values());
    }

    public static AttackStyle findAttackStyleByVarbitValue(int varbit) {
        Optional<AttackStyle> optional = stream().filter(attackStyle -> attackStyle.getVarbit() == varbit).findFirst();
        return optional.orElse(null);
    }
}
