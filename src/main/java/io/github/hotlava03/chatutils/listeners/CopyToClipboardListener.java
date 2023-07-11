package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.CopyToClipboardCallback;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import static io.github.hotlava03.chatutils.util.StringUtils.STRIP_COLOR_PATTERN;
import static io.github.hotlava03.chatutils.util.StringUtils.textToLegacy;

public class CopyToClipboardListener implements CopyToClipboardCallback {
    private long timestamp = -1L;

    @Override
    public void accept(Text textToCopy) {
        var client = MinecraftClient.getInstance();
        String toCopy = STRIP_COLOR_PATTERN.matcher(textToLegacy(textToCopy,
                ChatUtilsConfig.COPY_HEX_COLORS.value())).replaceAll(matchResult ->
                matchResult.group().replace("§", "&"));
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
