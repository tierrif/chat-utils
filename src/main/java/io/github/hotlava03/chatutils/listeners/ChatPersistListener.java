package io.github.hotlava03.chatutils.listeners;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;

import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.util.AntiSpamCounter;
import io.github.hotlava03.chatutils.util.ComponentJson;

public class ChatPersistListener implements ReceiveMessageCallback {

    @Override
    public Component accept(Component text, List<GuiMessage.Line> visibleLines) {
        if (!ChatUtilsConfig.ENABLE_CHAT_PERSIST.value()) return text;

        var client = Minecraft.getInstance();
        var serverInfo = client.getCurrentServer();
        var address = serverInfo != null ? serverInfo.ip : null;
        if (address == null) return text; // Don't store if it's single-player.

        var storage = ChatStorage.getInstance();

        if (storage.isBlockingChatEvents()) {
            return text;
        }

        dropCollapsedOccurrence(storage, address, text);

        var json = ComponentJson.toJson(text);
        if (json == null) return text;

        storage.pushChat(json, address);
        storage.saveAsync();

        // Purely an observer: whatever anti-spam settled on is what gets displayed.
        return text;
    }

    private void dropCollapsedOccurrence(ChatStorage storage, String address, Component message) {
        if (!ChatUtilsConfig.ANTI_SPAM.value()) {
            return;
        }

        var key = AntiSpamCounter.key(message);
        if (!AntiSpamCounter.hasCounter(key)) {
            return;
        }

        var lines = storage.getStoredChatLines(address);
        var stripped = AntiSpamCounter.strip(key);
        var oldest = Math.max(0, lines.size() - ChatUtilsConfig.ANTI_SPAM_RANGE.value());

        for (int i = lines.size() - 1; i >= oldest; i--) {
            var stored = ComponentJson.fromJson(lines.get(i));
            if (stored == null) {
                continue;
            }

            if (AntiSpamCounter.strip(AntiSpamCounter.key(stored)).equals(stripped)) {
                storage.removeChat(address, i);
                return;
            }
        }
    }
}
