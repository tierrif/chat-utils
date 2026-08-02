package io.github.hotlava03.chatutils.listeners;

import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;

import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.util.ComponentJson;

public class ChatPersistListener implements ReceiveMessageCallback {
    private static final String ANTI_SPAM_REGEX = " §8\\[§cx\\d§8]$";
    private static final Pattern ANTI_SPAM_PATTERN = Pattern.compile(ANTI_SPAM_REGEX);

    @Override
    public void accept(Component text, List<GuiMessage.Line> visibleLines) {
        var client = Minecraft.getInstance();
        var serverInfo = client.getCurrentServer();
        var address = serverInfo != null ? serverInfo.ip : null;
        if (address == null) return; // Don't store if it's single-player.

        var message = text.getString();

        var storage = ChatStorage.getInstance();
        var lines = storage.getStoredChatLines(address);

        if (!lines.isEmpty() && !storage.isBlockingChatEvents()) {
            var last = ComponentJson.fromJson(lines.get(lines.size() - 1));
            if (last == null) return;
            if (message.matches(".+" + ANTI_SPAM_REGEX)) {
                var lastLine = this.removeAntiSpamIndicator(last.getString());
                if (this.removeAntiSpamIndicator(message).equals(lastLine)) {
                    storage.removeChat(address, lines.size() - 1);
                }
            }
        }

        var json = ComponentJson.toJson(text);
        if (json == null) return;

        storage.pushChat(json, address);
        storage.saveAsync();
    }

    private String removeAntiSpamIndicator(String originalMessage) {
        return ANTI_SPAM_PATTERN.matcher(originalMessage).replaceAll("");
    }
}
