package io.github.hotlava03.chatutils.listeners;

import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;

import static io.github.hotlava03.chatutils.util.OrderedTextAdapter.*;

public class AntiSpamListener implements ReceiveMessageCallback {
    private static final String ANTISPAM_REGEX = ".+(§8)? ?\\[(§c)?x(\\d+)(§8)?]$";

    @Override
    public void accept(Text text, List<ChatHudLine.Visible> lines) {
        if (!ChatUtilsConfig.ANTI_SPAM.value()) return;
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
            String previousString = ChatUtilsConfig.ANTI_SPAM_IGNORE_COLORS.value()
                    ? orderedTextToString(history.get(i).content())
                    : LegacyComponentSerializer.legacySection().serialize(
                            Component.textOfChildren(orderedTextToMutable(history.get(i).content())));

            if (lineMatchCount <= splitLines.size() - 1) {
                String nextString = ChatUtilsConfig.ANTI_SPAM_IGNORE_COLORS.value()
                        ? orderedTextToString(splitLines.get(lineMatchCount))
                        : LegacyComponentSerializer.legacySection().serialize(
                        Component.textOfChildren(orderedTextToMutable(splitLines.get(lineMatchCount))));

                if (lineMatchCount < splitLines.size() - 1) {
                    if ((ChatUtilsConfig.ANTI_SPAM_IGNORE_COLORS.value() && previousString.equals(nextString))) {
                        lineMatchCount++;
                    } else {
                        lineMatchCount = 0;
                    }

                    continue;
                }

                if (!previousString.startsWith(nextString)) {
                    lineMatchCount = 0;
                    continue;
                }

                if (i > 0 && lineMatchCount == splitLines.size() - 1) {
                    if (hasAntispamIndicator(previousString)) {
                        int previousCounter = getAntispamCountFromMessage(previousString);

                        spamCounter += previousCounter;
                        lineMatchCount++;
                    }
                }

                if (previousString.length() == nextString.length()) spamCounter++;
                else {
                    if (!hasAntispamIndicator(previousString)) {
                        lineMatchCount = 0;
                        continue;
                    }

                    int previousCounter = getAntispamCountFromMessage(previousString);
                    spamCounter += previousCounter;
                }
            }

            if (i + lineMatchCount >= i) {
                history.subList(i, i + lineMatchCount + 1).clear();
            }
            lineMatchCount = 0;
        }

        if (spamCounter > 1) ((MutableText) text).append(" §8[§cx" + spamCounter + "§8]");
    }

    private boolean hasAntispamIndicator(String message) {
        return message.matches(ANTISPAM_REGEX);
    }

    private int getAntispamCountFromMessage(String message) {
        return Integer.parseInt(message.replaceAll(ANTISPAM_REGEX, "$3"));
    }
}
