package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.util.StringUtils;
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
            var last = Text.Serializer.fromJson(lines.get(lines.size() - 1)).getString();
            if (message.matches(".+" + ANTI_SPAM_REGEX)) {
                var lastLine = this.removeAntiSpamIndicator(last);
                if (StringUtils.getDifference(this.removeAntiSpamIndicator(message), lastLine) <= 0) {
                    storage.removeChat(address, lines.size() - 1);
                }
            }
        }

        storage.pushChat(Text.Serializer.toJson(text), address);
        storage.saveAsync();
    }

    private String removeAntiSpamIndicator(String originalMessage) {
        return ANTI_SPAM_PATTERN.matcher(originalMessage).replaceAll("");
    }
}
