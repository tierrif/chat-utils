package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.ReceiveMessageEvent;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

import static io.github.hotlava03.chatutils.util.OrderedTextAdapter.orderedTextToString;
import static io.github.hotlava03.chatutils.util.StringUtils.getDifference;
import static io.github.hotlava03.chatutils.util.StringUtils.isNumeric;

public class AntiSpamListener implements Consumer<ReceiveMessageEvent> {
    @Override
    public void accept(ReceiveMessageEvent e) {
        if (ChatStorage.getInstance().isBlockingChatEvents()) return;
        var client = MinecraftClient.getInstance();
        var chat = client.inGameHud.getChatHud();
        var serverInfo = client.getCurrentServerEntry();
        var address = serverInfo != null ? serverInfo.address : null;
        var fullHistory = e.getLines();
        var range = ChatUtilsConfig.ANTI_SPAM_RANGE.value();
        var history = fullHistory.size() >= range ? fullHistory.subList(0, range) : fullHistory;
        if (history.isEmpty()) return;

        var maxTextLength = MathHelper.floor(chat.getWidth() / chat.getChatScale());
        var splitLines = ChatMessages.breakRenderedChatMessageLines(
                e.getText(), maxTextLength, client.textRenderer);

        var spamCounter = 1;
        var lineMatchCount = 0;

        for (int i = history.size() - 1; i >= 0; i--) {
            var previous = orderedTextToString(history.get(i).content());

            if (lineMatchCount <= splitLines.size() - 1) {
                String next = orderedTextToString(splitLines.get(lineMatchCount));

                if (lineMatchCount < splitLines.size() - 1) {
                    if (getDifference(previous, next) <= 0) lineMatchCount++;
                    else lineMatchCount = 0;

                    continue;
                }

                if (!previous.startsWith(next)) {
                    lineMatchCount = 0;
                    continue;
                }

                if (i > 0 && lineMatchCount == splitLines.size() - 1) {
                    var appended = (previous + orderedTextToString(history.get(i - 1).content()))
                            .substring(next.length());

                    if (appended.startsWith(" [x") && appended.endsWith("]")) {
                        var previousCounter = appended.substring(3, appended.length() - 1);

                        try {
                            spamCounter += Integer.parseInt(previousCounter);
                            lineMatchCount++;
                            continue;
                        } catch (NumberFormatException ignored) {}
                    }
                }

                if (previous.length() == next.length()) spamCounter++;
                else {
                    var appended = previous.substring(next.length());
                    if (!appended.startsWith(" [x") || !appended.endsWith("]")) {
                        lineMatchCount = 0;
                        continue;
                    }

                    var previousCounter = appended.substring(3, appended.length() - 1);
                    try {
                        spamCounter += Integer.parseInt(previousCounter);
                    } catch (NumberFormatException e) {
                        lineMatchCount = 0;
                        continue;
                    }
                }
            }

            if (i + lineMatchCount >= i) {
                history.subList(i, i + lineMatchCount + 1).clear();
                /*if (address != null) {
                    var storage = ChatStorage.getInstance();
                    for (int j = i; j <= i + lineMatchCount; j++) {
                        storage.removeChat(address, j);
                    }
                    storage.saveAsync();
                }*/
            }
            lineMatchCount = 0;
        }

        if (spamCounter > 1) e.getTextAsMutable().append(" §8[§cx" + spamCounter + "§8]");
    }
}
