package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.SendCommandEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.function.Consumer;

public class CopyToClipboardListener implements Consumer<SendCommandEvent> {
    private long timestamp = -1L;

    @Override
    public void accept(SendCommandEvent e) {
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
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2f, 1.5f);
            timestamp = System.currentTimeMillis();
        }
    }
}
