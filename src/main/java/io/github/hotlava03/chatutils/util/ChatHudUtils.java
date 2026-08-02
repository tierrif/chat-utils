package io.github.hotlava03.chatutils.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.util.Mth;

import io.github.hotlava03.chatutils.mixin.ChatHudAccessor;

public class ChatHudUtils {
    public static GuiMessage getMessageAt(double x, double y) {
        var chat = Minecraft.getInstance().gui.hud.getChat();
        var accessor = (ChatHudAccessor) chat;

        int lineSelected = getMessageLineIndex(chat, accessor, x, y);

        // User clicked in the middle of nowhere
        if (lineSelected == -1) {
            return null;
        }

        var trimmedMessages = accessor.getTrimmedMessages();
        if (lineSelected >= trimmedMessages.size()) {
            return null;
        }

        return trimmedMessages.get(lineSelected).parent();
    }

    private static int getMessageLineIndex(
            ChatComponent chat,
            ChatHudAccessor accessor,
            double x,
            double y
    ) {
        var client = Minecraft.getInstance();
        if (!chat.isChatFocused()) {
            return -1;
        }

        double scale = accessor.invokeGetScale();
        if (scale <= 0.0) return -1;

        // Undo the pose transform applied while rendering the chat.
        double chatX = x / scale - 4.0;
        double chatY = y / scale;

        if (chatX < -4.0 || chatX > Mth.floor(accessor.invokeGetWidth() / scale)) {
            return -1;
        }

        int lineHeight = accessor.invokeGetLineHeight();
        if (lineHeight <= 0) return -1;

        int screenHeight = client.getWindow().getGuiScaledHeight();
        int chatBottom = Mth.floor((screenHeight - 40) / scale);

        // Lines stack upwards from the bottom of the chat area.
        int lineIndex = Mth.floor((chatBottom - chatY) / lineHeight);
        if (lineIndex < 0) return -1;

        int scrollPos = accessor.getChatScrollbarPos();
        int visibleLines = Math.min(
                accessor.getTrimmedMessages().size() - scrollPos,
                chat.getLinesPerPage()
        );

        if (lineIndex >= visibleLines) {
            return -1;
        }

        return lineIndex + scrollPos;
    }
}
