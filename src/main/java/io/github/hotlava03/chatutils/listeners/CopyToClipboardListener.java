package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.CopyToClipboardCallback;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class CopyToClipboardListener implements CopyToClipboardCallback {
    private long timestamp = -1L;

    @Override
    public void accept(Component component) {
        var client = MinecraftClient.getInstance();

        String toCopy = ChatUtilsConfig.COPY_COLORS.value()
                ? PlainTextComponentSerializer.plainText().serialize(component)
                : textToLegacy(component, ChatUtilsConfig.COPY_HEX_COLORS.value());

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

    private static String textToLegacy(Component component, boolean useHexCodes) {
        var builder = LegacyComponentSerializer.builder()
                .character(LegacyComponentSerializer.AMPERSAND_CHAR)
                .flattener(FabricClientAudiences.of().flattener());
        if (useHexCodes) builder.hexColors();
        return builder.build().serialize(component);
    }
}
