package io.github.hotlava03.chatutils.listeners;

import net.minecraft.client.Minecraft;

import net.kyori.adventure.text.Component;

import io.github.hotlava03.chatutils.events.CopyToClipboardCallback;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.TooltipAlert;

import static io.github.hotlava03.chatutils.util.StringUtils.componentToLegacy;
import static io.github.hotlava03.chatutils.util.StringUtils.componentToPlainText;

public class CopyToClipboardListener implements CopyToClipboardCallback {

    @Override
    public void accept(Component component, int creationTicks) {
        var client = Minecraft.getInstance();

        String toCopy = ChatUtilsConfig.COPY_COLORS.value()
                ? componentToLegacy(component, ChatUtilsConfig.COPY_HEX_COLORS.value())
                : componentToPlainText(component);

        // Copy to clipboard
        client.keyboardHandler.setClipboard(toCopy);

        TooltipAlert.getInstance().start(creationTicks);
    }
}
