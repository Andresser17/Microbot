package net.runelite.client.plugins.microbot.pvmfighter.helpers.skill;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.pvmfighter.HelperScript;
import net.runelite.client.plugins.microbot.pvmfighter.enums.PlayerState;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.*;


@Slf4j
public class AttackStyleScript extends Script {

    private final int CHANGE_STYLE_FREQUENCY = 10; // how many levels of one skill level up before change
    private final Skill[] SUPPORTED_SKILLS = new Skill[]{Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE};
    public static AttackStyle currentAttackStyle;
    private boolean init = true;

    public void run(PvmFighterConfig config) {
        // Early exit conditions
        if (HelperScript.helperState != PlayerState.ATTACK_STYLE || disableIfMaxed(config.disableOnMaxCombat())) return;

        if (init) {
            currentAttackStyle = AttackStyle.findAttackStyleByVarbitValue(Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE));
            init = false;
        }

        Skill lowestSkill = getLowestSkill(config, SUPPORTED_SKILLS);
        if (!Arrays.asList(currentAttackStyle.getSkills()).contains(lowestSkill)) {
            switch (lowestSkill) {
                case ATTACK:
                    currentAttackStyle = AttackStyle.ACCURATE;
                    break;
                case STRENGTH:
                    currentAttackStyle = AttackStyle.AGGRESSIVE;
                    break;
                case DEFENCE:
                    currentAttackStyle = AttackStyle.DEFENSIVE;
                    break;
                default:
                    Microbot.log("Style not supported");
            }

            changeAttackStyle();
        }
    }

    public boolean needsToChangeAttackStyle(PvmFighterConfig config) {
        if (!config.toggleSkilling()) return false;
        if (currentAttackStyle == null) return true;

        Skill[] skillsToTrain = currentAttackStyle.getSkills();
        return Arrays.stream(skillsToTrain).allMatch(skill -> Rs2Player.getRealSkillLevel(skill) == mapSkillToLevelTarget(config, skill));
    }

    private void changeAttackStyle() {
        if (Rs2Tab.getCurrentTab() != InterfaceTab.COMBAT) {
            Rs2Tab.switchToCombatOptionsTab();
            sleepUntil(() -> Rs2Tab.getCurrentTab() == InterfaceTab.COMBAT, 2000);
        }
        Rs2Widget.clickWidget(currentAttackStyle.getWidgetId());
    }

    private Skill getLowestSkill(PvmFighterConfig config, Skill[] skills) {
        Skill lowestSkill = null;
        int lowestLevel = Integer.MAX_VALUE;

        for (Skill skill : skills) {
            if (skill == null) {
                continue;
            }

            int level = Rs2Player.getRealSkillLevel(skill);
            int targetLevel = mapSkillToLevelTarget(config, skill);
            if (level < lowestLevel && level < targetLevel) {
                lowestSkill = skill;
                lowestLevel = level;
            }
        }

        // Check for multiple skills with the same lowest level
        List<Skill> lowestSkills = new ArrayList<>();
        for (Skill skill : skills) {
            if (skill != null && Rs2Player.getRealSkillLevel(skill) == lowestLevel) {
                lowestSkills.add(skill);
            }
        }

        // If there are more than one skill with the lowest level, select any of them
        if (lowestSkills.size() > 1) {
            log.info("Multiple lowest skills: {}", lowestSkills);
            return lowestSkills.get(0); // or any other selection logic if needed
        }
        log.info("Lowest Skill: {}", lowestSkill);
        return lowestSkill;
    }

    private int mapSkillToLevelTarget(PvmFighterConfig config, Skill skill) {
        int target = -1;

        switch (skill) {
            case ATTACK:
                target = config.attackSkillTarget();
                break;
            case STRENGTH:
                target = config.strengthSkillTarget();
                break;
            case DEFENCE:
                target = config.defenceSkillTarget();
                break;
        }

        return target;
    }

    private int getSkillLevel(Skill skill) {
        return Microbot.getClient().getRealSkillLevel(skill);
    }

    private boolean isMaxed() {
        return getSkillLevel(Skill.ATTACK) == 99 && getSkillLevel(Skill.STRENGTH) == 99 && getSkillLevel(Skill.DEFENCE) == 99;
    }

    private boolean disableIfMaxed(boolean disable) {
        return isMaxed() && disable;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        currentAttackStyle = null;
        init = true;
    }
}