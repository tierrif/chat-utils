package io.github.hotlava03.chatutils.listeners;

import net.minecraft.client.Minecraft;

import net.kyori.adventure.text.Component;

import io.github.hotlava03.chatutils.events.CopyToClipboardCallback;
import io.github.hotlava03.chatutils.util.TooltipAlert;

import static io.github.hotlava03.chatutils.util.StringUtils.forClipboard;

public class CopyToClipboardListener implements CopyToClipboardCallback {

    @Override
    public void accept(Component component, int creationTicks) {
        var client = Minecraft.getInstance();

        // Copy to clipboard
        client.keyboardHandler.setClipboard(forClipboard(component));

        TooltipAlert.getInstance().start(creationTicks);
    }
}
