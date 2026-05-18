package ru.lynceus.trollplayer.managers;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum TrollAction {
    CRASH("Send a crash string to the player's chat"),
    FAKE_BAN("Send a fake ban message"),
    FAKE_LAG("Simulate high ping with random setbacks and block glitches"),
    FREEZE("Freeze the player in place"),
    RANDOM_TP("Teleport to random locations 5 times"),
    SPAM_SOUNDS("Blast random sounds at the player");

    private final String description;

    TrollAction(String description) {
        this.description = description;
    }

    public String getDescription() { return description; }

    public static String listAll() {
        return Arrays.stream(values())
                .map(a -> a.name().toLowerCase())
                .collect(Collectors.joining(", "));
    }
}

