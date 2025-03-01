package net.runelite.client.plugins.microbot.pvmfighter.model;

import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcStats;
import net.runelite.client.plugins.microbot.pvmfighter.enums.CombatStyle;

public class Monster {
    public int id;
    public int attackAnim;
    public NPC npc;
    public Rs2NpcStats rs2NpcStats;
    public boolean delete;

    public int lastAttack = 0;
    public CombatStyle attackStyle;

    public Monster(int npcId, int attackAnim) {
        this.id = npcId;
        this.attackAnim = attackAnim;
    }
    public Monster(NPC npc, Rs2NpcStats rs2NpcStats) {
        this.npc = npc;
        this.rs2NpcStats = rs2NpcStats;
        this.lastAttack = Rs2NpcManager.getAttackSpeed(npc.getId());
    }
}
