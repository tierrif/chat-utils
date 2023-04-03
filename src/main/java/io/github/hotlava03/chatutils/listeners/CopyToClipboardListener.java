package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.SendCommandCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class CopyToClipboardListener implements SendCommandCallback {
    private long timestamp = -1L;

    @Override
    public void accept(CallbackInfo callbackInfo, String command) {
        var client = MinecraftClient.getInstance();

        if (command.startsWith("chatmacros ")) {
            callbackInfo.cancel();
            String toCopy = command.replaceFirst("chatmacros ", "");
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
