package io.github.hotlava03.chatutils.listeners;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.mixin.ChatHudAccessor;
import static io.github.hotlava03.chatutils.util.OrderedTextAdapter.*;

public class AntiSpamListener implements ReceiveMessageCallback {
    private static final String ANTISPAM_REGEX = ".+(§8)? ?\\[(§c)?x(\\d+)(§8)?]$";

    @Override
    public void accept(Component text, List<GuiMessage.Line> lines) {
        if (!ChatUtilsConfig.ANTI_SPAM.value()) return;
        if (ChatStorage.getInstance().isBlockingChatEvents()) return;

        var client = Minecraft.getInstance();
        var chat = (ChatHudAccessor) client.gui.hud.getChat();
        var range = ChatUtilsConfig.ANTI_SPAM_RANGE.value();
        var history = lines.size() >= range ? lines.subList(0, range) : lines;
        if (history.isEmpty()) return;

        var maxTextLength = Mth.floor(chat.invokeGetWidth() / chat.invokeGetScale());
        var splitLines = ComponentRenderUtils.wrapComponents(
                text, maxTextLength, client.font);

        var spamCounter = 1;
        var lineMatchCount = 0;

        for (int i = history.size() - 1; i >= 0; i--) {
            String previousString = ChatUtilsConfig.ANTI_SPAM_IGNORE_COLORS.value()
                    ? orderedTextToString(history.get(i).content())
                    : toLegacySection(history.get(i).content());

            if (lineMatchCount <= splitLines.size() - 1) {
                String nextString = ChatUtilsConfig.ANTI_SPAM_IGNORE_COLORS.value()
                        ? orderedTextToString(splitLines.get(lineMatchCount))
                        : toLegacySection(splitLines.get(lineMatchCount));

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

        if (spamCounter > 1 && text instanceof MutableComponent mutable) {
            mutable.append(" §8[§cx" + spamCounter + "§8]");
        }
    }

    private String toLegacySection(FormattedCharSequence line) {
        var mutable = orderedTextToMutable(line);
        if (mutable == null) return "";
        return LegacyComponentSerializer.legacySection()
                .serialize(MinecraftClientAudiences.of().asAdventure(mutable));
    }

    private boolean hasAntispamIndicator(String message) {
        return message.matches(ANTISPAM_REGEX);
    }

    private int getAntispamCountFromMessage(String message) {
        return Integer.parseInt(message.replaceAll(ANTISPAM_REGEX, "$3"));
    }
}
