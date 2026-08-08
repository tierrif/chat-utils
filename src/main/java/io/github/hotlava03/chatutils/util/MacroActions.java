package io.github.hotlava03.chatutils.util;

import net.minecraft.client.Minecraft;

public final class MacroActions {
    public static final int MAX_LENGTH = 256;

    private MacroActions() {
    }

    public static void run(String message) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        var trimmed = message.trim();
        if (trimmed.isEmpty()) return;

        if (trimmed.length() > MAX_LENGTH) {
            trimmed = trimmed.substring(0, MAX_LENGTH);
        }

        if (trimmed.startsWith("/")) {
            player.connection.sendCommand(trimmed.substring(1));
        } else {
            player.connection.sendChat(trimmed);
        }
    }
}
