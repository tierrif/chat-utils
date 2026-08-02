package io.github.hotlava03.chatutils.listeners;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;

import io.github.hotlava03.chatutils.events.ReceiveMessageCallback;
import io.github.hotlava03.chatutils.fileio.ChatStorage;
import io.github.hotlava03.chatutils.fileio.ChatUtilsConfig;
import io.github.hotlava03.chatutils.mixin.ChatHudAccessor;
import io.github.hotlava03.chatutils.util.AntiSpamCounter;

public class AntiSpamListener implements ReceiveMessageCallback {

    @Override
    public Component accept(Component text, List<GuiMessage.Line> lines) {
        if (!ChatUtilsConfig.ANTI_SPAM.value()) return text;
        if (ChatStorage.getInstance().isBlockingChatEvents()) return text;

        var chat = Minecraft.getInstance().gui.hud.getChat();
        var accessor = (ChatHudAccessor) chat;
        var allMessages = accessor.getAllMessages();

        var incoming = AntiSpamCounter.key(text);
        var range = Math.min(ChatUtilsConfig.ANTI_SPAM_RANGE.value(), allMessages.size());

        for (int i = 0; i < range; i++) {
            var previous = AntiSpamCounter.key(allMessages.get(i).content());

            if (!AntiSpamCounter.strip(previous).equals(incoming)) continue;

            removeMessage(chat, accessor, lines, i);

            return text.copy()
                    .append(AntiSpamCounter.suffix(AntiSpamCounter.countOf(previous) + 1));
        }

        return text;
    }

    private void removeMessage(
            ChatComponent chat,
            ChatHudAccessor accessor,
            List<GuiMessage.Line> lines,
            int index
    ) {
        var message = accessor.getAllMessages().remove(index);

        var scrollPos = accessor.getChatScrollbarPos();
        var removedBelowView = 0;
        for (int i = lines.size() - 1; i >= 0; i--) {
            if (lines.get(i).parent() != message) {
                continue;
            }

            lines.remove(i);
            if (i < scrollPos) {
                removedBelowView++;
            }
        }

        // Lines removed from under a scrolled-up viewport would otherwise shift its contents.
        if (removedBelowView > 0) {
            chat.scrollChat(-removedBelowView);
        }
    }
}
