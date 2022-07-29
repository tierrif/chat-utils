package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.EventHandler;
import io.github.hotlava03.chatutils.events.types.SendCommandEvent;
import io.github.hotlava03.chatutils.events.types.SendCommandListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class CopyToClipboardListener extends SendCommandListener {
    private long timestamp = -1L;

    @Override
    public void onCommandSent(SendCommandEvent e) {
        var client = MinecraftClient.getInstance();

        if (e.getCommand().startsWith("chatmacros ")) {
            e.getCallbackInfo().cancel();
            String toCopy = e.getCommand().replaceFirst("chatmacros ", "");
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(toCopy), null);
            SystemToast.show(client.getToastManager(),
                    SystemToast.Type.PERIODIC_NOTIFICATION,
                    Text.literal("Chat Utils"),
                    Text.literal("Text copied.")
            );
            if (client.player == null || System.currentTimeMillis() - timestamp < 5000L) {
                timestamp = System.currentTimeMillis();
                return;
            }
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING, 2f, 1.5f);
            timestamp = System.currentTimeMillis();
        }
    }

    @Override
    public EventHandler.EventType getType() {
        return EventHandler.EventType.SEND_COMMAND;
    }
}
