package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.CopyToClipboardCallback;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import net.kyori.adventure.text.Component;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import static io.github.hotlava03.chatutils.util.StringUtils.componentToLegacy;
import static io.github.hotlava03.chatutils.util.StringUtils.componentToPlainText;

public class CopyToClipboardListener implements CopyToClipboardCallback {
    private long timestamp = -1L;

    @Override
    public void accept(Component component) {
        var client = MinecraftClient.getInstance();

        String toCopy = ChatUtilsConfig.COPY_COLORS.value()
                ? componentToPlainText(component)
                : componentToLegacy(component, ChatUtilsConfig.COPY_HEX_COLORS.value());

        MinecraftClient.getInstance().keyboard.setClipboard(toCopy);
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
