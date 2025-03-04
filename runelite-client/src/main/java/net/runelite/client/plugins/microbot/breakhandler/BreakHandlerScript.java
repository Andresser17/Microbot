package net.runelite.client.plugins.microbot.breakhandler;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.ClientUI;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BreakHandlerScript extends Script {
    public static String version = "1.2.0";
    public static ScriptState scriptState;

    public static Duration breakDuration;
    public static Duration sessionDuration;
    public static int sessionTime = -1;
    public static int breakTime = -1;

    public static int completedSessions = 0;
    public static int completedBreaks = 0;
    public static int totalPlayTime = 0;

    @Setter
    @Getter
    public static boolean lockState = false;
    private String originalTitle;
    private boolean showMessage;
    public static boolean isBreakActive() {
        return breakTime > 0;
    }
    public static boolean scriptIsTurnedOn;

    public static String formatDuration(Duration duration, String header) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format(header + " %02d:%02d:%02d", hours, minutes, seconds);
    }

    public boolean run(BreakHandlerConfig config) {
        Microbot.enableAutoRunOn = false;
        originalTitle = ClientUI.getFrame().getTitle();
        sessionTime = Rs2Random.between(config.minPlayTime() * 60, config.maxPlayTime() * 60);
        totalPlayTime = sessionTime;
        scriptIsTurnedOn = true;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (Microbot.pauseAllScripts || isLockState()) return;

                if (completedSessions >= config.gameSessions() && !showMessage) {
                    Rs2Player.logout();
                    Microbot.showMessage(String.format("Game sessions completed (%d). %s", completedSessions, formatDuration(Duration.ofSeconds(totalPlayTime), "Total play time: ")));
                    showMessage = true;
                    lockState = true;
                    return;
                }

                // check if player is in desired location
                getScriptState(config);
                switch (scriptState) {
                    case SESSION:
                        if (sessionTime <= 0) {
                            sessionTime = Rs2Random.between(config.minPlayTime() * 60, config.maxPlayTime() * 60);
                            totalPlayTime += sessionTime;
                            ClientUI.getFrame().setTitle(originalTitle);
                        }
                        sessionTime--;
                        sessionDuration = Duration.between(LocalDateTime.now(), LocalDateTime.now().plusSeconds(sessionTime));
                        if (sessionTime == 0) completedSessions++;
                        break;
                    case BREAK:
                        if (breakTime <= 0) {
                            breakTime = Rs2Random.between(config.minBreakTime() * 60, config.maxBreakTime() * 60);
                        }

                        breakTime--;
                        breakDuration = Duration.between(LocalDateTime.now(), LocalDateTime.now().plusSeconds(breakTime));
                        long hours = BreakHandlerScript.breakDuration.toHours();
                        long minutes = BreakHandlerScript.breakDuration.toMinutes() % 60;
                        long seconds = BreakHandlerScript.breakDuration.getSeconds() % 60;
                        ClientUI.getFrame().setTitle(String.format("Break duration: %02d:%02d:%02d", hours, minutes, seconds));
                        if (breakTime == 0) completedBreaks++;
                        break;
                }

            } catch (Exception ex) {
                log.info(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private void getScriptState(BreakHandlerConfig config) {
        if (needsToBreak()) {
            scriptState = ScriptState.BREAK;
            return;
        }

        scriptState = ScriptState.SESSION;
    }

    private boolean needsToBreak() {
        return sessionTime == 0 && completedBreaks < completedSessions;
    }

    @Override
    public void shutdown() {
        sessionTime = 0;
        breakTime = 0;
        completedSessions = 0;
        completedBreaks = 0;
        totalPlayTime = 0;
        showMessage = false;
        lockState = false;
        scriptIsTurnedOn = false;
        ClientUI.getFrame().setTitle(originalTitle);
        super.shutdown();
    }

    public void reset() {
        sessionTime = 0;
        breakTime = 0;
        completedSessions = 0;
        completedBreaks = 0;
        totalPlayTime = 0;
        showMessage = false;
        lockState = false;
        ClientUI.getFrame().setTitle(originalTitle);
    }
}
