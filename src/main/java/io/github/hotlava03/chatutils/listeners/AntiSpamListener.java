package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;

import static io.github.hotlava03.chatutils.util.OrderedTextAdapter.orderedTextToString;
import static io.github.hotlava03.chatutils.util.StringUtils.getDifference;

public class AntiSpamListener implements ReceiveMessageCallback {
    @Override
    public void accept(Text text, List<ChatHudLine.Visible> lines) {
        if (ChatStorage.getInstance().isBlockingChatEvents()) return;
        var client = MinecraftClient.getInstance();
        var chat = client.inGameHud.getChatHud();
        var range = ChatUtilsConfig.ANTI_SPAM_RANGE.value();
        var history = lines.size() >= range ? lines.subList(0, range) : lines;
        if (history.isEmpty()) return;

        var maxTextLength = MathHelper.floor(chat.getWidth() / chat.getChatScale());
        var splitLines = ChatMessages.breakRenderedChatMessageLines(
                text, maxTextLength, client.textRenderer);

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
                    } catch (NumberFormatException ex) {
                        lineMatchCount = 0;
                        continue;
                    }
                }
            }

            if (i + lineMatchCount >= i) {
                history.subList(i, i + lineMatchCount + 1).clear();
            }
            lineMatchCount = 0;
        }

        if (spamCounter > 1) ((MutableText) text).append(" §8[§cx" + spamCounter + "§8]");
    }
}
