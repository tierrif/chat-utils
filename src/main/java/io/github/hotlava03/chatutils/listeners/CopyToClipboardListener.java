package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.CopyToClipboardCallback;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.TooltipAlert;
import net.kyori.adventure.text.Component;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import static io.github.hotlava03.chatutils.util.StringUtils.componentToLegacy;
import static io.github.hotlava03.chatutils.util.StringUtils.componentToPlainText;

public class CopyToClipboardListener implements CopyToClipboardCallback {

    @Override
    public void accept(Component component, int creationTicks) {
        var client = MinecraftClient.getInstance();

        String toCopy = ChatUtilsConfig.COPY_COLORS.value()
                ? componentToPlainText(component)
                : componentToLegacy(component, ChatUtilsConfig.COPY_HEX_COLORS.value());

        // Copy to clipboard
        client.keyboard.setClipboard(toCopy);

        TooltipAlert.getInstance().start(creationTicks);
    }
}
