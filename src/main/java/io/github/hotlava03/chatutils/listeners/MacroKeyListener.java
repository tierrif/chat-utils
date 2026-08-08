package io.github.hotlava03.chatutils.listeners;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import io.github.hotlava03.chatutils.fileio.ChatMacro;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.KeyUtils;
import io.github.hotlava03.chatutils.util.MacroActions;
import org.jetbrains.annotations.NotNull;

public class MacroKeyListener implements ClientTickEvents.EndTick {
    private Set<Integer> down = Set.of();

    @Override
    public void onEndTick(@NotNull Minecraft client) {
        // Any open screen owns the keyboard — a macro key must not fire while typing in chat.
        if (client.player == null || client.gui.screen() != null || !ChatUtilsConfig.MACROS_ENABLED.value()) {
            this.down = Set.of();
            return;
        }

        var pressed = new HashSet<Integer>();
        for (ChatMacro macro : ChatUtilsConfig.MACROS.value()) {
            if (!macro.isBound() || !KeyUtils.isKeyDown(macro.key())) {
                continue;
            }

            pressed.add(macro.key());
            // Held over from an earlier tick; only the tick it goes down counts.
            if (!this.down.contains(macro.key())) {
                MacroActions.run(macro.message());
            }
        }

        this.down = pressed;
    }
}
