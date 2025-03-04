package net.runelite.client.plugins.microbot.breakhandler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScriptState {
    SESSION,
    BREAK,
    MICRO_BREAK
}
