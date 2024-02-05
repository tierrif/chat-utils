package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;

import java.util.List;
import java.util.regex.Pattern;

public class ChatPersistListener implements ReceiveMessageCallback {
    private static final String ANTI_SPAM_REGEX = " §8\\[§cx\\d§8]$";
    private static final Pattern ANTI_SPAM_PATTERN = Pattern.compile(ANTI_SPAM_REGEX);

    @Override
    public void accept(Text text, List<ChatHudLine.Visible> visibleLines) {
        var client = MinecraftClient.getInstance();
        var serverInfo = client.getCurrentServerEntry();
        var address = serverInfo != null ? serverInfo.address : null;
        if (address == null) return; // Don't store if it's single-player.

        var message = text.getString();

        var storage = ChatStorage.getInstance();
        var lines = storage.getStoredChatLines(address);

        if (!lines.isEmpty() && !storage.isBlockingChatEvents()) {
            var last = Text.Serialization.fromJson(lines.get(lines.size() - 1));
            if (last == null) return;
            if (message.matches(".+" + ANTI_SPAM_REGEX)) {
                var lastLine = this.removeAntiSpamIndicator(last.getString());
                if (this.removeAntiSpamIndicator(message).equals(lastLine)) {
                    storage.removeChat(address, lines.size() - 1);
                }
            }
        }

        storage.pushChat(Text.Serialization.toJsonString(text), address);
        storage.saveAsync();
    }

    private String removeAntiSpamIndicator(String originalMessage) {
        return ANTI_SPAM_PATTERN.matcher(originalMessage).replaceAll("");
    }
}
