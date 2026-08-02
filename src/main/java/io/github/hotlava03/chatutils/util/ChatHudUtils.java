package io.github.hotlava03.chatutils.util;

import java.util.stream.IntStream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.util.Mth;

import org.apache.logging.log4j.LogManager;

import io.github.hotlava03.chatutils.mixin.ChatHudAccessor;

public class ChatHudUtils {
    public static GuiMessage getMessageAt(double x, double y) {
        var chat = Minecraft.getInstance().gui.hud.getChat();
        var accessor = (ChatHudAccessor) chat;

        int lineSelected = getMessageLineIndex(chat, accessor, x, y);

        // User clicked in the middle of nowhere
        if (lineSelected == -1) return null;

        var trimmedMessages = accessor.getTrimmedMessages();

        // This is a list containing all endOfEntry lines' indices
        var indexesOfEntryEnds = IntStream.range(0, trimmedMessages.size())
                .filter(index -> trimmedMessages.get(index).endOfEntry())
                .boxed()
                .toList();

        // Get the index of the final entry belonging to the message of this line
        int indexOfMessageEntryEnd = indexesOfEntryEnds
                .stream()
                .filter(index -> index <= lineSelected)
                .reduce((a, b) -> b) // Improvised findLast()
                .orElse(-1);

        if (indexOfMessageEntryEnd == -1) {
            LogManager.getLogger().warn("Something cursed happened (indexOfMessageEntryEnd == -1)");
            return null;
        }

        int indexOfMessage = indexesOfEntryEnds.indexOf(indexOfMessageEntryEnd);
        var allMessages = accessor.getAllMessages();
        if (indexOfMessage < 0 || indexOfMessage >= allMessages.size()) return null;
        return allMessages.get(indexOfMessage);
    }

    /**
     * Reimplementation of the {@code getMessageLineIndex}/{@code toChatLineX}/{@code toChatLineY}
     * trio that 26.x dropped from {@link ChatComponent}. The geometry mirrors
     * {@code ChatComponent#extractRenderState}: the pose is scaled by the chat scale and then
     * translated 4px right, lines are laid out upwards from {@code (screenHeight - 40) / scale},
     * and each line occupies {@code getLineHeight()} units.
     *
     * @return the index into {@code trimmedMessages}, or -1 if the point hits no line.
     */
    private static int getMessageLineIndex(ChatComponent chat, ChatHudAccessor accessor,
                                           double x, double y) {
        var client = Minecraft.getInstance();
        if (!chat.isChatFocused()) return -1;

        double scale = accessor.invokeGetScale();
        if (scale <= 0.0) return -1;

        // Undo the pose transform applied while rendering the chat.
        double chatX = x / scale - 4.0;
        double chatY = y / scale;

        if (chatX < -4.0 || chatX > Mth.floor(accessor.invokeGetWidth() / scale)) return -1;

        int lineHeight = accessor.invokeGetLineHeight();
        if (lineHeight <= 0) return -1;

        int screenHeight = client.getWindow().getGuiScaledHeight();
        int chatBottom = Mth.floor((screenHeight - 40) / scale);

        // Lines stack upwards from the bottom of the chat area.
        int lineIndex = Mth.floor((chatBottom - chatY) / lineHeight);
        if (lineIndex < 0) return -1;

        int scrollPos = accessor.getChatScrollbarPos();
        int visibleLines = Math.min(accessor.getTrimmedMessages().size() - scrollPos,
                chat.getLinesPerPage());
        if (lineIndex >= visibleLines) return -1;

        return lineIndex + scrollPos;
    }
}
