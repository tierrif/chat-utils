package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.MessageReceiveEvent;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.util.StringUtils;
import net.minecraft.client.MinecraftClient;

import java.util.function.Consumer;
import java.util.regex.Pattern;

public class ChatPersistListener implements Consumer<MessageReceiveEvent> {
    private static final String ANTI_SPAM_REGEX = " §8\\[§c\\dx§8]$";
    private static final Pattern ANTI_SPAM_PATTERN = Pattern.compile(ANTI_SPAM_REGEX);

    @Override
    public void accept(MessageReceiveEvent e) {
        var client = MinecraftClient.getInstance();
        var serverInfo = client.getCurrentServerEntry();
        var address = serverInfo != null ? serverInfo.address : ChatStorage.SINGLEPLAYER_ADDRESS;
        var message = StringUtils.textToLegacy(e.getText(), false);

        var storage = ChatStorage.getInstance();
        var lines = storage.getStoredLines(address);

        if (!lines.isEmpty() && !storage.isLockingChatEvents()) {
            var last = lines.get(lines.size() - 1);
            if (message.matches(".+" + ANTI_SPAM_REGEX)) {
                var lastLine = this.removeAntiSpamIndicator(last);
                if (StringUtils.getDifference(this.removeAntiSpamIndicator(message), lastLine) <= 0) {
                    storage.remove(address, lines.size() - 1);
                }
            }
        }

        storage.push(message, address);
        storage.saveAsync();
    }

    private String removeAntiSpamIndicator(String originalMessage) {
        return ANTI_SPAM_PATTERN.matcher(originalMessage).replaceAll("");
    }
}
